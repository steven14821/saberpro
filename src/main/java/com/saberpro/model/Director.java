package com.saberpro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "directores")
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "La cedula es obligatoria")
    @Column(nullable = false, unique = true)
    private String cedula;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facultad_id", nullable = false, unique = true)
    private Facultad facultad;

    public Director() {
    }

    public Director(Long id, String nombre, String cedula, Facultad facultad) {
        this.id = id;
        this.nombre = nombre;
        this.cedula = cedula;
        this.facultad = facultad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public Facultad getFacultad() {
        return facultad;
    }

    public void setFacultad(Facultad facultad) {
        this.facultad = facultad;
    }
}
