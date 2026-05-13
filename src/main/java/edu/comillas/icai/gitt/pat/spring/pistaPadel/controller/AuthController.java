package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Token;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.TokenRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.UserRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.util.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/pistaPadel/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    Hashing hashing;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario register(@RequestBody Usuario usuario) {
        try {
            usuario.setPassword(hashing.hash(usuario.getPassword()));
            usuario.setRol("USER");
            usuario.setActivo(true);
            usuario.setFechaRegistro(LocalDateTime.now());
            return userRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email duplicado", e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody Usuario datos) {
        Usuario usuario = userRepository.findByEmail(datos.getEmail()).orElse(null);

        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (!hashing.compare(usuario.getPassword(), datos.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Token token = tokenRepository.findByUsuario(usuario);

        if (token == null) {
            token = new Token();
            token.usuario = usuario;
            tokenRepository.save(token);
        }

        ResponseCookie cookie = ResponseCookie
                .from("session", token.id)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> logout(@CookieValue(value = "session", required = true) String session) {
        if (!tokenRepository.existsById(session)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        tokenRepository.deleteById(session);

        ResponseCookie cookie = ResponseCookie
                .from("session")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/me")
    public Usuario me(@CookieValue(value = "session", required = true) String session) {
        Token token = tokenRepository.findById(session).orElse(null);

        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return token.usuario;
    }
}