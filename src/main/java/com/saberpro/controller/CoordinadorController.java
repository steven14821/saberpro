package com.saberpro.controller;

import com.saberpro.model.*;
import com.saberpro.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

@Controller
@RequestMapping("/coordinador")
public class CoordinadorController {

    @Autowired
    private AlumnoRepository alumnoRepository;
    @Autowired
    private FacultadRepository facultadRepository;
    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private NotaRepository notaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @ModelAttribute("pageTitle")
    public String getPageTitle() {
        return "Coordinador";
    }

    // ==================== DASHBOARD ====================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalAlumnos", alumnoRepository.count());
        model.addAttribute("totalPagosPendientes", pagoRepository.findByEstado("PENDIENTE").size());
        model.addAttribute("totalNotasPublicadas", notaRepository.findByPublicado(true).size());
        model.addAttribute("totalFacultades", facultadRepository.count());
        return "coordinador/dashboard";
    }

    // ==================== ALUMNOS ====================
    @GetMapping("/alumnos")
    public String listarAlumnos(@RequestParam(required = false) Long facultadId,
                                @RequestParam(required = false) String estado,
                                Model model) {
        List<Alumno> alumnos;
        if (facultadId != null && estado != null && !estado.isEmpty()) {
            alumnos = alumnoRepository.findByFacultadIdAndEstado(facultadId, estado);
        } else if (facultadId != null) {
            alumnos = alumnoRepository.findByFacultadId(facultadId);
        } else if (estado != null && !estado.isEmpty()) {
            alumnos = alumnoRepository.findByEstado(estado);
        } else {
            alumnos = alumnoRepository.findAll();
        }
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("facultades", facultadRepository.findAll());
        model.addAttribute("alumno", new Alumno());
        model.addAttribute("facultadFiltro", facultadId);
        model.addAttribute("estadoFiltro", estado);
        return "coordinador/alumnos";
    }

    @PostMapping("/alumnos/guardar")
    public String guardarAlumno(@Valid @ModelAttribute Alumno alumno, BindingResult result,
                                RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("alumnos", alumnoRepository.findAll());
            model.addAttribute("facultades", facultadRepository.findAll());
            return "coordinador/alumnos";
        }
        if (alumno.getId() == null) {
            if (alumnoRepository.existsByCedula(alumno.getCedula())) {
                result.rejectValue("cedula", "error.alumno", "Ya existe un alumno con esa cedula");
                model.addAttribute("alumnos", alumnoRepository.findAll());
                model.addAttribute("facultades", facultadRepository.findAll());
                return "coordinador/alumnos";
            }
            if (alumnoRepository.existsByCodigo(alumno.getCodigo())) {
                result.rejectValue("codigo", "error.alumno", "Ya existe un alumno con ese codigo");
                model.addAttribute("alumnos", alumnoRepository.findAll());
                model.addAttribute("facultades", facultadRepository.findAll());
                return "coordinador/alumnos";
            }
            alumno.setEstado("PENDIENTE_PAGO");
        }
        alumnoRepository.save(alumno);

        // Crear usuario para el alumno
        if (!usuarioRepository.existsByCodigo(alumno.getCodigo())) {
            Usuario usuario = new Usuario();
            usuario.setCodigo(alumno.getCodigo());
            String password = alumno.getNombre().split(" ")[0].toLowerCase() + alumno.getCedula();
            usuario.setPassword(password);
            usuario.setNombre(alumno.getNombre());
            usuario.setCedula(alumno.getCedula());
            usuario.setCorreo(alumno.getCorreo());
            usuario.setRol("ESTUDIANTE");
            usuario.setFacultad(alumno.getFacultad());
            usuarioRepository.save(usuario);
        }

        redirect.addFlashAttribute("success", "Alumno guardado exitosamente");
        return "redirect:/coordinador/alumnos";
    }

    @GetMapping("/alumnos/editar/{id}")
    public String editarAlumno(@PathVariable Long id, Model model) {
        Alumno alumno = alumnoRepository.findById(id).orElseThrow();
        model.addAttribute("alumno", alumno);
        model.addAttribute("alumnos", alumnoRepository.findAll());
        model.addAttribute("facultades", facultadRepository.findAll());
        return "coordinador/alumnos";
    }

    @GetMapping("/alumnos/eliminar/{id}")
    public String eliminarAlumno(@PathVariable Long id, RedirectAttributes redirect) {
        Alumno alumno = alumnoRepository.findById(id).orElseThrow();
        // Eliminar el usuario de seguridad correspondiente para evitar registros huerfanos
        usuarioRepository.findByCodigo(alumno.getCodigo())
            .or(() -> usuarioRepository.findByCedula(alumno.getCedula()))
            .ifPresent(u -> usuarioRepository.delete(u));
        alumnoRepository.delete(alumno);
        redirect.addFlashAttribute("success", "Alumno eliminado exitosamente");
        return "redirect:/coordinador/alumnos";
    }

    @PostMapping("/alumnos/importar")
    public String importarAlumnos(@RequestParam("archivoExcel") MultipartFile archivo, RedirectAttributes redirect) {
        if (archivo.isEmpty()) {
            redirect.addFlashAttribute("error", "Debe seleccionar un archivo Excel");
            return "redirect:/coordinador/alumnos";
        }

        String filename = archivo.getOriginalFilename() != null ? archivo.getOriginalFilename().toLowerCase() : "";

        try (InputStream is = archivo.getInputStream()) {
            Workbook workbook;
            if (filename.endsWith(".xls")) {
                workbook = new HSSFWorkbook(is);
            } else {
                workbook = new XSSFWorkbook(is);
            }

            Sheet sheet = workbook.getSheetAt(0);
            
            // Buscar o crear la facultad
            String nombreFacultad = "FACULTAD DE CIENCIAS NATURALES E INGENIERIAS";
            Facultad facultad = facultadRepository.findAll().stream()
                    .filter(f -> f.getNombre().equalsIgnoreCase(nombreFacultad))
                    .findFirst()
                    .orElse(null);
            
            if (facultad == null) {
                facultad = new Facultad();
                facultad.setNombre(nombreFacultad);
                facultad.setCodigo("FCNI");
                facultad = facultadRepository.save(facultad);
            }

            int count = 0;
            boolean firstRow = true;

            // Detectar tipo de examen desde la primera fila de datos
            // Buscamos si hay columnas de modulos especificos de Ingenieria
            // (columnas 21-26) para determinar si es SABER_PRO
            String tipoExamen = "SABER_PRO";

            for (Row row : sheet) {
                if (firstRow) {
                    firstRow = false;
                    // Intentar detectar el tipo de examen desde el encabezado
                    for (Cell cell : row) {
                        String header = getCellValueAsString(cell).toUpperCase();
                        if (header.contains("T&T") || header.contains("TYT") || header.contains("TECNOL")) {
                            tipoExamen = "SABER_TYT";
                            break;
                        }
                    }
                    continue;
                }
                
                // Verificar celda vacia (fin de datos)
                Cell docCell = row.getCell(1);
                if (docCell == null || docCell.getCellType() == CellType.BLANK) {
                    continue;
                }

                // Datos personales del estudiante
                String cedula = getCellValueAsString(row.getCell(1));
                String apellido1 = getCellValueAsString(row.getCell(2));
                String apellido2 = getCellValueAsString(row.getCell(3));
                String nombre1 = getCellValueAsString(row.getCell(4));
                String nombre2 = getCellValueAsString(row.getCell(5));
                String nombreCompleto = String.join(" ", nombre1, nombre2, apellido1, apellido2).replaceAll("\\s+", " ").trim();
                
                String correo = getCellValueAsString(row.getCell(6));
                String codigo = getCellValueAsString(row.getCell(8));
                
                if (cedula.isEmpty() || codigo.isEmpty()) continue;
                
                if (correo.isEmpty()) {
                    correo = codigo.toLowerCase() + "@saberpro.edu.co";
                }

                // Actualizar si ya existe, crear si no
                Alumno alumno;
                if (alumnoRepository.existsByCedula(cedula)) {
                    alumno = alumnoRepository.findByCedula(cedula).orElse(new Alumno());
                } else if (alumnoRepository.existsByCodigo(codigo)) {
                    alumno = alumnoRepository.findByCodigo(codigo).orElse(new Alumno());
                } else {
                    alumno = new Alumno();
                }

                alumno.setNombre(nombreCompleto);
                alumno.setCedula(cedula);
                alumno.setCodigo(codigo);
                alumno.setPrograma("Ingenieria en Sistemas");
                alumno.setSemestre(10);
                alumno.setCorreo(correo);
                alumno.setFacultad(facultad);
                if (alumno.getId() == null) {
                    alumno.setEstado("PENDIENTE_PAGO");
                }
                alumno = alumnoRepository.save(alumno);

                // Crear o actualizar usuario
                if (!usuarioRepository.existsByCodigo(codigo)) {
                    Usuario usuario = new Usuario();
                    usuario.setCodigo(codigo);
                    String rawPassword = nombreCompleto.split(" ")[0].toLowerCase() + cedula;
                    usuario.setPassword(rawPassword);
                    usuario.setNombre(nombreCompleto);
                    usuario.setCedula(cedula);
                    usuario.setCorreo(correo);
                    usuario.setRol("ESTUDIANTE");
                    usuario.setFacultad(facultad);
                    usuarioRepository.save(usuario);
                }

                // Notas (reusar si ya existe)
                Nota nota = notaRepository.findByAlumnoId(alumno.getId()).orElse(new Nota());
                nota.setAlumno(alumno);
                nota.setTipoExamen(tipoExamen);
                nota.setPublicado(false);

                // Col 9: Puntaje Global, Col 10: Nivel global
                nota.setPuntajeGlobal(getCellValueAsInt(row.getCell(9)));
                nota.setNivelSaberPro(getCellValueAsString(row.getCell(10)));

                // Col 11-12: Comunicacion Escrita + Nivel
                nota.setComunicacionEscrita(getCellValueAsInt(row.getCell(11)));
                nota.setNivelComunicacionEscrita(getCellValueAsString(row.getCell(12)));
                // Col 13-14: Razonamiento Cuantitativo + Nivel
                nota.setRazonamientoCuantitativo(getCellValueAsInt(row.getCell(13)));
                nota.setNivelRazonamientoCuantitativo(getCellValueAsString(row.getCell(14)));
                // Col 15-16: Lectura Critica + Nivel
                nota.setLecturaCritica(getCellValueAsInt(row.getCell(15)));
                nota.setNivelLecturaCritica(getCellValueAsString(row.getCell(16)));
                // Col 17-18: Competencias Ciudadanas + Nivel
                nota.setCompetenciasCiudadanas(getCellValueAsInt(row.getCell(17)));
                nota.setNivelCompetenciasCiudadanas(getCellValueAsString(row.getCell(18)));
                // Col 19-20: Ingles puntaje + Nivel numerico
                nota.setIngles(getCellValueAsInt(row.getCell(19)));
                nota.setNivelIngles(getCellValueAsString(row.getCell(20)));

                // Col 21-26: Modulos especificos de Ingenieria (solo en SABER_PRO)
                if ("SABER_PRO".equals(tipoExamen)) {
                    nota.setFormulacionProyectos(getCellValueAsInt(row.getCell(21)));
                    nota.setNivelFormulacionProyectos(getCellValueAsString(row.getCell(22)));
                    nota.setPensamientoCientifico(getCellValueAsInt(row.getCell(23)));
                    nota.setNivelPensamientoCientifico(getCellValueAsString(row.getCell(24)));
                    nota.setDisenoSoftware(getCellValueAsInt(row.getCell(25)));
                    nota.setNivelDisenoSoftware(getCellValueAsString(row.getCell(26)));
                    nota.setNivelInglesCefr(getCellValueAsString(row.getCell(27)));
                } else {
                    // Para TYT: el nivel CEFR de Ingles esta en la columna 21
                    nota.setNivelInglesCefr(getCellValueAsString(row.getCell(21)));
                }

                nota.calcularNiveles();
                notaRepository.save(nota);
                count++;
            }

            workbook.close();
            redirect.addFlashAttribute("success", "Se importaron/actualizaron " + count + " alumnos exitosamente (" + tipoExamen + ")");

        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al procesar el archivo Excel: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/coordinador/alumnos";
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            long val = (long) cell.getNumericCellValue();
            return String.valueOf(val);
        }
        return "";
    }

    private int getCellValueAsInt(Cell cell) {
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    // ==================== PAGOS ====================
    @GetMapping("/pagos")
    public String listarPagosPendientes(Model model) {
        model.addAttribute("pagos", pagoRepository.findByEstado("PENDIENTE"));
        return "coordinador/pagos";
    }

    @PostMapping("/pagos/aprobar/{id}")
    public String aprobarPago(@PathVariable Long id, RedirectAttributes redirect) {
        Pago pago = pagoRepository.findById(id).orElseThrow();
        pago.setEstado("APROBADO");
        pagoRepository.save(pago);
        
        Alumno alumno = pago.getAlumno();
        if (alumno != null) {
            alumno.setEstado("ACTIVO");
            alumnoRepository.save(alumno);
        }
        
        redirect.addFlashAttribute("success", "Pago aprobado exitosamente");
        return "redirect:/coordinador/pagos";
    }

    @PostMapping("/pagos/rechazar/{id}")
    public String rechazarPago(@PathVariable Long id, @RequestParam @Valid @NotBlank String motivoRechazo,
                               RedirectAttributes redirect) {
        Pago pago = pagoRepository.findById(id).orElseThrow();
        pago.setEstado("RECHAZADO");
        pago.setMotivoRechazo(motivoRechazo);
        pagoRepository.save(pago);
        redirect.addFlashAttribute("success", "Pago rechazado exitosamente");
        return "redirect:/coordinador/pagos";
    }

    @GetMapping("/alumnos/perfil/{id}")
    public String verPerfilAlumno(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Alumno alumno = alumnoRepository.findById(id).orElse(null);
        if (alumno == null) {
            redirect.addFlashAttribute("error", "Alumno no encontrado");
            return "redirect:/coordinador/alumnos";
        }
        
        Nota nota = notaRepository.findByAlumnoId(alumno.getId()).orElse(null);
        model.addAttribute("alumno", alumno);
        model.addAttribute("nota", nota);
        return "coordinador/alumno-perfil";
    }

    @GetMapping("/alumnos/perfil-fragment/{id}")
    public String verPerfilAlumnoFragment(@PathVariable Long id, Model model) {
        Alumno alumno = alumnoRepository.findById(id).orElseThrow();
        Nota nota = notaRepository.findByAlumnoId(alumno.getId()).orElse(null);
        model.addAttribute("alumno", alumno);
        model.addAttribute("nota", nota);
        return "coordinador/alumno-perfil :: contenido";
    }

    // ==================== NOTAS ====================
    @GetMapping("/notas")
    public String listarNotas(Model model) {
        List<Nota> notas = notaRepository.findAll().stream()
                .filter(n -> n.getAlumno() != null && !"ANULADO".equals(n.getAlumno().getEstado()))
                .toList();
        List<Long> alumnosConNota = notas.stream().map(n -> n.getAlumno().getId()).toList();
        List<Alumno> alumnosSinNota = alumnoRepository.findByEstado("ACTIVO").stream()
                .filter(a -> !alumnosConNota.contains(a.getId()))
                .toList();

        model.addAttribute("notas", notas);
        model.addAttribute("alumnos", alumnosSinNota);
        model.addAttribute("nota", new Nota());
        return "coordinador/notas";
    }

    @PostMapping("/notas/guardar")
    public String guardarNota(@Valid @ModelAttribute Nota nota, BindingResult result,
                              @RequestParam Long alumnoId,
                              RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            List<Nota> notas = notaRepository.findAll().stream()
                    .filter(n -> n.getAlumno() != null && !"ANULADO".equals(n.getAlumno().getEstado()))
                    .toList();
            List<Long> alumnosConNota = notas.stream().map(n -> n.getAlumno().getId()).toList();
            List<Alumno> alumnosSinNota = alumnoRepository.findByEstado("ACTIVO").stream()
                    .filter(a -> !alumnosConNota.contains(a.getId()))
                    .toList();
            model.addAttribute("notas", notas);
            model.addAttribute("alumnos", alumnosSinNota);
            return "coordinador/notas";
        }
        Alumno alumno = alumnoRepository.findById(alumnoId).orElseThrow();
        nota.setAlumno(alumno);
        nota.calcularNiveles();
        notaRepository.save(nota);
        redirect.addFlashAttribute("success", "Notas registradas exitosamente");
        return "redirect:/coordinador/notas";
    }

    @PostMapping("/notas/publicar/{id}")
    public String publicarNotas(@PathVariable Long id, RedirectAttributes redirect) {
        Nota nota = notaRepository.findById(id).orElseThrow();
        nota.setPublicado(true);
        notaRepository.save(nota);
        redirect.addFlashAttribute("success", "Notas publicadas exitosamente");
        return "redirect:/coordinador/notas";
    }

    @GetMapping("/notas/editar/{id}")
    public String editarNota(@PathVariable Long id, Model model) {
        Nota nota = notaRepository.findById(id).orElseThrow();
        List<Nota> notas = notaRepository.findAll().stream()
                .filter(n -> n.getAlumno() != null && !"ANULADO".equals(n.getAlumno().getEstado()))
                .toList();
        model.addAttribute("nota", nota);
        model.addAttribute("notas", notas);
        model.addAttribute("alumnos", alumnoRepository.findByEstado("ACTIVO"));
        return "coordinador/notas";
    }

    // ==================== INFORME BENEFICIOS ====================
    @GetMapping("/informe-beneficios")
    public String informeBeneficios(@RequestParam(required = false) Long facultadId,
                                    @RequestParam(required = false) String programa,
                                    Model model) {
        List<Nota> todasLasNotas;
        if (facultadId != null) {
            todasLasNotas = notaRepository.findByFacultadIdAndPublicado(facultadId);
        } else {
            todasLasNotas = notaRepository.findByPublicado(true);
        }

        // Filtrar solo por programa si aplica (mostrar todos - aplicaBeneficio() en la vista)
        List<Nota> beneficiarios = todasLasNotas.stream()
                .filter(Nota::aplicaBeneficio)
                .filter(n -> programa == null || programa.isEmpty() || n.getAlumno().getPrograma().contains(programa))
                .toList();

        model.addAttribute("beneficiarios", beneficiarios);
        model.addAttribute("facultades", facultadRepository.findAll());
        model.addAttribute("facultadFiltro", facultadId);
        model.addAttribute("programaFiltro", programa);
        return "coordinador/informe-beneficios";
    }

    // ==================== INFORME GENERAL ====================
    @GetMapping("/informe-general")
    public String informeGeneral(Model model) {
        List<Nota> notasValidas = notaRepository.findAll().stream()
                .filter(n -> n.getAlumno() != null && !"ANULADO".equals(n.getAlumno().getEstado()))
                .toList();
        model.addAttribute("notas", notasValidas);
        return "coordinador/informe-general";
    }
}
