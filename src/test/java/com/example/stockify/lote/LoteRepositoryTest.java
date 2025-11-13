package com.example.stockify.lote;

import com.example.stockify.AbstractContainerBaseTest;
import com.example.stockify.almacen.domain.Almacen;
import com.example.stockify.almacen.infrastructure.AlmacenRepository;
import com.example.stockify.lote.domain.Lote;
import com.example.stockify.lote.dto.Estado;
import com.example.stockify.lote.infrastructure.LoteRepository;
import com.example.stockify.producto.domain.Producto;
import com.example.stockify.producto.infrastructure.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LoteRepositoryTest extends AbstractContainerBaseTest {

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private AlmacenRepository almacenRepository;

    // ------------------------------------------------------
    // Test: Guardar un lote con datos válidos
    // ------------------------------------------------------
    @Test
    void shouldSaveLoteWhenValidData() {
        // Arrange
        Producto producto = crearProductoBase("Aceite Vegetal");
        productoRepository.save(producto);
        Almacen almacen = crearAlmacenBase("Almacén Central");
        almacenRepository.save(almacen);

        Lote lote = crearLoteBase(producto, almacen);
        lote.setCodigoLote("L-001");

        // Act
        Lote saved = loteRepository.save(lote);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCodigoLote()).isEqualTo("L-001");
        assertThat(saved.getProducto()).isEqualTo(producto);
        assertThat(saved.getAlmacen()).isEqualTo(almacen);
    }

    // ------------------------------------------------------
    // Test: No guardar lote sin producto o sin almacén
    // ------------------------------------------------------
    @Test
    void shouldNotSaveLoteWithoutProductoOrAlmacen() {
        // Arrange
        Lote loteSinProducto = crearLoteBase(null, crearAlmacenBase("Depósito Sur"));
        Lote loteSinAlmacen = crearLoteBase(crearProductoBase("Harina Integral"), null);

        // Act & Assert
        assertThatThrownBy(() -> {
            loteRepository.save(loteSinProducto);
            loteRepository.flush();
        })
                .isInstanceOfAny(
                        org.springframework.dao.DataIntegrityViolationException.class,
                        org.springframework.orm.jpa.JpaSystemException.class,
                        org.springframework.dao.InvalidDataAccessApiUsageException.class
                );

        assertThatThrownBy(() -> {
            loteRepository.save(loteSinAlmacen);
            loteRepository.flush();
        })
                .isInstanceOfAny(
                        org.springframework.dao.DataIntegrityViolationException.class,
                        org.springframework.orm.jpa.JpaSystemException.class,
                        org.springframework.dao.InvalidDataAccessApiUsageException.class
                );
    }

    // ------------------------------------------------------
    // Test: Generar código de lote automáticamente
    // ------------------------------------------------------
    @Test
    void shouldGenerateCodigoLoteAutomaticallyWhenNotProvided() {
        // Arrange
        Producto producto = crearProductoBase("Arroz Extra");
        productoRepository.save(producto);
        Almacen almacen = crearAlmacenBase("Depósito Central");
        almacenRepository.save(almacen);

        Lote lote = crearLoteBase(producto, almacen);
        lote.setCodigoLote(null); // no se asigna manualmente

        // Act
        Lote saved = loteRepository.save(lote);

        // Assert
        assertThat(saved.getCodigoLote()).isNotBlank();
        assertThat(saved.getCodigoLote()).startsWith("L");
    }

    // ------------------------------------------------------
    // Test: Buscar lotes por producto ordenados por fecha de compra
    // ------------------------------------------------------
    @Test
    void shouldFindLotesByProductoIdOrderedByFechaCompraAsc() {
        // Arrange
        Producto producto = crearProductoBase("Leche Deslactosada");
        productoRepository.save(producto);
        Almacen almacen = crearAlmacenBase("Depósito Lima");
        almacenRepository.save(almacen);

        Lote lote1 = crearLoteBase(producto, almacen);
        lote1.setFechaCompra(LocalDateTime.now().minusDays(3));

        Lote lote2 = crearLoteBase(producto, almacen);
        lote2.setFechaCompra(LocalDateTime.now());

        loteRepository.saveAll(List.of(lote2, lote1));

        // Act
        List<Lote> lotes = loteRepository.findByProductoIdOrderByFechaCompraAsc(producto.getId());

        // Assert
        assertThat(lotes).hasSize(2);
        assertThat(lotes.get(0).getFechaCompra()).isBefore(lotes.get(1).getFechaCompra());
    }

    // ------------------------------------------------------
    // Test: Buscar lotes por rango de vencimiento
    // ------------------------------------------------------
    @Test
    void shouldFindLotesByFechaVencimientoBetween() {
        // Arrange
        Producto producto = crearProductoBase("Azúcar Blanca");
        productoRepository.save(producto);
        Almacen almacen = crearAlmacenBase("Depósito Norte");
        almacenRepository.save(almacen);

        Lote lote1 = crearLoteBase(producto, almacen);
        lote1.setFechaVencimiento(LocalDateTime.now().plusDays(5));

        Lote lote2 = crearLoteBase(producto, almacen);
        lote2.setFechaVencimiento(LocalDateTime.now().plusDays(20));

        loteRepository.saveAll(List.of(lote1, lote2));

        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fin = LocalDateTime.now().plusDays(10);

        // Act
        List<Lote> resultados = loteRepository.findByFechaVencimientoBetween(inicio, fin);

        // Assert
        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getFechaVencimiento()).isAfterOrEqualTo(inicio);
    }

    // ------------------------------------------------------
    // Test: Reducir cantidad disponible correctamente
    // ------------------------------------------------------
    @Test
    void shouldReduceCantidadDisponibleWhenValidReduction() {
        // Arrange
        Producto producto = crearProductoBase("Fideos Cortos");
        productoRepository.save(producto);
        Almacen almacen = crearAlmacenBase("Depósito Arequipa");
        almacenRepository.save(almacen);

        Lote lote = crearLoteBase(producto, almacen);
        loteRepository.save(lote);

        // Act
        lote.reducirCantidadDisponible(10.0);

        // Assert
        assertThat(lote.getCantidadDisponible()).isEqualTo(40.0);
    }

    // ------------------------------------------------------
    // Test: Lanzar excepción si reducción excede disponible
    // ------------------------------------------------------
    @Test
    void shouldThrowExceptionWhenReducirCantidadExceedsDisponible() {
        // Arrange
        Producto producto = crearProductoBase("Cacao Premium");
        productoRepository.save(producto);
        Almacen almacen = crearAlmacenBase("Depósito Central");
        almacenRepository.save(almacen);

        Lote lote = crearLoteBase(producto, almacen);
        loteRepository.save(lote);

        // Act & Assert
        assertThatThrownBy(() -> lote.reducirCantidadDisponible(200.0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No hay suficiente cantidad disponible");
    }

    // ------------------------------------------------------
    // Métodos auxiliares
    // ------------------------------------------------------
    private Producto crearProductoBase(String nombre) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion("Producto genérico");
        producto.setUnidadMedida("kg");
        producto.setCategoria("Grano");
        producto.setStockMinimo(10.0);
        producto.setStockActual(100.0);
        producto.setActivo(true);
        return producto;
    }

    private Almacen crearAlmacenBase(String nombre) {
        Almacen almacen = new Almacen();
        almacen.setNombre(nombre);
        almacen.setUbicacion("Ubicación genérica");
        almacen.setResponsable("Responsable de prueba");
        almacen.setCapacidadMaxima(1000.0);
        almacen.setActivo(true);
        return almacen;
    }

    private Lote crearLoteBase(Producto producto, Almacen almacen) {
        return Lote.builder()
                .producto(producto)
                .almacen(almacen)
                .costoUnitario(5.0)
                .costoTotal(250.0)
                .cantidadInicial(50.0)
                .cantidadDisponible(50.0)
                .estado(Estado.ACTIVO)
                .build();
    }
}
