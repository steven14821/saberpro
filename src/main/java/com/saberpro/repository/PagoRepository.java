package com.saberpro.repository;

import com.saberpro.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByEstado(String estado);
    List<Pago> findByAlumnoId(Long alumnoId);
    Optional<Pago> findFirstByAlumnoIdOrderByFechaRegistroDesc(Long alumnoId);
    boolean existsByAlumnoIdAndEstado(Long alumnoId, String estado);
}
