package com.oooldgreen.financemanager.dto;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsKpiDTO {
    private MetricDTO monthlyBalance;
    private MetricDTO monthlyRemaining;
    private MetricDTO topExpense;
    private MetricDTO savingRate;

    //  Object budget, 写了budget之后再补上
    public StatisticsKpiDTO(Object curIncome, Object curExpense, Object preIncome, Object preExpense,Object topExpense, Object preTopExpense) {
        BigDecimal income = convertToBigDecimal(curIncome);
        BigDecimal expense = convertToBigDecimal(curExpense);
        BigDecimal balance = income.add(expense);

        BigDecimal pIncome = convertToBigDecimal(preIncome);
        BigDecimal pExpense = convertToBigDecimal(preExpense);
        BigDecimal pBalance = pIncome.add(pExpense);

        BigDecimal cTopExpense = convertToBigDecimal(topExpense);
        BigDecimal pTopExpense = convertToBigDecimal(preTopExpense);

        this.monthlyBalance = new MetricDTO(balance, pBalance, "€");
        this.topExpense = new MetricDTO(cTopExpense, pTopExpense, "€");
        this.savingRate = new MetricDTO(
                calculSavingRate(balance, income),
                calculSavingRate(pBalance, pIncome),
                "%"
        );

        // remaining = budget - expense
//        BigDecimal budget = convertToBigDecimal(budget);
//        BigDecimal remaining = budget.subtract(expense);
//        Double remainingPercent = 0.0;
//        if (budget.compareTo(BigDecimal.ZERO) > 0) {
//            remainingPercent = remaining.divide(budget, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
//        }
//        this.monthlyRemaining = new MetricDTO(remaining, remainingPercent, "€");
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricDTO {
        private BigDecimal current;
        private BigDecimal previous;
        private Double mom;
        private String unit;

        public MetricDTO(BigDecimal current, BigDecimal previous, String unit) {
            this.current = current;
            this.previous = previous;
            this.unit = unit;

            if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
                this.mom = 0.0;
            } else {
                this.mom = current.subtract(previous)
                        .divide(previous, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
            }
        }

        public MetricDTO(BigDecimal value, Double percent, String unit) {
            this.current = value;
            this.previous = null;
            this.mom = percent;
            this.unit = unit;
        }
    }

    private BigDecimal convertToBigDecimal(Object val) {
        if (val instanceof BigDecimal) {
            return (BigDecimal) val;
        } else if (val instanceof Number) {
            return new BigDecimal(((Number) val).toString());
        } else {
            return BigDecimal.ZERO;
        }
    }

    @NonNull
    private BigDecimal calculSavingRate(BigDecimal balance, BigDecimal income) {
        if (income != null && income.compareTo(BigDecimal.ZERO) > 0) {
            return balance.divide(income, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}
