package com.lexicareer.quiz.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerRequest {
    
    @NotNull(message = "QuestionId é obrigatório")
    private Long questionId;
    
    @NotNull(message = "SelectedOptionId é obrigatório")
    private Long selectedOptionId;
}
