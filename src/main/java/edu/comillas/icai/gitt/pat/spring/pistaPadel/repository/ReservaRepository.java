package edu.comillas.icai.gitt.pat.spring.pistaPadel.repository;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Para AvailabilityController / ReservaController
    List<Reserva> findByIdPistaAndFechaReservaAndEstado(Long idPista, LocalDate fechaReserva, String estado);

    // Solape (crear): si lo estás usando en PadelService
    @Query("""
        SELECT COUNT(r) > 0
        FROM Reserva r
        WHERE r.idPista = :idPista
          AND r.fechaReserva = :fechaReserva
          AND r.estado = 'ACTIVA'
          AND (:horaInicio < r.horaFin AND :horaFin > r.horaInicio)
    """)
    boolean existsOverlappingReservation(
            @Param("idPista") Long idPista,
            @Param("fechaReserva") LocalDate fechaReserva,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin
    );

    // Solape (editar): excluye la propia reserva por idReserva
    @Query("""
        SELECT COUNT(r) > 0
        FROM Reserva r
        WHERE r.idPista = :idPista
          AND r.fechaReserva = :fechaReserva
          AND r.estado = 'ACTIVA'
          AND r.idReserva <> :idReserva
          AND (:horaInicio < r.horaFin AND :horaFin > r.horaInicio)
    """)
    boolean existsOverlappingReservationExcludingId(
            @Param("idPista") Long idPista,
            @Param("fechaReserva") LocalDate fechaReserva,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("idReserva") Long idReserva
    );
}
