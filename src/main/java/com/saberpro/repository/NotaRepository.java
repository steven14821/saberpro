package com.saberpro.repository;

import com.saberpro.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {
    Optional<Nota> findByAlumnoId(Long alumnoId);
    List<Nota> findByPublicado(boolean publicado);

    @Query("SELECT n FROM Nota n WHERE n.alumno.facultad.id = :facultadId AND n.publicado = true")
    List<Nota> findByFacultadIdAndPublicado(@Param("facultadId") Long facultadId);

    @Query("SELECT AVG(n.puntajeGlobal), " +
           "MAX(n.puntajeGlobal), " +
           "MIN(n.puntajeGlobal) " +
           "FROM Nota n WHERE n.alumno.facultad.id = :facultadId AND n.publicado = true")
    Object[] calcularEstadisticasByFacultad(@Param("facultadId") Long facultadId);
}
