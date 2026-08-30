package com.luisjimenez.portfolioapp.service;

import com.luisjimenez.portfolioapp.entity.Reserva;
import com.luisjimenez.portfolioapp.entity.Role;
import com.luisjimenez.portfolioapp.entity.Sala;
import com.luisjimenez.portfolioapp.entity.Usuario;
import com.luisjimenez.portfolioapp.exception.AccesoDenegadoException;
import com.luisjimenez.portfolioapp.exception.ReservaSolapadaException;
import com.luisjimenez.portfolioapp.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private SalaService salaService;
    @Mock
    private UsuarioService usuarioService;

    private ReservaService reservaService;

    private Sala sala;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        reservaService = new ReservaService(reservaRepository, salaService, usuarioService);

        sala = new Sala();
        sala.setId(1L);

        usuario = new Usuario();
        usuario.setId(10L);
        usuario.setRole(Role.USER);
    }

    private Reserva reserva(LocalDate fecha, LocalTime inicio, LocalTime fin) {
        Reserva r = new Reserva();
        r.setFecha(fecha);
        r.setHoraInicio(inicio);
        r.setHoraFin(fin);
        return r;
    }

    @Test
    void permiteCrearReservaSinSolapamiento() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        when(salaService.findById(1L)).thenReturn(sala);
        when(usuarioService.findById(10L)).thenReturn(usuario);
        when(reservaRepository.findBySalaIdAndFecha(1L, fecha)).thenReturn(List.of());
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva nueva = reserva(fecha, LocalTime.of(10, 0), LocalTime.of(11, 0));

        Reserva creada = reservaService.crear(1L, 10L, nueva);

        assertThat(creada.getSala()).isEqualTo(sala);
        assertThat(creada.getUsuario()).isEqualTo(usuario);
        verify(reservaRepository).save(nueva);
    }

    @Test
    void rechazaReservaConHorarioSolapado() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        Reserva existente = reserva(fecha, LocalTime.of(9, 0), LocalTime.of(11, 0));

        when(salaService.findById(1L)).thenReturn(sala);
        when(usuarioService.findById(10L)).thenReturn(usuario);
        when(reservaRepository.findBySalaIdAndFecha(1L, fecha)).thenReturn(List.of(existente));

        Reserva nueva = reserva(fecha, LocalTime.of(10, 0), LocalTime.of(12, 0));

        assertThatThrownBy(() -> reservaService.crear(1L, 10L, nueva))
                .isInstanceOf(ReservaSolapadaException.class);

        verify(reservaRepository, never()).save(any());
    }

    @Test
    void permiteReservasContiguasSinSolapamiento() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        Reserva existente = reserva(fecha, LocalTime.of(9, 0), LocalTime.of(10, 0));

        when(salaService.findById(1L)).thenReturn(sala);
        when(usuarioService.findById(10L)).thenReturn(usuario);
        when(reservaRepository.findBySalaIdAndFecha(1L, fecha)).thenReturn(List.of(existente));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva nueva = reserva(fecha, LocalTime.of(10, 0), LocalTime.of(11, 0));

        assertThat(reservaService.crear(1L, 10L, nueva)).isNotNull();
    }

    @Test
    void elDuenioPuedeCancelarSuPropiaReserva() {
        Reserva existente = reserva(LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(10, 0));
        existente.setUsuario(usuario);
        when(reservaRepository.findById(5L)).thenReturn(Optional.of(existente));

        reservaService.cancelar(5L, 10L, false);

        verify(reservaRepository).deleteById(5L);
    }

    @Test
    void unUsuarioNoPuedeCancelarLaReservaDeOtro() {
        Reserva existente = reserva(LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(10, 0));
        existente.setUsuario(usuario);
        when(reservaRepository.findById(5L)).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> reservaService.cancelar(5L, 999L, false))
                .isInstanceOf(AccesoDenegadoException.class);

        verify(reservaRepository, never()).deleteById(any());
    }

    @Test
    void unAdminPuedeCancelarLaReservaDeCualquierUsuario() {
        Reserva existente = reserva(LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(10, 0));
        existente.setUsuario(usuario);
        when(reservaRepository.findById(5L)).thenReturn(Optional.of(existente));

        reservaService.cancelar(5L, 999L, true);

        verify(reservaRepository).deleteById(5L);
    }
}
