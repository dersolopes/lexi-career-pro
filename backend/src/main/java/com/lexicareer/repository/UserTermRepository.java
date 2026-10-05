package com.lexicareer.repository;

import com.lexicareer.entity.UserTerm;
import com.lexicareer.entity.User;
import com.lexicareer.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserTermRepository extends JpaRepository<UserTerm, Long> {
    Optional<UserTerm> findByUserAndTerm(User user, Term term);
    List<UserTerm> findByUser(User user);
    List<UserTerm> findByUserAndStatusIn(User user, List<UserTerm.Status> statuses);
    
    @Query("SELECT ut FROM UserTerm ut WHERE ut.user = :user AND ut.nextReviewAt <= :now AND ut.status IN ('STUDIED', 'IN_REVIEW')")
    List<UserTerm> findTermsToReview(@Param("user") User user, @Param("now") LocalDateTime now);
    
    List<UserTerm> findByUserAndFavoriteTrue(User user);
    
    long countByUserAndStatus(User user, UserTerm.Status status);
}
