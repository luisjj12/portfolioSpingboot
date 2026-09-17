package com.luisjimenez.portfolioapp.repository;

import com.luisjimenez.portfolioapp.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuarioId(Long usuarioId);

    List<Reserva> findBySalaIdAndFecha(Long salaId, LocalDate fecha);

    boolean existsBySalaId(Long salaId);
}
