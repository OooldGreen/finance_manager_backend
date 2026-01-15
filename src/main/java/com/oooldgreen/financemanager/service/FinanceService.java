package com.oooldgreen.financemanager.service;

import com.oooldgreen.financemanager.entity.BalanceMode;
import com.oooldgreen.financemanager.entity.Ticket;
import com.oooldgreen.financemanager.entity.TicketType;
import com.oooldgreen.financemanager.entity.TotalBalance;
import com.oooldgreen.financemanager.repository.BalanceModeRepository;
import com.oooldgreen.financemanager.repository.TicketRepository;
import com.oooldgreen.financemanager.repository.TotalBalanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FinanceService {
    private final TicketRepository ticketRepository;
    private final BalanceModeRepository balanceModeRepository;
    private final TotalBalanceRepository totalBalanceRepository;

    @Transactional
    public Ticket addTicket(Ticket ticket) {
        Ticket savedTicket = ticketRepository.save(ticket);

        BalanceMode mode = balanceModeRepository.findById(ticket.getBalanceMode().getId()).orElseThrow(() -> new RuntimeException("Can't find account"));
        TotalBalance total = totalBalanceRepository.findByUserId(ticket.getUser().getId()).orElseThrow(() ->  new RuntimeException("Can't find total account"));

        BigDecimal amount = ticket.getAmount();

        // update balance of account
        if (ticket.getTicketType().equals(TicketType.EXPENSE)) {
            mode.setBalance(mode.getBalance().subtract(amount));
            total.setTotalAmount(total.getTotalAmount().subtract(amount));
        } else if (ticket.getTicketType().equals((TicketType.INCOME))) {
            mode.setBalance(mode.getBalance().add(amount));
            total.setTotalAmount(total.getTotalAmount().add(amount));
        }
        balanceModeRepository.save(mode);
        totalBalanceRepository.save(total);

        return savedTicket;
    }

    @Transactional
    public BalanceMode createBalanceMode(BalanceMode mode) {
        if (mode.getBalance() == null) {
            mode.setBalance(BigDecimal.ZERO);
        }
        BalanceMode savedMode = balanceModeRepository.save(mode);
        TotalBalance updateBalance = savedMode.getTotalBalance();

        if (updateBalance == null ) {
            // find total balance by user id
            updateBalance = totalBalanceRepository.findByUserId(savedMode.getUser().getId()).orElseThrow(() -> new RuntimeException("Can't find user"));
        }

        updateBalance.setTotalAmount(updateBalance.getTotalAmount().add(savedMode.getBalance()));
        totalBalanceRepository.save(updateBalance);

        return savedMode;
    }
}
