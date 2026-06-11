package com.saberpro.repository;

import com.saberpro.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {
    Optional<Docente> findByCedula(String cedula);
    List<Docente> findByFacultadId(Long facultadId);
    boolean existsByCedula(String cedula);
}
