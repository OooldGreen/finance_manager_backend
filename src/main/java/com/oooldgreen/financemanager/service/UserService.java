package com.oooldgreen.financemanager.service;

import com.oooldgreen.financemanager.entity.TotalBalance;
import com.oooldgreen.financemanager.entity.User;
import com.oooldgreen.financemanager.repository.TotalBalanceRepository;
import com.oooldgreen.financemanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TotalBalanceRepository totalBalanceRepository;

    @Transactional
    public User createUser(User user) {
        User savedUser = userRepository.save(user);

        TotalBalance total = new TotalBalance();
        total.setUser(savedUser);
        total.setTotalAmount(BigDecimal.ZERO);
        totalBalanceRepository.save(total);

        return savedUser;
    }
}
