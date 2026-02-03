package com.oooldgreen.financemanager.service;

import com.oooldgreen.financemanager.dto.PasswordUpdateDTO;
import com.oooldgreen.financemanager.entity.TotalBalance;
import com.oooldgreen.financemanager.entity.User;
import com.oooldgreen.financemanager.repository.TotalBalanceRepository;
import com.oooldgreen.financemanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final TotalBalanceRepository totalBalanceRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(User user) {
        // verify username
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists. Please choose another one.");
        }

        if (!validatePassword(user.getPassword())) {
            throw new RuntimeException("Password must be at least 8 characters, including uppercase, lowercase and a number");
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

        Optional<User> existingUser = userRepository.findByUsername((user.getUsername()));
        if (existingUser.isPresent() && !Objects.equals(existingUser.get().getId(), currentUser.getId())) {
            throw new RuntimeException("Username already exists. Please choose another one.");
        }

        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            currentUser.setUsername(user.getUsername());
        }

        if (user.getFirstName() != null && !user.getFirstName().trim().isEmpty()) {
            currentUser.setFirstName(user.getFirstName());
        }

        if (user.getLastName() != null && !user.getLastName().trim().isEmpty()) {
            currentUser.setLastName(user.getLastName());
        }

        if (user.getDateOfBirth() != null && user.getDateOfBirth().isBefore(LocalDate.now())) {
            currentUser.setDateOfBirth(user.getDateOfBirth());
        }

        return userRepository.save(currentUser);
    }

    @Transactional
    public void updatePassword(User user, PasswordUpdateDTO request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is wrong, please try again");
        }

        String newPassword = request.getNewPassword();
        if (!validatePassword(newPassword)) {
            throw new RuntimeException("Password must be at least 8 characters, including uppercase, lowercase and a number");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        totalBalanceRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    public User getCurrentAuthUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)\\S{8,20}$";
        return password.matches(passwordRegex);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from the database by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return buildUserDetails(user);
    }

    public UserDetails loadUserByUserId(Long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}
