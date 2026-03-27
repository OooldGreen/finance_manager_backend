package com.oooldgreen.financemanager.controller;

import com.oooldgreen.financemanager.dto.PasswordUpdateDTO;
import com.oooldgreen.financemanager.entity.User;
import com.oooldgreen.financemanager.repository.UserRepository;
import com.oooldgreen.financemanager.service.JwtService;
import com.oooldgreen.financemanager.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.web.bind.annotation.*;

import java.net.PasswordAuthentication;
import java.util.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RememberMeServices rememberMeServices;

    @Transactional
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getUserById() {
        Long id = userService.getCurrentAuthUser().getId();
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

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteUser() {
        Long userId = userService.getCurrentAuthUser().getId();
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(
            @RequestBody User loginUser,
            @RequestParam(value = "remember", defaultValue = "false") boolean remember,
            HttpServletRequest request,
            HttpServletResponse response) {
        Optional<User> user = userRepository.findByUsername(loginUser.getUsername());
        boolean isMatch = false;

        if (user.isPresent()) {
            isMatch = passwordEncoder.matches(loginUser.getPassword(), user.get().getPassword());
        }

        if (isMatch) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.get().getUsername(), null, new ArrayList<>());

            if(remember) {
                rememberMeServices.loginSuccess(request, response, authentication);
            }

            String token = jwtService.generateToken(user.get().getId());
            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("id", user.get().getId());
            userResponse.put("username", user.get().getUsername());
            userResponse.put("token", token);

            return ResponseEntity.ok(userResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }
}
