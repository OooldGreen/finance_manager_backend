package com.oooldgreen.financemanager.controller;

import com.oooldgreen.financemanager.dto.TotalBalanceDTO;
import com.oooldgreen.financemanager.entity.Account;
import com.oooldgreen.financemanager.service.AccountService;
import com.oooldgreen.financemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
