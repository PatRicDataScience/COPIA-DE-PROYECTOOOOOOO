package com.example.stockify.movimiento.infrastructure;

import com.example.stockify.movimiento.domain.Movimiento;
import com.example.stockify.movimiento.domain.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    @Query("SELECT m FROM Movimiento m WHERE " +
            "(:tipo IS NULL OR m.tipoMovimiento = :tipo) AND " +
            "(:productoId IS NULL OR m.producto.id = :productoId) AND " +
            "(:almacenId IS NULL OR m.almacen.id = :almacenId) AND " +
            "(:desde IS NULL OR m.fechaMovimiento >= :desde) AND " +
            "(:hasta IS NULL OR m.fechaMovimiento <= :hasta)")
    List<Movimiento> filtrarMovimientos(
            @Param("tipo") TipoMovimiento tipo,
            @Param("productoId") Long productoId,
            @Param("almacenId") Long almacenId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);

    List<Movimiento> findByLoteId(Long loteId);
}