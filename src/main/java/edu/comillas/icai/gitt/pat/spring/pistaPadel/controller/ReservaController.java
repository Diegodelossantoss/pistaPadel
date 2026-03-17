package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.service.PadelService;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Reserva;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.PistaRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.security.Principal;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.UserRepository;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/pistaPadel/reservations")
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final PadelService padelService;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReservaController.class);

    public ReservaController(ReservaRepository reservaRepository, PadelService PadelService, UserRepository userRepository) {
        this.reservaRepository = reservaRepository;
        this.padelService = PadelService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getReservations(Principal principal,
                                             @RequestParam(required = false) String from,
                                             @RequestParam(required = false) String to) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        String email = principal.getName();

        return userRepository.findByEmail(email)
                .<ResponseEntity<?>>map(usuario -> {
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
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado"));
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id, Authentication authentication) {
        Reserva reserva = reservaRepository.findById(id).orElse(null);
        if (reserva == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada");

        Usuario logueado = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (logueado == null || (!logueado.getRol().equals("ADMIN") && !logueado.getIdUsuario().equals(reserva.getIdUsuario()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para ver esta reserva");
        }

        return ResponseEntity.ok(reserva);
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Reserva reserva) {
        logger.info("Intento de reserva para usuario {}", reserva.getIdUsuario());
        try {
            Reserva saved = padelService.crearReserva(reserva);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            logger.error("Error al reservar: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id, Authentication authentication) {

        Reserva reserva = reservaRepository.findById(id).orElse(null);
        if (reserva == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada");
        }

        Usuario logueado = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (logueado == null || (!logueado.getRol().equals("ADMIN") && !logueado.getIdUsuario().equals(reserva.getIdUsuario()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes cancelar una reserva que no es tuya");
        }

        if ("CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La reserva ya está cancelada");
        }

        reserva.setEstado("CANCELADA");
        reservaRepository.save(reserva);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchReservation(@PathVariable Long id, @RequestBody Reserva cambios, Authentication authentication) {

        Reserva reserva = reservaRepository.findById(id).orElse(null);
        if (reserva == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada");
        }

        Usuario logueado = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (logueado == null || (!logueado.getRol().equals("ADMIN") && !logueado.getIdUsuario().equals(reserva.getIdUsuario()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para modificar esta reserva");
        }

        if ("CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede modificar una reserva que ya ha sido cancelada");
        }

        if (cambios.getFechaReserva() != null) reserva.setFechaReserva(cambios.getFechaReserva());
        if (cambios.getHoraInicio() != null) reserva.setHoraInicio(cambios.getHoraInicio());
        if (cambios.getDuracionMinutos() > 0) reserva.setDuracionMinutos(cambios.getDuracionMinutos());

        LocalTime horaFinReal = reserva.getHoraInicio().plusMinutes(reserva.getDuracionMinutos());
        reserva.setHoraFin(horaFinReal);

        if (!reserva.getHoraInicio().isBefore(reserva.getHoraFin())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Horario inválido");
        }

        boolean overlap = reservaRepository.existsOverlappingReservationExcludingId(
                reserva.getIdPista(),
                reserva.getFechaReserva(),
                reserva.getHoraInicio(),
                reserva.getHoraFin(),
                reserva.getIdReserva()
        );

        if (overlap) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nuevo horario ocupado");
        }

        Reserva saved = reservaRepository.save(reserva);
        return ResponseEntity.ok(saved);
    }
}