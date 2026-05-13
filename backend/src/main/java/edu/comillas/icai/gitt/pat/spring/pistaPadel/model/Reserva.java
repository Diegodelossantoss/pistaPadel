
package edu.comillas.icai.gitt.pat.spring.pistaPadel.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    @Column(nullable = false)
    private Long idUsuario;

    @Column(nullable = false)
    private Long idPista;

    @Column(nullable = false)
    private LocalDate fechaReserva;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private int duracionMinutos;

    @Column(nullable = false)
    private LocalTime horaFin;

    @Column(nullable = false)
    private String estado; // ACTIVA o CANCELADA

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    public Reserva() {}

    public Reserva(Long idReserva, Long idUsuario, Long idPista, LocalDate fechaReserva,
                   LocalTime horaInicio, int duracionMinutos, LocalTime horaFin,
                   String estado, LocalDateTime fechaCreacion) {
        this.idReserva = idReserva;
        this.idUsuario = idUsuario;
        this.idPista = idPista;
        this.fechaReserva = fechaReserva;
        this.horaInicio = horaInicio;
        this.duracionMinutos = duracionMinutos;
        this.horaFin = horaFin;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getIdReserva() { return idReserva; }
    public Long getIdUsuario() { return idUsuario; }
    public Long getIdPista() { return idPista; }
    public LocalDate getFechaReserva() { return fechaReserva; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public LocalTime getHoraFin() { return horaFin; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public void setIdReserva(Long idReserva) { this.idReserva = idReserva; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public void setIdPista(Long idPista) { this.idPista = idPista; }
    public void setFechaReserva(LocalDate fechaReserva) { this.fechaReserva = fechaReserva; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
