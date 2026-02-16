package edu.comillas.icai.gitt.pat.spring.pistaPadel.repository;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Usuario, Long> {
    // Buscamos por email porque la guía dice que debe ser único
    Optional<Usuario> findByEmail(String email);
}