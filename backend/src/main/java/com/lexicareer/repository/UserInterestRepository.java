package com.lexicareer.repository;

import com.lexicareer.entity.UserInterest;
import com.lexicareer.entity.User;
import com.lexicareer.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    List<UserInterest> findByUser(User user);
    Optional<UserInterest> findByUserAndCategory(User user, Category category);
}
