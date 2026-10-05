package com.lexicareer.repository;

import com.lexicareer.entity.DailyTermLog;
import com.lexicareer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;

public interface DailyTermLogRepository extends JpaRepository<DailyTermLog, Long> {
    @Query("SELECT dtl FROM DailyTermLog dtl WHERE dtl.user = :user AND CAST(dtl.shownAt AS DATE) = :date")
    Optional<DailyTermLog> findTodayLog(@Param("user") User user, @Param("date") LocalDate date);
}
