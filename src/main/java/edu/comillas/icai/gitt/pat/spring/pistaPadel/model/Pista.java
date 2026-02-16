package edu.comillas.icai.gitt.pat.spring.pistaPadel.model;

import java.time.LocalDateTime;

public record Pista(
        Long idPista,
        String nombre,
        String ubicacion,
        Double precioHora,
        boolean activa,
        LocalDateTime fechaAlta
) {}