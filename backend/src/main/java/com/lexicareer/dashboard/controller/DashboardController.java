package com.lexicareer.dashboard.controller;

import com.lexicareer.entity.User;
import com.lexicareer.repository.UserRepository;
import com.lexicareer.term.dto.DashboardResponse;
import com.lexicareer.term.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        DashboardResponse dashboard = dashboardService.getDashboard(user);
        return ResponseEntity.ok(dashboard);
    }
}
