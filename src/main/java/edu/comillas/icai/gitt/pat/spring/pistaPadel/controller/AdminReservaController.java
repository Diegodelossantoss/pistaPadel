package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Reserva;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Token;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.ReservaRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.TokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pistaPadel/admin")
public class AdminReservaController {

    private final ReservaRepository reservaRepository;
    private final TokenRepository tokenRepository;

    public AdminReservaController(ReservaRepository reservaRepository,
                                  TokenRepository tokenRepository) {
        this.reservaRepository = reservaRepository;
        this.tokenRepository = tokenRepository;
    }

    private Usuario usuarioLogueado(String session) {
        if (session == null) return null;

        Token token = tokenRepository.findById(session).orElse(null);

        if (token == null) return null;

        return token.usuario;
    }

    @GetMapping("/reservations")
    public ResponseEntity<?> getAllReservationsAdmin(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Long courtId,
            @RequestParam(required = false) Long userId,
            @CookieValue(value = "session", required = false) String session) {

        Usuario usuario = usuarioLogueado(session);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        if (!"ADMIN".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }

        List<Reserva> reservas = reservaRepository.findAll();

        if (date != null) {
            LocalDate fechaFiltro = LocalDate.parse(date);
            reservas = reservas.stream()
                    .filter(r -> r.getFechaReserva().equals(fechaFiltro))
                    .collect(Collectors.toList());
        }

        if (courtId != null) {
            reservas = reservas.stream()
                    .filter(r -> r.getIdPista().equals(courtId))
                    .collect(Collectors.toList());
        }

        if (userId != null) {
            reservas = reservas.stream()
                    .filter(r -> r.getIdUsuario().equals(userId))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(reservas);
    }
}