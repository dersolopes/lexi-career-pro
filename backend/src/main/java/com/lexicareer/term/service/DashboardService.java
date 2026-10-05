package com.lexicareer.term.service;

import com.lexicareer.entity.User;
import com.lexicareer.entity.UserTerm;
import com.lexicareer.repository.UserTermRepository;
import com.lexicareer.term.dto.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserTermRepository userTermRepository;

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

        return DashboardResponse.builder()
            .termsLearned(termsLearned)
            .termsToReview(toReviewCount)
            .favoriteCount(favoriteCount)
            .averageScore(Math.round(averageScore * 10.0) / 10.0)
            .currentLevel(user.getLevel().toString())
            .build();
    }
}
