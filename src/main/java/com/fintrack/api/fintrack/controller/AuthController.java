package com.fintrack.api.fintrack.controller;

import org.springframework.web.bind.annotation.RestController;

import com.fintrack.api.fintrack.dto.AuthRequest;
import com.fintrack.api.fintrack.dto.MessageResponse;
import com.fintrack.api.fintrack.service.AuthService;

import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;



@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status","ok");
    }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse register(@Valid @RequestBody AuthRequest request) {
        authService.register(request.email(), request.password());
        
        
        return new MessageResponse("Account Created successfully.");
    }
    
    
}
