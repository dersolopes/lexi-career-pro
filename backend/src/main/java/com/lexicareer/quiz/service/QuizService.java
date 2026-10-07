package com.lexicareer.quiz.service;

import com.lexicareer.entity.*;
import com.lexicareer.repository.*;
import com.lexicareer.quiz.dto.*;
import com.lexicareer.term.dto.TermResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizOptionRepository quizOptionRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final TermRepository termRepository;
    private final UserTermRepository userTermRepository;
    private final UserRepository userRepository;

    public QuizQuestionDTO getQuizForTerm(Long termId) {
        QuizQuestion question = quizQuestionRepository.findRandomByTermId(termId)
            .orElseThrow(() -> new RuntimeException("Nenhuma pergunta encontrada para este termo"));

        List<QuizOption> options = quizOptionRepository.findByQuestionIdOrderByOrderIndex(question.getId());

        List<QuizOptionDTO> optionDTOs = options.stream()
            .map(opt -> QuizOptionDTO.builder()
                .id(opt.getId())
                .optionText(opt.getOptionText())
                .orderIndex(opt.getOrderIndex())
                .build())
            .collect(Collectors.toList());

        return QuizQuestionDTO.builder()
            .id(question.getId())
            .questionText(question.getQuestionText())
            .explanation(question.getExplanation())
            .difficultyLevel(question.getDifficultyLevel().toString())
            .termId(question.getTerm().getId())
            .options(optionDTOs)
            .build();
    }

    @Transactional
    public QuizAnswerResponse submitAnswer(Long userId, Long questionId, Long selectedOptionId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        QuizQuestion question = quizQuestionRepository.findById(questionId)
            .orElseThrow(() -> new RuntimeException("Pergunta não encontrada"));

        QuizOption selectedOption = quizOptionRepository.findById(selectedOptionId)
            .orElseThrow(() -> new RuntimeException("Opção não encontrada"));

        boolean isCorrect = selectedOption.isCorrect();

        QuizAnswer answer = QuizAnswer.builder()
            .user(user)
            .question(question)
            .selectedOption(selectedOption)
            .correct(isCorrect)
            .answeredAt(LocalDateTime.now())
            .build();

        quizAnswerRepository.save(answer);

        QuizOption correctOption = quizOptionRepository.findByQuestionIdAndCorrect(questionId, true)
            .orElse(null);

        return QuizAnswerResponse.builder()
            .isCorrect(isCorrect)
            .correctOptionId(correctOption != null ? correctOption.getId() : null)
            .explanation(question.getExplanation())
            .build();
    }

    public QuizStatsDTO getQuizStats(Long userId) {
        long totalAnswered = quizAnswerRepository.countByUserId(userId);
        long correctCount = quizAnswerRepository.countByUserIdAndCorrect(userId, true);
        long incorrectCount = quizAnswerRepository.countByUserIdAndCorrect(userId, false);

        double averageScore = totalAnswered > 0 ? (double) correctCount / totalAnswered * 100 : 0.0;

        return QuizStatsDTO.builder()
            .totalAnswered(totalAnswered)
            .correctCount(correctCount)
            .incorrectCount(incorrectCount)
            .averageScore(averageScore)
            .build();
    }

    public List<TermResponse> getWrongAnswersForReview(Long userId) {
        List<QuizAnswer> wrongAnswers = quizAnswerRepository.findByUserId(userId).stream()
            .filter(qa -> !qa.isCorrect())
            .collect(Collectors.toList());

        return wrongAnswers.stream()
            .map(qa -> qa.getQuestion().getTerm())
            .distinct()
            .map(term -> TermResponse.builder()
                .id(term.getId())
                .name(term.getName())
                .definition(term.getDefinition())
                .simpleExplanation(term.getSimpleExplanation())
                .example(term.getExample())
                .difficultyLevel(term.getDifficultyLevel().toString())
                .categoryId(term.getCategory().getId())
                .categoryName(term.getCategory().getName())
                .planType(term.getPlanType().toString())
                .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public QuizQuestion generateQuizForTerm(Long termId, String questionText, String explanation, 
                                           String difficultyLevel, List<Map<String, Object>> optionsData) {
        Term term = termRepository.findById(termId)
            .orElseThrow(() -> new RuntimeException("Termo não encontrado"));

        QuizQuestion question = QuizQuestion.builder()
            .term(term)
            .questionText(questionText)
            .explanation(explanation)
            .difficultyLevel(QuizQuestion.DifficultyLevel.valueOf(difficultyLevel))
            .build();

        QuizQuestion savedQuestion = quizQuestionRepository.save(question);

        for (int i = 0; i < optionsData.size(); i++) {
            Map<String, Object> optionData = optionsData.get(i);
            QuizOption option = QuizOption.builder()
                .question(savedQuestion)
                .optionText((String) optionData.get("optionText"))
                .correct((Boolean) optionData.get("isCorrect"))
                .orderIndex(i)
                .build();
            quizOptionRepository.save(option);
        }

        return savedQuestion;
    }
}
