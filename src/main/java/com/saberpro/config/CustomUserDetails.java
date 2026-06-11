package com.saberpro.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {
    private final String nombre;
    private final String realRol;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String nombre, String realRol) {
        super(username, password, authorities);
        this.nombre = nombre;
        this.realRol = realRol;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRealRol() {
        if (realRol == null) return "";
        switch (realRol.toUpperCase()) {
            case "DOCENTE": return "Docente";
            case "ESTUDIANTE": return "Estudiante";
            case "COORDINADOR": return "Coordinador";
            case "ADMIN": return "Administrador";
            default: return realRol;
        }
    }
}
