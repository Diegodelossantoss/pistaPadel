package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Token;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.TokenRepository;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pistaPadel/users")
public class UserController {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public UserController(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    private Usuario usuarioLogueado(String session) {
        Token token = tokenRepository.findById(session).orElse(null);
        if (token == null) return null;
        return token.usuario;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(@CookieValue(value = "session", required = false) String session) {
        Usuario logueado = usuarioLogueado(session);

        if (logueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        if (!"ADMIN".equals(logueado.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado");
        }

        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId,
                                         @CookieValue(value = "session", required = false) String session) {
        Usuario logueado = usuarioLogueado(session);

        if (logueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        if (!"ADMIN".equals(logueado.getRol()) && !logueado.getIdUsuario().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso");
        }

        Usuario usuario = userRepository.findById(userId).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        return ResponseEntity.ok(usuario);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> patchUser(@PathVariable Long userId,
                                       @RequestBody Map<String, Object> updates,
                                       @CookieValue(value = "session", required = false) String session) {
        Usuario logueado = usuarioLogueado(session);

        if (logueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        if (!"ADMIN".equals(logueado.getRol()) && !logueado.getIdUsuario().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso");
        }

        Usuario usuario = userRepository.findById(userId).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        if (updates.containsKey("nombre")) usuario.setNombre((String) updates.get("nombre"));
        if (updates.containsKey("telefono")) usuario.setTelefono((String) updates.get("telefono"));

        if (updates.containsKey("email")) {
            String email = (String) updates.get("email");

            boolean existe = userRepository.findByEmail(email)
                    .filter(other -> !other.getIdUsuario().equals(userId))
                    .isPresent();

            if (existe) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email duplicado");
            }

            usuario.setEmail(email);
        }

        return ResponseEntity.ok(userRepository.save(usuario));
    }
}