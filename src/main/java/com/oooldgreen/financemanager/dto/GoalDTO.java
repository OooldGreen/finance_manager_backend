package com.oooldgreen.financemanager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@JsonInclude
@NoArgsConstructor
@AllArgsConstructor
public class GoalDTO {
    private Long id;
    private String title;
    private String description;
    private String currency;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private String category;
    private Boolean isPriority;
    private Boolean isActive;
    private LocalDate createdAt;

    private String status;
    private Double progress;
    private Boolean isReached;
}
