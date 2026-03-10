package com.oooldgreen.financemanager.service;

import com.oooldgreen.financemanager.dto.BudgetCategoryDTO;
import com.oooldgreen.financemanager.dto.BudgetDTO;
import com.oooldgreen.financemanager.dto.BudgetUsageProjection;
import com.oooldgreen.financemanager.dto.StatisticsDTO;
import com.oooldgreen.financemanager.entity.*;
import com.oooldgreen.financemanager.mapper.BudgetMapper;
import com.oooldgreen.financemanager.repository.BudgetRepository;
import com.oooldgreen.financemanager.repository.StatisticsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final UserService userService;
    private final BudgetMapper budgetMapper;

    @Transactional
    public BudgetDTO getBudgetByMonth(LocalDate startDate) {
        User user = userService.getCurrentAuthUser();
        Long userId = user.getId();
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return budgetRepository.findBudgetByUserIdAndBudgetDateAndIsDeletedFalse(userId, startDate)
                .map(budget -> {
                    LocalDateTime startTime = startDate.atStartOfDay();
                    LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
                    List<BudgetUsageProjection> actualUsage = budgetRepository.findBudgetUsageByCategory(userId, TransactionStatus.COMPLETED, TransactionStatus.PENDING, TransactionType.EXPENSE, startTime, endTime);

                    return budgetMapper.toDTO(budget, actualUsage);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Budget not found"));
    }

    @Transactional
    public BudgetDTO syncFromLastMonth(LocalDate date) {
        User user = userService.getCurrentAuthUser();

        Budget templateBudget =  budgetRepository.findFirstByUserIdAndIsDeletedFalseOrderByBudgetDateDesc(user.getId())
                .orElseGet(() -> {
                    Budget budget = new Budget();
                    budget.setBudgetDate(date);
                    budget.setUser(user);
                    budget.setTotalBudget(BigDecimal.ZERO);
                    budget.setCategories(new ArrayList<>());
                    return budget;
                });

        Budget savedBudget = createBudget(user, date);

        savedBudget.setBudgetDate(date);
        savedBudget.setUser(user);
        savedBudget.setTotalBudget(templateBudget.getTotalBudget());
        savedBudget.setCurrency(templateBudget.getCurrency());

        List<BudgetCategory> clonedCat = (templateBudget.getCategories() != null)
                ? templateBudget.getCategories().stream()
                    .map(cat -> new BudgetCategory(cat.getName(), cat.getLimitAmount()))
                    .toList()
                : new ArrayList<>();
        savedBudget.getCategories().addAll(clonedCat);

        budgetRepository.save(savedBudget);
        return getBudgetByMonth(date);
    }

    @Transactional
    public BudgetDTO saveBudget(BudgetDTO budgetDetail) {
        User user = userService.getCurrentAuthUser();
        LocalDate date = budgetDetail.getBudgetDate().withDayOfMonth(1);

        Budget budget = createBudget(user, date);
        budget.setTotalBudget(budgetDetail.getTotalBudget());
        budget.setCurrency(budgetDetail.getCurrency());
        budget.getCategories().clear();

        List<BudgetCategory> newCategories = budgetDetail.getCategories().stream()
                .map(category -> {
                    TransactionCategory catEnum = TransactionCategory.valueOf(category.getCategoryName());
                    if (!catEnum.isExpense()) {
                        throw new RuntimeException("Category not found.");
                    }
                    return new BudgetCategory(catEnum, category.getLimitAmount());
                }).toList();
        budget.getCategories().addAll(newCategories);

        Budget savedBudget = budgetRepository.save(budget);
        return getBudgetByMonth(savedBudget.getBudgetDate());
    }

    private Budget createBudget(User user, LocalDate date) {
        // if budget for this month exists, set is_deleted as false
        // if not, create a new budget entity
        return budgetRepository.findBudgetByUserIdAndBudgetDate(user.getId(), date)
                .map(existing -> {
                    existing.setIsDeleted(false);
                    existing.setTotalBudget(BigDecimal.ZERO);
                    existing.getCategories().clear();
                    return existing;
                })
                .orElseGet(() -> {
                    Budget budget = new Budget();
                    budget.setBudgetDate(date);
                    budget.setUser(user);
                    budget.setTotalBudget(BigDecimal.ZERO);
                    budget.setCategories(new ArrayList<>());
                    return budget;
                });
    }

    @Transactional
    public ResponseEntity<?> deleteBudget(Integer year, Integer month) throws AccessDeniedException {
        User user = userService.getCurrentAuthUser();
        Budget budget = budgetRepository.findByYearAndMonthAndIsDeletedFalse(year, month);

        if (budget == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Budget does not exist.");
        }

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied.");
        }
        budget.setIsDeleted(true);
        budget.setTotalBudget(BigDecimal.ZERO);
        budget.setCurrency("EUR");
        budget.getCategories().clear();

        budgetRepository.save(budget);
        return ResponseEntity.ok("Deleted successfully.");
    }
}
