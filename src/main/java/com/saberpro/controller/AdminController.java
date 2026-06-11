package com.saberpro.controller;

import com.saberpro.model.*;
import com.saberpro.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private FacultadRepository facultadRepository;
    @Autowired
    private DocenteRepository docenteRepository;
    @Autowired
    private DirectorRepository directorRepository;
    @Autowired
    private ResolucionRepository resolucionRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @ModelAttribute("pageTitle")
    public String getPageTitle() {
        return "Administrador";
    }

    // ==================== DASHBOARD ====================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalFacultades", facultadRepository.count());
        model.addAttribute("totalDocentes", docenteRepository.count());
        model.addAttribute("totalDirectores", directorRepository.count());
        model.addAttribute("totalResoluciones", resolucionRepository.count());
        return "admin/dashboard";
    }

    // ==================== FACULTADES ====================
    @GetMapping("/facultades")
    public String listarFacultades(Model model) {
        model.addAttribute("facultades", facultadRepository.findAll());
        model.addAttribute("facultad", new Facultad());
        return "admin/facultades";
    }

    @PostMapping("/facultades/guardar")
    public String guardarFacultad(@Valid @ModelAttribute Facultad facultad, BindingResult result,
                                  RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("facultades", facultadRepository.findAll());
            return "admin/facultades";
        }
        if (facultad.getId() == null && facultadRepository.existsByCodigo(facultad.getCodigo())) {
            result.rejectValue("codigo", "error.facultad", "Ya existe una facultad con ese codigo");
            model.addAttribute("facultades", facultadRepository.findAll());
            return "admin/facultades";
        }
        facultadRepository.save(facultad);
        redirect.addFlashAttribute("success", "Facultad guardada exitosamente");
        return "redirect:/admin/facultades";
    }

    @GetMapping("/facultades/editar/{id}")
    public String editarFacultad(@PathVariable Long id, Model model) {
        Facultad facultad = facultadRepository.findById(id).orElseThrow();
        model.addAttribute("facultad", facultad);
        model.addAttribute("facultades", facultadRepository.findAll());
        return "admin/facultades";
    }

    @GetMapping("/facultades/eliminar/{id}")
    public String eliminarFacultad(@PathVariable Long id, RedirectAttributes redirect) {
        Facultad facultad = facultadRepository.findById(id).orElseThrow();
        if (!facultad.getDocentes().isEmpty() || !facultad.getAlumnos().isEmpty()) {
            redirect.addFlashAttribute("error", "No se puede eliminar la facultad porque tiene dependencias activas");
            return "redirect:/admin/facultades";
        }
        // Desvincular usuarios asociados a esta facultad
        List<Usuario> usuarios = usuarioRepository.findByFacultadId(id);
        for (Usuario u : usuarios) {
            u.setFacultad(null);
            usuarioRepository.save(u);
        }
        facultadRepository.deleteById(id);
        redirect.addFlashAttribute("success", "Facultad eliminada exitosamente");
        return "redirect:/admin/facultades";
    }

    // ==================== DOCENTES ====================
    @GetMapping("/docentes")
    public String listarDocentes(@RequestParam(required = false) Long facultadId, Model model) {
        List<Docente> docentes = facultadId != null ? docenteRepository.findByFacultadId(facultadId) : docenteRepository.findAll();
        model.addAttribute("docentes", docentes);
        model.addAttribute("facultades", facultadRepository.findAll());
        model.addAttribute("docente", new Docente());
        model.addAttribute("facultadFiltro", facultadId);
        return "admin/docentes";
    }

    @PostMapping("/docentes/guardar")
    public String guardarDocente(@Valid @ModelAttribute Docente docente, BindingResult result,
                                 RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("docentes", docenteRepository.findAll());
            model.addAttribute("facultades", facultadRepository.findAll());
            return "admin/docentes";
        }
        if (docente.getId() == null && docenteRepository.existsByCedula(docente.getCedula())) {
            result.rejectValue("cedula", "error.docente", "Ya existe un docente con esa cedula");
            model.addAttribute("docentes", docenteRepository.findAll());
            model.addAttribute("facultades", facultadRepository.findAll());
            return "admin/docentes";
        }
        docenteRepository.save(docente);

        // Crear usuario para el docente
        if (docente.getId() != null && !usuarioRepository.existsByCodigo(docente.getCedula())) {
            Usuario usuario = new Usuario();
            usuario.setCodigo(docente.getCedula());
            String password = docente.getNombre().split(" ")[0].toLowerCase() + docente.getCedula();
            usuario.setPassword(password);
            usuario.setNombre(docente.getNombre());
            usuario.setCedula(docente.getCedula());
            usuario.setCorreo(docente.getCorreo());
            usuario.setRol("DOCENTE");
            usuario.setFacultad(docente.getFacultad());
            usuarioRepository.save(usuario);
        }

        redirect.addFlashAttribute("success", "Docente guardado exitosamente");
        return "redirect:/admin/docentes";
    }

    @GetMapping("/docentes/editar/{id}")
    public String editarDocente(@PathVariable Long id, Model model) {
        Docente docente = docenteRepository.findById(id).orElseThrow();
        model.addAttribute("docente", docente);
        model.addAttribute("docentes", docenteRepository.findAll());
        model.addAttribute("facultades", facultadRepository.findAll());
        return "admin/docentes";
    }

    @GetMapping("/docentes/eliminar/{id}")
    public String eliminarDocente(@PathVariable Long id, RedirectAttributes redirect) {
        Docente docente = docenteRepository.findById(id).orElseThrow();
        // Eliminar el usuario de seguridad correspondiente para evitar registros huerfanos
        usuarioRepository.findByCodigo(docente.getCedula())
            .or(() -> usuarioRepository.findByCedula(docente.getCedula()))
            .ifPresent(u -> usuarioRepository.delete(u));
        docenteRepository.delete(docente);
        redirect.addFlashAttribute("success", "Docente eliminado exitosamente");
        return "redirect:/admin/docentes";
    }

    // ==================== DIRECTORES ====================
    @GetMapping("/directores")
    public String listarDirectores(Model model) {
        model.addAttribute("directores", directorRepository.findAll());
        model.addAttribute("facultades", facultadRepository.findAll());
        model.addAttribute("director", new Director());
        return "admin/directores";
    }

    @PostMapping("/directores/guardar")
    public String guardarDirector(@Valid @ModelAttribute Director director, BindingResult result,
                                  RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("directores", directorRepository.findAll());
            model.addAttribute("facultades", facultadRepository.findAll());
            return "admin/directores";
        }
        if (director.getId() == null && directorRepository.existsByFacultadId(director.getFacultad().getId())) {
            result.rejectValue("facultad", "error.director", "Esta facultad ya tiene un director asignado");
            model.addAttribute("directores", directorRepository.findAll());
            model.addAttribute("facultades", facultadRepository.findAll());
            return "admin/directores";
        }
        directorRepository.save(director);
        redirect.addFlashAttribute("success", "Director guardado exitosamente");
        return "redirect:/admin/directores";
    }

    @GetMapping("/directores/editar/{id}")
    public String editarDirector(@PathVariable Long id, Model model) {
        Director director = directorRepository.findById(id).orElseThrow();
        model.addAttribute("director", director);
        model.addAttribute("directores", directorRepository.findAll());
        model.addAttribute("facultades", facultadRepository.findAll());
        return "admin/directores";
    }

    @GetMapping("/directores/eliminar/{id}")
    public String eliminarDirector(@PathVariable Long id, RedirectAttributes redirect) {
        directorRepository.deleteById(id);
        redirect.addFlashAttribute("success", "Director eliminado exitosamente");
        return "redirect:/admin/directores";
    }

    // ==================== RESOLUCIONES ====================
    @GetMapping("/resoluciones")
    public String listarResoluciones(@RequestParam(required = false) Long facultadId, Model model) {
        List<Resolucion> resoluciones = facultadId != null ?
                resolucionRepository.findByFacultadId(facultadId) : resolucionRepository.findAll();
        model.addAttribute("resoluciones", resoluciones);
        model.addAttribute("facultades", facultadRepository.findAll());
        model.addAttribute("resolucion", new Resolucion());
        model.addAttribute("facultadFiltro", facultadId);
        return "admin/resoluciones";
    }

    @PostMapping("/resoluciones/guardar")
    public String guardarResolucion(@Valid @ModelAttribute Resolucion resolucion, BindingResult result,
                                    RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("resoluciones", resolucionRepository.findAll());
            model.addAttribute("facultades", facultadRepository.findAll());
            return "admin/resoluciones";
        }
        resolucionRepository.save(resolucion);
        redirect.addFlashAttribute("success", "Resolucion guardada exitosamente");
        return "redirect:/admin/resoluciones";
    }

    @GetMapping("/resoluciones/editar/{id}")
    public String editarResolucion(@PathVariable Long id, Model model) {
        Resolucion resolucion = resolucionRepository.findById(id).orElseThrow();
        model.addAttribute("resolucion", resolucion);
        model.addAttribute("resoluciones", resolucionRepository.findAll());
        model.addAttribute("facultades", facultadRepository.findAll());
        return "admin/resoluciones";
    }

    @GetMapping("/resoluciones/eliminar/{id}")
    public String eliminarResolucion(@PathVariable Long id, RedirectAttributes redirect) {
        resolucionRepository.deleteById(id);
        redirect.addFlashAttribute("success", "Resolucion eliminada exitosamente");
        return "redirect:/admin/resoluciones";
    }
}
