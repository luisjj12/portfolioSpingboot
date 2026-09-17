package com.luisjimenez.portfolioapp.dto;

public class AuthResponse {

    private String token;
    private Long id;
    private String nombre;
    private String email;
    private String role;

    public AuthResponse(String token, Long id, String nombre, String email, String role) {
        this.token = token;
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
