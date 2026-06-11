package com.saberpro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El codigo es obligatorio")
    @Column(nullable = false, unique = true)
    private String codigo;

    @NotBlank(message = "La contrasena es obligatoria")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "La cedula es obligatoria")
    @Column(nullable = false, unique = true)
    private String cedula;

    @Email(message = "El correo debe tener un formato valido")
    @Column(unique = true)
    private String correo;

    @NotBlank(message = "El rol es obligatorio")
    @Column(nullable = false)
    private String rol;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facultad_id")
    private Facultad facultad;

    public Usuario() {
    }

    public Usuario(Long id, String codigo, String password, String nombre, String cedula, String correo, String rol, Facultad facultad) {
        this.id = id;
        this.codigo = codigo;
        this.password = password;
        this.nombre = nombre;
        this.cedula = cedula;
        this.correo = correo;
        this.rol = rol;
        this.facultad = facultad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Facultad getFacultad() {
        return facultad;
    }

    public void setFacultad(Facultad facultad) {
        this.facultad = facultad;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(rol);
    }

    public boolean isCoordinador() {
        return "COORDINADOR".equals(rol);
    }

    public boolean isDocente() {
        return "DOCENTE".equals(rol);
    }

    public boolean isEstudiante() {
        return "ESTUDIANTE".equals(rol);
    }
}
