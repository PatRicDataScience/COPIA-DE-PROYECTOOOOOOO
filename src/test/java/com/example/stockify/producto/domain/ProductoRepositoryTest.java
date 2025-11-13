package com.example.stockify.producto.domain;

import com.example.stockify.producto.infrastructure.ProductoRepository;
import com.example.stockify.AbstractContainerBaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Usa PostgreSQL del contenedor
public class ProductoRepositoryTest extends AbstractContainerBaseTest {

    @Autowired
    private ProductoRepository productoRepository;


    //Test: Crear y guardar un producto válido

    @Test
    void shouldSaveProductoWhenDataIsValid() {
        // Arrange
        Producto producto = new Producto();
        producto.setNombre("Arroz Extra");
        producto.setDescripcion("Grano seleccionado tipo I");
        producto.setUnidadMedida("kg");
        producto.setCategoria("Grano");
        producto.setStockMinimo(5.0);
        producto.setStockActual(20.0);
        producto.setActivo(true);

        // Act
        Producto savedProducto = productoRepository.save(producto);

        // Assert
        assertThat(savedProducto.getId()).isNotNull();
        assertThat(savedProducto.getNombre()).isEqualTo("Arroz Extra");
        assertThat(savedProducto.getCategoria()).isEqualTo("Grano");
        assertThat(savedProducto.getStockActual()).isEqualTo(20.0);
    }


    // Test: Actualizar un producto existente

    @Test
    void shouldUpdateProductoWhenExistingRecordIsModified() {
        // Arrange
        Producto producto = new Producto();
        producto.setNombre("Aceite Vegetal");
        producto.setDescripcion("Aceite de girasol refinado");
        producto.setUnidadMedida("L");
        producto.setCategoria("Aceite");
        producto.setStockMinimo(2.0);
        producto.setStockActual(10.0);
        producto.setActivo(true);

        Producto saved = productoRepository.save(producto);

        // Act
        saved.setNombre("Aceite de Soya");
        saved.setStockActual(8.0);
        Producto updated = productoRepository.save(saved);

        // Assert
        Optional<Producto> found = productoRepository.findById(updated.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getNombre()).isEqualTo("Aceite de Soya");
        assertThat(found.get().getStockActual()).isEqualTo(8.0);
    }

    // ------------------------------------------------------
    // Test: Eliminar un producto
    // ------------------------------------------------------
    @Test
    void shouldDeleteProductoWhenExists() {
        // Arrange
        Producto producto = new Producto();
        producto.setNombre("Leche Deslactosada");
        producto.setDescripcion("Envase de 1L");
        producto.setUnidadMedida("L");
        producto.setCategoria("Lácteo");
        producto.setStockMinimo(3.0);
        producto.setStockActual(5.0);
        producto.setActivo(true);

        Producto saved = productoRepository.save(producto);

        // Act
        productoRepository.deleteById(saved.getId());
        Optional<Producto> found = productoRepository.findById(saved.getId());

        // Assert
        assertThat(found).isEmpty();
    }

    // ------------------------------------------------------
    // Test: Buscar productos activos
    // ------------------------------------------------------
    @Test
    void shouldFindProductosByActivoWhenTrue() {
        // Arrange
        Producto activo = new Producto();
        activo.setNombre("Azúcar Blanca");
        activo.setDescripcion("Paquete 1kg");
        activo.setUnidadMedida("kg");
        activo.setCategoria("Grano");
        activo.setStockMinimo(2.0);
        activo.setStockActual(10.0);
        activo.setActivo(true);

        Producto inactivo = new Producto();
        inactivo.setNombre("Harina Integral");
        inactivo.setDescripcion("Bolsa 1kg");
        inactivo.setUnidadMedida("kg");
        inactivo.setCategoria("Grano");
        inactivo.setStockMinimo(2.0);
        inactivo.setStockActual(5.0);
        inactivo.setActivo(false);

        productoRepository.saveAll(List.of(activo, inactivo));

        // Act
        List<Producto> activos = productoRepository.findByActivo(true);

        // Assert
        assertThat(activos).hasSize(1);
        assertThat(activos.get(0).getNombre()).isEqualTo("Azúcar Blanca");
    }

    // ------------------------------------------------------
    // Test: Buscar por coincidencia parcial de nombre
    // ------------------------------------------------------
    @Test
    void shouldFindProductosByNombreContainingIgnoreCase() {
        // Arrange
        Producto p1 = new Producto();
        p1.setNombre("Aceite de Girasol");
        p1.setDescripcion("Aceite vegetal");
        p1.setUnidadMedida("L");
        p1.setCategoria("Aceite");
        p1.setStockMinimo(1.0);
        p1.setStockActual(10.0);
        p1.setActivo(true);

        Producto p2 = new Producto();
        p2.setNombre("Aceituna en conserva");
        p2.setDescripcion("Aceitunas verdes");
        p2.setUnidadMedida("kg");
        p2.setCategoria("Vegetal");
        p2.setStockMinimo(2.0);
        p2.setStockActual(4.0);
        p2.setActivo(true);

        productoRepository.saveAll(List.of(p1, p2));

        // Act
        List<Producto> resultados = productoRepository.findByNombreContainingIgnoreCase("acei");

        // Assert
        assertThat(resultados).hasSize(2);
    }

    // ------------------------------------------------------
    // Test: Buscar por categoría ignorando mayúsculas
    // ------------------------------------------------------
    @Test
    void shouldFindProductosByCategoriaIgnoreCase() {
        // Arrange
        Producto p = new Producto();
        p.setNombre("Carne de Res");
        p.setDescripcion("Corte especial");
        p.setUnidadMedida("kg");
        p.setCategoria("Cárnico");
        p.setStockMinimo(5.0);
        p.setStockActual(12.0);
        p.setActivo(true);

        productoRepository.save(p);

        // Act
        List<Producto> resultados = productoRepository.findByCategoriaIgnoreCase("CÁRNICO");

        // Assert
        assertThat(resultados).isNotEmpty();
        assertThat(resultados.get(0).getNombre()).isEqualTo("Carne de Res");
    }

    // ------------------------------------------------------
    // Test: Buscar por categoría y estado activo
    // ------------------------------------------------------
    @Test
    void shouldFindProductosByCategoriaAndActivoWhenMatchingBoth() {
        // Arrange
        Producto activo = new Producto();
        activo.setNombre("Maíz Amarillo");
        activo.setDescripcion("Grano seco");
        activo.setUnidadMedida("kg");
        activo.setCategoria("Grano");
        activo.setStockMinimo(2.0);
        activo.setStockActual(6.0);
        activo.setActivo(true);

        Producto inactivo = new Producto();
        inactivo.setNombre("Maíz Blanco");
        inactivo.setDescripcion("Grano seco");
        inactivo.setUnidadMedida("kg");
        inactivo.setCategoria("Grano");
        inactivo.setStockMinimo(2.0);
        inactivo.setStockActual(6.0);
        inactivo.setActivo(false);

        productoRepository.saveAll(List.of(activo, inactivo));

        // Act
        List<Producto> resultados = productoRepository.findByCategoriaIgnoreCaseAndActivo("grano", true);

        // Assert
        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getNombre()).isEqualTo("Maíz Amarillo");
    }

    // ------------------------------------------------------
    // Test: Verificar timestamps automáticos
    // ------------------------------------------------------
    @Test
    void shouldSetTimestampsOnPersistAndUpdateAutomatically() throws InterruptedException {
        // Arrange
        Producto producto = new Producto();
        producto.setNombre("Pechuga de Pollo");
        producto.setDescripcion("Corte fresco sin piel");
        producto.setUnidadMedida("kg");
        producto.setCategoria("Cárnico");
        producto.setStockMinimo(3.0);
        producto.setStockActual(8.0);
        producto.setActivo(true);

        // Act - guardar
        Producto saved = productoRepository.save(producto);
        productoRepository.flush();
        var fechaCreacionInicial = saved.getFechaCreacion();

        Thread.sleep(1000); // para ver diferencia temporal
        saved.setStockActual(7.0);
        productoRepository.save(saved);
        productoRepository.flush();
        Producto updated = productoRepository.findById(saved.getId()).orElseThrow();

        // Assert
        assertThat(updated.getFechaCreacion()).isEqualTo(fechaCreacionInicial);
        assertThat(updated.getUltimoActualizado()).isAfterOrEqualTo(fechaCreacionInicial);
    }
}
