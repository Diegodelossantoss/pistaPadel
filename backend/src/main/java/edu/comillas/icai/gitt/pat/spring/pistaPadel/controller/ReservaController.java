package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Reserva;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Token;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.ReservaRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.TokenRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.UserRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.service.PadelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"}, allowCredentials = "true")
@RestController
@RequestMapping("/pistaPadel/reservations")
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final PadelService padelService;
    private final TokenRepository tokenRepository;

    public ReservaController(ReservaRepository reservaRepository,
                             PadelService padelService,
                             TokenRepository tokenRepository) {
        this.reservaRepository = reservaRepository;
        this.padelService = padelService;
        this.tokenRepository = tokenRepository;
    }

    private Usuario usuarioLogueado(String session) {
        Token token = tokenRepository.findById(session).orElse(null);
        if (token == null) return null;
        return token.usuario;
    }

    @GetMapping
    public ResponseEntity<?> getReservations(@CookieValue(value = "session", required = false) String session,
                                             @RequestParam(required = false) String from,
                                             @RequestParam(required = false) String to) {
        Usuario usuario = usuarioLogueado(session);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        List<Reserva> reservas = reservaRepository.findByIdUsuario(usuario.getIdUsuario());

        if (from != null) {
            LocalDate fromDate = LocalDate.parse(from);
            reservas = reservas.stream()
                    .filter(r -> !r.getFechaReserva().isBefore(fromDate))
                    .collect(Collectors.toList());
        }

        if (to != null) {
            LocalDate toDate = LocalDate.parse(to);
            reservas = reservas.stream()
                    .filter(r -> !r.getFechaReserva().isAfter(toDate))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id,
                                                @CookieValue(value = "session", required = false) String session) {
        Usuario logueado = usuarioLogueado(session);

        if (logueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        Reserva reserva = reservaRepository.findById(id).orElse(null);

        if (reserva == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada");
        }

        if (!"ADMIN".equals(logueado.getRol()) && !logueado.getIdUsuario().equals(reserva.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso");
        }

        return ResponseEntity.ok(reserva);
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Reserva reserva,
                                               @CookieValue(value = "session", required = false) String session) {
        Usuario usuario = usuarioLogueado(session);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        reserva.setIdUsuario(usuario.getIdUsuario());

        Reserva saved = padelService.crearReserva(reserva);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id,
                                               @CookieValue(value = "session", required = false) String session) {
        Usuario logueado = usuarioLogueado(session);

        if (logueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        Reserva reserva = reservaRepository.findById(id).orElse(null);

        if (reserva == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada");
        }

        if (!"ADMIN".equals(logueado.getRol()) && !logueado.getIdUsuario().equals(reserva.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes cancelar esta reserva");
        }

        if ("CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La reserva ya está cancelada");
        }

        reserva.setEstado("CANCELADA");
        reservaRepository.save(reserva);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchReservation(@PathVariable Long id,
                                              @RequestBody Reserva cambios,
                                              @CookieValue(value = "session", required = false) String session) {
        Usuario logueado = usuarioLogueado(session);

        if (logueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        Reserva reserva = reservaRepository.findById(id).orElse(null);

        if (reserva == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada");
        }

        if (!"ADMIN".equals(logueado.getRol()) && !logueado.getIdUsuario().equals(reserva.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso");
        }

        if ("CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede modificar una reserva cancelada");
        }

        if (cambios.getFechaReserva() != null) reserva.setFechaReserva(cambios.getFechaReserva());
        if (cambios.getHoraInicio() != null) reserva.setHoraInicio(cambios.getHoraInicio());
        if (cambios.getDuracionMinutos() > 0) reserva.setDuracionMinutos(cambios.getDuracionMinutos());

        LocalTime horaFin = reserva.getHoraInicio().plusMinutes(reserva.getDuracionMinutos());
        reserva.setHoraFin(horaFin);

        boolean ocupada = reservaRepository.existsOverlappingReservationExcludingId(
                reserva.getIdPista(),
                reserva.getFechaReserva(),
                reserva.getHoraInicio(),
                reserva.getHoraFin(),
                reserva.getIdReserva()
        );

        if (ocupada) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Horario ocupado");
        }

        return ResponseEntity.ok(reservaRepository.save(reserva));
    }
}