package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Pista;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.PistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/pistaPadel/courts")
public class CourtController {

    private static final Logger logger = LoggerFactory.getLogger(CourtController.class);

    @Autowired
    private PistaRepository pistaRepository;
    @GetMapping
    public List<Pista> getAllCourts(@RequestParam(required = false) Boolean active) {
        logger.info("Consultando listado de pistas. Filtro activo: {}", active);
        if (active == null) return pistaRepository.findAll();
        return pistaRepository.findByActiva(active);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getCourtById(@PathVariable Long id) {
        logger.debug("Buscando detalle de la pista con ID: {}", id);
        return pistaRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.error("Pista con ID {} no encontrada", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pista no encontrada"); // 404 [cite: 162]
                });
    }

    @PostMapping
    public ResponseEntity<?> createCourt(@RequestBody Pista pista) {
        logger.info("ADMIN intentando crear pista: {}", pista.getNombre());

        if (pistaRepository.findByNombre(pista.getNombre()).isPresent()) {
            logger.error("Error: Nombre de pista ya existe: {}", pista.getNombre());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nombre duplicado");
        }

        pista.setActiva(true);
        pista.setFechaAlta(java.time.LocalDateTime.now());
        Pista saved = pistaRepository.save(pista);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);

    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourt(@PathVariable Long id, @RequestBody Pista pista) {
        logger.info("ADMIN actualizando pista con ID: {}", id);

        if (!pistaRepository.existsById(id)) {
            logger.error("No se puede actualizar: Pista {} no existe", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pista no encontrada");
        }

        pista.setIdPista(id);
        Pista saved = pistaRepository.save(pista);
        return ResponseEntity.ok(saved);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourt(@PathVariable Long id) {
        logger.info("ADMIN eliminando pista con ID: {}", id);

        if (!pistaRepository.existsById(id)) {
            logger.error("Error al borrar: Pista {} no encontrada", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 [cite: 164]
        }

        pistaRepository.deleteById(id);
        logger.debug("Pista {} borrada correctamente", id);
        return ResponseEntity.noContent().build(); // 204 OK [cite: 164]
    }
}
