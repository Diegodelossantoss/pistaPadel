package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Pista;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.PistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)

public class CourtControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PistaRepository pistaRepository;

    @BeforeEach
    void setUp() {
        pistaRepository.deleteAll();

        Pista pista = new Pista();
        pista.setNombre("Pista Test");
        pista.setUbicacion("Madrid");
        pista.setPrecioHora(20.0);
        pista.setActiva(true);
        pista.setFechaAlta(LocalDateTime.now());

        pistaRepository.save(pista);
    }

    @Test
    void getAllCourts_shouldReturn200() throws Exception {
        mockMvc.perform(get("/pistaPadel/courts"))
                .andExpect(status().isOk());
    }

    @Test
    void getCourtById_shouldReturn200() throws Exception {
        Long id = pistaRepository.findAll().get(0).getIdPista();

        mockMvc.perform(get("/pistaPadel/courts/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void createCourt_shouldReturn201() throws Exception {
        String body = """
            {
              "nombre": "Pista Nueva",
              "ubicacion": "Alcobendas",
              "precioHora": 25.0
            }
            """;

        mockMvc.perform(post("/pistaPadel/courts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void getCourtById_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/pistaPadel/courts/99999"))
                .andExpect(status().isNotFound());
    }
}
