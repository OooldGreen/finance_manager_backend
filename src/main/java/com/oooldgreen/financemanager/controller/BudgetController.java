package com.oooldgreen.financemanager.controller;

import com.oooldgreen.financemanager.dto.BudgetDTO;
import com.oooldgreen.financemanager.entity.Budget;
import com.oooldgreen.financemanager.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<BudgetDTO> getBudget(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        LocalDate budgetDate = LocalDate.of(targetYear, targetMonth, 1);
        return ResponseEntity.ok(budgetService.getBudgetByMonth(budgetDate));
    }

    @PostMapping
    public ResponseEntity<BudgetDTO> saveBudget(@RequestBody BudgetDTO budgetDetail) {
        return ResponseEntity.ok(budgetService.saveBudget(budgetDetail));
    }

    @PostMapping("/sync-budget")
    public ResponseEntity<BudgetDTO> syncBudget(@RequestParam Integer year, @RequestParam Integer month) {
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        LocalDate budgetDate = LocalDate.of(targetYear, targetMonth, 1);
        return ResponseEntity.ok(budgetService.syncFromLastMonth(budgetDate));
    }

    @DeleteMapping("/delete-budget")
    public ResponseEntity<?> deleteBudget(@RequestParam Integer year, @RequestParam Integer month) throws AccessDeniedException {
        return budgetService.deleteBudget(year, month);
    }
}
