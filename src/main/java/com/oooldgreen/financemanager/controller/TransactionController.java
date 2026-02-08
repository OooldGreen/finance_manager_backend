package com.oooldgreen.financemanager.controller;

import com.oooldgreen.financemanager.entity.Transaction;
import com.oooldgreen.financemanager.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final FinanceService financeService;

    @PostMapping
    public ResponseEntity<Transaction> createRecord(@RequestBody Transaction transaction) {
        Transaction savedTransaction = financeService.addRecord(transaction);
        return ResponseEntity.ok(savedTransaction);
    }
}
