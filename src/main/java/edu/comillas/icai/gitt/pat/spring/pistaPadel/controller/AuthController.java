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
}
