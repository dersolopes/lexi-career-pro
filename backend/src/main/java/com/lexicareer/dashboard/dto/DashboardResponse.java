package com.lexicareer.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

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
    private int streakDays;
    private Map<String, Long> knowledgeLevelDistribution;
}
