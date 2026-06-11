package com.saberpro.controller;

import com.saberpro.model.Usuario;
import com.saberpro.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // DTO liviano para el panel de acceso rapido
    public static class UsuarioQuick {
        public String correo;
        public String codigo;
        public String nombre;
        public String inicial;
        public String password; // contrasena de muestra (primer nombre + cedula)

        public UsuarioQuick(String correo, String codigo, String nombre, String cedula) {
            this.correo  = (correo  != null && !correo.isBlank())  ? correo  : "";
            this.codigo  = (codigo  != null && !codigo.isBlank())  ? codigo  : "";
            this.nombre  = (nombre  != null && !nombre.isBlank())  ? nombre  : codigo;
            this.inicial = this.nombre.substring(0, 1).toUpperCase();
            // Para usuarios por defecto, la contrasena es especifica (no es nombre+cedula)
            if ("admin".equals(codigo)) {
                this.password = "admin123";
            } else if ("coord".equals(codigo)) {
                this.password = "coord123";
            } else if ("docente".equals(codigo)) {
                this.password = "docente123";
            } else if ("estudiante".equals(codigo)) {
                this.password = "estudiante123";
            } else {
                // Para usuarios reales importados/creados: primer nombre en minuscula + cedula
                String primerNombre = this.nombre.trim().split("\\s+")[0].toLowerCase();
                this.password = (cedula != null && !cedula.isBlank()) ? primerNombre + cedula : primerNombre + "123";
            }
        }

        public String getCorreo()   { return correo; }
        public String getCodigo()   { return codigo; }
        public String getNombre()   { return nombre; }
        public String getInicial()  { return inicial; }
        public String getPassword() { return password; }
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error",  required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error  != null) model.addAttribute("error",  "Correo o contrasena incorrectos");
        if (logout != null) model.addAttribute("logout", "Has cerrado sesion exitosamente");

        Map<String, List<UsuarioQuick>> porRol = new LinkedHashMap<>();
        porRol.put("ADMIN",       new ArrayList<>());
        porRol.put("COORDINADOR", new ArrayList<>());
        porRol.put("DOCENTE",     new ArrayList<>());
        porRol.put("ESTUDIANTE",  new ArrayList<>());

        try {
            List<Usuario> todos = usuarioRepository.findAll();
            for (Usuario u : todos) {
                String rol = u.getRol();
                if (porRol.containsKey(rol)) {
                    porRol.get(rol).add(
                        new UsuarioQuick(u.getCorreo(), u.getCodigo(), u.getNombre(), u.getCedula())
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Login panel: BD no disponible -> " + e.getMessage());
            // Fallback con usuarios semilla
            porRol.get("ADMIN")       .add(new UsuarioQuick("admin@uts.edu.co",      "admin",      "Administrador Principal", "00000001"));
            porRol.get("COORDINADOR") .add(new UsuarioQuick("coord@uts.edu.co",      "coord",      "Coordinador Academico",   "00000002"));
            porRol.get("DOCENTE")     .add(new UsuarioQuick("docente@uts.edu.co",    "docente",    "Docente Prueba",          "00000003"));
            porRol.get("ESTUDIANTE")  .add(new UsuarioQuick("estudiante@uts.edu.co", "estudiante", "Estudiante Prueba",       "00000004"));
        }

        model.addAttribute("admins",        porRol.get("ADMIN"));
        model.addAttribute("coordinadores", porRol.get("COORDINADOR"));
        model.addAttribute("docentes",      porRol.get("DOCENTE"));
        model.addAttribute("estudiantes",   porRol.get("ESTUDIANTE"));

        return "login/login";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/redirect";
    }

    @GetMapping("/redirect")
    public String redirectByRole(Authentication authentication) {
        if (authentication == null) return "redirect:/login";
        for (GrantedAuthority a : authentication.getAuthorities()) {
            switch (a.getAuthority()) {
                case "ROLE_ADMIN"       -> { return "redirect:/admin/dashboard"; }
                case "ROLE_COORDINADOR" -> { return "redirect:/coordinador/dashboard"; }
                case "ROLE_DOCENTE"     -> { return "redirect:/docente/dashboard"; }
                case "ROLE_ESTUDIANTE"  -> { return "redirect:/estudiante/dashboard"; }
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) new SecurityContextLogoutHandler().logout(request, response, authentication);
        return "redirect:/login?logout=true";
    }
}
