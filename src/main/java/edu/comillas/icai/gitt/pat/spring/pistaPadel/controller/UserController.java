package edu.comillas.icai.gitt.pat.spring.pistaPadel.controller;

import edu.comillas.icai.gitt.pat.spring.pistaPadel.model.Usuario;
import edu.comillas.icai.gitt.pat.spring.pistaPadel.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pistaPadel/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET /pistaPadel/users  (ADMIN)
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll()); // 200
    }

    // GET /pistaPadel/users/{userId} (ADMIN o dueño; por ahora solo ADMIN por SecurityConfig)
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .<ResponseEntity<?>>map(ResponseEntity::ok) // 200
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado")); // 404
    }

    // PATCH /pistaPadel/users/{userId}
    @PatchMapping("/{userId}")
    public ResponseEntity<?> patchUser(@PathVariable Long userId,
                                       @RequestBody Map<String, Object> updates) {

        Usuario u = userRepository.findById(userId).orElse(null);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado"); // 404
        }

        if (updates.containsKey("nombre")) {
            u.setNombre((String) updates.get("nombre"));
        }
        if (updates.containsKey("apellidos")) {
            u.setApellidos((String) updates.get("apellidos"));
        }
        if (updates.containsKey("telefono")) {
            u.setTelefono((String) updates.get("telefono"));
        }

        // Si permitís cambiar email, aquí está la prueba del 409 (email duplicado)
        if (updates.containsKey("email")) {
            String nuevoEmail = (String) updates.get("email");
            if (nuevoEmail == null || nuevoEmail.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email inválido"); // 400
            }

            boolean existe = userRepository.findByEmail(nuevoEmail)
                    .filter(other -> !other.getIdUsuario().equals(u.getIdUsuario()))
                    .isPresent();

            if (existe) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya existe"); // 409
            }
            u.setEmail(nuevoEmail);
        }

        Usuario saved = userRepository.save(u);
        return ResponseEntity.ok(saved); // 200
    }
}
