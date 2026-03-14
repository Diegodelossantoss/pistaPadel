package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;
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

import java.time.LocalTime;
import java.util.List;
import java.security.Principal;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.UserRepository;


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
    public ResponseEntity<?> getReservations(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        String email = principal.getName();

        return userRepository.findByEmail(email)
                .<ResponseEntity<?>>map(usuario -> ResponseEntity.ok(reservaRepository.findByUsuarioId(usuario.getIdUsuario())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado"));
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        return reservaRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada"));
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Reserva reserva) {
        logger.info("Intento de reserva para usuario {}", reserva.getIdUsuario()); // Traza obligatoria
        try {
            Reserva saved = padelService.crearReserva(reserva);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved); // Código 201 [cite: 169]
        } catch (Exception e) {
            logger.error("Error al reservar: {}", e.getMessage()); // Traza de error
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // Código 409 [cite: 169, 182]
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {

        Reserva reserva = reservaRepository.findById(id).orElse(null);

        if (reserva == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada"); // 404
        }

        if ("CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La reserva ya está cancelada"); // 409
        }

        reserva.setEstado("CANCELADA");
        reservaRepository.save(reserva);

        return ResponseEntity.noContent().build(); // 204
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchReservation(@PathVariable Long id, @RequestBody Reserva cambios) {

        Reserva reserva = reservaRepository.findById(id).orElse(null);
        if (reserva == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada"); // 404
        }

        if ("CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede modificar una reserva cancelada"); // 409
        }

        if (cambios.getFechaReserva() != null) reserva.setFechaReserva(cambios.getFechaReserva());
        if (cambios.getHoraInicio() != null) reserva.setHoraInicio(cambios.getHoraInicio());
        if (cambios.getDuracionMinutos() > 0) reserva.setDuracionMinutos(cambios.getDuracionMinutos());

        LocalTime horaFinReal = reserva.getHoraInicio().plusMinutes(reserva.getDuracionMinutos());
        reserva.setHoraFin(horaFinReal);

        if (!reserva.getHoraInicio().isBefore(reserva.getHoraFin())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Horario inválido"); // 400
        }

        boolean overlap = reservaRepository.existsOverlappingReservationExcludingId(
                reserva.getIdPista(),
                reserva.getFechaReserva(),
                reserva.getHoraInicio(),
                reserva.getHoraFin(),
                reserva.getIdReserva()
        );


        if (overlap) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nuevo horario ocupado"); // 409
        }

        Reserva saved = reservaRepository.save(reserva);
        return ResponseEntity.ok(saved); // 200
    }


}


