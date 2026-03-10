package com.oooldgreen.financemanager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oooldgreen.financemanager.entity.BudgetCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.boot.internal.Abstract;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@JsonInclude
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDTO {
    private Long budgetId;
    private LocalDate budgetDate;
    private BigDecimal totalBudget;
    private String currency;

    private BigDecimal totalExpense;
    private BigDecimal totalPending;

    private List<BudgetCategoryDTO> categories;

    // remaining
    public BigDecimal getRemaining() {
        if (totalBudget == null) return BigDecimal.ZERO;
        BigDecimal expense = (totalExpense != null) ? totalExpense : BigDecimal.ZERO;
        BigDecimal pending = (totalPending != null) ? totalPending : BigDecimal.ZERO;
        return totalBudget.subtract(expense).subtract(pending);
    }
}

