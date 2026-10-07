package com.lexicareer.term.controller;

import com.lexicareer.entity.User;
import com.lexicareer.repository.UserRepository;
import com.lexicareer.term.dto.TermRequest;
import com.lexicareer.term.dto.TermResponse;
import com.lexicareer.term.service.TermService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
public class TermController {

    private final TermService termService;
    private final UserRepository userRepository;

    @GetMapping("/daily")
    public ResponseEntity<TermResponse> getDailyTerm() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        TermResponse term = termService.getDailyTerm(user);
        return ResponseEntity.ok(term);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TermResponse>> searchTerms(@RequestParam String keyword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<TermResponse> terms = termService.searchTerms(keyword, user);
        return ResponseEntity.ok(terms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TermResponse> getTermById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        TermResponse term = termService.getTermById(id, user);
        return ResponseEntity.ok(term);
    }

    @PostMapping("/{id}/studied")
    public ResponseEntity<Void> markAsStudied(@PathVariable Long id, @RequestParam(required = false) String knowledgeLevel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        termService.markAsStudied(id, user, knowledgeLevel);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> toggleFavorite(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        termService.toggleFavorite(id, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<TermResponse>> getFavoriteTerms() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<TermResponse> favorites = termService.getFavoriteTerms(user);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/{id}/favorite")
    public ResponseEntity<Boolean> isFavorite(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        boolean isFavorite = termService.isFavorite(id, user);
        return ResponseEntity.ok(isFavorite);
    }

    @GetMapping("/review")
    public ResponseEntity<List<TermResponse>> getTermsForReview() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<TermResponse> terms = termService.getTermsForReview(user);
        return ResponseEntity.ok(terms);
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<Void> markTermAsReviewed(@PathVariable Long id, @RequestParam boolean success) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        termService.markTermAsReviewed(id, user, success);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<TermResponse> createTerm(@Valid @RequestBody TermRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return ResponseEntity.ok(termService.createTerm(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TermResponse> updateTerm(@PathVariable Long id, @Valid @RequestBody TermRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return ResponseEntity.ok(termService.updateTerm(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTerm(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        termService.deleteTerm(id, user);
        return ResponseEntity.noContent().build();
    }
}
