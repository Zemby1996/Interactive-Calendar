package pl.calendar.interactive_calendar_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.calendar.interactive_calendar_backend.dto.ApiResponse;
import pl.calendar.interactive_calendar_backend.dto.AuthRequest;
import pl.calendar.interactive_calendar_backend.dto.AuthResponse;
import pl.calendar.interactive_calendar_backend.dto.RegisterRequest;
import pl.calendar.interactive_calendar_backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        ApiResponse<Void> response = new ApiResponse<>(true, "User registered!", null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        String token = authService.login(request);
        AuthResponse authResponse = new AuthResponse(token);
        ApiResponse<AuthResponse> response = new ApiResponse<>(true, "Logged in successfully", authResponse);
        return ResponseEntity.ok(response);
    }
}