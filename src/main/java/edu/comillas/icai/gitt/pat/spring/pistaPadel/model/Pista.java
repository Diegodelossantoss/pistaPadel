package edu.comillas.icai.gitt.pat.spring.pistaPadel.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pistas")
public class Pista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPista;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private String ubicacion;

    @Column(nullable = false)
    private Double precioHora;

    @Column(nullable = false)
    private boolean activa;

    @Column(nullable = false)
    private LocalDateTime fechaAlta;

    public Pista() {
        // obligatorio para JPA
    }

    public Pista(Long idPista, String nombre, String ubicacion,
                 Double precioHora, boolean activa, LocalDateTime fechaAlta) {
        this.idPista = idPista;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.precioHora = precioHora;
        this.activa = activa;
        this.fechaAlta = fechaAlta;
    }

    public Long getIdPista() { return idPista; }
    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }
    public Double getPrecioHora() { return precioHora; }
    public boolean isActiva() { return activa; }
    public LocalDateTime getFechaAlta() { return fechaAlta; }

    public void setIdPista(Long idPista) { this.idPista = idPista; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public void setPrecioHora(Double precioHora) { this.precioHora = precioHora; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public void setFechaAlta(LocalDateTime fechaAlta) { this.fechaAlta = fechaAlta; }
}
