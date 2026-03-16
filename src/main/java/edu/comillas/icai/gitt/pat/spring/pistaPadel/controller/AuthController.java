package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/pistaPadel/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository usuarioRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        logger.info("Intentando registrar usuario: {}", usuario.getEmail());

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            logger.error("Registro fallido: el email {} ya existe", usuario.getEmail());

            return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya existe");
        }

        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setRol("USER");
        usuario.setActivo(true);

        Usuario guardado = usuarioRepository.save(usuario);
        logger.info("Usuario registrado con éxito: {}", guardado.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        logger.info("Intento de login para: {}", usuario.getEmail());

        return usuarioRepository.findByEmail(usuario.getEmail())
                .map(u -> {
                    if (u.getPassword().equals(usuario.getPassword())) {
                        logger.info("Login correcto: {}", u.getEmail());
                        return ResponseEntity.ok("Login correcto");
                    } else {
                        logger.error("Password incorrecto para: {}", u.getEmail());
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
                    }
                })
                .orElseGet(() -> {
                    logger.error("Usuario no encontrado: {}", usuario.getEmail());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
                });
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        logger.info("Logout realizado");
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("No autenticado");
        }

        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .<ResponseEntity<?>>map(usuario -> ResponseEntity.ok(usuario))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado"));
    }



}