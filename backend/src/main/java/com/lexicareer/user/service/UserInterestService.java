package com.lexicareer.user.service;

import com.lexicareer.entity.Category;
import com.lexicareer.entity.Term;
import com.lexicareer.entity.User;
import com.lexicareer.entity.UserInterest;
import com.lexicareer.repository.CategoryRepository;
import com.lexicareer.repository.TermRepository;
import com.lexicareer.repository.UserInterestRepository;
import com.lexicareer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserInterestService {

    private final UserInterestRepository userInterestRepository;
    private final CategoryRepository categoryRepository;
    private final TermRepository termRepository;
    private final UserRepository userRepository;

    public List<UserInterest> getUserInterests(User user) {
        return userInterestRepository.findByUser(user);
    }

    @Transactional
    public UserInterest addInterest(Long userId, Long categoryId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (userInterestRepository.findByUserAndCategory(user, category).isPresent()) {
            throw new RuntimeException("Interesse já existe");
        }

        UserInterest userInterest = UserInterest.builder()
            .user(user)
            .category(category)
            .build();

        return userInterestRepository.save(userInterest);
    }

    @Transactional
    public void removeInterest(Long userId, Long categoryId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        UserInterest userInterest = userInterestRepository.findByUserAndCategory(user, category)
            .orElseThrow(() -> new RuntimeException("Interesse não encontrado"));

        userInterestRepository.delete(userInterest);
    }

    public List<Term> getRecommendedTerms(User user) {
        List<UserInterest> interests = userInterestRepository.findByUser(user);
        
        if (interests.isEmpty()) {
            return List.of();
        }

        List<Long> categoryIds = interests.stream()
            .map(ui -> ui.getCategory().getId())
            .collect(Collectors.toList());

        return termRepository.findAll().stream()
            .filter(t -> t.isActive())
            .filter(t -> categoryIds.contains(t.getCategory().getId()))
            .filter(t -> t.getPlanType().toString().equals(user.getPlan().toString()) || 
                    t.getPlanType().toString().equals("FREE"))
            .limit(10)
            .collect(Collectors.toList());
    }

    @Transactional
    public void updateUserProfession(Long userId, String profession) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setProfession(profession);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserLevel(Long userId, String level) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setLevel(User.Level.valueOf(level));
        userRepository.save(user);
    }
}
