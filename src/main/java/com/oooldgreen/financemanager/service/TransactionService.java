package com.oooldgreen.financemanager.service;

import com.oooldgreen.financemanager.dto.TransactionDTO;
import com.oooldgreen.financemanager.entity.*;
import com.oooldgreen.financemanager.mapper.TransactionMapper;
import com.oooldgreen.financemanager.repository.AccountRepository;
import com.oooldgreen.financemanager.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionDTO createTransaction(Transaction transaction) {
        User user = userService.getCurrentAuthUser();
        transaction.setUser(user);

        Account account = accountRepository.findById(transaction.getAccount().getId()).orElseThrow(() -> new RuntimeException("Can't find account"));
        BigDecimal amount = transaction.getAmount();
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDTO(savedTransaction);
    }

    @Transactional
    public Page<TransactionDTO> getAllTransactions(Integer year, Integer month, Pageable pageable) {
        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month !=null) ? month : now.getMonthValue();
        User user = userService.getCurrentAuthUser();
        Page<Transaction> transactions = transactionRepository.getMonthlyTransactions(targetYear, targetMonth, user,  pageable);

        return transactions.map(transactionMapper::toDTO);
    }

    @Transactional
    public Map<String, BigDecimal> getMonthTotalBalance(Integer year, Integer month) {
        User user = userService.getCurrentAuthUser();
        List<Transaction> transactions = transactionRepository.getMonthlyTransactions(year, month, user);
        BigDecimal expense = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal income = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("expense", expense);
        result.put("income", income);
        result.put("total", expense.add(income));
        return result;
    }

    @Transactional
    public TransactionDTO getTransaction(Long transactionId) throws AccessDeniedException {
        User user = userService.getCurrentAuthUser();
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied! You can not delete this record.");
        }
        return transactionMapper.toDTO(transaction);
    }

    @Transactional
    public List<TransactionDTO> getTransactionsByAccount(Long accountId) {
        User user = userService.getCurrentAuthUser();
        List<Transaction> transactions = transactionRepository.getTransactionsByAccount(user, accountId);
        return transactions.stream().map(transactionMapper::toDTO).toList();
    }

    @Transactional
    public TransactionDTO updateTransaction(Long transactionId, Transaction newT) throws AccessDeniedException {
        User user = userService.getCurrentAuthUser();
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied! You can not modify this record.");
        }
        transactionMapper.updateTransaction(newT, transaction);
        Transaction updatedT = transactionRepository.save(transaction);
        return transactionMapper.toDTO(updatedT);
    }

    @Transactional
    public void deleteTransaction(Long transactionId) throws AccessDeniedException {
        User user = userService.getCurrentAuthUser();
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied! You can not delete this record.");
        }

        Account account = accountRepository.findById(transaction.getAccount().getId()).orElseThrow(() -> new RuntimeException("Can't find account"));
        account.setBalance(account.getBalance().subtract(transaction.getAmount()));

        transactionRepository.deleteById(transactionId);
    }

    @Transactional
    public void deleteTransactions(List<Long> ids) {
        Long userId = userService.getCurrentAuthUser().getId();
        transactionRepository.deleteTransactions(ids, userId);
    }
}
