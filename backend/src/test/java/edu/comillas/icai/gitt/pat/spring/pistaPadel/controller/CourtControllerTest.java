package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Pista;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Token;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.TokenRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.UserRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.PistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private ObjectMapper objectMapper;  
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
    void createCourt_shouldReturn401_withoutLogin() throws Exception {
        Pista nueva = new Pista();
        nueva.setNombre("Pista Nueva");
        nueva.setUbicacion("Exterior");
        nueva.setPrecioHora(20.0);
        nueva.setActiva(true);
        nueva.setFechaAlta(LocalDateTime.now());

        mockMvc.perform(post("/pistaPadel/courts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createCourt_shouldReturn201_withAdmin() throws Exception {
        Usuario admin = new Usuario();
        admin.setNombre("Admin");
        admin.setApellidos("Padel");
        admin.setEmail("admin@test.com");
        admin.setPassword("1234");
        admin.setTelefono("600000000");
        admin.setRol("ADMIN");
        admin.setActivo(true);
        admin.setFechaRegistro(LocalDateTime.now());

        admin = userRepository.save(admin);

        Token token = new Token();
        token.usuario = admin;
        token = tokenRepository.save(token);

        Pista nueva = new Pista();
        nueva.setNombre("Pista Nueva");
        nueva.setUbicacion("Exterior");
        nueva.setPrecioHora(20.0);
        nueva.setActiva(true);
        nueva.setFechaAlta(LocalDateTime.now());

        mockMvc.perform(post("/pistaPadel/courts")
                        .cookie(new jakarta.servlet.http.Cookie("session", token.id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated());
    }

    @Test
    void getCourtById_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/pistaPadel/courts/99999"))
                .andExpect(status().isNotFound());
    }
}
