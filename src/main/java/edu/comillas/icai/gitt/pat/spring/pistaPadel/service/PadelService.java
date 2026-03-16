package edu.comillas.icai.gitt.pat.spring.pistaPadel.service;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Reserva;
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

    public PadelService(ReservaRepository reservaRepository, PistaRepository pistaRepository) {
        this.reservaRepository = reservaRepository;
        this.pistaRepository = pistaRepository;
    }

    public Reserva crearReserva(Reserva reserva) {
        logger.debug("Iniciando validación de reserva para pista: {} en fecha: {}",
                reserva.getIdPista(), reserva.getFechaReserva());

        if (reserva.getIdPista() == null || pistaRepository.findById(reserva.getIdPista()).isEmpty()) {
            logger.error("Error al crear reserva: Pista no existe");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La pista indicada no existe");
        }

        if (reserva.getFechaReserva() == null || reserva.getHoraInicio() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan datos de fecha u hora");
        }

        LocalTime horaFinReal = reserva.getHoraFin();
        if (horaFinReal == null) {
            if (reserva.getDuracionMinutos() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Debes indicar horaFin o una duracionMinutos > 0");
            }
            horaFinReal = reserva.getHoraInicio().plusMinutes(reserva.getDuracionMinutos());
        }

        if (!reserva.getHoraInicio().isBefore(horaFinReal)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La hora de inicio debe ser anterior a la de fin");
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