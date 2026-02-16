package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pistaPadel/auth")
public class AuthController {

    @Autowired
    private UserRepository usuarioRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        // Regla: El email debe ser único [cite: 224]
        if (usuarioRepository.findByEmail(usuario.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya existe"); // Error 409
        }

        // Guardamos el usuario (en una app real aquí cifraríamos la password [cite: 208])
        usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario); // 201 Creado [cite: 152]
    }
}