package com.lexicareer.repository;

import com.lexicareer.entity.QuizQuestion;
import com.lexicareer.entity.QuizQuestion.DifficultyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByTermId(Long termId);
    List<QuizQuestion> findByDifficultyLevel(DifficultyLevel level);
    
    @Query("SELECT q FROM QuizQuestion q WHERE q.term.id = :termId ORDER BY RANDOM()")
    Optional<QuizQuestion> findRandomByTermId(@Param("termId") Long termId);
}
