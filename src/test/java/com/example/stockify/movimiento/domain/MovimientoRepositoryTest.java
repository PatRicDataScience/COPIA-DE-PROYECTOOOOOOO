package com.example.stockify.movimiento.domain;

import com.example.stockify.AbstractContainerBaseTest;
import com.example.stockify.almacen.domain.Almacen;
import com.example.stockify.almacen.infrastructure.AlmacenRepository;
import com.example.stockify.lote.domain.Lote;
import com.example.stockify.lote.infrastructure.LoteRepository;
import com.example.stockify.movimiento.infrastructure.MovimientoRepository;
import com.example.stockify.producto.domain.Producto;
import com.example.stockify.producto.infrastructure.ProductoRepository;
import com.example.stockify.usuario.domain.Usuario;
import com.example.stockify.usuario.infrastructure.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MovimientoRepositoryTest extends AbstractContainerBaseTest {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private AlmacenRepository almacenRepository;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ------------------------------------------------------
    // Test: Guardar movimiento de ENTRADA con datos válidos
    // ------------------------------------------------------
    @Test
    void shouldSaveMovimientoEntradaWhenValidData() {
        // Arrange
        Producto producto = crearProductoBase("Aceite de Oliva");
        productoRepository.save(producto);
        Almacen almacen = crearAlmacenBase("Almacén Principal");
        almacenRepository.save(almacen);

        Movimiento movimiento = crearMovimientoBase(TipoMovimiento.ENTRADA, producto, almacen, null, null);
        movimiento.setObservacion("Compra mensual de aceite");

        // Act
        Movimiento saved = movimientoRepository.save(movimiento);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTipoMovimiento()).isEqualTo(TipoMovimiento.ENTRADA);
        assertThat(saved.getCantidad()).isEqualTo(100.0);
        assertThat(saved.getProducto()).isEqualTo(producto);
        assertThat(saved.getAlmacen()).isEqualTo(almacen);
        assertThat(saved.getAnulado()).isFalse();
    }

    // ------------------------------------------------------
    // Test: Guardar movimiento de SALIDA con lote y usuario
    // ------------------------------------------------------
    @Test
    void shouldSaveMovimientoSalidaWhenWithLoteAndUsuario() {
        // Arrange
        Producto producto = crearProductoBase("Arroz Integral");
        productoRepository.save(producto);
        Almacen almacen = crearAlmacenBase("Almacén Secundario");
        almacenRepository.save(almacen);
        Lote lote = crearLoteBase(producto, almacen);
        loteRepository.save(lote);
        Usuario usuario = crearUsuarioBase("operario1");
        usuarioRepository.save(usuario);

        Movimiento movimiento = crearMovimientoBase(TipoMovimiento.SALIDA, producto, almacen, lote, usuario);
        movimiento.setObservacion("Salida para venta al por mayor");

        // Act
        Movimiento saved = movimientoRepository.save(movimiento);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTipoMovimiento()).isEqualTo(TipoMovimiento.SALIDA);
        assertThat(saved.getLote()).isEqualTo(lote);
        assertThat(saved.getUsuario()).isEqualTo(usuario);
        assertThat(saved.getOrigen()).isEqualTo("Sistema Stockify");
    }

    // ------------------------------------------------------
    // Test: Filtrar movimientos por tipo y producto
    // ------------------------------------------------------
    @Test
    void shouldFindMovimientosWhenFilterByTipoAndProducto() {
        // Arrange
        Producto producto1 = crearProductoBase("Producto A");
        Producto producto2 = crearProductoBase("Producto B");
        productoRepository.saveAll(List.of(producto1, producto2));

        Almacen almacen = crearAlmacenBase("Almacén Central");
        almacenRepository.save(almacen);

        Movimiento entrada1 = crearMovimientoBase(TipoMovimiento.ENTRADA, producto1, almacen, null, null);
        Movimiento entrada2 = crearMovimientoBase(TipoMovimiento.ENTRADA, producto2, almacen, null, null);
        Movimiento salida1 = crearMovimientoBase(TipoMovimiento.SALIDA, producto1, almacen, null, null);

        movimientoRepository.saveAll(List.of(entrada1, entrada2, salida1));

        // Act
        List<Movimiento> resultados = movimientoRepository.filtrarMovimientos(
                TipoMovimiento.ENTRADA,
                producto1.getId(),
                null,
                null,
                null
        );

        // Assert
        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getTipoMovimiento()).isEqualTo(TipoMovimiento.ENTRADA);
        assertThat(resultados.get(0).getProducto().getId()).isEqualTo(producto1.getId());
    }

    // ------------------------------------------------------
    // Test: Filtrar movimientos por rango de fechas
    // ------------------------------------------------------
    @Test
    void shouldFindMovimientosWhenFilterByFechaRange() {
        // Arrange
        Producto producto = crearProductoBase("Harina de Trigo");
        productoRepository.save(producto);
        Almacen almacen = crearAlmacenBase("Almacén Norte");
        almacenRepository.save(almacen);

        Movimiento movimiento1 = crearMovimientoBase(TipoMovimiento.ENTRADA, producto, almacen, null, null);
        movimiento1.setFechaMovimiento(LocalDateTime.now().minusDays(5));

        Movimiento movimiento2 = crearMovimientoBase(TipoMovimiento.SALIDA, producto, almacen, null, null);
        movimiento2.setFechaMovimiento(LocalDateTime.now().minusDays(1));

        movimientoRepository.saveAll(List.of(movimiento1, movimiento2));

        LocalDateTime desde = LocalDateTime.now().minusDays(7);
        LocalDateTime hasta = LocalDateTime.now().minusDays(2);

        // Act
        List<Movimiento> resultados = movimientoRepository.filtrarMovimientos(
                null, null, null, desde, hasta
        );

        // Assert
        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getFechaMovimiento()).isAfterOrEqualTo(desde);
        assertThat(resultados.get(0).getFechaMovimiento()).isBeforeOrEqualTo(hasta);
    }

    // ------------------------------------------------------
    // Test: Buscar movimientos por lote
    // ------------------------------------------------------
    @Test
    void shouldFindMovimientosWhenFindByLoteId() {
        // Arrange
        Producto producto = crearProductoBase("Azúcar Rubia");
        productoRepository.save(producto);
        Almacen almacen = crearAlmacenBase("Almacén Sur");
        almacenRepository.save(almacen);
        Lote lote = crearLoteBase(producto, almacen);
        loteRepository.save(lote);

        Movimiento movimiento1 = crearMovimientoBase(TipoMovimiento.ENTRADA, producto, almacen, lote, null);
        Movimiento movimiento2 = crearMovimientoBase(TipoMovimiento.SALIDA, producto, almacen, lote, null);
        Movimiento movimientoSinLote = crearMovimientoBase(TipoMovimiento.ENTRADA, producto, almacen, null, null);

        movimientoRepository.saveAll(List.of(movimiento1, movimiento2, movimientoSinLote));

        // Act
        List<Movimiento> resultados = movimientoRepository.findByLoteId(lote.getId());

        // Assert
        assertThat(resultados).hasSize(2);
        assertThat(resultados).allMatch(mov -> mov.getLote().getId().equals(lote.getId()));
    }

    // ------------------------------------------------------
    // Test: Filtrar movimientos con todos los parámetros nulos
    // ------------------------------------------------------
    @Test
    void shouldReturnAllMovimientosWhenAllFilterParamsAreNull() {
        // Arrange
        Producto producto = crearProductoBase("Sal Marina");
        productoRepository.save(producto);
        Almacen almacen = crearAlmacenBase("Almacén Este");
        almacenRepository.save(almacen);

        Movimiento movimiento1 = crearMovimientoBase(TipoMovimiento.ENTRADA, producto, almacen, null, null);
        Movimiento movimiento2 = crearMovimientoBase(TipoMovimiento.SALIDA, producto, almacen, null, null);

        movimientoRepository.saveAll(List.of(movimiento1, movimiento2));

        // Act
        List<Movimiento> resultados = movimientoRepository.filtrarMovimientos(null, null, null, null, null);

        // Assert
        assertThat(resultados).hasSize(2);
    }

    // ------------------------------------------------------
    // Test: Campos obligatorios se establecen correctamente
    // ------------------------------------------------------
    @Test
    void shouldSetDefaultValuesWhenCreatingMovimiento() {
        // Arrange
        Producto producto = crearProductoBase("Café Molido");
        productoRepository.save(producto);
        Almacen almacen = crearAlmacenBase("Almacén Oeste");
        almacenRepository.save(almacen);

        Movimiento movimiento = crearMovimientoBase(TipoMovimiento.ENTRADA, producto, almacen, null, null);

        // Act
        Movimiento saved = movimientoRepository.save(movimiento);

        // Assert
        assertThat(saved.getFechaMovimiento()).isNotNull();
        assertThat(saved.getAnulado()).isFalse();
        assertThat(saved.getOrigen()).isEqualTo("Sistema Stockify");
        assertThat(saved.getCostoTotal()).isEqualTo(500.0); // 100 * 5.0
    }

    // ------------------------------------------------------
    // Métodos auxiliares (CON SETTERS)
    // ------------------------------------------------------
    private Producto crearProductoBase(String nombre) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion("Descripción de " + nombre);
        producto.setUnidadMedida("kg");
        producto.setCategoria("Alimentos");
        producto.setStockMinimo(10.0);
        producto.setStockActual(100.0);
        producto.setActivo(true);
        return producto;
    }

    private Almacen crearAlmacenBase(String nombre) {
        Almacen almacen = new Almacen();
        almacen.setNombre(nombre);
        almacen.setUbicacion("Ubicación de " + nombre);
        almacen.setResponsable("Responsable " + nombre);
        almacen.setCapacidadMaxima(5000.0);
        almacen.setActivo(true);
        return almacen;
    }

    private Lote crearLoteBase(Producto producto, Almacen almacen) {// Lote SÍ tiene Builder
        return Lote.builder()
                .producto(producto)
                .almacen(almacen)
                .codigoLote("LOTE-" + System.currentTimeMillis())
                .costoUnitario(4.5)
                .costoTotal(225.0)
                .cantidadInicial(50.0)
                .cantidadDisponible(50.0)
                .fechaCompra(LocalDateTime.now())
                .fechaVencimiento(LocalDateTime.now().plusMonths(6))
                .build();
    }

    private Usuario crearUsuarioBase(String username) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Usuario " + username);
        usuario.setApellido("Apellido");
        usuario.setEmail(username + "@stockify.com");
        usuario.setPassword("password123");
        usuario.setRol(com.example.stockify.usuario.domain.Rol.COCINERO); // Usa el enum correcto
        usuario.setTelefono("123456789");
        usuario.setSede("Sede Principal");
        usuario.setActivo(true);
        return usuario;
    }

    private Movimiento crearMovimientoBase(TipoMovimiento tipo, Producto producto, Almacen almacen,
                                           Lote lote, Usuario usuario) {
        Movimiento movimiento = new Movimiento();
        movimiento.setTipoMovimiento(tipo);
        movimiento.setCantidad(100.0);
        movimiento.setCostoUnitario(5.0);
        movimiento.setCostoTotal(500.0);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setObservacion("Movimiento de prueba");
        movimiento.setOrigen("Sistema Stockify");
        movimiento.setProducto(producto);
        movimiento.setLote(lote);
        movimiento.setUsuario(usuario);
        movimiento.setAlmacen(almacen);
        movimiento.setAnulado(false);
        return movimiento;
    }
}