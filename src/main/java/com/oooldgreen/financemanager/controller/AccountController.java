package com.oooldgreen.financemanager.controller;

import com.oooldgreen.financemanager.dto.TotalBalanceDTO;
import com.oooldgreen.financemanager.entity.Account;
import com.oooldgreen.financemanager.entity.AccountType;
import com.oooldgreen.financemanager.repository.AccountRepository;
import com.oooldgreen.financemanager.service.AccountService;
import com.oooldgreen.financemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;
    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        Long userId = userService.getCurrentAuthUser().getId();
        return ResponseEntity.ok(accountService.getAllAccounts(userId));
    }

    @GetMapping("/total_balance")
    public ResponseEntity<TotalBalanceDTO> getTotalBalance() {
        Long userId = userService.getCurrentAuthUser().getId();

        return ResponseEntity.ok(accountService.getTotalBalanceByUserId(userId));
    }

    @GetMapping("/types")
    public List<String> getAccountTypes() {
        return Arrays.stream(AccountType.values())
                .map(Enum::name)
                .toList();
    }

    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestBody Account accountData) throws IllegalAccessException {
        Account newAccount = accountService.createAccount(accountData);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAccount);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountDetail(@PathVariable Long id) throws AccessDeniedException {
       return ResponseEntity.ok(accountService.getAccountDetail(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Account> updateAccountDetail(@PathVariable Long id, @RequestBody Account account) throws AccessDeniedException {
        return ResponseEntity.ok(accountService.updateAccountDetail(id, account));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) throws AccessDeniedException {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
