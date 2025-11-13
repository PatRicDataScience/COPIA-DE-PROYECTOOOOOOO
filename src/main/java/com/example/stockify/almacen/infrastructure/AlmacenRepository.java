package com.example.stockify.almacen.infrastructure;

import com.example.stockify.almacen.domain.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlmacenRepository extends JpaRepository<Almacen, Long> {
    List<Almacen> findByActivo(Boolean activo);
    List<Almacen> findByNombreContainingIgnoreCase(String nombre);

}
