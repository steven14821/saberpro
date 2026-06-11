package com.saberpro.repository;

import com.saberpro.model.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
    Optional<Director> findByCedula(String cedula);
    Optional<Director> findByFacultadId(Long facultadId);
    boolean existsByFacultadId(Long facultadId);
    boolean existsByCedula(String cedula);
}
