package com.example.stockify.movimiento.domain;

import com.example.stockify.AbstractContainerBaseTest;
import com.example.stockify.almacen.domain.Almacen;
import com.example.stockify.almacen.infrastructure.AlmacenRepository;
import com.example.stockify.lote.domain.Lote;
import com.example.stockify.lote.dto.Estado;
import com.example.stockify.lote.infrastructure.LoteRepository;
import com.example.stockify.movimiento.infrastructure.MovimientoRepository;
import com.example.stockify.producto.domain.Producto;
import com.example.stockify.producto.infrastructure.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MovimiRepositoryTest extends AbstractContainerBaseTest {

    @Autowired private MovimientoRepository movimientoRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private AlmacenRepository almacenRepository;
    @Autowired private LoteRepository loteRepository;

    // ------------------------------------------------------
    // 1️⃣ Guardar un movimiento válido
    // ------------------------------------------------------
    @Test
    void shouldSaveMovimientoWhenValidData() {
        // Arrange
        Producto producto = productoRepository.save(crearProductoBase("Aceite Vegetal"));
        Almacen almacen = almacenRepository.save(crearAlmacenBase("Almacén Central"));
        Lote lote = loteRepository.save(crearLoteBase(producto, almacen));

        Movimiento movimiento = Movimiento.builder()
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .cantidad(20.0)
                .costoUnitario(10.0)
                .costoTotal(200.0)
                .observacion("Compra semanal de aceite")
                .origen("Proveedor A")
                .producto(producto)
                .almacen(almacen)
                .lote(lote)
                // anulado se mantiene en false por @Builder.Default
                .build();

        // Act
        Movimiento saved = movimientoRepository.save(movimiento);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTipoMovimiento()).isEqualTo(TipoMovimiento.ENTRADA);
        assertThat(saved.getCostoTotal()).isEqualTo(200.0);
        assertThat(saved.getProducto().getNombre()).isEqualTo("Aceite Vegetal");
        assertThat(saved.getAnulado()).isFalse(); // default aplicado por @Builder.Default
    }

    // ------------------------------------------------------
    // 2️⃣ No guardar sin producto o sin almacén
    // ------------------------------------------------------
    @Test
    void shouldNotSaveMovimientoWithoutProductoOrAlmacen() {
        // Arrange
        Producto producto = productoRepository.save(crearProductoBase("Arroz Extra"));
        Almacen almacen = almacenRepository.save(crearAlmacenBase("Depósito Sur"));

        Movimiento sinProducto = Movimiento.builder()
                .tipoMovimiento(TipoMovimiento.SALIDA)
                .cantidad(5.0)
                .costoUnitario(8.0)
                .costoTotal(40.0)
                .observacion("Salida de prueba sin producto")
                .origen("Test")
                .almacen(almacen)
                .build();

        Movimiento sinAlmacen = Movimiento.builder()
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .cantidad(10.0)
                .costoUnitario(9.0)
                .costoTotal(90.0)
                .observacion("Entrada sin almacén")
                .origen("Test")
                .producto(producto)
                .build();

        // Act & Assert
        assertThatThrownBy(() -> {
            movimientoRepository.save(sinProducto);
            movimientoRepository.flush();
        }).isInstanceOfAny(
                org.springframework.dao.DataIntegrityViolationException.class,
                org.springframework.dao.InvalidDataAccessApiUsageException.class,
                org.springframework.orm.jpa.JpaSystemException.class
        );

        assertThatThrownBy(() -> {
            movimientoRepository.save(sinAlmacen);
            movimientoRepository.flush();
        }).isInstanceOfAny(
                org.springframework.dao.DataIntegrityViolationException.class,
                org.springframework.dao.InvalidDataAccessApiUsageException.class,
                org.springframework.orm.jpa.JpaSystemException.class
        );
    }

    // ------------------------------------------------------
    // 3️⃣ Buscar movimientos por lote
    // ------------------------------------------------------
    @Test
    void shouldFindMovimientosByLoteId() {
        // Arrange
        Producto producto = productoRepository.save(crearProductoBase("Fideos"));
        Almacen almacen = almacenRepository.save(crearAlmacenBase("Depósito Norte"));
        Lote lote = loteRepository.save(crearLoteBase(producto, almacen));

        Movimiento m1 = crearMovimientoBase(producto, almacen, lote, TipoMovimiento.ENTRADA);
        Movimiento m2 = crearMovimientoBase(producto, almacen, lote, TipoMovimiento.SALIDA);
        movimientoRepository.saveAll(List.of(m1, m2));

        // Act
        List<Movimiento> resultados = movimientoRepository.findByLoteId(lote.getId());

        // Assert
        assertThat(resultados).hasSize(2);
        assertThat(resultados.get(0).getLote()).isEqualTo(lote);
    }

    // ------------------------------------------------------
    // 4️⃣ Filtrar por tipo, producto, almacén y fechas
    // ------------------------------------------------------
    @Test
    void shouldFilterMovimientosByTipoProductoAlmacenAndFechas() {
        // Arrange
        Producto producto = productoRepository.save(crearProductoBase("Azúcar Blanca"));
        Almacen almacen = almacenRepository.save(crearAlmacenBase("Depósito Lima"));

        Movimiento m1 = crearMovimientoBase(producto, almacen, null, TipoMovimiento.ENTRADA);
        m1.setFechaMovimiento(LocalDateTime.now().minusDays(3));
        Movimiento m2 = crearMovimientoBase(producto, almacen, null, TipoMovimiento.SALIDA);
        m2.setFechaMovimiento(LocalDateTime.now());

        movimientoRepository.saveAll(List.of(m1, m2));

        LocalDateTime desde = LocalDateTime.now().minusDays(5);
        LocalDateTime hasta = LocalDateTime.now().plusDays(1);

        // Act
        List<Movimiento> resultados = movimientoRepository.filtrarMovimientos(
                TipoMovimiento.SALIDA,
                producto.getId(),
                almacen.getId(),
                desde,
                hasta
        );

        // Assert
        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getTipoMovimiento()).isEqualTo(TipoMovimiento.SALIDA);
    }

    // ------------------------------------------------------
    // 5️⃣ Filtrar solo por tipo (resto nulos)
    // ------------------------------------------------------
    @Test
    void shouldFilterMovimientosByTipoOnly() {
        // Arrange
        Producto producto = productoRepository.save(crearProductoBase("Café"));
        Almacen almacen = almacenRepository.save(crearAlmacenBase("Depósito Sur"));

        Movimiento m1 = crearMovimientoBase(producto, almacen, null, TipoMovimiento.ENTRADA);
        Movimiento m2 = crearMovimientoBase(producto, almacen, null, TipoMovimiento.SALIDA);
        movimientoRepository.saveAll(List.of(m1, m2));

        // Act
        List<Movimiento> entradas = movimientoRepository.filtrarMovimientos(
                TipoMovimiento.ENTRADA, null, null, null, null
        );

        // Assert
        assertThat(entradas).hasSize(1);
        assertThat(entradas.get(0).getTipoMovimiento()).isEqualTo(TipoMovimiento.ENTRADA);
    }

    // ------------------------------------------------------
    // Helpers
    // ------------------------------------------------------
    private Producto crearProductoBase(String nombre) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion("Producto de prueba");
        p.setUnidadMedida("kg");
        p.setCategoria("Grano");
        p.setStockMinimo(5.0);
        p.setStockActual(20.0);
        p.setActivo(true);
        return p;
    }

    private Almacen crearAlmacenBase(String nombre) {
        Almacen a = new Almacen();
        a.setNombre(nombre);
        a.setUbicacion("Ubicación genérica");
        a.setResponsable("Responsable genérico");
        a.setCapacidadMaxima(1000.0);
        a.setActivo(true);
        return a;
    }

    private Lote crearLoteBase(Producto producto, Almacen almacen) {
        Lote l = new Lote();
        l.setProducto(producto);
        l.setAlmacen(almacen);
        l.setCostoUnitario(10.0);
        l.setCostoTotal(100.0);
        l.setCantidadInicial(10.0);
        l.setCantidadDisponible(10.0);
        l.setEstado(Estado.ACTIVO);
        return l;
    }

    private Movimiento crearMovimientoBase(Producto producto, Almacen almacen, Lote lote, TipoMovimiento tipo) {
        return Movimiento.builder()
                .tipoMovimiento(tipo)
                .cantidad(5.0)
                .costoUnitario(10.0)
                .costoTotal(50.0)
                .observacion("Movimiento de prueba")
                .origen("Test")
                .producto(producto)
                .almacen(almacen)
                .lote(lote)
                // anulado queda en false por @Builder.Default
                .build();
    }
}
