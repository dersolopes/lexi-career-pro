package com.lexicareer.term.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private long termsLearned;
    private long termsToReview;
    private long favoriteCount;
    private double averageScore;
    private String currentLevel;
}
