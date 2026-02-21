package com.oooldgreen.financemanager.controller;

import com.oooldgreen.financemanager.dto.TransactionDTO;
import com.oooldgreen.financemanager.entity.Transaction;
import com.oooldgreen.financemanager.entity.TransactionCategory;
import com.oooldgreen.financemanager.service.TransactionService;
import com.oooldgreen.financemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody Transaction transaction) {
        TransactionDTO savedTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.ok(savedTransaction);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionDTO>> getAllTransactionsById(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @PageableDefault(sort = "ticketCompletionDate", direction = Sort.Direction.DESC) Pageable pageable)
    {
        return ResponseEntity.ok(transactionService.getAllTransactions(year, month, pageable));
    }

    @GetMapping("/categories")
    public List<Map<String, String>> getAllCategories() {
        return Arrays.stream(TransactionCategory.values())
                .map(category -> Map.of(
                "name", category.name(),
                "group", category.getGroup()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/month-balance")
    public ResponseEntity<Map<String, BigDecimal>> getTotalMonthBalance() {
        Long userId = userService.getCurrentAuthUser().getId();
        return ResponseEntity.ok(transactionService.getMonthTotalBalance(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable Long id) throws AccessDeniedException {
        return ResponseEntity.ok(transactionService.getTransaction(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable Long id, @RequestBody Transaction t) throws AccessDeniedException {
        return ResponseEntity.ok(transactionService.updateTransaction(id, t));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecord(@PathVariable Long id) throws AccessDeniedException {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<?> deleteRecords(@RequestBody List<Long> ids) {
        transactionService.deleteTransactions(ids);
        return ResponseEntity.noContent().build();
    }
}
