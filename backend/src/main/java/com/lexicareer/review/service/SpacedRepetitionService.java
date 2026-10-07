package com.lexicareer.review.service;

import com.lexicareer.entity.UserTerm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SpacedRepetitionService {

    private static final double EASING_FACTOR = 2.5;
    private static final int INITIAL_INTERVAL_DAYS = 1;

    public LocalDateTime calculateNextReview(UserTerm userTerm, boolean success) {
        int currentReviewCount = userTerm.getReviewCount();
        LocalDateTime nextReview;

        if (success) {
            if (currentReviewCount == 0) {
                nextReview = LocalDateTime.now().plusDays(INITIAL_INTERVAL_DAYS);
            } else {
                int currentInterval = getDaysSinceLastReview(userTerm.getLastReviewAt());
                int newInterval = (int) (currentInterval * EASING_FACTOR);
                nextReview = LocalDateTime.now().plusDays(newInterval);
            }
        } else {
            nextReview = LocalDateTime.now().plusDays(INITIAL_INTERVAL_DAYS);
        }

        return nextReview;
    }

    public UserTerm.KnowledgeLevel updateKnowledgeLevel(UserTerm userTerm, boolean success) {
        int correctAnswers = userTerm.getCorrectAnswers();
        int totalAttempts = userTerm.getTotalQuizAttempts();

        if (totalAttempts == 0) {
            return userTerm.getKnowledgeLevel();
        }

        double successRate = (double) correctAnswers / totalAttempts;

        if (successRate >= 0.8 && correctAnswers >= 3) {
            return UserTerm.KnowledgeLevel.USE_IN_WORK;
        } else if (successRate >= 0.6 && correctAnswers >= 2) {
            return UserTerm.KnowledgeLevel.KNOWN;
        } else if (successRate >= 0.4) {
            return UserTerm.KnowledgeLevel.HEARD_NOT_EXPLAINED;
        } else {
            return UserTerm.KnowledgeLevel.NEVER_SEEN;
        }
    }

    public UserTerm.Status updateStatus(UserTerm userTerm, boolean success) {
        int reviewCount = userTerm.getReviewCount();

        if (success && reviewCount >= 5) {
            return UserTerm.Status.MASTERED;
        } else if (reviewCount >= 1) {
            return UserTerm.Status.IN_REVIEW;
        } else {
            return UserTerm.Status.STUDIED;
        }
    }

    private int getDaysSinceLastReview(LocalDateTime lastReviewAt) {
        if (lastReviewAt == null) {
            return INITIAL_INTERVAL_DAYS;
        }
        return (int) java.time.Duration.between(lastReviewAt, LocalDateTime.now()).toDays();
    }
}
