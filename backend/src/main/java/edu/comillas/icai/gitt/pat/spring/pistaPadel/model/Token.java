package edu.comillas.icai.gitt.pat.spring.pistaPadel.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Token {

    @Id
    public String id = UUID.randomUUID().toString();

    @ManyToOne
    public Usuario usuario;
}