package com.lexicareer.term.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TermRequest {
    @NotBlank(message = "Nome do termo é obrigatório")
    private String name;
    
    @NotBlank(message = "Definição é obrigatória")
    private String definition;
    
    @NotBlank(message = "Explicação simples é obrigatória")
    private String simpleExplanation;
    
    @NotBlank(message = "Exemplo é obrigatório")
    private String example;
    
    private String difficultyLevel;
    private Long categoryId;
}
