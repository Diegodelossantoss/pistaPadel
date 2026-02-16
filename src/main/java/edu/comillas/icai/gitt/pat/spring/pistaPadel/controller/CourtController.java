package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Pista;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.PistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/pistaPadel/courts")
public class CourtController {

    @Autowired
    private PistaRepository pistaRepository;

    @GetMapping
    public List<Pista> getAllCourts() {
        return pistaRepository.findAll(); // Devuelve 200 OK con la lista [cite: 161]
    }
}