package com.luisjimenez.portfolioapp.controller;

import com.luisjimenez.portfolioapp.dto.AuthResponse;
import com.luisjimenez.portfolioapp.dto.LoginRequest;
import com.luisjimenez.portfolioapp.entity.Usuario;
import com.luisjimenez.portfolioapp.security.JwtService;
import com.luisjimenez.portfolioapp.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UsuarioService usuarioService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody Usuario usuario) {
        Usuario guardado = usuarioService.registrar(usuario);
        String token = jwtService.generarToken(guardado);
        return new AuthResponse(token, guardado.getId(), guardado.getNombre(), guardado.getEmail(), guardado.getRole().name());
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Usuario usuario = usuarioService.buscarPorEmail(request.getEmail());
        String token = jwtService.generarToken(usuario);
        return new AuthResponse(token, usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRole().name());
    }
}
