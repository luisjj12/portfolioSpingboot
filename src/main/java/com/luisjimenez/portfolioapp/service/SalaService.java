package com.luisjimenez.portfolioapp.service;

import com.luisjimenez.portfolioapp.entity.Sala;
import com.luisjimenez.portfolioapp.exception.RecursoNoEncontradoException;
import com.luisjimenez.portfolioapp.exception.SalaConReservasException;
import com.luisjimenez.portfolioapp.repository.ReservaRepository;
import com.luisjimenez.portfolioapp.repository.SalaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaService {

    private static final Logger logger = LoggerFactory.getLogger(SalaService.class);

    private final SalaRepository salaRepository;
    private final ReservaRepository reservaRepository;

    public SalaService(SalaRepository salaRepository, ReservaRepository reservaRepository) {
        this.salaRepository = salaRepository;
        this.reservaRepository = reservaRepository;
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
        if (reservaRepository.existsBySalaId(id)) {
            throw new SalaConReservasException("No se puede eliminar la sala porque tiene reservas asociadas");
        }
        salaRepository.deleteById(id);
        logger.info("Sala eliminada, id {}", id);
    }
}
