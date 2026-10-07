package com.lexicareer.repository;

import com.lexicareer.entity.QuizOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface QuizOptionRepository extends JpaRepository<QuizOption, Long> {
    List<QuizOption> findByQuestionId(Long questionId);
    Optional<QuizOption> findByQuestionIdAndCorrect(Long questionId, boolean correct);
    
    @Query("SELECT o FROM QuizOption o WHERE o.question.id = :questionId ORDER BY o.orderIndex")
    List<QuizOption> findByQuestionIdOrderByOrderIndex(@Param("questionId") Long questionId);
}
