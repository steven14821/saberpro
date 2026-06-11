package com.saberpro.repository;

import com.saberpro.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    Optional<Alumno> findByCedula(String cedula);
    Optional<Alumno> findByCodigo(String codigo);
    List<Alumno> findByFacultadId(Long facultadId);
    List<Alumno> findByFacultadIdAndEstado(Long facultadId, String estado);
    List<Alumno> findByEstado(String estado);
    boolean existsByCedula(String cedula);
    boolean existsByCodigo(String codigo);

    @Query("SELECT AVG(n.puntajeGlobal), " +
           "MAX(n.puntajeGlobal), " +
           "MIN(n.puntajeGlobal) " +
           "FROM Nota n WHERE n.alumno.facultad.id = :facultadId AND n.publicado = true")
    List<Object[]> findEstadisticasByFacultadId(@Param("facultadId") Long facultadId);
}
