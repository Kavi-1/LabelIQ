package com.labeliq.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "food_logs")
public class FoodLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "fdc_id")
    private String fdcId;

    @Column(nullable = false)
    private String description;

    @Column(name = "brand_name")
    private String brandName;

    private String category;

    @Column(name = "serving_size")
    private Double servingSize;

    @Column(name = "serving_unit")
    private String servingUnit;

    @Column(nullable = false)
    private String amount;

    @Column(length = 1000)
    private String notes;

    private Double calories;

    private Double protein;

    private Double fat;

    private Double carbs;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
