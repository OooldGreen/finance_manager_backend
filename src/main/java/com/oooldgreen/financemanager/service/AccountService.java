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
import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final UserService userService;

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

    @Transactional
    public Account createAccount(Account accountData) throws IllegalAccessException {
        if (accountData.getName() == null || accountData.getName().isEmpty()) {
            throw new IllegalAccessException("Account name can not be empty.");
        }

        User user = userService.getCurrentAuthUser();
        accountData.setUser(user);

        if (accountData.getBalance() == null) {
            accountData.setBalance(BigDecimal.ZERO);
        }

        accountData.setIsActive(true);

        return accountRepository.save(accountData);
    }

    @Transactional
    public Account getAccountDetail(Long accountId) throws AccessDeniedException {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account does not exist."));
        User user = userService.getCurrentAuthUser();
        if (!account.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied.");
        }
        return account;
    }

    @Transactional
    public Account updateAccountDetail(Long accountId, Account newAccountDetail) throws AccessDeniedException {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account does not exist."));
        User user = userService.getCurrentAuthUser();
        if (!account.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied.");
        }

        if (newAccountDetail.getName() != null && !newAccountDetail.getName().trim().isEmpty()) {
            account.setName(newAccountDetail.getName());
        }

        if (newAccountDetail.getType() != null) {
            account.setType(newAccountDetail.getType());
        }

        if (newAccountDetail.getRemark() != null && !newAccountDetail.getRemark().trim().isEmpty()) {
            account.setRemark(newAccountDetail.getRemark());
        }

        if (newAccountDetail.getBalance() != null) {
            account.setBalance(newAccountDetail.getBalance());
        }

        return accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(Long accountId) throws AccessDeniedException {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account does not exist."));
        User user = userService.getCurrentAuthUser();
        if (!account.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied.");
        }
        accountRepository.deleteById(accountId);
    }
}
