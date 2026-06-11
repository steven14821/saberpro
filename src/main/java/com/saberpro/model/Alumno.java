package com.saberpro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alumnos")
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "La cedula es obligatoria")
    @Column(nullable = false, unique = true)
    private String cedula;

    @NotBlank(message = "El codigo es obligatorio")
    @Column(nullable = false, unique = true)
    private String codigo;

    @NotBlank(message = "El programa es obligatorio")
    @Column(nullable = false)
    private String programa;

    @Min(value = 1, message = "El semestre debe ser minimo 1")
    @Max(value = 12, message = "El semestre debe ser maximo 12")
    @Column(nullable = false)
    private Integer semestre;

    @Email(message = "El correo debe tener un formato valido")
    @NotBlank(message = "El correo es obligatorio")
    @Column(nullable = false)
    private String correo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facultad_id", nullable = false)
    private Facultad facultad;

    @Column(nullable = false)
    private String estado = "ACTIVO";

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pago> pagos = new ArrayList<>();

    @OneToOne(mappedBy = "alumno", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Nota nota;

    public Alumno() {
    }

    public Alumno(Long id, String nombre, String cedula, String codigo, String programa, Integer semestre, String correo, Facultad facultad, String estado, List<Pago> pagos, Nota nota) {
        this.id = id;
        this.nombre = nombre;
        this.cedula = cedula;
        this.codigo = codigo;
        this.programa = programa;
        this.semestre = semestre;
        this.correo = correo;
        this.facultad = facultad;
        this.estado = estado;
        this.pagos = pagos;
        this.nota = nota;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getPrograma() {
        return programa;
    }

    public void setPrograma(String programa) {
        this.programa = programa;
    }

    public Integer getSemestre() {
        return semestre;
    }

    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Facultad getFacultad() {
        return facultad;
    }

    public void setFacultad(Facultad facultad) {
        this.facultad = facultad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public Nota getNota() {
        return nota;
    }

    public void setNota(Nota nota) {
        this.nota = nota;
    }
}
