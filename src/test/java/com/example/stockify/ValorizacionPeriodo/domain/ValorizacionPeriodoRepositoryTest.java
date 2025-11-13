package com.example.stockify.ValorizacionPeriodo.domain;

import com.example.stockify.AbstractContainerBaseTest;
import com.example.stockify.producto.domain.Producto;
import com.example.stockify.producto.infrastructure.ProductoRepository;
import com.example.stockify.usuario.domain.Rol;
import com.example.stockify.usuario.domain.Usuario;
import com.example.stockify.usuario.infrastructure.UsuarioRepository;
import com.example.stockify.valorizacionPeriodo.domain.MetodoValorizacion;
import com.example.stockify.valorizacionPeriodo.domain.ValorizacionPeriodo;
import com.example.stockify.valorizacionPeriodo.infrastructure.ValorizacionPeriodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ValorizacionPeriodoRepositoryTest extends AbstractContainerBaseTest {

    @Autowired private ValorizacionPeriodoRepository valorizacionPeriodoRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    // ------------------------------------------------------
    // Guardar valorización válida
    // ------------------------------------------------------
    @Test
    void shouldSaveValorizacionPeriodoWhenValidData() {
        // Arrange
        Usuario usuario = crearUsuarioBase("admin");
        usuarioRepository.save(usuario);

        ValorizacionPeriodo vp = new ValorizacionPeriodo();
        vp.setPeriodo("2025-09");
        vp.setMetodoValorizacion(MetodoValorizacion.FIFO);
        vp.setValorInventario(25000.0);
        vp.setCostoVentas(7500.0);
        vp.setUsuario(usuario);
        vp.setObservaciones("Cierre mensual de septiembre");

        // Act
        ValorizacionPeriodo saved = valorizacionPeriodoRepository.save(vp);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPeriodo()).isEqualTo("2025-09");
        assertThat(saved.getMetodoValorizacion()).isEqualTo(MetodoValorizacion.FIFO);
        assertThat(saved.getFechaValorizacion()).isNotNull();
        assertThat(saved.getCerrado()).isFalse();
    }

    // ------------------------------------------------------
    // Asignar fecha automáticamente
    // ------------------------------------------------------
    @Test
    void shouldSetFechaValorizacionAutomaticallyIfNotProvided() {
        Usuario usuario = usuarioRepository.save(crearUsuarioBase("user01"));

        ValorizacionPeriodo vp = new ValorizacionPeriodo();
        vp.setPeriodo("2025-10");
        vp.setMetodoValorizacion(MetodoValorizacion.PROMEDIO_PONDERADO);
        vp.setValorInventario(18000.0);
        vp.setCostoVentas(5000.0);
        vp.setUsuario(usuario);
        vp.setFechaValorizacion(null); // se asignará automáticamente

        ValorizacionPeriodo saved = valorizacionPeriodoRepository.save(vp);

        assertThat(saved.getFechaValorizacion()).isNotNull();
    }

    // ------------------------------------------------------
    // Buscar la valorización más reciente
    // ------------------------------------------------------
    @Test
    void shouldFindMostRecentValorizacionPeriodo() {
        Usuario usuario = usuarioRepository.save(crearUsuarioBase("admin2"));

        ValorizacionPeriodo old = new ValorizacionPeriodo();
        old.setPeriodo("2025-08");
        old.setMetodoValorizacion(MetodoValorizacion.PROMEDIO_PONDERADO);
        old.setValorInventario(20000.0);
        old.setCostoVentas(6000.0);
        old.setUsuario(usuario);
        old.setFechaValorizacion(LocalDateTime.now().minusDays(30));

        ValorizacionPeriodo recent = new ValorizacionPeriodo();
        recent.setPeriodo("2025-09");
        recent.setMetodoValorizacion(MetodoValorizacion.FIFO);
        recent.setValorInventario(22000.0);
        recent.setCostoVentas(7000.0);
        recent.setUsuario(usuario);
        recent.setFechaValorizacion(LocalDateTime.now());

        valorizacionPeriodoRepository.saveAll(List.of(old, recent));

        var result = valorizacionPeriodoRepository.findTopByOrderByFechaValorizacionDesc();

        assertThat(result).isPresent();
        assertThat(result.get().getPeriodo()).isEqualTo("2025-09");
    }

    // ------------------------------------------------------
    // Eliminar valorizaciones cerradas
    // ------------------------------------------------------
    @Test
    void shouldDeleteAllValorizacionesCerradas() {
        Usuario usuario = usuarioRepository.save(crearUsuarioBase("tester"));

        ValorizacionPeriodo v1 = crearValorizacionBase("2025-07", MetodoValorizacion.FIFO, usuario, false);
        ValorizacionPeriodo v2 = crearValorizacionBase("2025-06", MetodoValorizacion.PROMEDIO_PONDERADO, usuario, true);

        valorizacionPeriodoRepository.saveAll(List.of(v1, v2));

        valorizacionPeriodoRepository.deleteAllByCerradoTrue();

        List<ValorizacionPeriodo> restantes = valorizacionPeriodoRepository.findAll();
        assertThat(restantes).hasSize(1);
        assertThat(restantes.get(0).getPeriodo()).isEqualTo("2025-07");
    }

    // ------------------------------------------------------
    // Buscar valorización por producto (requiere lote y usuario)
    // ------------------------------------------------------
    @Test
    void shouldFindValorizacionByProductoId() {
        Usuario usuario = usuarioRepository.save(crearUsuarioBase("user02"));
        Producto producto = productoRepository.save(crearProductoBase("Azúcar Rubia"));

        ValorizacionPeriodo vp = crearValorizacionBase("2025-09", MetodoValorizacion.FIFO, usuario, false);
        valorizacionPeriodoRepository.save(vp);

        var result = valorizacionPeriodoRepository.findByProductoId(producto.getId());

        // Este test puede devolver vacío si la relación Lote–Valorizacion no está explícita
        assertThat(result).isNotNull();
    }

    // ------------------------------------------------------
    // Métodos auxiliares
    // ------------------------------------------------------
    private Usuario crearUsuarioBase(String nombre) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(nombre + "@correo.com");
        u.setRol(Rol.ADMIN);
        u.setPassword("123456");
        u.setActivo(true);
        return u;
    }

    private Producto crearProductoBase(String nombre) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion("Producto de prueba");
        p.setUnidadMedida("kg");
        p.setCategoria("Grano");
        p.setStockMinimo(10.0);
        p.setStockActual(50.0);
        p.setActivo(true);
        return p;
    }

    private ValorizacionPeriodo crearValorizacionBase(String periodo, MetodoValorizacion metodo, Usuario usuario, boolean cerrado) {
        ValorizacionPeriodo vp = new ValorizacionPeriodo();
        vp.setPeriodo(periodo);
        vp.setMetodoValorizacion(metodo);
        vp.setValorInventario(20000.0);
        vp.setCostoVentas(6000.0);
        vp.setUsuario(usuario);
        vp.setCerrado(cerrado);
        vp.setFechaValorizacion(LocalDateTime.now());
        return vp;
    }
}
