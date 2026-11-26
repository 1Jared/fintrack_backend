package com.fintrack.api.fintrack.controller;

import org.springframework.web.bind.annotation.RestController;

import com.fintrack.api.fintrack.dto.AuthRequest;
import com.fintrack.api.fintrack.dto.LoginResponse;
import com.fintrack.api.fintrack.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "Register, sign in, and check API health.")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/health")
    @Operation(summary = "Check API health")
    @ApiResponse(responseCode = "200", description = "API is healthy.")
    public Map<String, String> health() {
        return Map.of("status","ok");
    }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register an account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created; returns a JWT and email."),
            @ApiResponse(responseCode = "400", description = "Invalid email or password."),
            @ApiResponse(responseCode = "409", description = "An account with this email already exists.")
    })
    public LoginResponse register(@Valid @RequestBody AuthRequest request) {
        return authService.register(request.email(), request.password());
    }

    @PostMapping("/auth/login")
    @Operation(summary = "Log in")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful; returns a JWT and email."),
            @ApiResponse(responseCode = "400", description = "Invalid request."),
            @ApiResponse(responseCode = "401", description = "Invalid email or password.")
    })
    public LoginResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request.email(), request.password());
    }
}
