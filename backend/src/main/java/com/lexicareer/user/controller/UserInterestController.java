package com.lexicareer.user.controller;

import com.lexicareer.entity.Term;
import com.lexicareer.entity.User;
import com.lexicareer.entity.UserInterest;
import com.lexicareer.repository.UserRepository;
import com.lexicareer.term.dto.TermResponse;
import com.lexicareer.user.dto.UserInterestRequest;
import com.lexicareer.user.dto.UserInterestResponse;
import com.lexicareer.user.dto.UserProfileUpdateRequest;
import com.lexicareer.user.service.UserInterestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/interests")
@RequiredArgsConstructor
public class UserInterestController {

    private final UserInterestService userInterestService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<UserInterestResponse>> getUserInterests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<UserInterest> interests = userInterestService.getUserInterests(user);
        List<UserInterestResponse> responses = interests.stream()
            .map(ui -> UserInterestResponse.builder()
                .categoryId(ui.getCategory().getId())
                .categoryName(ui.getCategory().getName())
                .categoryDescription(ui.getCategory().getDescription())
                .build())
            .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<UserInterestResponse> addInterest(@Valid @RequestBody UserInterestRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        UserInterest userInterest = userInterestService.addInterest(user.getId(), request.getCategoryId());

        UserInterestResponse response = UserInterestResponse.builder()
            .categoryId(userInterest.getCategory().getId())
            .categoryName(userInterest.getCategory().getName())
            .categoryDescription(userInterest.getCategory().getDescription())
            .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> removeInterest(@PathVariable Long categoryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        userInterestService.removeInterest(user.getId(), categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<TermResponse>> getRecommendations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Term> terms = userInterestService.getRecommendedTerms(user);
        List<TermResponse> responses = terms.stream()
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

        return ResponseEntity.ok(responses);
    }
}
