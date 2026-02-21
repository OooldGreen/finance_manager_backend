package com.oooldgreen.financemanager.service;

import com.oooldgreen.financemanager.entity.*;
import com.oooldgreen.financemanager.entity.Transaction;
import com.oooldgreen.financemanager.repository.AccountRepository;
import com.oooldgreen.financemanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TransactionServiceTest {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TotalBalanceRepository totalBalanceRepository;

    @Transactional
    @Test
    void updateBalancesWhenTicketAdded() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user = userRepository.save(user);

        TotalBalance total = new TotalBalance();
        total.setTotalAmount(new BigDecimal("1000.00"));
        total.setUser(user);
        total = totalBalanceRepository.save(total);

        Account mode = new Account();
        mode.setName("Cash");
        mode.setBalance(new BigDecimal("1000.00"));
        mode.setTotalBalance(total);
        mode = accountRepository.save(mode);

        Transaction transaction = new Transaction();
        transaction.setTitle("shopping");
        transaction.setAmount(new BigDecimal("220.00"));
        transaction.setTransactionType(TransactionType.EXPENSE);
        transaction.setUser(user);
        transaction.setAccount(mode);

        transactionService.addRecord(transaction);

        Account updatedMode = accountRepository.findById(mode.getId()).orElseThrow();
        TotalBalance updatedTotal = totalBalanceRepository.findById(total.getId()).orElseThrow();

        assertThat(updatedTotal.getTotalAmount()).isEqualTo("780.00");
        assertThat(updatedMode.getBalance()).isEqualTo("780.00");

    }
}
