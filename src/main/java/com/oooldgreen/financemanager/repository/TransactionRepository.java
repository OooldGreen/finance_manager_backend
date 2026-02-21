package com.oooldgreen.financemanager.repository;

import com.oooldgreen.financemanager.dto.TransactionDTO;
import com.oooldgreen.financemanager.entity.Transaction;
import com.oooldgreen.financemanager.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT new com.oooldgreen.financemanager.dto.TransactionDTO(t.id, t.title, t.amount, t.description, t.ticketCompletionDate, t.transactionType, t.transactionCategory, t.transactionStatus,  a.id, a.name, u.username) " +
            "FROM Transaction t JOIN t.account a JOIN t.user u WHERE u.id = :userId ")
    List<TransactionDTO> getAllTransactionsById(@Param("userId") Long userId);

    @Query("SELECT t from Transaction t WHERE YEAR(t.ticketCompletionDate) = :year AND MONTH(t.ticketCompletionDate) = :month AND t.user = :user")
    List<Transaction> getMonthlyTransactions(
            @Param("year") int year,
            @Param("month") int month,
            @Param("user") User user
    );

    @Query("SELECT t from Transaction t WHERE YEAR(t.ticketCompletionDate) = :year AND MONTH(t.ticketCompletionDate) = :month AND t.user = :user")
    Page<Transaction> getMonthlyTransactions(
            @Param("year") int year,
            @Param("month") int month,
            @Param("user") User user,
            Pageable pageable
    );

    @Modifying
    @Transactional
    @Query("DELETE FROM Transaction t WHERE t.id IN :ids AND t.user.id = :userId")
    void deleteTransactions(List<Long> ids, Long userId);
}
