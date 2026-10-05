package com.lexicareer.term.controller;

import com.lexicareer.entity.User;
import com.lexicareer.repository.UserRepository;
import com.lexicareer.term.dto.TermResponse;
import com.lexicareer.term.service.TermService;
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
}
