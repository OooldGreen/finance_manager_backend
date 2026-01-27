package com.oooldgreen.financemanager.service;

import com.oooldgreen.financemanager.entity.TotalBalance;
import com.oooldgreen.financemanager.entity.User;
import com.oooldgreen.financemanager.repository.TotalBalanceRepository;
import com.oooldgreen.financemanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TotalBalanceRepository totalBalanceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(User user) {
        // verify username
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists. Please choose another one.");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = userRepository.save(user);

        TotalBalance total = new TotalBalance();
        total.setUser(savedUser);
        total.setTotalAmount(BigDecimal.ZERO);
        totalBalanceRepository.save(total);

        return savedUser;
    }

    @Transactional
    public User updateUser(Long id, User user) {
        User currentUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User does not exist"));

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists. Please choose another one.");
        }

        if (user.getUsername() != null && user.getUsername().trim().isEmpty()) {
            currentUser.setUsername(user.getUsername());
        }

        if (user.getFirstName() != null && user.getFirstName().trim().isEmpty()) {
            currentUser.setFirstName(user.getFirstName());
        }

        if (user.getLastName() != null && user.getLastName().trim().isEmpty()) {
            currentUser.setLastName(user.getLastName());
        }

        if (user.getDateOfBirth() != null && user.getDateOfBirth().isBefore(LocalDate.now())) {
            currentUser.setDateOfBirth(user.getDateOfBirth());
        }


        return userRepository.save(currentUser);
    }
}
