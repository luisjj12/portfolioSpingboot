package com.luisjimenez.portfolioapp.service;

import com.luisjimenez.portfolioapp.entity.Sala;
import com.luisjimenez.portfolioapp.exception.RecursoNoEncontradoException;
import com.luisjimenez.portfolioapp.repository.SalaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaService {

    private static final Logger logger = LoggerFactory.getLogger(SalaService.class);

    private final SalaRepository salaRepository;

    public SalaService(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    public List<Sala> findAll() {
        return salaRepository.findAll();
    }

    public Sala findById(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la sala con id " + id));
    }

    public Sala save(Sala sala) {
        Sala guardada = salaRepository.save(sala);
        logger.info("Sala guardada: id {}, nombre {}", guardada.getId(), guardada.getNombre());
        return guardada;
    }

    public void deleteById(Long id) {
        salaRepository.deleteById(id);
        logger.info("Sala eliminada, id {}", id);
    }
}
