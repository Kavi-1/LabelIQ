package com.labeliq.backend.repository;

import com.labeliq.backend.model.FoodLog;
import com.labeliq.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodLogRepository extends JpaRepository<FoodLog, Long> {

    // all logs for user
    List<FoodLog> findByUserOrderByCreatedAtDesc(User user);

    // food log by id and user
    Optional<FoodLog> findByIdAndUser(Long id, User user);

    // query by date range
    @Query("SELECT f FROM FoodLog f WHERE f.user = :user AND f.createdAt >= :startDate AND f.createdAt < :endDate ORDER BY f.createdAt DESC")
    List<FoodLog> findByUserAndDateRange(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    void deleteByIdAndUser(Long id, User user);
}
