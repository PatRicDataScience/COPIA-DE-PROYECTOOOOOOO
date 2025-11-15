package com.example.stockify.auth.application;

import com.example.stockify.auth.components.JwtService;
import com.example.stockify.auth.domain.AuthService;
import com.example.stockify.auth.dto.AuthMeResponse;
import com.example.stockify.auth.dto.SignInRequest;
import com.example.stockify.auth.dto.SignUpRequest;
import com.example.stockify.auth.dto.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signUp(@jakarta.validation.Valid @RequestBody SignUpRequest request){
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<TokenResponse> signIn(@RequestBody SignInRequest request) {
        TokenResponse tokenResponse = authService.signIn(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthMeResponse> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authorization) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        Long id = null;
        String rol = null;
        String email = authentication.getName();

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            if (jwtService.isTokenValid(token)) {
                id = jwtService.extractUserId(token);
                rol = jwtService.extractRole(token);
            }
        }

        return ResponseEntity.ok(new AuthMeResponse(id, email, rol));
    }
}