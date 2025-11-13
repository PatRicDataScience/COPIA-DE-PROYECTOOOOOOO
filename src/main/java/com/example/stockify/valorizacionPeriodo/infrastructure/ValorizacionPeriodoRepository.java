package com.example.stockify.valorizacionPeriodo.infrastructure;

import com.example.stockify.valorizacionPeriodo.domain.ValorizacionPeriodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ValorizacionPeriodoRepository extends JpaRepository<ValorizacionPeriodo, Long> {
    Optional<ValorizacionPeriodo> findTopByOrderByFechaValorizacionDesc();

    @Query("SELECT v FROM ValorizacionPeriodo v JOIN v.usuario u JOIN Lote l ON l.producto.id = :productoId WHERE v.id IS NOT NULL")
    Optional<ValorizacionPeriodo> findByProductoId(Long productoId);
    void deleteAllByCerradoTrue();
}
