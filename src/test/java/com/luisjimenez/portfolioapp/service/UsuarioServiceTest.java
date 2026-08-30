package com.luisjimenez.portfolioapp.service;

import com.luisjimenez.portfolioapp.entity.Role;
import com.luisjimenez.portfolioapp.entity.Usuario;
import com.luisjimenez.portfolioapp.exception.EmailDuplicadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.luisjimenez.portfolioapp.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder);
    }

    @Test
    void registrarCodificaLaPasswordYAsignaRolUser() {
        Usuario datos = new Usuario();
        datos.setEmail("nuevo@test.com");
        datos.setPassword("secreto123");

        when(usuarioRepository.findByEmail("nuevo@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secreto123")).thenReturn("hash-codificado");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario registrado = usuarioService.registrar(datos);

        assertThat(registrado.getPassword()).isEqualTo("hash-codificado");
        assertThat(registrado.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void noPermiteRegistrarUnEmailYaExistente() {
        Usuario existente = new Usuario();
        existente.setEmail("dup@test.com");

        Usuario datos = new Usuario();
        datos.setEmail("dup@test.com");
        datos.setPassword("secreto123");

        when(usuarioRepository.findByEmail("dup@test.com")).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> usuarioService.registrar(datos))
                .isInstanceOf(EmailDuplicadoException.class);

        verify(usuarioRepository, never()).save(any());
    }
}
