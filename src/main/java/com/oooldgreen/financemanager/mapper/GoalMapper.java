package com.oooldgreen.financemanager.mapper;

import com.oooldgreen.financemanager.dto.GoalDTO;
import com.oooldgreen.financemanager.entity.Goal;
import com.oooldgreen.financemanager.entity.GoalCategory;
import com.oooldgreen.financemanager.entity.User;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface GoalMapper {
    public default GoalDTO toDTO (Goal goal) {
        if(goal == null) return null;

        GoalDTO dto = new GoalDTO();
        dto.setId(goal.getId());
        dto.setTitle(goal.getTitle());
        dto.setDescription(goal.getDescription());
        dto.setCurrency(goal.getCurrency());
        dto.setTargetAmount(goal.getTargetAmount());
        dto.setCurrentAmount(goal.getCurrentAmount());
        dto.setCategory(String.valueOf(goal.getCategory()));
        dto.setIsPriority(goal.getIsPriority());
        dto.setIsActive(goal.getIsActive());

        if (goal.getCreatedAt() != null) {
            dto.setCreatedAt(goal.getCreatedAt().toLocalDate());
        }

        dto.setStatus(goal.getStatus().toString());
        dto.setProgress(getProgress(goal.getTargetAmount(), goal.getCurrentAmount()));
        dto.setIsReached(goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0);

        return dto;
    }

    private double getProgress (BigDecimal targetAmount, BigDecimal currentAmount) {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return currentAmount
                .divide(targetAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .doubleValue();
    }

    public default Goal toEntity(GoalDTO dto, User user, Boolean active, Boolean priority) {
        Goal goal = new Goal();
        goal.setTitle(dto.getTitle());
        goal.setDescription(dto.getDescription());
        goal.setCurrency(dto.getCurrency());
        goal.setTargetAmount((dto.getTargetAmount() != null) ? dto.getTargetAmount() : BigDecimal.ZERO);
        goal.setCurrentAmount((dto.getCurrentAmount() != null) ? dto.getCurrentAmount() : BigDecimal.ZERO);

        if(dto.getCategory() != null) {
            try {
                goal.setCategory(GoalCategory.valueOf(dto.getCategory().toUpperCase()));
            } catch (IllegalArgumentException e) {
                goal.setCategory(GoalCategory.OTHERS);
            }
        } else {
            goal.setCategory(GoalCategory.OTHERS);
        }

        goal.setCreatedAt(LocalDateTime.now());
        goal.setUser(user);
        goal.setIsActive(active != null ? active : true);
        goal.setIsPriority(priority != null ? priority : false);
        return goal;
    }
}
