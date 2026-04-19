package com.notesapp.controller;

import com.notesapp.dto.LoginRequest;
import com.notesapp.dto.LoginResponse;
import com.notesapp.dto.RegisterRequest;
import com.notesapp.security.UserPrincipal;
import com.notesapp.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        String response = authService.registerUser(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = authService.authenticateUser(loginRequest);
        
        ResponseCookie cookie = ResponseCookie.from("jwt", loginResponse.getAccessToken())
                .httpOnly(true)
                .secure(false) // Needs to be true in HTTPS
                .path("/")
                .maxAge(86400) // 24 hours
                .sameSite("Lax") // Lax works well locally crossing ports if sometimes strict fails
                .build();
                
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        
        Map<String, String> body = new HashMap<>();
        body.put("message", "Logged in successfully");
        return ResponseEntity.ok(body);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0) // Expire immediately
                .sameSite("Lax")
                .build();
                
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        
        Map<String, String> body = new HashMap<>();
        body.put("message", "Logged out successfully");
        return ResponseEntity.ok(body);
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("id", currentUser.getId());
        userInfo.put("email", currentUser.getEmail());
        userInfo.put("name", currentUser.getName());
        return ResponseEntity.ok(userInfo);
    }
}
