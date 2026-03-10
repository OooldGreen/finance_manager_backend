package com.oooldgreen.financemanager.dto;

import java.math.BigDecimal;

public interface BudgetUsageProjection {
    String getCategoryName();
    BigDecimal getSpentAmount();
    BigDecimal getPendingAmount();
}
