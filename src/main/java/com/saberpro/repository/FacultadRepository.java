package com.saberpro.repository;

import com.saberpro.model.Facultad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacultadRepository extends JpaRepository<Facultad, Long> {
    Optional<Facultad> findByNombre(String nombre);
    Optional<Facultad> findByCodigo(String codigo);
    boolean existsByNombre(String nombre);
    boolean existsByCodigo(String codigo);
}
