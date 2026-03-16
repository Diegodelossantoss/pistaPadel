package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/pistaPadel/auth")
public class AuthController {

    @Autowired
    private UserRepository usuarioRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya existe"); // 409
        }

        if (usuario.getFechaRegistro() == null) usuario.setFechaRegistro(LocalDateTime.now());
        if (usuario.getRol() == null) usuario.setRol("USER");
        usuario.setActivo(true);

        usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario); // 201
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {

        if (usuario.getEmail() == null || usuario.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Falta email o password"); // 400
        }

        return usuarioRepository.findByEmail(usuario.getEmail())
                .map(u -> {
                    if (u.getPassword().equals(usuario.getPassword())) {
                        return ResponseEntity.ok("Login correcto"); // 200
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Credenciales incorrectas"); // 401
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Credenciales incorrectas")); // 401
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.noContent().build(); // 204
    }


    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestParam String email) {

        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falta el email"); // 400
        }

        return usuarioRepository.findByEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok) // 200
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado")); // 404
    }




}
