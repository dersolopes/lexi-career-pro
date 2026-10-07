package com.lexicareer.quiz.controller;

import com.lexicareer.entity.User;
import com.lexicareer.repository.UserRepository;
import com.lexicareer.quiz.dto.QuizAnswerRequest;
import com.lexicareer.quiz.dto.QuizAnswerResponse;
import com.lexicareer.quiz.dto.QuizQuestionDTO;
import com.lexicareer.quiz.dto.QuizStatsDTO;
import com.lexicareer.quiz.service.QuizService;
import com.lexicareer.term.dto.TermResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final UserRepository userRepository;

    @GetMapping("/term/{termId}")
    public ResponseEntity<QuizQuestionDTO> getQuizForTerm(@PathVariable Long termId) {
        QuizQuestionDTO quiz = quizService.getQuizForTerm(termId);
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("/answer")
    public ResponseEntity<QuizAnswerResponse> submitAnswer(@Valid @RequestBody QuizAnswerRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        QuizAnswerResponse response = quizService.submitAnswer(user.getId(), request.getQuestionId(), request.getSelectedOptionId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<QuizStatsDTO> getQuizStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        QuizStatsDTO stats = quizService.getQuizStats(user.getId());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/review-needed")
    public ResponseEntity<List<TermResponse>> getWrongAnswersForReview() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<TermResponse> terms = quizService.getWrongAnswersForReview(user.getId());
        return ResponseEntity.ok(terms);
    }
}
