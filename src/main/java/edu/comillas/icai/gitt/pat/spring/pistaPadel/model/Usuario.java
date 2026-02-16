package edu.comillas.icai.gitt.pat.spring.pistaPadel.model;

import java.time.LocalDateTime;

public record Usuario(
        Long idUsuario,
        String nombre,
        String apellidos,
        String email,
        String password,
        String telefono,
        String rol,             // USER o ADMIN
        LocalDateTime fechaRegistro,
        boolean activo
) {}