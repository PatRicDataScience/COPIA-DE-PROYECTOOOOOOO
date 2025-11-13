package com.example.stockify.producto.domain;

import com.example.stockify.AbstractContainerBaseTest;
import com.example.stockify.producto.infrastructure.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductoRepositoryIntegrationTest extends AbstractContainerBaseTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void shouldSaveProductoWhenValidData() {
        Producto producto = new Producto();
        producto.setNombre("Azúcar Rubia");
        producto.setDescripcion("Azúcar de caña natural");
        producto.setUnidadMedida("kg");
        producto.setCategoria("Granos");
        producto.setStockMinimo(10.0);
        producto.setStockActual(50.0);
        producto.setActivo(true);

        Producto saved = productoRepository.save(producto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNombre()).isEqualTo("Azúcar Rubia");
    }
}
