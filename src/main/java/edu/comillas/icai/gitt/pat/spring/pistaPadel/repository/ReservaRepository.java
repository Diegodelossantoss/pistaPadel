package edu.comillas.icai.gitt.pat.spring.pistaPadel.repository;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByIdUsuario(Long idUsuario);
    List<Reserva> findByIdPistaAndFechaReservaAndEstado(Long idPista, LocalDate fechaReserva, String estado);
    
    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
        FROM Reserva r
        WHERE r.idPista = :idPista
          AND r.fechaReserva = :fechaReserva
          AND (r.horaInicio < :horaFin AND :horaInicio < r.horaFin)
    """)
    boolean existsOverlappingReservation(
            @Param("idPista") Long idPista,
            @Param("fechaReserva") LocalDate fechaReserva,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin
    );
}
