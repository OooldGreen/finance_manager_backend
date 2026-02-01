package com.oooldgreen.financemanager.controller;

import com.oooldgreen.financemanager.dto.PasswordUpdateDTO;
import com.oooldgreen.financemanager.entity.User;
import com.oooldgreen.financemanager.repository.UserRepository;
import com.oooldgreen.financemanager.service.JwtService;
import com.oooldgreen.financemanager.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/me")
    public ResponseEntity<User> updateUserById(@RequestBody User user) {
        User currentUser = userService.getCurrentAuthUser();
        User updatedUser = userService.updateUser(currentUser.getId(), user);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/me/password")
    public  ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        User currentUser = userService.getCurrentAuthUser();
        userService.updatePassword(currentUser, passwordUpdateDTO);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody User loginUser) {
        Optional<User> user = userRepository.findByUsername(loginUser.getUsername());
        boolean isMatch = false;

        if (user.isPresent()) {
            isMatch = passwordEncoder.matches(loginUser.getPassword(), user.get().getPassword());
        }

        if (isMatch) {
            String token = jwtService.generateToken(user.get().getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.get().getId());
            response.put("username", user.get().getUsername());
            response.put("token", token);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }
}
