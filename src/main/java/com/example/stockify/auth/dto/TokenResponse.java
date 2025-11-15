package com.example.stockify.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String rol;

    // Constructor legacy para compatibilidad
    public TokenResponse(String token) {
        this.token = token;
    }
}