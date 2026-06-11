package com.saberpro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "resoluciones")
public class Resolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El numero de resolucion es obligatorio")
    @Column(nullable = false)
    private String numero;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    @NotBlank(message = "La vigencia es obligatoria")
    @Column(nullable = false)
    private String vigencia;

    @NotBlank(message = "El programa es obligatorio")
    @Column(nullable = false)
    private String programa;

    @NotBlank(message = "La descripcion es obligatoria")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facultad_id", nullable = false)
    private Facultad facultad;

    public Resolucion() {
    }

    public Resolucion(Long id, String numero, LocalDate fecha, String vigencia, String programa, String descripcion, Facultad facultad) {
        this.id = id;
        this.numero = numero;
        this.fecha = fecha;
        this.vigencia = vigencia;
        this.programa = programa;
        this.descripcion = descripcion;
        this.facultad = facultad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public String getPrograma() {
        return programa;
    }

    public void setPrograma(String programa) {
        this.programa = programa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Facultad getFacultad() {
        return facultad;
    }

    public void setFacultad(Facultad facultad) {
        this.facultad = facultad;
    }

    public boolean isVigente() {
        if (vigencia == null || vigencia.isEmpty()) return false;
        try {
            LocalDate fechaVigencia = LocalDate.parse(vigencia);
            return !fechaVigencia.isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }
}
