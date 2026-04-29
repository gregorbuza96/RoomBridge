package com.roombridge.userservice.controller;

import com.roombridge.userservice.model.dto.AuthResponse;
import com.roombridge.userservice.model.dto.LoginRequest;
import com.roombridge.userservice.model.entity.AppUser;
import com.roombridge.userservice.repository.AppUserRepository;
import com.roombridge.userservice.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

            AppUser user = userRepository.findByUsername(req.getUsername()).orElseThrow();
            String token = jwtService.generateToken(user);
            log.info("User logged in: {}", req.getUsername());

            return ResponseEntity.ok(AuthResponse.builder()
                    .id(user.getId()).username(user.getUsername())
                    .email(user.getEmail()).role(user.getRole().name())
                    .token(token)
                    .build());
        } catch (BadCredentialsException e) {
            log.warn("Failed login for: {}", req.getUsername());
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("X-User-Id") String userId,
                                       @RequestHeader("X-Username") String username,
                                       @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(Map.of("id", userId, "username", username, "role", role));
    }
}
