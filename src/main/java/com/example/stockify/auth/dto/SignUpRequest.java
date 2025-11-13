package com.example.stockify.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String password;

    public SignUpRequest() {
    }

    public SignUpRequest(String nombre, String apellido, String email, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
    }
}