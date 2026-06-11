package com.saberpro.repository;

import com.saberpro.model.Resolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ResolucionRepository extends JpaRepository<Resolucion, Long> {
    List<Resolucion> findByFacultadId(Long facultadId);

    @Query("SELECT r FROM Resolucion r WHERE r.facultad.id = :facultadId AND r.vigencia >= :fechaActual")
    List<Resolucion> findVigentesByFacultadId(@Param("facultadId") Long facultadId, @Param("fechaActual") String fechaActual);

    @Query("SELECT r FROM Resolucion r WHERE r.facultad.id = :facultadId AND r.vigencia < :fechaActual")
    List<Resolucion> findVencidasByFacultadId(@Param("facultadId") Long facultadId, @Param("fechaActual") String fechaActual);
}
