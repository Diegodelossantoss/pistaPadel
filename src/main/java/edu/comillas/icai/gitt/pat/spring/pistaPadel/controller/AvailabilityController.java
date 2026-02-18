package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Pista;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Reserva;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.PistaRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.ReservaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/pistaPadel")
public class AvailabilityController {

    private final ReservaRepository reservaRepository;
    private final PistaRepository pistaRepository;

    private static final LocalTime OPEN = LocalTime.of(9, 0);
    private static final LocalTime CLOSE = LocalTime.of(22, 0);
    private static final int SLOT_MINUTES = 60;

    public AvailabilityController(ReservaRepository reservaRepository, PistaRepository pistaRepository) {
        this.reservaRepository = reservaRepository;
        this.pistaRepository = pistaRepository;
    }

    
    @GetMapping("/availability")
    public ResponseEntity<?> availability(@RequestParam String date, @RequestParam Long courtId) {
        return availabilityForCourtAndDate(courtId, date);
    }
 
    @GetMapping("/courts/{courtId}/availability")
    public ResponseEntity<?> availabilityByCourt(@PathVariable Long courtId, @RequestParam String date) {
        return availabilityForCourtAndDate(courtId, date);
    }

    private ResponseEntity<?> availabilityForCourtAndDate(Long courtId, String dateStr) {


        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Formato de fecha inválido (usa YYYY-MM-DD)");
        }


        Pista pista = pistaRepository.findById(courtId).orElse(null);
        if (pista == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pista no encontrada");
        }

        List<Reserva> reservas = reservaRepository.findByIdPistaAndFechaReservaAndEstado(courtId, date, "ACTIVA");

        List<String> libres = new ArrayList<>();
        LocalTime t = OPEN;

        while (t.plusMinutes(SLOT_MINUTES).compareTo(CLOSE) <= 0) {
            LocalTime slotInicio = t;
            LocalTime slotFin = t.plusMinutes(SLOT_MINUTES);

            boolean ocupado = reservas.stream().anyMatch(r ->
                    slotInicio.isBefore(r.getHoraFin()) && slotFin.isAfter(r.getHoraInicio())
            );

            if (!ocupado) {
                libres.add(slotInicio.toString()); // "09:00"
            }

            t = t.plusMinutes(SLOT_MINUTES);
        }

        return ResponseEntity.ok(libres); // 200
    }
}
