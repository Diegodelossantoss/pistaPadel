package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Pista;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Token;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.PistaRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.ReservaRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.TokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"}, allowCredentials = "true")
@RestController
@RequestMapping("/pistaPadel/courts")
public class CourtController {

    private static final Logger logger = LoggerFactory.getLogger(CourtController.class);

    private final ReservaRepository reservaRepository;
    private final PistaRepository pistaRepository;
    private final TokenRepository tokenRepository;

    public CourtController(ReservaRepository reservaRepository,
                           PistaRepository pistaRepository,
                           TokenRepository tokenRepository) {
        this.reservaRepository = reservaRepository;
        this.pistaRepository = pistaRepository;
        this.tokenRepository = tokenRepository;
    }

    private Usuario usuarioLogueado(String session) {
        if (session == null) return null;

        Token token = tokenRepository.findById(session).orElse(null);

        if (token == null) return null;

        return token.usuario;
    }

    private boolean esAdmin(String session) {
        Usuario usuario = usuarioLogueado(session);
        return usuario != null && "ADMIN".equals(usuario.getRol());
    }

    @GetMapping
    public List<Pista> getAllCourts(@RequestParam(required = false) Boolean active) {
        logger.info("Consultando listado de pistas. Filtro activo: {}", active);

        if (active == null) {
            return pistaRepository.findAll();
        }

        return pistaRepository.findByActiva(active);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourtById(@PathVariable Long id) {
        logger.debug("Buscando detalle de la pista con ID: {}", id);

        Pista pista = pistaRepository.findById(id).orElse(null);

        if (pista == null) {
            logger.error("Pista con ID {} no encontrada", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pista no encontrada");
        }

        return ResponseEntity.ok(pista);
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<?> getCourtAvailability(
            @PathVariable Long id,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        Pista pista = pistaRepository.findById(id).orElse(null);

        if (pista == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pista no encontrada");
        }

        List<String> franjasDisponibles = new ArrayList<>();

        LocalTime hora = LocalTime.of(9, 0);
        LocalTime cierre = LocalTime.of(22, 0);

        while (hora.isBefore(cierre)) {
            LocalTime horaFin = hora.plusHours(1);

            boolean ocupada = reservaRepository.existsOverlappingReservation(
                    id,
                    date,
                    hora,
                    horaFin
            );

            if (!ocupada) {
                franjasDisponibles.add(hora.toString());
            }

            hora = hora.plusHours(1);
        }

        return ResponseEntity.ok(Map.of(
                "idPista", id,
                "fecha", date.toString(),
                "franjasDisponibles", franjasDisponibles
        ));
    }

    @PostMapping
    public ResponseEntity<?> createCourt(@RequestBody Pista pista,
                                         @CookieValue(value = "session", required = false) String session) {
        logger.info("Intentando crear pista: {}", pista.getNombre());

        Usuario usuario = usuarioLogueado(session);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        if (!"ADMIN".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }

        if (pistaRepository.findByNombre(pista.getNombre()).isPresent()) {
            logger.error("Error: Nombre de pista ya existe: {}", pista.getNombre());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nombre duplicado");
        }

        pista.setActiva(true);
        pista.setFechaAlta(LocalDateTime.now());

        Pista saved = pistaRepository.save(pista);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCourt(@PathVariable Long id,
                                         @RequestBody Pista cambios,
                                         @CookieValue(value = "session", required = false) String session) {
        logger.info("Intentando actualizar pista con ID: {}", id);

        Usuario usuario = usuarioLogueado(session);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        if (!"ADMIN".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }

        Pista pista = pistaRepository.findById(id).orElse(null);

        if (pista == null) {
            logger.error("No se puede actualizar: Pista {} no existe", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pista no encontrada");
        }

        if (cambios.getNombre() != null) pista.setNombre(cambios.getNombre());
        if (cambios.getUbicacion() != null) pista.setUbicacion(cambios.getUbicacion());
        if (cambios.getPrecioHora() > 0) pista.setPrecioHora(cambios.getPrecioHora());
        pista.setActiva(cambios.isActiva());

        Pista saved = pistaRepository.save(pista);

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourt(@PathVariable Long id,
                                         @CookieValue(value = "session", required = false) String session) {
        logger.info("Intentando eliminar pista con ID: {}", id);

        Usuario usuario = usuarioLogueado(session);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        if (!"ADMIN".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }

        if (!pistaRepository.existsById(id)) {
            logger.error("Error al borrar: Pista {} no encontrada", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        pistaRepository.deleteById(id);

        logger.debug("Pista {} borrada correctamente", id);

        return ResponseEntity.noContent().build();
    }
}