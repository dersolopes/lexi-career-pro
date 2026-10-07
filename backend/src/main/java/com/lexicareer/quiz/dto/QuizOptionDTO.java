package com.lexicareer.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizOptionDTO {
    
    private Long id;
    private String optionText;
    private int orderIndex;
}
