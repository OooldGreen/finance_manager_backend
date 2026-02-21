package com.oooldgreen.financemanager.entity;

import lombok.Getter;

@Getter
public enum TransactionCategory {
    FOOD_DRINK("EXPENSE"),
    SHOPPING("EXPENSE"),
    TRANSPORT("EXPENSE"),
    DAILY("EXPENSE"),
    RENT("EXPENSE"),
    SOCIAL("EXPENSE"),
    SUBSCRIPTION("EXPENSE"),

    SALARY("INCOME"),
    INVESTMENT("INCOME"),
    SIDELINE("INCOME"),

    OTHERS("GENERAL");

    private final String group;
    TransactionCategory(String group) { this.group = group; }
}
