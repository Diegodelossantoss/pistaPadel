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

import java.util.List;

@RestController
@RequestMapping("/pistaPadel/courts")
public class CourtController {

    @Autowired
    private PistaRepository pistaRepository;

    @GetMapping
    public List<Pista> getAllCourts() {
        return pistaRepository.findAll(); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourtById(@PathVariable Long id) {
        return pistaRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok) // 200 OK
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pista no encontrada")); // 404
    }


    @PostMapping
    public ResponseEntity<Pista> createCourt(@RequestBody Pista pista) {
    if (pista.getFechaAlta() == null) pista.setFechaAlta(java.time.LocalDateTime.now());
    pista.setActiva(true);
    Pista saved = pistaRepository.save(pista);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved); // 201
}


    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourt(@PathVariable Long id, @RequestBody Pista pista) {

        if (!pistaRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pista no encontrada"); // 404
        }

        Pista updated = pista;
        Pista saved = pistaRepository.save(updated);
        return ResponseEntity.ok(saved); // 200 
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourt(@PathVariable Long id) {

        if (!pistaRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pista no encontrada"); // 404
        }

        pistaRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 
    }
}
