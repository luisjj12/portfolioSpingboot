package com.luisjimenez.portfolioapp.controller;

import com.luisjimenez.portfolioapp.entity.Reserva;
import com.luisjimenez.portfolioapp.entity.Role;
import com.luisjimenez.portfolioapp.entity.Usuario;
import com.luisjimenez.portfolioapp.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public Reserva crear(@RequestParam Long salaId, @Valid @RequestBody Reserva reserva, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return reservaService.crear(salaId, usuario.getId(), reserva);
    }

    @GetMapping("/mias")
    public List<Reserva> misReservas(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return reservaService.listarPorUsuario(usuario.getId());
    }

    @GetMapping
    public List<Reserva> todas() {
        return reservaService.listarTodas();
    }

    @DeleteMapping("/{id}")
    public void cancelar(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        boolean esAdmin = usuario.getRole() == Role.ADMIN;
        reservaService.cancelar(id, usuario.getId(), esAdmin);
    }
}
