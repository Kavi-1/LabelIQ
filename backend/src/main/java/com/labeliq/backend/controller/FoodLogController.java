package com.labeliq.backend.controller;

import com.labeliq.backend.model.FoodLog;
import com.labeliq.backend.service.FoodLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/foodlogs")
@RequiredArgsConstructor
public class FoodLogController {

    private final FoodLogService foodLogService;

    @PostMapping
    public ResponseEntity<FoodLog> createFoodLog(@RequestBody FoodLog foodLog) {
        FoodLog created = foodLogService.createFoodLog(foodLog);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<FoodLog>> getAllFoodLogs() {
        List<FoodLog> logs = foodLogService.getAllFoodLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/today")
    public ResponseEntity<List<FoodLog>> getTodaysFoodLogs() {
        List<FoodLog> logs = foodLogService.getTodaysFoodLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodLog> getFoodLogById(@PathVariable Long id) {
        FoodLog log = foodLogService.getFoodLogById(id);
        return ResponseEntity.ok(log);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodLog> updateFoodLog(
            @PathVariable Long id,
            @RequestBody FoodLog foodLog) {
        FoodLog updated = foodLogService.updateFoodLog(id, foodLog);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodLog(@PathVariable Long id) {
        foodLogService.deleteFoodLog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/range")
    public ResponseEntity<List<FoodLog>> getFoodLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<FoodLog> logs = foodLogService.getFoodLogsByDateRange(startDate, endDate);
        return ResponseEntity.ok(logs);
    }
}
