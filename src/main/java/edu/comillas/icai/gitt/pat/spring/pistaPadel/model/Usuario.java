package edu.comillas.icai.gitt.pat.spring.pistaPadel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String telefono;

    @Column(nullable = false)
    private String rol; // USER o ADMIN

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(nullable = false)
    private boolean activo;

    public Usuario() {
        // constructor vacío obligatorio para JPA
    }

    public Usuario(Long idUsuario, String nombre, String apellidos, String email, String password,
                   String telefono, String rol, LocalDateTime fechaRegistro, boolean activo) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
    }

    public Long getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getTelefono() { return telefono; }
    public String getRol() { return rol; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public boolean isActivo() { return activo; }

    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setRol(String rol) { this.rol = rol; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
