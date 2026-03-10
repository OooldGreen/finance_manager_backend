package com.oooldgreen.financemanager.repository;

import com.oooldgreen.financemanager.dto.BudgetUsageProjection;
import com.oooldgreen.financemanager.entity.Budget;
import com.oooldgreen.financemanager.entity.TransactionStatus;
import com.oooldgreen.financemanager.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findBudgetByUserIdAndBudgetDate(Long userId, LocalDate budgetDate);
    Optional<Budget> findBudgetByUserIdAndBudgetDateAndIsDeletedFalse(Long userId, LocalDate budgetDate);
    // find previous budget sorted by date desc
    Optional<Budget> findFirstByUserIdAndIsDeletedFalseOrderByBudgetDateDesc(Long userId);

    @Query("SELECT b FROM Budget b WHERE YEAR(b.budgetDate) = :year " +
            "AND MONTH(b.budgetDate) = :month " +
            "AND isDeleted = false")
    Budget findByYearAndMonthAndIsDeletedFalse(Integer year, Integer month);

    @Query("SELECT t.transactionCategory AS categoryName, " +
            "ABS(SUM(CASE WHEN t.transactionStatus = :expense THEN t.amount ELSE 0 END)) AS spentAmount, " +
            "ABS(SUM(CASE WHEN t.transactionStatus = :pending THEN t.amount ELSE 0 END)) AS pendingAmount " +
            "FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.transactionType = :type " +
            "AND t.ticketCompletionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY t.transactionCategory")
    List<BudgetUsageProjection> findBudgetUsageByCategory(
            @Param("userId") Long userId,
            @Param("expense") TransactionStatus expense,
            @Param("pending") TransactionStatus pending,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
