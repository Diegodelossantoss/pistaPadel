package edu.comillas.icai.gitt.pat.spring.pistaPadel.repository;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Reserva;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReservaRepositoryTest {

    @Autowired
    private ReservaRepository reservaRepository;

    private Reserva crearReservaBase() {
        Reserva r = new Reserva();
        r.setIdUsuario(1L);
        r.setIdPista(1L);
        r.setFechaReserva(LocalDate.now());
        r.setHoraInicio(LocalTime.of(10, 0));
        r.setHoraFin(LocalTime.of(11, 0));
        r.setDuracionMinutos(60);
        r.setEstado("ACTIVA");
        r.setFechaCreacion(LocalDateTime.now());
        return reservaRepository.save(r);
    }

    @Test
    void shouldDetectOverlappingReservation() {

        crearReservaBase();

        boolean overlapping = reservaRepository.existsOverlappingReservation(
                1L,
                LocalDate.now(),
                LocalTime.of(10, 30),
                LocalTime.of(11, 30)
        );

        assertTrue(overlapping);
    }
    @Test
    void shouldNotDetectOverlappingReservation() {

        crearReservaBase();

        boolean overlapping = reservaRepository.existsOverlappingReservation(
                1L,
                LocalDate.now(),
                LocalTime.of(11, 0),
                LocalTime.of(12, 0)
        );

        assertFalse(overlapping);
    }

    @Test
    void shouldNotDetectOverlapWhenEditingSameReservation() {

        Reserva r = crearReservaBase();

        boolean overlapping = reservaRepository.existsOverlappingReservationExcludingId(
                1L,
                LocalDate.now(),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                r.getIdReserva()
        );

        assertFalse(overlapping);
    }
}
