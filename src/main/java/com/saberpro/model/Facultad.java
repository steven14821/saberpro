package com.saberpro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facultades")
public class Facultad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la facultad es obligatorio")
    @Column(nullable = false, unique = true)
    private String nombre;

    @NotBlank(message = "El codigo de la facultad es obligatorio")
    @Column(nullable = false, unique = true)
    private String codigo;

    @OneToMany(mappedBy = "facultad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Docente> docentes = new ArrayList<>();

    @OneToMany(mappedBy = "facultad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Director> directores = new ArrayList<>();

    @OneToMany(mappedBy = "facultad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Alumno> alumnos = new ArrayList<>();

    @OneToMany(mappedBy = "facultad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Resolucion> resoluciones = new ArrayList<>();

    public Facultad() {
    }

    public Facultad(Long id, String nombre, String codigo, List<Docente> docentes, List<Director> directores, List<Alumno> alumnos, List<Resolucion> resoluciones) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.docentes = docentes;
        this.directores = directores;
        this.alumnos = alumnos;
        this.resoluciones = resoluciones;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<Docente> getDocentes() {
        return docentes;
    }

    public void setDocentes(List<Docente> docentes) {
        this.docentes = docentes;
    }

    public List<Director> getDirectores() {
        return directores;
    }

    public void setDirectores(List<Director> directores) {
        this.directores = directores;
    }

    public List<Alumno> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(List<Alumno> alumnos) {
        this.alumnos = alumnos;
    }

    public List<Resolucion> getResoluciones() {
        return resoluciones;
    }

    public void setResoluciones(List<Resolucion> resoluciones) {
        this.resoluciones = resoluciones;
    }
}
