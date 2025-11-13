package com.example.stockify.reportes.infrastructure;

import com.example.stockify.reportes.domain.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReporteRepository extends JpaRepository<Reporte, Long> {
}
