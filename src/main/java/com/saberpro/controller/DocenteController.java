package com.saberpro.controller;

import com.saberpro.model.Alumno;
import com.saberpro.model.Facultad;
import com.saberpro.model.Nota;
import com.saberpro.model.Usuario;
import com.saberpro.repository.AlumnoRepository;
import com.saberpro.repository.FacultadRepository;
import com.saberpro.repository.NotaRepository;
import com.saberpro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/docente")
public class DocenteController {

    @Autowired
    private AlumnoRepository alumnoRepository;
    @Autowired
    private NotaRepository notaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private FacultadRepository facultadRepository;

    @ModelAttribute("pageTitle")
    public String getPageTitle() {
        return "Docente";
    }

    private Long getFacultadIdFromAuth(Authentication auth) {
        String codigo = auth.getName();
        Usuario usuario = usuarioRepository.findByCodigo(codigo).orElseThrow();
        return usuario.getFacultad() != null ? usuario.getFacultad().getId() : null;
    }

    // ==================== DASHBOARD ====================
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Long facultadId = getFacultadIdFromAuth(auth);
        if (facultadId != null) {
            List<Alumno> alumnos = alumnoRepository.findByFacultadId(facultadId);
            List<Alumno> sortedAlumnos = new java.util.ArrayList<>(alumnos);
            sortedAlumnos.sort((a1, a2) -> {
                int score1 = (a1.getNota() != null && a1.getNota().isPublicado()) ? a1.getNota().getPuntajeGlobal() : 0;
                int score2 = (a2.getNota() != null && a2.getNota().isPublicado()) ? a2.getNota().getPuntajeGlobal() : 0;
                return Integer.compare(score2, score1);
            });
            model.addAttribute("totalAlumnos", alumnos.size());
            model.addAttribute("alumnosFacultad", sortedAlumnos);
        }
        return "docente/dashboard";
    }

    // ==================== CONSULTA ALUMNOS ====================
    @GetMapping("/alumnos")
    public String listarAlumnos(Authentication auth, Model model) {
        Long facultadId = getFacultadIdFromAuth(auth);
        if (facultadId != null) {
            List<Alumno> alumnos = alumnoRepository.findByFacultadId(facultadId);
            List<Alumno> sortedAlumnos = new java.util.ArrayList<>(alumnos);
            sortedAlumnos.sort((a1, a2) -> {
                int score1 = (a1.getNota() != null && a1.getNota().isPublicado()) ? a1.getNota().getPuntajeGlobal() : 0;
                int score2 = (a2.getNota() != null && a2.getNota().isPublicado()) ? a2.getNota().getPuntajeGlobal() : 0;
                return Integer.compare(score2, score1);
            });
            model.addAttribute("alumnos", sortedAlumnos);
        }
        return "docente/alumnos";
    }

    // ==================== BUSCAR ALUMNO POR CEDULA ====================
    @GetMapping("/alumnos/buscar")
    public String buscarAlumno(@RequestParam String cedula, Authentication auth,
                               Model model, RedirectAttributes redirect) {
        if (cedula == null || cedula.trim().isEmpty()) {
            redirect.addFlashAttribute("error", "La cedula es obligatoria");
            return "redirect:/docente/alumnos";
        }

        Alumno alumno = alumnoRepository.findByCedula(cedula).orElse(null);
        if (alumno == null) {
            redirect.addFlashAttribute("error", "Alumno no encontrado");
            return "redirect:/docente/alumnos";
        }

        // Verificar que el alumno pertenezca a la facultad del docente
        Long facultadId = getFacultadIdFromAuth(auth);
        if (facultadId == null || !alumno.getFacultad().getId().equals(facultadId)) {
            redirect.addFlashAttribute("error", "No tiene acceso a este alumno");
            return "redirect:/docente/alumnos";
        }

        Nota nota = notaRepository.findByAlumnoId(alumno.getId()).orElse(null);
        model.addAttribute("alumno", alumno);
        model.addAttribute("nota", nota);
        return "docente/alumno-detalle";
    }
}
