package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.service.PadelService;


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

    private final ReservaRepository reservaRepository;
    private final PadelService PadelService;

    public ReservaController(ReservaRepository reservaRepository, PadelService PadelService) {
        this.reservaRepository = reservaRepository;
        this.PadelService = PadelService;
    }

    @GetMapping
    public List<Reserva> getAllReservations() {
        return reservaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        return reservaRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada"));
    }

    @PostMapping
    public ResponseEntity<Reserva> createReservation(@RequestBody Reserva reserva) {
        Reserva saved = PadelService.crearReserva(reserva);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        PadelService.borrarReserva(id);
        return ResponseEntity.noContent().build();
    }
}


