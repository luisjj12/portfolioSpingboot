package com.luisjimenez.portfolioapp.controller;

import com.luisjimenez.portfolioapp.entity.Sala;
import com.luisjimenez.portfolioapp.service.SalaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salas")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @GetMapping
    public List<Sala> getAll() {
        return salaService.findAll();
    }

    @GetMapping("/{id}")
    public Sala getById(@PathVariable Long id) {
        return salaService.findById(id);
    }

    @PostMapping
    public Sala create(@Valid @RequestBody Sala sala) {
        return salaService.save(sala);
    }

    @PutMapping("/{id}")
    public Sala update(@PathVariable Long id, @Valid @RequestBody Sala sala) {
        sala.setId(id);
        return salaService.save(sala);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        salaService.deleteById(id);
    }
}
