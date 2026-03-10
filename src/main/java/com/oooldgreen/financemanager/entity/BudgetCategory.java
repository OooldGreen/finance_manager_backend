package com.oooldgreen.financemanager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetCategory {
    @Enumerated(EnumType.STRING)
    @Column(name = "category_name", nullable = false)
    private TransactionCategory name;

    @Column(name = "limit_amount")
    private BigDecimal limitAmount;
}
