package com.example.stockify.auth.dto;

public class AuthMeResponse {
    private Long id;
    private String email;
    private String rol;

    public AuthMeResponse() {}

    public AuthMeResponse(Long id, String email, String rol) {
        this.id = id;
        this.email = email;
        this.rol = rol;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}

