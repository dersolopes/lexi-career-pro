package com.lexicareer.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizStatsDTO {
    
    private long totalAnswered;
    private long correctCount;
    private long incorrectCount;
    private double averageScore;
}
