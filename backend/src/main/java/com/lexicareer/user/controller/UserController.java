package com.lexicareer.user.controller;

import com.lexicareer.entity.User;
import com.lexicareer.repository.UserRepository;
import com.lexicareer.user.dto.UserProfileUpdateRequest;
import com.lexicareer.user.service.UserInterestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserInterestService userInterestService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return ResponseEntity.ok(user);
    }

    @PutMapping("/profession")
    public ResponseEntity<Void> updateProfession(@Valid @RequestBody UserProfileUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        userInterestService.updateUserProfession(user.getId(), request.getProfession());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/level")
    public ResponseEntity<Void> updateLevel(@Valid @RequestBody UserProfileUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        userInterestService.updateUserLevel(user.getId(), request.getLevel());
        return ResponseEntity.ok().build();
    }
}
