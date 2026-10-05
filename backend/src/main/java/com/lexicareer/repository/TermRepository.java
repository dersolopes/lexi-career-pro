package com.lexicareer.repository;

import com.lexicareer.entity.Term;
import com.lexicareer.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {
    List<Term> findByCategory(Category category);
    Optional<Term> findByNameIgnoreCase(String name);
    
    @Query("SELECT t FROM Term t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND t.active = true")
    List<Term> searchByName(@Param("keyword") String keyword);
}
