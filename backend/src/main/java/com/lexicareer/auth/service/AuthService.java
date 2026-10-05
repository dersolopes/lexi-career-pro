package com.lexicareer.auth.service;

import com.lexicareer.auth.dto.AuthRequest;
import com.lexicareer.auth.dto.AuthResponse;
import com.lexicareer.entity.User;
import com.lexicareer.repository.UserRepository;
import com.lexicareer.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .name("Usuário")
            .level(User.Level.INTERMEDIATE)
            .plan(User.Plan.FREE)
            .active(true)
            .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
            .token(token)
            .name(user.getName())
            .email(user.getEmail())
            .build();
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
            .token(token)
            .name(user.getName())
            .email(user.getEmail())
            .build();
    }
}
