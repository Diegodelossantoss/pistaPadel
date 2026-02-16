package edu.comillas.icai.gitt.pat.spring.pistaPadel.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public record Reserva(
        Long idReserva,
        Long idUsuario,
        Long idPista,
        LocalDate fechaReserva,
        LocalTime horaInicio,
        int duracionMinutos,
        LocalTime horaFin,
        String estado,          // ACTIVA o CANCELADA
        LocalDateTime fechaCreacion
) {}