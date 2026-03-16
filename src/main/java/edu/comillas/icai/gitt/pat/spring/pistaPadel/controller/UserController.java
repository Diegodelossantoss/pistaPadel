package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.Map;

@RestController
@RequestMapping("/pistaPadel/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        logger.info("ADMIN solicitando lista de usuarios");
        return ResponseEntity.ok(userRepository.findAll());
    }


    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId, Authentication authentication) {
        logger.debug("Buscando usuario ID: {}", userId);


        Usuario logueado = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (logueado == null || (!logueado.getRol().equals("ADMIN") && !logueado.getIdUsuario().equals(userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para ver este perfil");
        }

        return userRepository.findById(userId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.error("Usuario {} no encontrado", userId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
                });
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> patchUser(@PathVariable Long userId, @RequestBody Map<String, Object> updates, Authentication authentication) { // AÑADIDO: Authentication
        logger.info("Actualizando usuario ID: {}", userId);

        Usuario logueado = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (logueado == null || (!logueado.getRol().equals("ADMIN") && !logueado.getIdUsuario().equals(userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para modificar este perfil");
        }

        Usuario u = userRepository.findById(userId).orElse(null);
        if (u == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        if (updates.containsKey("nombre")) u.setNombre((String) updates.get("nombre"));
        if (updates.containsKey("telefono")) u.setTelefono((String) updates.get("telefono"));

        if (updates.containsKey("email")) {
            String mail = (String) updates.get("email");
            boolean existe = userRepository.findByEmail(mail)
                    .filter(other -> !other.getIdUsuario().equals(userId)).isPresent();
            if (existe) {
                logger.error("Conflicto: El email {} ya existe", mail);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email duplicado");
            }
            u.setEmail(mail);
        }
        return ResponseEntity.ok(userRepository.save(u));
    }
}

