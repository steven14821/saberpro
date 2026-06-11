package com.saberpro.config;

import com.saberpro.model.*;
import com.saberpro.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Order(2)
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private FacultadRepository facultadRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private DocenteRepository docenteRepository;
    @Autowired
    private DirectorRepository directorRepository;
    @Autowired
    private AlumnoRepository alumnoRepository;
    @Autowired
    private NotaRepository notaRepository;
    @Autowired
    private ResolucionRepository resolucionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Solo inicializar si no hay datos
        if (usuarioRepository.count() == 0) {
            initFacultades();
            initUsuarios();
            initDocentes();
            initDirectores();
            initAlumnos();
            initNotas();
            initResoluciones();
            System.out.println("=== DATOS DE PRUEBA INICIALIZADOS ===");
        }
    }

    private void initFacultades() {
        String[][] facultades = {
            {"Ingenieria", "ING"},
            {"Ciencias de la Salud", "SAL"},
            {"Ciencias Administrativas", "ADM"},
            {"Ciencias Humanas", "HUM"},
            {"Ciencias Basicas", "BAS"}
        };
        for (String[] f : facultades) {
            Facultad facultad = new Facultad();
            facultad.setNombre(f[0]);
            facultad.setCodigo(f[1]);
            facultadRepository.save(facultad);
        }
    }

    private void initUsuarios() {
        String password = "admin123";

        // Admin
        Usuario admin = new Usuario();
        admin.setCodigo("admin");
        admin.setPassword(password);
        admin.setNombre("Administrador Principal");
        admin.setCedula("99999999");
        admin.setCorreo("admin@saberpro.edu.co");
        admin.setRol("ADMIN");
        usuarioRepository.save(admin);

        // Coordinador
        Usuario coord = new Usuario();
        coord.setCodigo("coord");
        coord.setPassword("coord123");
        coord.setNombre("Coordinador Academico");
        coord.setCedula("88888888");
        coord.setCorreo("coord@saberpro.edu.co");
        coord.setRol("COORDINADOR");
        usuarioRepository.save(coord);

        // Docente
        Usuario docente = new Usuario();
        docente.setCodigo("docente");
        docente.setPassword("docente123");
        docente.setNombre("Profesor Docente");
        docente.setCedula("77777777");
        docente.setCorreo("docente@saberpro.edu.co");
        docente.setRol("DOCENTE");
        docente.setFacultad(facultadRepository.findById(1L).orElse(null));
        usuarioRepository.save(docente);

        // Estudiante
        Usuario estudiante = new Usuario();
        estudiante.setCodigo("estudiante");
        estudiante.setPassword("estudiante123");
        estudiante.setNombre("Estudiante Prueba");
        estudiante.setCedula("11111111");
        estudiante.setCorreo("estudiante@mail.com");
        estudiante.setRol("ESTUDIANTE");
        estudiante.setFacultad(facultadRepository.findById(1L).orElse(null));
        usuarioRepository.save(estudiante);
    }

    private void initDocentes() {
        String[][] docentes = {
            {"Carlos Rodriguez", "12345678", "crodriguez@mail.com", "1"},
            {"Maria Gonzalez", "23456789", "mgonzalez@mail.com", "1"},
            {"Pedro Martinez", "34567890", "pmartinez@mail.com", "2"},
            {"Ana Lopez", "45678901", "alopez@mail.com", "3"}
        };
        for (String[] d : docentes) {
            Docente docente = new Docente();
            docente.setNombre(d[0]);
            docente.setCedula(d[1]);
            docente.setCorreo(d[2]);
            docente.setFacultad(facultadRepository.findById(Long.parseLong(d[3])).orElse(null));
            docenteRepository.save(docente);
        }
    }

    private void initDirectores() {
        String[][] directores = {
            {"Dr. Luis Hernandez", "56789012", "1"},
            {"Dra. Carmen Silva", "67890123", "2"},
            {"Dr. Jorge Ramirez", "78901234", "3"}
        };
        for (String[] d : directores) {
            Director director = new Director();
            director.setNombre(d[0]);
            director.setCedula(d[1]);
            director.setFacultad(facultadRepository.findById(Long.parseLong(d[2])).orElse(null));
            directorRepository.save(director);
        }
    }

    private void initAlumnos() {
        String[][] alumnos = {
            {"Juan Perez", "10000001", "2024001", "Ingenieria de Sistemas", "5", "juan@mail.com", "1"},
            {"Maria Garcia", "10000002", "2024002", "Ingenieria de Sistemas", "6", "maria@mail.com", "1"},
            {"Luis Torres", "10000003", "2024003", "Ingenieria Civil", "4", "luis@mail.com", "1"},
            {"Ana Martinez", "10000004", "2024004", "Medicina", "7", "ana@mail.com", "2"},
            {"Pedro Sanchez", "10000005", "2024005", "Administracion de Empresas", "3", "pedro@mail.com", "3"},
            {"Laura Diaz", "10000006", "2024006", "Psicologia", "5", "laura@mail.com", "4"},
            {"Carlos Ruiz", "10000007", "2024007", "Ingenieria de Sistemas", "8", "carlos@mail.com", "1"},
            {"Sofia Vargas", "10000008", "2024008", "Enfermeria", "4", "sofia@mail.com", "2"}
        };
        for (String[] a : alumnos) {
            Alumno alumno = new Alumno();
            alumno.setNombre(a[0]);
            alumno.setCedula(a[1]);
            alumno.setCodigo(a[2]);
            alumno.setPrograma(a[3]);
            alumno.setSemestre(Integer.parseInt(a[4]));
            alumno.setCorreo(a[5]);
            alumno.setFacultad(facultadRepository.findById(Long.parseLong(a[6])).orElse(null));
            alumno.setEstado("ACTIVO");
            alumnoRepository.save(alumno);
        }
    }

    private void initNotas() {
        int[][] notas = {
            {250, 260, 240, 270, 230, 250},
            {280, 290, 270, 300, 260, 280},
            {200, 220, 210, 230, 190, 220},
            {300, 290, 280, 300, 270, 290},
            {220, 240, 230, 250, 210, 240},
            {260, 250, 270, 280, 240, 260},
            {290, 300, 280, 300, 270, 290},
            {210, 230, 220, 240, 200, 230}
        };
        for (int i = 0; i < notas.length; i++) {
            Nota nota = new Nota();
            nota.setComunicacionEscrita(notas[i][0]);
            nota.setRazonamientoCuantitativo(notas[i][1]);
            nota.setLecturaCritica(notas[i][2]);
            nota.setCompetenciasCiudadanas(notas[i][3]);
            nota.setIngles(notas[i][4]);
            nota.setFormulacionProyectos(notas[i][5]);
            nota.setNivelFormulacionProyectos("Nivel 3");
            nota.setPensamientoCientifico(notas[i][5]);
            nota.setNivelPensamientoCientifico("Nivel 3");
            nota.setDisenoSoftware(notas[i][5]);
            nota.setNivelDisenoSoftware("Nivel 3");
            
            nota.setPuntajeGlobal(notas[i][0] + notas[i][1] + notas[i][2] + notas[i][3] + notas[i][4] + notas[i][5]);
            nota.setNivelSaberPro("Nivel 3 - Competente");
            nota.setNivelComunicacionEscrita("Nivel 3");
            nota.setNivelRazonamientoCuantitativo("Nivel 3");
            nota.setNivelLecturaCritica("Nivel 3");
            nota.setNivelCompetenciasCiudadanas("Nivel 3");
            nota.setNivelIngles("Nivel 3");
            nota.setNivelInglesCefr("B1");
            nota.setPublicado(true);
            nota.setAlumno(alumnoRepository.findById((long) (i + 1)).orElse(null));
            notaRepository.save(nota);
        }
    }

    private void initResoluciones() {
        String[][] resoluciones = {
            {"001-2024", "2024-01-15", "2024-12-31", "Ingenieria de Sistemas", "Beneficio de matricula para estudiantes con puntaje igual o superior a 1200 puntos en el examen Saber Pro.", "1"},
            {"002-2024", "2024-02-01", "2024-12-31", "Todas", "Reconocimiento academico y beca de excelencia para estudiantes con puntaje igual o superior a 1400 puntos.", "1"},
            {"003-2024", "2024-03-01", "2025-06-30", "Medicina", "Descuento del 50% en matricula para estudiantes de medicina con puntaje superior a 1300.", "2"},
            {"001-2023", "2023-01-15", "2023-12-31", "Ingenieria de Sistemas", "Resolucion anterior de beneficios - vigencia vencida.", "1"}
        };
        for (String[] r : resoluciones) {
            Resolucion resolucion = new Resolucion();
            resolucion.setNumero(r[0]);
            resolucion.setFecha(LocalDate.parse(r[1]));
            resolucion.setVigencia(r[2]);
            resolucion.setPrograma(r[3]);
            resolucion.setDescripcion(r[4]);
            resolucion.setFacultad(facultadRepository.findById(Long.parseLong(r[5])).orElse(null));
            resolucionRepository.save(resolucion);
        }
    }
}
