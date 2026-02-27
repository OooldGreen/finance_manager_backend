package com.oooldgreen.financemanager.repository;

import com.oooldgreen.financemanager.dto.StatisticsDTO;
import com.oooldgreen.financemanager.entity.Transaction;
import com.oooldgreen.financemanager.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT new com.oooldgreen.financemanager.dto.StatisticsDTO(STR(t.transactionCategory), SUM(t.amount)) from Transaction t WHERE t.user.id = :userId GROUP BY t.transactionCategory")
    List<StatisticsDTO> getDataByUserId(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT new com.oooldgreen.financemanager.dto.StatisticsDTO(STR(t.transactionCategory), SUM(t.amount)) " +
            "FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.transactionType = :type " +
            "AND (CAST(:startDate AS LocalDate) IS NULL OR CAST( t.ticketCompletionDate AS LocalDate) >= :startDate) " +
            "AND (CAST(:endDate AS LocalDate) IS NULL OR CAST( t.ticketCompletionDate AS LocalDate) <= :endDate)  " +
            "GROUP BY t.transactionCategory")
    List<StatisticsDTO> getDataByCat(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
