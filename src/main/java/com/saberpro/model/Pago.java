package com.saberpro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreArchivo;

    @Column(nullable = false)
    private String tipoArchivo;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] archivo;

    @Column(nullable = false)
    private String estado = "PENDIENTE";

    @Column(columnDefinition = "TEXT")
    private String motivoRechazo;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;

    public Pago() {
    }

    public Pago(Long id, String nombreArchivo, String tipoArchivo, byte[] archivo, String estado, String motivoRechazo, LocalDateTime fechaRegistro, Alumno alumno) {
        this.id = id;
        this.nombreArchivo = nombreArchivo;
        this.tipoArchivo = tipoArchivo;
        this.archivo = archivo;
        this.estado = estado;
        this.motivoRechazo = motivoRechazo;
        this.fechaRegistro = fechaRegistro;
        this.alumno = alumno;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }
}
