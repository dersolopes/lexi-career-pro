package com.lexicareer.category.controller;

import com.lexicareer.entity.Category;
import com.lexicareer.entity.User;
import com.lexicareer.repository.CategoryRepository;
import com.lexicareer.repository.UserRepository;
import com.lexicareer.term.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<CategoryResponse> categories = categoryRepository.findAll().stream()
            .filter(c -> c.isActive())
            .filter(c -> c.getPlanType().toString().equals(user.getPlan().toString()) || 
                    c.getPlanType().toString().equals("FREE"))
            .map(this::mapToResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (!category.isActive()) {
            throw new RuntimeException("Categoria não disponível");
        }

        if (!category.getPlanType().toString().equals(user.getPlan().toString()) && 
            !category.getPlanType().toString().equals("FREE")) {
            throw new RuntimeException("Acesso negado. Upgrade para Premium");
        }

        return ResponseEntity.ok(mapToResponse(category));
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
            .id(category.getId())
            .name(category.getName())
            .description(category.getDescription())
            .slug(category.getSlug())
            .planType(category.getPlanType().toString())
            .displayOrder(category.getDisplayOrder())
            .build();
    }
}
