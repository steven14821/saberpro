package com.saberpro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notas")
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 0, message = "La nota de comunicacion escrita debe ser minimo 0")
    @Max(value = 300, message = "La nota de comunicacion escrita debe ser maximo 300")
    @Column(nullable = false)
    private Integer comunicacionEscrita;

    @Min(value = 0, message = "La nota de razonamiento cuantitativo debe ser minimo 0")
    @Max(value = 300, message = "La nota de razonamiento cuantitativo debe ser maximo 300")
    @Column(nullable = false)
    private Integer razonamientoCuantitativo;

    @Min(value = 0, message = "La nota de lectura critica debe ser minimo 0")
    @Max(value = 300, message = "La nota de lectura critica debe ser maximo 300")
    @Column(nullable = false)
    private Integer lecturaCritica;

    @Min(value = 0, message = "La nota de competencias ciudadanas debe ser minimo 0")
    @Max(value = 300, message = "La nota de competencias ciudadanas debe ser maximo 300")
    @Column(nullable = false)
    private Integer competenciasCiudadanas;

    @Min(value = 0, message = "La nota de ingles debe ser minimo 0")
    @Max(value = 300, message = "La nota de ingles debe ser maximo 300")
    @Column(nullable = false)
    private Integer ingles;

    @Column(nullable = false)
    private Integer puntajeGlobal;

    @Column(nullable = false)
    private String nivelSaberPro = "";

    @Column(nullable = false)
    private String nivelComunicacionEscrita = "";

    @Column(nullable = false)
    private String nivelRazonamientoCuantitativo = "";

    @Column(nullable = false)
    private String nivelLecturaCritica = "";

    @Column(nullable = false)
    private String nivelCompetenciasCiudadanas = "";

    @Column(nullable = false)
    private String nivelIngles = "";

    @Column(nullable = false)
    private String nivelInglesCefr = "";

    @Min(value = 0, message = "Minimo 0")
    @Max(value = 300, message = "Maximo 300")
    @Column(nullable = false)
    private Integer formulacionProyectos;

    @Column(nullable = false)
    private String nivelFormulacionProyectos = "";

    @Min(value = 0, message = "Minimo 0")
    @Max(value = 300, message = "Maximo 300")
    @Column(nullable = false)
    private Integer pensamientoCientifico;

    @Column(nullable = false)
    private String nivelPensamientoCientifico = "";

    @Min(value = 0, message = "Minimo 0")
    @Max(value = 300, message = "Maximo 300")
    @Column(nullable = false)
    private Integer disenoSoftware;

    @Column(nullable = false)
    private String nivelDisenoSoftware = "";

    @Column(nullable = false)
    private String tipoExamen = "SABER_PRO"; // SABER_PRO o SABER_TYT

    @Column(nullable = false)
    private boolean publicado = false;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "alumno_id", nullable = false, unique = true)
    private Alumno alumno;

    public Nota() {
    }

    public Nota(Long id, Integer comunicacionEscrita, Integer razonamientoCuantitativo, Integer lecturaCritica, Integer competenciasCiudadanas, Integer ingles, boolean publicado, Alumno alumno) {
        this.id = id;
        this.comunicacionEscrita = comunicacionEscrita;
        this.razonamientoCuantitativo = razonamientoCuantitativo;
        this.lecturaCritica = lecturaCritica;
        this.competenciasCiudadanas = competenciasCiudadanas;
        this.ingles = ingles;
        this.publicado = publicado;
        this.alumno = alumno;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getComunicacionEscrita() {
        return comunicacionEscrita;
    }

    public void setComunicacionEscrita(Integer comunicacionEscrita) {
        this.comunicacionEscrita = comunicacionEscrita;
    }

    public Integer getRazonamientoCuantitativo() {
        return razonamientoCuantitativo;
    }

    public void setRazonamientoCuantitativo(Integer razonamientoCuantitativo) {
        this.razonamientoCuantitativo = razonamientoCuantitativo;
    }

    public Integer getLecturaCritica() {
        return lecturaCritica;
    }

    public void setLecturaCritica(Integer lecturaCritica) {
        this.lecturaCritica = lecturaCritica;
    }

    public Integer getCompetenciasCiudadanas() {
        return competenciasCiudadanas;
    }

    public void setCompetenciasCiudadanas(Integer competenciasCiudadanas) {
        this.competenciasCiudadanas = competenciasCiudadanas;
    }

    public Integer getIngles() {
        return ingles;
    }

    public void setIngles(Integer ingles) {
        this.ingles = ingles;
    }

    public Integer getPuntajeGlobal() {
        return puntajeGlobal;
    }

    public void setPuntajeGlobal(Integer puntajeGlobal) {
        this.puntajeGlobal = puntajeGlobal;
    }

    public String getNivelSaberPro() {
        if (nivelSaberPro == null || nivelSaberPro.trim().isEmpty()) {
            return calcularNivel1a4(this.puntajeGlobal);
        }
        return nivelSaberPro;
    }

    public void setNivelSaberPro(String nivelSaberPro) {
        this.nivelSaberPro = nivelSaberPro;
    }

    public String getNivelComunicacionEscrita() {
        if (nivelComunicacionEscrita == null || nivelComunicacionEscrita.trim().isEmpty()) {
            return calcularNivel1a4(this.comunicacionEscrita);
        }
        return nivelComunicacionEscrita;
    }

    public void setNivelComunicacionEscrita(String nivelComunicacionEscrita) {
        this.nivelComunicacionEscrita = nivelComunicacionEscrita;
    }

    public String getNivelRazonamientoCuantitativo() {
        if (nivelRazonamientoCuantitativo == null || nivelRazonamientoCuantitativo.trim().isEmpty()) {
            return calcularNivel1a4(this.razonamientoCuantitativo);
        }
        return nivelRazonamientoCuantitativo;
    }

    public void setNivelRazonamientoCuantitativo(String nivelRazonamientoCuantitativo) {
        this.nivelRazonamientoCuantitativo = nivelRazonamientoCuantitativo;
    }

    public String getNivelLecturaCritica() {
        if (nivelLecturaCritica == null || nivelLecturaCritica.trim().isEmpty()) {
            return calcularNivel1a4(this.lecturaCritica);
        }
        return nivelLecturaCritica;
    }

    public void setNivelLecturaCritica(String nivelLecturaCritica) {
        this.nivelLecturaCritica = nivelLecturaCritica;
    }

    public String getNivelCompetenciasCiudadanas() {
        if (nivelCompetenciasCiudadanas == null || nivelCompetenciasCiudadanas.trim().isEmpty()) {
            return calcularNivel1a4(this.competenciasCiudadanas);
        }
        return nivelCompetenciasCiudadanas;
    }

    public void setNivelCompetenciasCiudadanas(String nivelCompetenciasCiudadanas) {
        this.nivelCompetenciasCiudadanas = nivelCompetenciasCiudadanas;
    }

    public String getNivelIngles() {
        if (nivelIngles == null || nivelIngles.trim().isEmpty()) {
            return calcularNivel1a4(this.ingles);
        }
        return nivelIngles;
    }

    public void setNivelIngles(String nivelIngles) {
        this.nivelIngles = nivelIngles;
    }

    public String getNivelInglesCefr() {
        if (nivelInglesCefr == null || nivelInglesCefr.trim().isEmpty()) {
            return calcularNivelCEFR(this.ingles);
        }
        return nivelInglesCefr;
    }

    public void setNivelInglesCefr(String nivelInglesCefr) {
        this.nivelInglesCefr = nivelInglesCefr;
    }

    public Integer getFormulacionProyectos() {
        return formulacionProyectos;
    }

    public void setFormulacionProyectos(Integer formulacionProyectos) {
        this.formulacionProyectos = formulacionProyectos;
    }

    public String getNivelFormulacionProyectos() {
        if (nivelFormulacionProyectos == null || nivelFormulacionProyectos.trim().isEmpty()) {
            return calcularNivel1a4(this.formulacionProyectos);
        }
        return nivelFormulacionProyectos;
    }

    public void setNivelFormulacionProyectos(String nivelFormulacionProyectos) {
        this.nivelFormulacionProyectos = nivelFormulacionProyectos;
    }

    public Integer getPensamientoCientifico() {
        return pensamientoCientifico;
    }

    public void setPensamientoCientifico(Integer pensamientoCientifico) {
        this.pensamientoCientifico = pensamientoCientifico;
    }

    public String getNivelPensamientoCientifico() {
        if (nivelPensamientoCientifico == null || nivelPensamientoCientifico.trim().isEmpty()) {
            return calcularNivel1a4(this.pensamientoCientifico);
        }
        return nivelPensamientoCientifico;
    }

    public void setNivelPensamientoCientifico(String nivelPensamientoCientifico) {
        this.nivelPensamientoCientifico = nivelPensamientoCientifico;
    }

    public Integer getDisenoSoftware() {
        return disenoSoftware;
    }

    public void setDisenoSoftware(Integer disenoSoftware) {
        this.disenoSoftware = disenoSoftware;
    }

    public String getNivelDisenoSoftware() {
        if (nivelDisenoSoftware == null || nivelDisenoSoftware.trim().isEmpty()) {
            return calcularNivel1a4(this.disenoSoftware);
        }
        return nivelDisenoSoftware;
    }

    public void setNivelDisenoSoftware(String nivelDisenoSoftware) {
        this.nivelDisenoSoftware = nivelDisenoSoftware;
    }

    public boolean isPublicado() {
        return publicado;
    }

    public void setPublicado(boolean publicado) {
        this.publicado = publicado;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public String getTipoExamen() {
        return tipoExamen;
    }

    public void setTipoExamen(String tipoExamen) {
        this.tipoExamen = tipoExamen;
    }

    public String getNivelDesempeno() {
        return nivelSaberPro;
    }

    /**
     * Determina el beneficio segun el Acuerdo 01-009 de UTS (vigente desde enero 2025).
     * Saber TYT (max 200 por modulo, pero el puntajeGlobal es el general):
     *   120-150: Exencion informe final / Seminario Grado II con 4.5
     *   151-170: Lo anterior con 4.7 + beca 50%
     *   171+   : Lo anterior con 5.0 + beca 100%
     * Saber Pro (max 300 por modulo):
     *   180-210: Exencion informe final / Seminario Grado IV con 4.5
     *   211-240: Lo anterior con 4.7 + beca 50%
     *   241+   : Lo anterior con 5.0 + beca 100%
     */
    public String getBeneficio() {
        int p = getPuntajeGlobal();
        if ("SABER_TYT".equalsIgnoreCase(tipoExamen)) {
            if (p >= 171) return "Exencion trabajo de grado / Seminario II (nota 5.0) + Beca 100% derecho pecuniario";
            if (p >= 151) return "Exencion trabajo de grado / Seminario II (nota 4.7) + Beca 50% derecho pecuniario";
            if (p >= 120) return "Exencion trabajo de grado / Seminario II (nota 4.5)";
            return "Sin beneficio (puntaje menor a 120)";
        } else { // SABER_PRO
            if (p >= 241) return "Exencion trabajo de grado / Seminario IV (nota 5.0) + Beca 100% derecho pecuniario";
            if (p >= 211) return "Exencion trabajo de grado / Seminario IV (nota 4.7) + Beca 50% derecho pecuniario";
            if (p >= 180) return "Exencion trabajo de grado / Seminario IV (nota 4.5)";
            return "Sin beneficio (puntaje menor a 180)";
        }
    }

    public boolean aplicaBeneficio() {
        int p = getPuntajeGlobal();
        if ("SABER_TYT".equalsIgnoreCase(tipoExamen)) {
            return p >= 120;
        } else {
            return p >= 180;
        }
    }

    public void calcularNiveles() {
        this.nivelComunicacionEscrita = calcularNivel1a4(this.comunicacionEscrita);
        this.nivelRazonamientoCuantitativo = calcularNivel1a4(this.razonamientoCuantitativo);
        this.nivelLecturaCritica = calcularNivel1a4(this.lecturaCritica);
        this.nivelCompetenciasCiudadanas = calcularNivel1a4(this.competenciasCiudadanas);
        this.nivelIngles = calcularNivel1a4(this.ingles);
        this.nivelInglesCefr = calcularNivelCEFR(this.ingles);
        
        if ("SABER_PRO".equals(this.tipoExamen)) {
            this.nivelFormulacionProyectos = calcularNivel1a4(this.formulacionProyectos);
            this.nivelPensamientoCientifico = calcularNivel1a4(this.pensamientoCientifico);
            this.nivelDisenoSoftware = calcularNivel1a4(this.disenoSoftware);
        }
        
        this.nivelSaberPro = calcularNivel1a4(this.puntajeGlobal);
    }
    
    private String calcularNivel1a4(Integer score) {
        if (score == null) return "Nivel 1";
        if (score >= 191) return "Nivel 4";
        if (score >= 156) return "Nivel 3";
        if (score >= 126) return "Nivel 2";
        return "Nivel 1";
    }
    
    private String calcularNivelCEFR(Integer score) {
        if (score == null) return "A0";
        if (score >= 170) return "B2";
        if (score >= 150) return "B1";
        if (score >= 135) return "A2";
        if (score >= 100) return "A1";
        return "A0";
    }
}
