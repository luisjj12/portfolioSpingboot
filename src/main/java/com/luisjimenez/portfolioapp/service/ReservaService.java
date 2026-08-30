package com.luisjimenez.portfolioapp.service;

import com.luisjimenez.portfolioapp.entity.Reserva;
import com.luisjimenez.portfolioapp.entity.Role;
import com.luisjimenez.portfolioapp.entity.Sala;
import com.luisjimenez.portfolioapp.entity.Usuario;
import com.luisjimenez.portfolioapp.exception.AccesoDenegadoException;
import com.luisjimenez.portfolioapp.exception.RecursoNoEncontradoException;
import com.luisjimenez.portfolioapp.exception.ReservaSolapadaException;
import com.luisjimenez.portfolioapp.repository.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {

    private static final Logger logger = LoggerFactory.getLogger(ReservaService.class);

    private final ReservaRepository reservaRepository;
    private final SalaService salaService;
    private final UsuarioService usuarioService;

    public ReservaService(ReservaRepository reservaRepository, SalaService salaService, UsuarioService usuarioService) {
        this.reservaRepository = reservaRepository;
        this.salaService = salaService;
        this.usuarioService = usuarioService;
    }

    public Reserva crear(Long salaId, Long usuarioId, Reserva datos) {
        Sala sala = salaService.findById(salaId);
        Usuario usuario = usuarioService.findById(usuarioId);

        boolean solapa = reservaRepository.findBySalaIdAndFecha(salaId, datos.getFecha()).stream()
                .anyMatch(r -> r.getHoraInicio().isBefore(datos.getHoraFin())
                        && datos.getHoraInicio().isBefore(r.getHoraFin()));

        if (solapa) {
            logger.warn("Solapamiento de horario: sala {} el {} de {} a {}",
                    salaId, datos.getFecha(), datos.getHoraInicio(), datos.getHoraFin());
            throw new ReservaSolapadaException("La sala ya está reservada en ese horario");
        }

        datos.setSala(sala);
        datos.setUsuario(usuario);

        Reserva guardada = reservaRepository.save(datos);
        logger.info("Reserva creada: id {}, sala {}, usuario {}, {} {}-{}",
                guardada.getId(), salaId, usuarioId, guardada.getFecha(), guardada.getHoraInicio(), guardada.getHoraFin());
        return guardada;
    }

    public List<Reserva> listarPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }

    public void cancelar(Long reservaId, Long usuarioId, boolean esAdmin) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la reserva con id " + reservaId));

        if (!esAdmin && !reserva.getUsuario().getId().equals(usuarioId)) {
            logger.warn("Usuario {} intentó cancelar la reserva {} de otro usuario", usuarioId, reservaId);
            throw new AccesoDenegadoException("No puedes cancelar una reserva que no es tuya");
        }

        reservaRepository.deleteById(reservaId);
        logger.info("Reserva {} cancelada por usuario {} (admin: {})", reservaId, usuarioId, esAdmin);
    }
}
