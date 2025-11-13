package com.example.stockify.alertaStock.domain;

import com.example.stockify.AbstractContainerBaseTest;
import com.example.stockify.alertaStock.infrastructure.AlertaStockRepository;
import com.example.stockify.producto.domain.Producto;
import com.example.stockify.producto.infrastructure.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AlertaStockRepositoryTest extends AbstractContainerBaseTest {

    @Autowired
    private AlertaStockRepository alertaStockRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // ------------------------------------------------------
    // Test: Guardar una alerta válida
    // ------------------------------------------------------
    @Test
    void shouldSaveAlertaStockWhenValidData() {
        // Arrange
        Producto producto = new Producto();
        producto.setNombre("Aceite Vegetal");
        producto.setDescripcion("Aceite de girasol");
        producto.setUnidadMedida("L");
        producto.setCategoria("Aceite");
        producto.setStockMinimo(2.0);
        producto.setStockActual(1.0);
        producto.setActivo(true);
        productoRepository.save(producto);

        AlertaStock alerta = new AlertaStock();
        alerta.setMensaje("Stock bajo: Aceite Vegetal");
        alerta.setPrioridad(Prioridad.ALTA);
        alerta.setProducto(producto);

        // Act
        AlertaStock saved = alertaStockRepository.save(alerta);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFechaAlerta()).isNotNull();
        assertThat(saved.getAtendido()).isFalse();
        assertThat(saved.getPrioridad()).isEqualTo(Prioridad.ALTA);
    }

    // ------------------------------------------------------
    // Test: No guardar alerta sin producto asociado
    // ------------------------------------------------------
    @Test
    void shouldNotSaveAlertaStockWithoutProducto() {
        // Arrange
        AlertaStock alerta = new AlertaStock();
        alerta.setMensaje("Stock bajo sin producto");
        alerta.setPrioridad(Prioridad.MEDIA);

        // Act & Assert
        assertThatThrownBy(() -> alertaStockRepository.saveAndFlush(alerta))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // ------------------------------------------------------
    // Test: Buscar alertas pendientes
    // ------------------------------------------------------
    @Test
    void shouldFindAlertaStockByAtendidoFalse() {
        // Arrange
        Producto producto = crearProductoBase("Arroz Extra");
        productoRepository.save(producto);

        AlertaStock alerta1 = crearAlerta(producto, "Stock bajo Arroz", Prioridad.MEDIA, false);
        AlertaStock alerta2 = crearAlerta(producto, "Stock bajo Arroz", Prioridad.ALTA, true);
        alertaStockRepository.saveAll(List.of(alerta1, alerta2));

        // Act
        List<AlertaStock> pendientes = alertaStockRepository.findByAtendidoFalse();

        // Assert
        assertThat(pendientes).hasSize(1);
        assertThat(pendientes.get(0).getAtendido()).isFalse();
    }

    // ------------------------------------------------------
    // Test: Buscar alertas atendidas (atendido = true)
    // ------------------------------------------------------
    @Test
    void shouldFindAlertaStockByAtendidoTrue() {
        // Arrange
        Producto producto = crearProductoBase("Harina Integral");
        productoRepository.save(producto);

        AlertaStock alerta1 = crearAlerta(producto, "Stock bajo Harina", Prioridad.BAJA, true);
        AlertaStock alerta2 = crearAlerta(producto, "Stock bajo Harina", Prioridad.ALTA, false);
        alertaStockRepository.saveAll(List.of(alerta1, alerta2));

        // Act
        List<AlertaStock> atendidas = alertaStockRepository.findByAtendidoTrue();

        // Assert
        assertThat(atendidas).hasSize(1);
        assertThat(atendidas.get(0).getAtendido()).isTrue();
    }

    // ------------------------------------------------------
    // Test: Buscar alertas por prioridad
    // ------------------------------------------------------
    @Test
    void shouldFindAlertaStockByPrioridad() {
        // Arrange
        Producto producto = crearProductoBase("Leche Deslactosada");
        productoRepository.save(producto);

        AlertaStock alerta1 = crearAlerta(producto, "Stock bajo Leche", Prioridad.ALTA, false);
        AlertaStock alerta2 = crearAlerta(producto, "Stock bajo Leche", Prioridad.BAJA, false);
        alertaStockRepository.saveAll(List.of(alerta1, alerta2));

        // Act
        List<AlertaStock> altas = alertaStockRepository.findByPrioridad(Prioridad.ALTA);

        // Assert
        assertThat(altas).hasSize(1);
        assertThat(altas.get(0).getPrioridad()).isEqualTo(Prioridad.ALTA);
    }

    // ------------------------------------------------------
    // Test: Eliminar alertas atendidas
    // ------------------------------------------------------
    @Test
    void shouldDeleteAlertaStockByAtendidoTrue() {
        // Arrange
        Producto producto = crearProductoBase("Aceite de Oliva");
        productoRepository.save(producto);

        AlertaStock alertaAtendida = crearAlerta(producto, "Stock bajo Aceite", Prioridad.MEDIA, true);
        AlertaStock alertaPendiente = crearAlerta(producto, "Stock bajo Aceite", Prioridad.ALTA, false);
        alertaStockRepository.saveAll(List.of(alertaAtendida, alertaPendiente));

        // Act
        alertaStockRepository.deleteByAtendidoTrue();
        List<AlertaStock> restantes = alertaStockRepository.findAll();

        // Assert
        assertThat(restantes).hasSize(1);
        assertThat(restantes.get(0).getAtendido()).isFalse();
    }

    // ------------------------------------------------------
    // Métodos auxiliares para crear entidades
    // ------------------------------------------------------
    private Producto crearProductoBase(String nombre) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion("Producto de prueba");
        producto.setUnidadMedida("kg");
        producto.setCategoria("Grano");
        producto.setStockMinimo(5.0);
        producto.setStockActual(2.0);
        producto.setActivo(true);
        return producto;
    }

    private AlertaStock crearAlerta(Producto producto, String mensaje, Prioridad prioridad, boolean atendido) {
        AlertaStock alerta = new AlertaStock();
        alerta.setMensaje(mensaje);
        alerta.setPrioridad(prioridad);
        alerta.setAtendido(atendido);
        alerta.setProducto(producto);
        return alerta;
    }
}
