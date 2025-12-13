package com.labeliq.backend.service;

import com.labeliq.backend.model.FoodLog;
import com.labeliq.backend.model.User;
import com.labeliq.backend.repository.FoodLogRepository;
import com.labeliq.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodLogService {

    private final FoodLogRepository foodLogRepository;
    private final UserRepository userRepository;

    // get current authenticated user
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // create food log for current user
    @Transactional
    public FoodLog createFoodLog(FoodLog foodLog) {
        User currentUser = getCurrentUser();
        foodLog.setUser(currentUser);
        return foodLogRepository.save(foodLog);
    }

    // all food logs for current user
    public List<FoodLog> getAllFoodLogs() {
        User currentUser = getCurrentUser();
        return foodLogRepository.findByUserOrderByCreatedAtDesc(currentUser);
    }

    // today food log
    public List<FoodLog> getTodaysFoodLogs() {
        User currentUser = getCurrentUser();
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN);
        return foodLogRepository.findByUserAndDateRange(currentUser, startOfDay, endOfDay);
    }

    // food log by id
    public FoodLog getFoodLogById(Long id) {
        User currentUser = getCurrentUser();
        return foodLogRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Food log not found or access denied"));
    }

    // update food log for current user
    @Transactional
    public FoodLog updateFoodLog(Long id, FoodLog updatedFoodLog) {
        User currentUser = getCurrentUser();
        FoodLog existingLog = foodLogRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Food log not found or access denied"));

        // Update fields (don't update user or id)
        existingLog.setFdcId(updatedFoodLog.getFdcId());
        existingLog.setDescription(updatedFoodLog.getDescription());
        existingLog.setBrandName(updatedFoodLog.getBrandName());
        existingLog.setCategory(updatedFoodLog.getCategory());
        existingLog.setServingSize(updatedFoodLog.getServingSize());
        existingLog.setServingUnit(updatedFoodLog.getServingUnit());
        existingLog.setAmount(updatedFoodLog.getAmount());
        existingLog.setNotes(updatedFoodLog.getNotes());
        existingLog.setCalories(updatedFoodLog.getCalories());
        existingLog.setProtein(updatedFoodLog.getProtein());
        existingLog.setFat(updatedFoodLog.getFat());
        existingLog.setCarbs(updatedFoodLog.getCarbs());

        return foodLogRepository.save(existingLog);
    }

    // delete food log
    @Transactional
    public void deleteFoodLog(Long id) {
        User currentUser = getCurrentUser();
        FoodLog foodLog = foodLogRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Food log not found or access denied"));
        foodLogRepository.delete(foodLog);
    }

    // food logs by date range
    public List<FoodLog> getFoodLogsByDateRange(LocalDate startDate, LocalDate endDate) {
        User currentUser = getCurrentUser();
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate.plusDays(1), LocalTime.MIN);
        return foodLogRepository.findByUserAndDateRange(currentUser, startDateTime, endDateTime);
    }
}
