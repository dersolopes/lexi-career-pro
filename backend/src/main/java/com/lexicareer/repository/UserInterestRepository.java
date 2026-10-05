package com.lexicareer.repository;

import com.lexicareer.entity.UserInterest;
import com.lexicareer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    List<UserInterest> findByUser(User user);
}
