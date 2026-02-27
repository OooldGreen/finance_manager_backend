package com.oooldgreen.financemanager.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class StatisticsDTO {
    private String name;
    private BigDecimal value;

    public StatisticsDTO(Object category, Object sumValue) {
        this.name = (category != null) ? category.toString() : "null";

        if (sumValue instanceof BigDecimal) {
            this.value = (BigDecimal) sumValue;
        } else if (sumValue instanceof Number) {
            this.value = new BigDecimal(((Number) sumValue).toString());
        } else {
            this.value = BigDecimal.ZERO;
        }
    }
}
