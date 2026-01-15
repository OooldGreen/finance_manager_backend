package com.oooldgreen.financemanager.controller;

import com.oooldgreen.financemanager.entity.BalanceMode;
import com.oooldgreen.financemanager.repository.BalanceModeRepository;
import com.oooldgreen.financemanager.repository.TotalBalanceRepository;
import com.oooldgreen.financemanager.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/balance-modes")
@RequiredArgsConstructor
public class BalanceModeController {
    private final FinanceService financeService;

    @PostMapping
    public ResponseEntity<BalanceMode> createBalanceMode(@RequestBody BalanceMode mode) {
        BalanceMode savedMode = financeService.createBalanceMode(mode);
        return ResponseEntity.status(201).body(savedMode);
    }
}
