package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Reserva;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.ReservaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pistaPadel/admin")
public class AdminReservaController {

    private final ReservaRepository reservaRepository;

    public AdminReservaController(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @GetMapping("/reservations")
    public ResponseEntity<?> getAllReservationsAdmin(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Long courtId,
            @RequestParam(required = false) Long userId) {

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