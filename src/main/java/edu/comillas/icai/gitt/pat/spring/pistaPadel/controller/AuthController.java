package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/pistaPadel/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        logger.info("Intentando registrar usuario: {}", usuario.getEmail());

        if (usuario.getNombre() == null || usuario.getNombre().isBlank() ||
                usuario.getApellidos() == null || usuario.getApellidos().isBlank() ||
                usuario.getEmail() == null || usuario.getEmail().isBlank() ||
                usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            logger.error("Registro fallido: faltan datos obligatorios");
            return ResponseEntity.badRequest().body("Faltan datos obligatorios");
        }

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            logger.error("Registro fallido: el email {} ya existe", usuario.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya existe");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
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

        if (usuario.getEmail() == null || usuario.getEmail().isBlank() ||
                usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Email y password son obligatorios");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            usuario.getEmail(),
                            usuario.getPassword()
                    )
            );

            logger.info("Login correcto: {}", authentication.getName());

            return ResponseEntity.ok(Map.of(
                    "message", "Login correcto",
                    "email", authentication.getName()
            ));

        } catch (BadCredentialsException e) {
            logger.error("Credenciales incorrectas para: {}", usuario.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        logger.info("Logout realizado");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado"));
    }



}