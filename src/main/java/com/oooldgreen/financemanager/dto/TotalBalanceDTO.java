package com.oooldgreen.financemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TotalBalanceDTO {
    private BigDecimal totalAssets;
    private BigDecimal totalDebts;
    private String currency;
}
