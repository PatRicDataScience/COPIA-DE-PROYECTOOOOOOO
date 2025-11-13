package com.example.stockify.recetaDetalle.infrastructure;

import com.example.stockify.recetaDetalle.domain.RecetaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecetaDetalleRepository extends JpaRepository<RecetaDetalle, Long> {
    List<RecetaDetalle> findByRecetaBase_Id(Long recetaBaseId);

}
