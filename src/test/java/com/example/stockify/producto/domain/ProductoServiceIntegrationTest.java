package com.example.stockify.producto.domain;

import com.example.stockify.AbstractContainerBaseTest;
import com.example.stockify.producto.dto.ProductoNewDTO;
import com.example.stockify.producto.dto.ProductoRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductoServiceIntegrationTest extends AbstractContainerBaseTest {

    @Autowired
    private ProductoService productoService;

    @Test
    void shouldCreateProductoWhenValidRequest() {
        ProductoNewDTO dto = new ProductoNewDTO();
        dto.setNombre("Café Molido");
        dto.setDescripcion("Café orgánico premium");
        dto.setUnidadMedida("kg");
        dto.setCategoria("Bebidas");
        dto.setStockMinimo(5.0);
        dto.setStockActual(25.0);
        dto.setActivo(true);

        ProductoRequestDTO result = productoService.create(dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Café Molido");
    }
}
