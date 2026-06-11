package com.saberpro.repository;

import com.saberpro.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCodigo(String codigo);
    Optional<Usuario> findByCedula(String cedula);
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByCodigo(String codigo);
    boolean existsByCedula(String cedula);
    boolean existsByCorreo(String correo);
    List<Usuario> findByFacultadId(Long facultadId);
}
