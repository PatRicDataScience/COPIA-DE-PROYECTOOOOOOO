package com.example.stockify.almacen.domain;

import com.example.stockify.AbstractContainerBaseTest;
import com.example.stockify.almacen.infrastructure.AlmacenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AlmacenRepositoryTest extends AbstractContainerBaseTest {

    @Autowired
    private AlmacenRepository almacenRepository;

    // ------------------------------------------------------
    // Test: Guardar un almacén con datos válidos
    // ------------------------------------------------------
    @Test
    void shouldSaveAlmacenWhenValidData() {
        // Arrange
        Almacen almacen = new Almacen();
        almacen.setNombre("Almacén Central");
        almacen.setUbicacion("Av. Los Héroes 345, Lima");
        almacen.setResponsable("Juan Pérez");
        almacen.setCapacidadMaxima(1000.0);
        almacen.setActivo(true);

        // Act
        Almacen saved = almacenRepository.save(almacen);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNombre()).isEqualTo("Almacén Central");
        assertThat(saved.getActivo()).isTrue();
        assertThat(saved.getFechaCreacion()).isNotNull();
    }

    // ------------------------------------------------------
    // Test: Actualizar un almacén existente
    // ------------------------------------------------------
    @Test
    void shouldUpdateAlmacenWhenExistingRecordIsModified() {
        // Arrange
        Almacen almacen = new Almacen();
        almacen.setNombre("Depósito Secundario");
        almacen.setUbicacion("Calle 25, Arequipa");
        almacen.setResponsable("Luis Gómez");
        almacen.setCapacidadMaxima(500.0);
        almacen.setActivo(true);
        Almacen saved = almacenRepository.save(almacen);

        // Act
        saved.setResponsable("Carlos Gómez");
        saved.setActivo(false);
        Almacen updated = almacenRepository.save(saved);

        // Assert
        Optional<Almacen> found = almacenRepository.findById(updated.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getResponsable()).isEqualTo("Carlos Gómez");
        assertThat(found.get().getActivo()).isFalse();
    }

    // ------------------------------------------------------
    // Test: Filtrar almacenes activos
    // ------------------------------------------------------
    @Test
    void shouldFindAlmacenesByActivoTrue() {
        // Arrange
        Almacen activo = crearAlmacenBase("Almacén Norte", true);
        Almacen inactivo = crearAlmacenBase("Almacén Sur", false);
        almacenRepository.saveAll(List.of(activo, inactivo));

        // Act
        List<Almacen> activos = almacenRepository.findByActivo(true);

        // Assert
        assertThat(activos).hasSize(1);
        assertThat(activos.get(0).getNombre()).isEqualTo("Almacén Norte");
    }

    // ------------------------------------------------------
    // Test: Buscar por coincidencia parcial del nombre
    // ------------------------------------------------------
    @Test
    void shouldFindAlmacenesByNombreContainingIgnoreCase() {
        // Arrange
        Almacen a1 = crearAlmacenBase("Almacén Central", true);
        Almacen a2 = crearAlmacenBase("Depósito Central", true);
        almacenRepository.saveAll(List.of(a1, a2));

        // Act
        List<Almacen> resultados = almacenRepository.findByNombreContainingIgnoreCase("central");

        // Assert
        assertThat(resultados).hasSize(2);
    }

    // ------------------------------------------------------
    // Test: Verificar timestamps automáticos
    // ------------------------------------------------------
    @Test
    void shouldSetTimestampsOnPersistAndUpdateAutomatically() throws InterruptedException {
        // Arrange
        Almacen almacen = crearAlmacenBase("Almacén Temporal", true);
        Almacen saved = almacenRepository.save(almacen);
        var fechaCreacionInicial = saved.getFechaCreacion();

        Thread.sleep(1000); // diferencia temporal visible

        // Act
        saved.setResponsable("Pedro Ramírez");
        Almacen updated = almacenRepository.save(saved);
        almacenRepository.flush();
        Almacen reloaded = almacenRepository.findById(updated.getId()).orElseThrow();

        // Assert
        assertThat(reloaded.getFechaCreacion()).isEqualTo(fechaCreacionInicial);
        assertThat(reloaded.getUltimoActualizado()).isAfterOrEqualTo(fechaCreacionInicial);
    }

    // ------------------------------------------------------
    // Métodos auxiliares
    // ------------------------------------------------------
    private Almacen crearAlmacenBase(String nombre, boolean activo) {
        Almacen almacen = new Almacen();
        almacen.setNombre(nombre);
        almacen.setUbicacion("Ubicación genérica");
        almacen.setResponsable("Responsable de prueba");
        almacen.setCapacidadMaxima(800.0);
        almacen.setActivo(activo);
        return almacen;
    }
}
