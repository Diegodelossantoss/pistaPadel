package edu.comillas.icai.gitt.pat.spring.pistaPadel.repository;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Pista;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PistaRepository extends JpaRepository<Pista, Long> {
    // Buscamos por nombre porque la guía dice que el nombre de la pista es único
    Optional<Pista> findByNombre(String nombre);
}