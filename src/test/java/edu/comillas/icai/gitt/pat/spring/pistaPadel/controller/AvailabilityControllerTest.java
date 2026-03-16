package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Pista;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.PistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc

public class AvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PistaRepository pistaRepository;

    private Long idPista;

    @BeforeEach
    void setUp() {
        pistaRepository.deleteAll();

        Pista pista = new Pista();
        pista.setNombre("Pista Availability");
        pista.setUbicacion("Madrid");
        pista.setPrecioHora(18.0);
        pista.setActiva(true);
        pista.setFechaAlta(LocalDateTime.now());

        pista = pistaRepository.save(pista);
        idPista = pista.getIdPista();
    }

    @Test
    void getAvailability_shouldReturn200() throws Exception {
        mockMvc.perform(get("/pistaPadel/availability")
                        .param("date", "2026-03-20")
                        .param("courtId", idPista.toString()))
                .andExpect(status().isOk());
    }
}
