package edu.comillas.icai.gitt.pat.spring.pistaPadel.config;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Pista;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.PistaRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.UserRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.util.Hashing;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PistaRepository pistaRepository;
    private final UserRepository userRepository;
    private final Hashing hashing;

    public DataInitializer(PistaRepository pistaRepository,
                           UserRepository userRepository,
                           Hashing hashing) {
        this.pistaRepository = pistaRepository;
        this.userRepository = userRepository;
        this.hashing = hashing;
    }

    @Override
    public void run(String... args) {
        crearAdmin();
        crearPistas();
    }

    private void crearAdmin() {
        if (userRepository.findByEmail("adminapp@test.com").isPresent()) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setNombre("Admin");
        admin.setApellidos("Padel");
        admin.setEmail("adminapp@test.com");
        admin.setPassword(hashing.hash("1234"));
        admin.setTelefono("666666666");
        admin.setRol("ADMIN");
        admin.setActivo(true);
        admin.setFechaRegistro(LocalDateTime.now());

        userRepository.save(admin);
    }

    private void crearPistas() {
        if (pistaRepository.count() > 0) {
            return;
        }

        Pista p1 = new Pista();
        p1.setNombre("Pista 1");
        p1.setUbicacion("Exterior");
        p1.setPrecioHora(20.0);
        p1.setActiva(true);
        p1.setFechaAlta(LocalDateTime.now());
        pistaRepository.save(p1);

        Pista p2 = new Pista();
        p2.setNombre("Pista 2");
        p2.setUbicacion("Interior");
        p2.setPrecioHora(25.0);
        p2.setActiva(true);
        p2.setFechaAlta(LocalDateTime.now());
        pistaRepository.save(p2);

        Pista p3 = new Pista();
        p3.setNombre("Pista 3");
        p3.setUbicacion("Exterior cubierta");
        p3.setPrecioHora(22.0);
        p3.setActiva(true);
        p3.setFechaAlta(LocalDateTime.now());
        pistaRepository.save(p3);
    }
}