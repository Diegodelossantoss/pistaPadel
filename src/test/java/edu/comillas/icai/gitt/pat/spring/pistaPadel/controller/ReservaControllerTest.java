package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Pista;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Reserva;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.PistaRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.ReservaRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc

public class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PistaRepository pistaRepository;

    @Autowired
    private UserRepository userRepository;

    private Long idUsuario;
    private Long idPista;

    @BeforeEach
    void setUp() {
        reservaRepository.deleteAll();
        pistaRepository.deleteAll();
        userRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setNombre("Lucia");
        usuario.setApellidos("Garcia");
        usuario.setEmail("lucia@test.com");
        usuario.setPassword("1234");
        usuario.setTelefono("666111222");
        usuario.setRol("USER");
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario = userRepository.save(usuario);
        idUsuario = usuario.getIdUsuario();

        Pista pista = new Pista();
        pista.setNombre("Pista Reserva Test");
        pista.setUbicacion("Madrid");
        pista.setPrecioHora(20.0);
        pista.setActiva(true);
        pista.setFechaAlta(LocalDateTime.now());
        pista = pistaRepository.save(pista);
        idPista = pista.getIdPista();
    }

    @Test
    void getReservations_shouldReturn200() throws Exception {
        mockMvc.perform(get("/pistaPadel/reservations"))
                .andExpect(status().isOk());
    }

    @Test
    void createReservation_shouldReturn201() throws Exception {
        String body = """
            {
              "idUsuario": %d,
              "idPista": %d,
              "fechaReserva": "2026-03-20",
              "horaInicio": "10:00:00",
              "duracionMinutos": 60
            }
            """.formatted(idUsuario, idPista);

        mockMvc.perform(post("/pistaPadel/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void getReservationById_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/pistaPadel/reservations/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelReservation_shouldReturn204() throws Exception {
        Reserva reserva = new Reserva();
        reserva.setIdUsuario(idUsuario);
        reserva.setIdPista(idPista);
        reserva.setFechaReserva(LocalDate.of(2026, 3, 21));
        reserva.setHoraInicio(LocalTime.of(11, 0));
        reserva.setDuracionMinutos(60);
        reserva.setHoraFin(LocalTime.of(12, 0));
        reserva.setEstado("CONFIRMADA");
        reserva = reservaRepository.save(reserva);

        mockMvc.perform(delete("/pistaPadel/reservations/" + reserva.getIdReserva()))
                .andExpect(status().isNoContent());
    }

    @Test
    void patchReservation_shouldReturn200() throws Exception {
        Reserva reserva = new Reserva();
        reserva.setIdUsuario(idUsuario);
        reserva.setIdPista(idPista);
        reserva.setFechaReserva(LocalDate.of(2026, 3, 22));
        reserva.setHoraInicio(LocalTime.of(12, 0));
        reserva.setDuracionMinutos(60);
        reserva.setHoraFin(LocalTime.of(13, 0));
        reserva.setEstado("CONFIRMADA");
        reserva = reservaRepository.save(reserva);

        String body = """
            {
              "horaInicio": "13:00:00",
              "duracionMinutos": 90
            }
            """;

        mockMvc.perform(patch("/pistaPadel/reservations/" + reserva.getIdReserva())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }
}
