package com.example.stockify.lote.infrastructure;

import com.example.stockify.lote.domain.Lote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LoteRepository extends JpaRepository<Lote, Long> {
    List<Lote> findByProductoIdOrderByFechaCompraAsc(Long productoId);
    List<Lote> findByFechaVencimientoBetween(LocalDateTime inicio, LocalDateTime fin);
}