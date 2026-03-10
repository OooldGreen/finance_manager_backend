package com.oooldgreen.financemanager.mapper;

import com.oooldgreen.financemanager.dto.BudgetCategoryDTO;
import com.oooldgreen.financemanager.dto.BudgetDTO;
import com.oooldgreen.financemanager.dto.BudgetUsageProjection;
import com.oooldgreen.financemanager.entity.Budget;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BudgetMapper {
    public default BudgetDTO toDTO(Budget budget, List<BudgetUsageProjection> actualUsage) {
        if (budget == null) return null;

        Map<String, BudgetUsageProjection> usageMap = actualUsage.stream().collect(Collectors.toMap(BudgetUsageProjection::getCategoryName, p -> p));

        List<BudgetCategoryDTO> categoryDTOS = budget.getCategories().stream()
                .map(budgetCategory -> {
                    BudgetUsageProjection usage = usageMap.get(budgetCategory.getName().name());
                    BigDecimal expense = (usage != null) ? usage.getSpentAmount() : BigDecimal.ZERO;
                    BigDecimal pending = (usage != null) ? usage.getPendingAmount() : BigDecimal.ZERO;
                    BigDecimal limit = budgetCategory.getLimitAmount();

                    return BudgetCategoryDTO.builder()
                            .categoryName(budgetCategory.getName().name())
                            .limitAmount(limit)
                            .spentAmount(expense)
                            .pendingAmount(pending)
                            .build();
                }).toList();

        BigDecimal totalExpense = categoryDTOS.stream().map(BudgetCategoryDTO::getSpentAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPending = categoryDTOS.stream().map(BudgetCategoryDTO::getPendingAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalBudget = budget.getTotalBudget() != null ? budget.getTotalBudget() : BigDecimal.ZERO;

        return BudgetDTO.builder()
                .budgetId(budget.getId())
                .budgetDate(budget.getBudgetDate())
                .totalBudget(totalBudget)
                .currency(budget.getCurrency())
                .totalExpense(totalExpense)
                .totalPending(totalPending)
                .categories(categoryDTOS).build();
    }
}
