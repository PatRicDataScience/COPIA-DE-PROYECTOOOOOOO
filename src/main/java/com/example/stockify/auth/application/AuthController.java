package com.example.stockify.auth.application;

import com.example.stockify.auth.domain.AuthService;
import com.example.stockify.auth.dto.SignInRequest;
import com.example.stockify.auth.dto.SignUpRequest;
import com.example.stockify.auth.dto.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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

}