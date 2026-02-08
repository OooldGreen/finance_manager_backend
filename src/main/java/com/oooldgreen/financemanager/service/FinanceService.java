package com.oooldgreen.financemanager.service;

import com.oooldgreen.financemanager.entity.Account;
import com.oooldgreen.financemanager.entity.Transaction;
import com.oooldgreen.financemanager.entity.TransactionType;
import com.oooldgreen.financemanager.repository.AccountRepository;
import com.oooldgreen.financemanager.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FinanceService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Transaction addRecord(Transaction transaction) {
        Transaction savedTransaction = transactionRepository.save(transaction);

        Account account = accountRepository.findById(transaction.getAccount().getId()).orElseThrow(() -> new RuntimeException("Can't find account"));
        BigDecimal amount = transaction.getAmount();

        // update balance of account
        if (transaction.getTransactionType().equals(TransactionType.EXPENSE)) {
            account.setBalance(account.getBalance().subtract(amount));
        } else if (transaction.getTransactionType().equals((TransactionType.INCOME))) {
            account.setBalance(account.getBalance().add(amount));
        }
        accountRepository.save(account);

        return savedTransaction;
    }

    @Transactional
    public Account createAccount(Account account) {
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }

        if (account.getCurrency() == null) {
            account.setCurrency("EUR");
        }

        return accountRepository.save(account);
    }
}
