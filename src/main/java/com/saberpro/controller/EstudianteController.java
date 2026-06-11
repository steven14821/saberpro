package com.saberpro.controller;

import com.saberpro.model.*;
import com.saberpro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/estudiante")
public class EstudianteController {

    @Autowired
    private AlumnoRepository alumnoRepository;
    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private NotaRepository notaRepository;
    @Autowired
    private ResolucionRepository resolucionRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private FacultadRepository facultadRepository;

    @ModelAttribute("pageTitle")
    public String getPageTitle() {
        return "Estudiante";
    }

    private Alumno getAlumnoFromAuth(Authentication auth) {
        String codigo = auth.getName();
        return alumnoRepository.findByCodigo(codigo).orElse(null);
    }

    private Long getFacultadIdFromAuth(Authentication auth) {
        Alumno alumno = getAlumnoFromAuth(auth);
        return alumno != null && alumno.getFacultad() != null ? alumno.getFacultad().getId() : null;
    }

    // ==================== DASHBOARD ====================
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Alumno alumno = getAlumnoFromAuth(auth);
        if (alumno != null) {
            model.addAttribute("alumno", alumno);
            Optional<Pago> ultimoPago = pagoRepository.findFirstByAlumnoIdOrderByFechaRegistroDesc(alumno.getId());
            model.addAttribute("ultimoPago", ultimoPago.orElse(null));
            Optional<Nota> nota = notaRepository.findByAlumnoId(alumno.getId());
            model.addAttribute("nota", nota.orElse(null));
        }
        return "estudiante/dashboard";
    }

    // ==================== CARGUE COMPROBANTE ====================
    @GetMapping("/comprobante")
    public String verComprobante(Authentication auth, Model model) {
        Alumno alumno = getAlumnoFromAuth(auth);
        if (alumno != null) {
            Optional<Pago> ultimoPago = pagoRepository.findFirstByAlumnoIdOrderByFechaRegistroDesc(alumno.getId());
            model.addAttribute("ultimoPago", ultimoPago.orElse(null));
        }
        return "estudiante/comprobante";
    }

    @PostMapping("/comprobante/subir")
    public String subirComprobante(@RequestParam("archivo") MultipartFile archivo,
                                   Authentication auth,
                                   RedirectAttributes redirect) {
        Alumno alumno = getAlumnoFromAuth(auth);
        if (alumno == null) {
            redirect.addFlashAttribute("error", "Error al identificar el alumno");
            return "redirect:/estudiante/comprobante";
        }

        if (archivo.isEmpty()) {
            redirect.addFlashAttribute("error", "Debe seleccionar un archivo");
            return "redirect:/estudiante/comprobante";
        }

        String contentType = archivo.getContentType();
        if (contentType == null || (!contentType.equals("application/pdf") &&
                !contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            redirect.addFlashAttribute("error", "Solo se permiten archivos PDF, JPG o PNG");
            return "redirect:/estudiante/comprobante";
        }

        try {
            // Verificar si ya tiene un comprobante activo
            Optional<Pago> pagoExistente = pagoRepository.findFirstByAlumnoIdOrderByFechaRegistroDesc(alumno.getId());
            if (pagoExistente.isPresent() && "PENDIENTE".equals(pagoExistente.get().getEstado())) {
                redirect.addFlashAttribute("error", "Ya tiene un comprobante pendiente de revision");
                return "redirect:/estudiante/comprobante";
            }

            Pago pago = new Pago();
            pago.setNombreArchivo(archivo.getOriginalFilename());
            pago.setTipoArchivo(contentType);
            pago.setArchivo(archivo.getBytes());
            pago.setEstado("PENDIENTE");
            pago.setAlumno(alumno);
            pagoRepository.save(pago);

            redirect.addFlashAttribute("success", "Comprobante cargado exitosamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al cargar el archivo: " + e.getMessage());
        }
        return "redirect:/estudiante/comprobante";
    }

    @GetMapping("/comprobante/descargar/{id}")
    public ResponseEntity<byte[]> descargarComprobante(@PathVariable Long id) {
        Pago pago = pagoRepository.findById(id).orElseThrow();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(pago.getTipoArchivo()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pago.getNombreArchivo() + "\"")
                .body(pago.getArchivo());
    }

    // ==================== RESULTADOS INDIVIDUALES ====================
    @GetMapping("/resultados")
    public String verResultados(Authentication auth, Model model) {
        Alumno alumno = getAlumnoFromAuth(auth);
        if (alumno != null) {
            model.addAttribute("alumno", alumno);
            if ("ANULADO".equals(alumno.getEstado())) {
                model.addAttribute("anulado", true);
            } else {
                Optional<Nota> nota = notaRepository.findByAlumnoId(alumno.getId());
                if (nota.isPresent() && nota.get().isPublicado()) {
                    model.addAttribute("nota", nota.get());
                } else {
                    model.addAttribute("noPublicado", true);
                }
            }
        }
        return "estudiante/resultados";
    }

    // ==================== ESTADISTICAS FACULTAD ====================
    @GetMapping("/estadisticas")
    public String verEstadisticas(Authentication auth, Model model) {
        Long facultadId = getFacultadIdFromAuth(auth);
        if (facultadId != null) {
            Object[] estadisticas = notaRepository.calcularEstadisticasByFacultad(facultadId);
            if (estadisticas != null && estadisticas.length > 0 && estadisticas[0] != null) {
                model.addAttribute("promedio", estadisticas[0]);
                model.addAttribute("maximo", estadisticas[1]);
                model.addAttribute("minimo", estadisticas[2]);
            }
        }
        return "estudiante/estadisticas";
    }

    // ==================== RESOLUCIONES ====================
    @GetMapping("/resoluciones")
    public String verResoluciones(Authentication auth, Model model) {
        Long facultadId = getFacultadIdFromAuth(auth);
        if (facultadId != null) {
            String fechaActual = LocalDate.now().toString();
            List<Resolucion> vigentes = resolucionRepository.findVigentesByFacultadId(facultadId, fechaActual);
            List<Resolucion> vencidas = resolucionRepository.findVencidasByFacultadId(facultadId, fechaActual);
            model.addAttribute("vigentes", vigentes);
            model.addAttribute("vencidas", vencidas);
        }
        return "estudiante/resoluciones";
    }
}
