package com.oooldgreen.financemanager.service;

import com.oooldgreen.financemanager.dto.TotalBalanceDTO;
import com.oooldgreen.financemanager.entity.Account;
import com.oooldgreen.financemanager.entity.User;
import com.oooldgreen.financemanager.repository.AccountRepository;
import com.oooldgreen.financemanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public TotalBalanceDTO getTotalBalanceByUserId(Long userId) {
        BigDecimal assets = accountRepository.getTotalAssetsByUserId(userId);
        BigDecimal debts = accountRepository.getTotalDebtsByUserId(userId);

        assets = (assets != null) ? assets : BigDecimal.ZERO;
        debts = (debts != null) ? debts : BigDecimal.ZERO;

        return new TotalBalanceDTO(assets, debts, "EUR");
    }

    @Transactional
    public List<Account> getAllAccounts(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found."));
        return user.getAccounts();
    }
}
