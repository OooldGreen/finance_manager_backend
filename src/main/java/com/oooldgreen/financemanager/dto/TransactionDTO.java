package com.oooldgreen.financemanager.dto;

import com.oooldgreen.financemanager.entity.TransactionCategory;
import com.oooldgreen.financemanager.entity.TransactionStatus;
import com.oooldgreen.financemanager.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private Long id;
    private String title;
    private BigDecimal amount;
    private String description;
    private LocalDateTime ticketCompletionDate;
    private TransactionType transactionType;
    private TransactionCategory transactionCategory;
    private TransactionStatus transactionStatus;
    private Long accountId;
    private String accountName;
    private String username;
}
