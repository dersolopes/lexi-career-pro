package com.lexicareer.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
    @NotBlank(message = "Nome da categoria é obrigatório")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Slug é obrigatório")
    private String slug;
    
    private String planType;
    
    private Integer displayOrder;
    
    private Long parentId;
}
