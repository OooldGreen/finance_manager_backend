package com.oooldgreen.financemanager.repository;

import com.oooldgreen.financemanager.dto.HeatmapProjection;
import com.oooldgreen.financemanager.dto.StatisticsAllDTO;
import com.oooldgreen.financemanager.dto.StatisticsDTO;
import com.oooldgreen.financemanager.entity.Transaction;
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

@Repository
public interface StatisticsRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT new com.oooldgreen.financemanager.dto.StatisticsAllDTO(" +
            "FUNCTION('to_char', t.ticketCompletionDate, :dateFormat) AS dateLabel, " +
            "SUM(CASE WHEN t.transactionType = com.oooldgreen.financemanager.entity.TransactionType.INCOME THEN t.amount ELSE 0 END), " +
            "SUM(CASE WHEN t.transactionType = com.oooldgreen.financemanager.entity.TransactionType.EXPENSE THEN t.amount ELSE 0 END)) " +
            "FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.transactionStatus = :status " +
            "AND t.ticketCompletionDate >= :startDate " +
            "AND t.ticketCompletionDate <= :endDate " +
            "GROUP BY dateLabel " +
            "ORDER BY dateLabel ASC")
    List<StatisticsAllDTO> getData(
            @Param("userId") Long userId,
            @Param("dateFormat") String dateFormat,
            @Param("status") TransactionStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate")LocalDateTime endDate);

    @Query("SELECT new com.oooldgreen.financemanager.dto.StatisticsDTO(STR(t.transactionCategory), SUM(t.amount)) " +
            "FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.transactionType = :type " +
            "AND t.transactionStatus = :status " +
            "AND (CAST(:startDate AS LocalDate) IS NULL OR CAST( t.ticketCompletionDate AS LocalDate) >= :startDate) " +
            "AND (CAST(:endDate AS LocalDate) IS NULL OR CAST( t.ticketCompletionDate AS LocalDate) <= :endDate)  " +
            "GROUP BY t.transactionCategory")
    List<StatisticsDTO> getDataByCatAndType(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("SELECT " +
            "SUM(CASE WHEN t.transactionType = com.oooldgreen.financemanager.entity.TransactionType.INCOME THEN t.amount ELSE 0.0 END) as totalIncome, " +
            "SUM(CASE WHEN t.transactionType = com.oooldgreen.financemanager.entity.TransactionType.EXPENSE THEN t.amount ELSE 0.0 END) as totalExpense, " +
            "MAX(CASE WHEN t.transactionType = com.oooldgreen.financemanager.entity.TransactionType.EXPENSE THEN ABS(t.amount) END) as topExpense " +
            "FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.transactionStatus = :status " +
            "AND t.ticketCompletionDate >= :startDate " +
            "AND t.ticketCompletionDate <= :endDate")
    List<Object[]> getMonthlyStats(
            @Param("userId") Long userId,
            @Param("status") TransactionStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT CAST(t.ticketCompletionDate as DATE) as date, " +
            "COUNT(t.id) as count " +
            "FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.transactionStatus = :status " +
            "AND YEAR(t.ticketCompletionDate) = :year " +
            "GROUP BY date")
    List<HeatmapProjection> getHeatmapData(
            @Param("userId") Long userId,
            @Param("status") TransactionStatus status,
            @Param("year") int year);
}
