package com.oooldgreen.financemanager.controller;

import com.oooldgreen.financemanager.entity.User;
import com.oooldgreen.financemanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserControllerTest {
    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TotalBalanceRepository totalBalanceRepository;

    @Transactional
    @Test
    void createUser() {
        User user = new User();
        user.setUsername("test_create");
        user.setPassword("test_create");

        userController.createUser(user);

        User savedUser = userRepository.findByUsername("test_create").orElseThrow(() -> new RuntimeException("User not found"));
        assertThat(savedUser.getId()).isNotNull();

        List<TotalBalance> balances = totalBalanceRepository.findAll();
        Long savedId = savedUser.getId();
        boolean exist = balances.stream().anyMatch(b -> b.getUser().getId().equals(savedId));
        assertThat(exist).isEqualTo(true);
    }
}
