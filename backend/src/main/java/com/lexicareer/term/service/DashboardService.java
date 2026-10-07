package com.lexicareer.term.service;

import com.lexicareer.entity.User;
import com.lexicareer.entity.UserTerm;
import com.lexicareer.repository.DailyTermLogRepository;
import com.lexicareer.repository.UserTermRepository;
import com.lexicareer.dashboard.dto.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserTermRepository userTermRepository;
    private final DailyTermLogRepository dailyTermLogRepository;

    public DashboardResponse getDashboard(User user) {
        // Termos aprendidos
        long termsLearned = userTermRepository.countByUserAndStatus(user, UserTerm.Status.STUDIED)
            + userTermRepository.countByUserAndStatus(user, UserTerm.Status.IN_REVIEW)
            + userTermRepository.countByUserAndStatus(user, UserTerm.Status.MASTERED);

        // Termos para revisar
        List<UserTerm> termsToReview = userTermRepository.findTermsToReview(user, LocalDateTime.now());
        long toReviewCount = termsToReview.size();

        // Favoritos
        List<UserTerm> favorites = userTermRepository.findByUserAndFavoriteTrue(user);
        long favoriteCount = favorites.size();

        // Média de acertos
        List<UserTerm> allUserTerms = userTermRepository.findByUser(user);
        double averageScore = allUserTerms.stream()
            .filter(ut -> ut.getTotalQuizAttempts() > 0)
            .mapToDouble(ut -> (double) ut.getCorrectAnswers() / ut.getTotalQuizAttempts() * 100)
            .average()
            .orElse(0.0);

        // Streak de dias consecutivos
        int streakDays = calculateStreakDays(user);

        // Distribuição por nível de conhecimento
        Map<String, Long> knowledgeLevelDistribution = allUserTerms.stream()
            .filter(ut -> ut.getKnowledgeLevel() != null)
            .collect(Collectors.groupingBy(
                ut -> ut.getKnowledgeLevel().toString(),
                Collectors.counting()
            ));

        return DashboardResponse.builder()
            .termsLearned(termsLearned)
            .termsToReview(toReviewCount)
            .favoriteCount(favoriteCount)
            .averageScore(Math.round(averageScore * 10.0) / 10.0)
            .currentLevel(user.getLevel().toString())
            .streakDays(streakDays)
            .knowledgeLevelDistribution(knowledgeLevelDistribution)
            .build();
    }

    private int calculateStreakDays(User user) {
        int streak = 0;
        LocalDate currentDate = LocalDate.now();

        while (true) {
            if (dailyTermLogRepository.findTodayLog(user, currentDate).isPresent()) {
                streak++;
                currentDate = currentDate.minusDays(1);
            } else if (streak == 0 && currentDate.equals(LocalDate.now())) {
                break;
            } else {
                break;
            }
        }

        return streak;
    }
}
