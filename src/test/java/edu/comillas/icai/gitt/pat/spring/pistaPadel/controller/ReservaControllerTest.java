package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Reserva;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Token;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.ReservaRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.TokenRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.service.PadelService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaControllerTest {

    ReservaRepository reservaRepository = mock(ReservaRepository.class);
    PadelService padelService = mock(PadelService.class);
    TokenRepository tokenRepository = mock(TokenRepository.class);

    ReservaController controller = new ReservaController(
            reservaRepository,
            padelService,
            tokenRepository
    );

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombre("Blanca");
        usuario.setEmail("blanca@test.com");
        usuario.setRol("USER");
        return usuario;
    }

    private Usuario admin() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(2L);
        usuario.setNombre("Admin");
        usuario.setEmail("admin@test.com");
        usuario.setRol("ADMIN");
        return usuario;
    }

    private Token token(Usuario usuario) {
        Token token = new Token();
        token.usuario = usuario;
        return token;
    }

    private Reserva reserva() {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setIdUsuario(1L);
        reserva.setIdPista(1L);
        reserva.setFechaReserva(LocalDate.of(2026, 5, 13));
        reserva.setHoraInicio(LocalTime.of(10, 0));
        reserva.setDuracionMinutos(60);
        reserva.setHoraFin(LocalTime.of(11, 0));
        reserva.setEstado("ACTIVA");
        return reserva;
    }

    @Test
    void getReservationsSinLoginDevuelve401() {
        ResponseEntity<?> response = controller.getReservations(null, null, null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getReservationsConLoginDevuelveReservas() {
        Usuario usuario = usuario();
        Token token = token(usuario);

        when(tokenRepository.findById("abc")).thenReturn(java.util.Optional.of(token));
        when(reservaRepository.findByIdUsuario(1L)).thenReturn(List.of(reserva()));

        ResponseEntity<?> response = controller.getReservations("abc", null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getReservationByIdSinLoginDevuelve401() {
        ResponseEntity<?> response = controller.getReservationById(1L, null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getReservationByIdNoExisteDevuelve404() {
        Usuario usuario = usuario();

        when(tokenRepository.findById("abc")).thenReturn(java.util.Optional.of(token(usuario)));
        when(reservaRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        ResponseEntity<?> response = controller.getReservationById(1L, "abc");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createReservationSinLoginDevuelve401() {
        ResponseEntity<?> response = controller.createReservation(reserva(), null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void createReservationConLoginDevuelve201() {
        Usuario usuario = usuario();
        Reserva reserva = reserva();

        when(tokenRepository.findById("abc")).thenReturn(java.util.Optional.of(token(usuario)));
        when(padelService.crearReserva(any(Reserva.class))).thenReturn(reserva);

        ResponseEntity<?> response = controller.createReservation(reserva, "abc");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void cancelReservationSinLoginDevuelve401() {
        ResponseEntity<?> response = controller.cancelReservation(1L, null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void cancelReservationConUsuarioDuenioDevuelve204() {
        Usuario usuario = usuario();
        Reserva reserva = reserva();

        when(tokenRepository.findById("abc")).thenReturn(java.util.Optional.of(token(usuario)));
        when(reservaRepository.findById(1L)).thenReturn(java.util.Optional.of(reserva));

        ResponseEntity<?> response = controller.cancelReservation(1L, "abc");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("CANCELADA", reserva.getEstado());
    }

    @Test
    void cancelReservationUsuarioNoDuenioDevuelve403() {
        Usuario otro = usuario();
        otro.setIdUsuario(99L);

        when(tokenRepository.findById("abc")).thenReturn(java.util.Optional.of(token(otro)));
        when(reservaRepository.findById(1L)).thenReturn(java.util.Optional.of(reserva()));

        ResponseEntity<?> response = controller.cancelReservation(1L, "abc");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}