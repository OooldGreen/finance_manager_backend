package com.oooldgreen.financemanager.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class StatisticsAllDTO {
    private String name;
    private BigDecimal value1;
    private BigDecimal value2;

    public StatisticsAllDTO(Object category, Object value1, Object value2) {
        this.name = (category != null) ? category.toString() : "null";
        this.value1 = convertToBigDecimal(value1);
        this.value2 = convertToBigDecimal(value2);
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
}