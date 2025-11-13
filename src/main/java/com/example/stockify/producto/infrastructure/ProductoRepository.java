package com.example.stockify.producto.infrastructure;

import com.example.stockify.producto.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivo(boolean activo);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByCategoriaIgnoreCaseAndActivo(String categoria, Boolean activo);
    List<Producto> findByCategoriaIgnoreCase(String categoria);
}