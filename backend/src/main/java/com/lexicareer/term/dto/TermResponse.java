package com.lexicareer.term.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TermResponse {
    private Long id;
    private String name;
    private String definition;
    private String simpleExplanation;
    private String example;
    private String difficultyLevel;
    private Long categoryId;
    private String categoryName;
    private String planType;
}
