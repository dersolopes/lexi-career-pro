package com.lexicareer.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionDTO {
    
    private Long id;
    private String questionText;
    private String explanation;
    private String difficultyLevel;
    private Long termId;
    private List<QuizOptionDTO> options;
}
