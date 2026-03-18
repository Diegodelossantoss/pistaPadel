package edu.comillas.icai.gitt.pat.spring.pistaPadel.service;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Reserva;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Pista;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.PistaRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class PadelService {

    private static final Logger logger = LoggerFactory.getLogger(PadelService.class);
    private final ReservaRepository reservaRepository;
    private final PistaRepository pistaRepository;

    private static final LocalTime HORA_APERTURA = LocalTime.of(9, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(22, 0);

    public PadelService(ReservaRepository reservaRepository, PistaRepository pistaRepository) {
        this.reservaRepository = reservaRepository;
        this.pistaRepository = pistaRepository;
    }

    public Reserva crearReserva(Reserva reserva) {
        logger.debug("Iniciando validación de reserva para pista: {} en fecha: {}",
                reserva.getIdPista(), reserva.getFechaReserva());

        // 1. Verificar si la pista existe
        Pista pista = pistaRepository.findById(reserva.getIdPista()).orElse(null);
        if (pista == null) {
            logger.error("Error al crear reserva: Pista no existe");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La pista indicada no existe");
        }

        if (!pista.isActiva()) {
            logger.error("Error: Intento de reserva en pista inactiva (ID: {})", pista.getIdPista());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La pista seleccionada no está disponible actualmente");
        }

        if (reserva.getFechaReserva() == null || reserva.getHoraInicio() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan datos de fecha u hora");
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime momentoReserva = LocalDateTime.of(reserva.getFechaReserva(), reserva.getHoraInicio());

        if (momentoReserva.isBefore(ahora)) {
            logger.error("Error: Intento de reserva en fecha/hora pasada: {}", momentoReserva);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes reservar en una fecha o hora que ya ha pasado");
        }

        LocalTime horaFinReal = reserva.getHoraFin();
        if (horaFinReal == null) {
            if (reserva.getDuracionMinutos() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes indicar duración");
            }
            horaFinReal = reserva.getHoraInicio().plusMinutes(reserva.getDuracionMinutos());
        }


        if (reserva.getHoraInicio().isBefore(HORA_APERTURA) || horaFinReal.isAfter(HORA_CIERRE)) {
            logger.error("Error: Reserva fuera de horario del club ({} - {})", reserva.getHoraInicio(), horaFinReal);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El club solo abre de 09:00 a 22:00");
        }

        if (!reserva.getHoraInicio().isBefore(horaFinReal)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La hora de inicio debe ser anterior a la de fin");
        }

        boolean overlap = reservaRepository.existsOverlappingReservation(
                reserva.getIdPista(),
                reserva.getFechaReserva(),
                reserva.getHoraInicio(),
                horaFinReal
        );

        if (overlap) {
            logger.error("Conflicto: Slot horario ocupado para la pista {}", reserva.getIdPista());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una reserva en ese horario");
        }

        reserva.setHoraFin(horaFinReal);
        if (reserva.getEstado() == null) reserva.setEstado("ACTIVA");
        if (reserva.getFechaCreacion() == null) reserva.setFechaCreacion(LocalDateTime.now());

        Reserva guardada = reservaRepository.save(reserva);
        logger.info("Reserva {} creada con éxito para el usuario {}",
                guardada.getIdReserva(), guardada.getIdUsuario());

        return guardada;
    }

    public void borrarReserva(Long id) {
        if (!reservaRepository.existsById(id)) {
            logger.error("Fallo al borrar: Reserva {} no encontrada", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada");
        }
        reservaRepository.deleteById(id);
        logger.info("Reserva {} eliminada correctamente", id);
    }
}