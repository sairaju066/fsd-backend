package com.scholarlink.controller;

import com.scholarlink.dto.Dto.*;
import com.scholarlink.model.User;
import com.scholarlink.repository.UserRepository;
import com.scholarlink.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (!hasText(req.getName()) || !hasText(req.getEmail()) || !hasText(req.getPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Please add all fields"));
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("User already exists"));
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole("admin".equalsIgnoreCase(req.getRole()) ? User.Role.admin : User.Role.student);

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getId(), saved.getRole().name());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole(), token));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        return userRepository.findByEmail(req.getEmail())
                .filter(u -> passwordEncoder.matches(req.getPassword(), u.getPassword()))
                .map(u -> {
                    String token = jwtUtil.generateToken(u.getId(), u.getRole().name());
                    return ResponseEntity.ok().body((Object) new AuthResponse(u.getId(), u.getName(), u.getEmail(), u.getRole(), token));
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid credentials")));
    }

    // GET /api/auth/me
    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Not authorized"));
        }
        return ResponseEntity.ok(new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), null));
    }

    private boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}
