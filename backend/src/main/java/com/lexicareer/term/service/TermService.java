package com.lexicareer.term.service;

import com.lexicareer.entity.*;
import com.lexicareer.repository.*;
import com.lexicareer.term.dto.TermRequest;
import com.lexicareer.term.dto.TermResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TermService {

    private final TermRepository termRepository;
    private final UserTermRepository userTermRepository;
    private final DailyTermLogRepository dailyTermLogRepository;
    private final UserInterestRepository userInterestRepository;
    private final CategoryRepository categoryRepository;

    public TermResponse getDailyTerm(User user) {
        // Verificar se já recebeu termo hoje
        LocalDate today = LocalDate.now();
        if (dailyTermLogRepository.findTodayLog(user, today).isPresent()) {
            DailyTermLog log = dailyTermLogRepository.findTodayLog(user, today).get();
            return mapToResponse(log.getTerm());
        }

        // Buscar categorias de interesse do usuário
        List<UserInterest> interests = userInterestRepository.findByUser(user);
        if (interests.isEmpty()) {
            // Se nenhuma categoria selecionada, usar categoria Geral
            Category geral = categoryRepository.findBySlug("geral")
                .orElseThrow(() -> new RuntimeException("Categoria Geral não encontrada"));
            interests.add(UserInterest.builder().user(user).category(geral).build());
        }

        // Buscar termos ativos nas categorias de interesse
        List<Long> categoryIds = interests.stream()
            .map(ui -> ui.getCategory().getId())
            .collect(Collectors.toList());

        List<Term> availableTerms = termRepository.findAll().stream()
            .filter(t -> t.isActive() && categoryIds.contains(t.getCategory().getId()))
            .filter(t -> t.getPlanType().toString().equals(user.getPlan().toString()) || 
                    t.getPlanType().toString().equals("FREE"))
            .collect(Collectors.toList());

        if (availableTerms.isEmpty()) {
            throw new RuntimeException("Nenhum termo disponível");
        }

        // Selecionar termo aleatoriamente
        Term dailyTerm = availableTerms.get(new Random().nextInt(availableTerms.size()));

        // Registrar no log
        DailyTermLog log = DailyTermLog.builder()
            .user(user)
            .term(dailyTerm)
            .shownAt(LocalDateTime.now())
            .build();
        dailyTermLogRepository.save(log);

        // Registrar em user_term se ainda não existe
        userTermRepository.findByUserAndTerm(user, dailyTerm)
            .orElseGet(() -> {
                UserTerm userTerm = UserTerm.builder()
                    .user(user)
                    .term(dailyTerm)
                    .status(UserTerm.Status.PRESENTED)
                    .build();
                return userTermRepository.save(userTerm);
            });

        return mapToResponse(dailyTerm);
    }

    public List<TermResponse> searchTerms(String keyword, User user) {
        List<Term> terms = termRepository.searchByName(keyword).stream()
            .filter(t -> t.isActive())
            .filter(t -> t.getPlanType().toString().equals(user.getPlan().toString()) || 
                    t.getPlanType().toString().equals("FREE"))
            .collect(Collectors.toList());

        return terms.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public TermResponse getTermById(Long id, User user) {
        Term term = termRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Termo não encontrado"));

        if (!term.isActive()) {
            throw new RuntimeException("Termo não disponível");
        }

        if (!term.getPlanType().toString().equals(user.getPlan().toString()) && 
            !term.getPlanType().toString().equals("FREE")) {
            throw new RuntimeException("Acesso negado. Upgrade para Premium");
        }

        return mapToResponse(term);
    }

    public void markAsStudied(Long termId, User user, String knowledgeLevel) {
        Term term = termRepository.findById(termId)
            .orElseThrow(() -> new RuntimeException("Termo não encontrado"));

        UserTerm userTerm = userTermRepository.findByUserAndTerm(user, term)
            .orElseGet(() -> UserTerm.builder()
                .user(user)
                .term(term)
                .build());

        userTerm.setStatus(UserTerm.Status.STUDIED);
        if (knowledgeLevel != null) {
            userTerm.setKnowledgeLevel(UserTerm.KnowledgeLevel.valueOf(knowledgeLevel));
        }
        userTerm.setLastReviewAt(LocalDateTime.now());
        userTerm.setNextReviewAt(LocalDateTime.now().plusDays(1));

        userTermRepository.save(userTerm);
    }

    public void toggleFavorite(Long termId, User user) {
        Term term = termRepository.findById(termId)
            .orElseThrow(() -> new RuntimeException("Termo não encontrado"));

        UserTerm userTerm = userTermRepository.findByUserAndTerm(user, term)
            .orElseGet(() -> UserTerm.builder()
                .user(user)
                .term(term)
                .build());

        userTerm.setFavorite(!userTerm.isFavorite());
        userTermRepository.save(userTerm);
    }

    private TermResponse mapToResponse(Term term) {
        return TermResponse.builder()
            .id(term.getId())
            .name(term.getName())
            .definition(term.getDefinition())
            .simpleExplanation(term.getSimpleExplanation())
            .example(term.getExample())
            .difficultyLevel(term.getDifficultyLevel().toString())
            .categoryId(term.getCategory().getId())
            .categoryName(term.getCategory().getName())
            .planType(term.getPlanType().toString())
            .build();
    }

    @Transactional
    public TermResponse createTerm(TermRequest request, User user) {
        if (!user.getRole().toString().equals("ADMIN")) {
            throw new RuntimeException("Apenas administradores podem criar termos");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Term term = Term.builder()
            .name(request.getName())
            .definition(request.getDefinition())
            .simpleExplanation(request.getSimpleExplanation())
            .example(request.getExample())
            .difficultyLevel(request.getDifficultyLevel() != null ? 
                Term.DifficultyLevel.valueOf(request.getDifficultyLevel()) : Term.DifficultyLevel.INTERMEDIATE)
            .category(category)
            .planType(category.getPlanType())
            .active(true)
            .build();

        Term saved = termRepository.save(term);
        return mapToResponse(saved);
    }

    @Transactional
    public TermResponse updateTerm(Long id, TermRequest request, User user) {
        if (!user.getRole().toString().equals("ADMIN")) {
            throw new RuntimeException("Apenas administradores podem atualizar termos");
        }

        Term term = termRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Termo não encontrado"));

        if (request.getName() != null) {
            term.setName(request.getName());
        }
        if (request.getDefinition() != null) {
            term.setDefinition(request.getDefinition());
        }
        if (request.getSimpleExplanation() != null) {
            term.setSimpleExplanation(request.getSimpleExplanation());
        }
        if (request.getExample() != null) {
            term.setExample(request.getExample());
        }
        if (request.getDifficultyLevel() != null) {
            term.setDifficultyLevel(Term.DifficultyLevel.valueOf(request.getDifficultyLevel()));
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            term.setCategory(category);
        }

        Term saved = termRepository.save(term);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteTerm(Long id, User user) {
        if (!user.getRole().toString().equals("ADMIN")) {
            throw new RuntimeException("Apenas administradores podem deletar termos");
        }

        Term term = termRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Termo não encontrado"));

        term.setActive(false);
        termRepository.save(term);
    }
}
