package com.lexicareer.repository;

import com.lexicareer.entity.QuizAnswer;
import com.lexicareer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    List<QuizAnswer> findByUserId(Long userId);
    List<QuizAnswer> findByUserIdAndQuestionId(Long userId, Long questionId);
    long countByUserId(Long userId);
    long countByUserIdAndCorrect(Long userId, boolean correct);
    
    @Query("SELECT COUNT(qa) FROM QuizAnswer qa WHERE qa.user.id = :userId AND qa.answeredAt >= :startDate")
    long countByUserIdAndAnsweredAtAfter(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
}
