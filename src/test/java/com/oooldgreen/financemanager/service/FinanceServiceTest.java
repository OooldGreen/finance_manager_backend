package com.oooldgreen.financemanager.service;

import com.oooldgreen.financemanager.entity.*;
import com.oooldgreen.financemanager.repository.BalanceModeRepository;
import com.oooldgreen.financemanager.repository.TotalBalanceRepository;
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
class FinanceServiceTest {
    @Autowired
    private FinanceService financeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BalanceModeRepository balanceModeRepository;

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

        BalanceMode mode = new BalanceMode();
        mode.setName("Cash");
        mode.setBalance(new BigDecimal("1000.00"));
        mode.setTotalBalance(total);
        mode = balanceModeRepository.save(mode);

        Ticket ticket = new Ticket();
        ticket.setTitle("shopping");
        ticket.setAmount(new BigDecimal("220.00"));
        ticket.setTicketType(TicketType.EXPENSE);
        ticket.setUser(user);
        ticket.setBalanceMode(mode);

        financeService.addTicket(ticket);

        BalanceMode updatedMode = balanceModeRepository.findById(mode.getId()).orElseThrow();
        TotalBalance updatedTotal = totalBalanceRepository.findById(total.getId()).orElseThrow();

        assertThat(updatedTotal.getTotalAmount()).isEqualTo("780.00");
        assertThat(updatedMode.getBalance()).isEqualTo("780.00");

    }
}
