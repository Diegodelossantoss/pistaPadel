package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Reserva;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.PistaRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/pistaPadel/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PistaRepository pistaRepository;

    @GetMapping
    public List<Reserva> getAllReservations() {
        return reservaRepository.findAll(); // 200
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        return reservaRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok) // 200
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada")); // 404
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Reserva reserva) {

        if (reserva.getIdPista() == null || pistaRepository.findById(reserva.getIdPista()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La pista indicada no existe"); // 400
        }

        if (reserva.getFechaReserva() == null || reserva.getHoraInicio() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Faltan datos de fecha u hora"); // 400
        }

        LocalTime horaFinReal = reserva.getHoraFin();
        if (horaFinReal == null) {
            if (reserva.getDuracionMinutos() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Debes indicar horaFin o una duracionMinutos > 0"); // 400
            }
            horaFinReal = reserva.getHoraInicio().plusMinutes(reserva.getDuracionMinutos());
        }

        if (!reserva.getHoraInicio().isBefore(horaFinReal)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("La hora de inicio debe ser anterior a la de fin"); // 400
        }

        boolean overlap = reservaRepository.existsOverlappingReservation(
                reserva.getIdPista(),
                reserva.getFechaReserva(),
                reserva.getHoraInicio(),
                horaFinReal
        );

        if (overlap) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe una reserva en ese horario"); // 409
        }

        Reserva reservaParaGuardar = new Reserva(
                reserva.getIdReserva(),
                reserva.getIdUsuario(),
                reserva.getIdPista(),
                reserva.getFechaReserva(),
                reserva.getHoraInicio(),
                reserva.getDuracionMinutos(),
                horaFinReal,
                reserva.getEstado(),
                reserva.getFechaCreacion()
        );

        Reserva saved = reservaRepository.save(reservaParaGuardar);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved); // 201
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        if (!reservaRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada"); // 404
        }
        reservaRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
