package com.lexicareer.category.service;

import com.lexicareer.category.dto.CategoryRequest;
import com.lexicareer.category.dto.CategoryResponse;
import com.lexicareer.entity.Category;
import com.lexicareer.entity.User;
import com.lexicareer.repository.CategoryRepository;
import com.lexicareer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public List<CategoryResponse> getAllCategories(User user) {
        return categoryRepository.findAll().stream()
            .filter(c -> c.isActive())
            .filter(c -> c.getPlanType().toString().equals(user.getPlan().toString()) || 
                    c.getPlanType().toString().equals("FREE"))
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id, User user) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (!category.isActive()) {
            throw new RuntimeException("Categoria não disponível");
        }

        if (!category.getPlanType().toString().equals(user.getPlan().toString()) && 
            !category.getPlanType().toString().equals("FREE")) {
            throw new RuntimeException("Acesso negado. Upgrade para Premium");
        }

        return mapToResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request, User user) {
        if (!user.getRole().toString().equals("ADMIN")) {
            throw new RuntimeException("Apenas administradores podem criar categorias");
        }

        if (categoryRepository.findBySlug(request.getSlug()).isPresent()) {
            throw new RuntimeException("Slug já existe");
        }

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                .orElseThrow(() -> new RuntimeException("Categoria pai não encontrada"));
        }

        Category category = Category.builder()
            .name(request.getName())
            .description(request.getDescription())
            .slug(request.getSlug())
            .planType(request.getPlanType() != null ? 
                Category.PlanType.valueOf(request.getPlanType()) : Category.PlanType.FREE)
            .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
            .parent(parent)
            .active(true)
            .build();

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request, User user) {
        if (!user.getRole().toString().equals("ADMIN")) {
            throw new RuntimeException("Apenas administradores podem atualizar categorias");
        }

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (request.getSlug() != null && !request.getSlug().equals(category.getSlug())) {
            if (categoryRepository.findBySlug(request.getSlug()).isPresent()) {
                throw new RuntimeException("Slug já existe");
            }
            category.setSlug(request.getSlug());
        }

        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getPlanType() != null) {
            category.setPlanType(Category.PlanType.valueOf(request.getPlanType()));
        }
        if (request.getDisplayOrder() != null) {
            category.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                .orElseThrow(() -> new RuntimeException("Categoria pai não encontrada"));
            category.setParent(parent);
        }

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteCategory(Long id, User user) {
        if (!user.getRole().toString().equals("ADMIN")) {
            throw new RuntimeException("Apenas administradores podem deletar categorias");
        }

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        category.setActive(false);
        categoryRepository.save(category);
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
