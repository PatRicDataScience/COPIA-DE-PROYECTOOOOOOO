package com.example.stockify.producto.domain;

import com.example.stockify.excepciones.BadRequestException;
import com.example.stockify.excepciones.ResourceNotFoundException;
import com.example.stockify.excepciones.ValidacionException;
import com.example.stockify.producto.dto.ProductoNewDTO;
import com.example.stockify.producto.dto.ProductoRequestDTO;
import com.example.stockify.producto.infrastructure.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private ProductoNewDTO newDto;
    private ProductoRequestDTO requestDto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Azúcar Rubia");
        producto.setDescripcion("Azúcar de caña natural");
        producto.setUnidadMedida("kg");
        producto.setCategoria("Granos");
        producto.setStockMinimo(10.0);
        producto.setStockActual(50.0);
        producto.setActivo(true);

        newDto = new ProductoNewDTO();
        newDto.setNombre("Azúcar Rubia");
        newDto.setDescripcion("Azúcar de caña natural");
        newDto.setUnidadMedida("kg");
        newDto.setCategoria("Granos");
        newDto.setStockMinimo(10.0);
        newDto.setStockActual(50.0);
        newDto.setActivo(true);

        requestDto = new ProductoRequestDTO();
        requestDto.setId(1L);
        requestDto.setNombre("Azúcar Rubia");


    }


    @Test
    void shouldReturnAllProductsWhenFindAll() {
        when(productoRepository.findAll()).thenReturn(List.of(producto));
        when(modelMapper.map(any(), eq(ProductoRequestDTO.class))).thenReturn(requestDto);

        var result = productoService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Azúcar Rubia");
        verify(productoRepository).findAll();
    }

    @Test
    void shouldReturnProductWhenFindByIdExists() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(modelMapper.map(any(), eq(ProductoRequestDTO.class))).thenReturn(requestDto);

        var result = productoService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        verify(productoRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenFindByIdNotFound() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Producto no encontrado");
    }


    @Test
    void shouldCreateProductWhenValidData() {
        when(productoRepository.findByNombreContainingIgnoreCase(anyString())).thenReturn(List.of());
        when(modelMapper.map(newDto, Producto.class)).thenReturn(producto);
        when(productoRepository.save(producto)).thenReturn(producto);
        when(modelMapper.map(producto, ProductoRequestDTO.class)).thenReturn(requestDto);

        var result = productoService.create(newDto);

        assertThat(result.getNombre()).isEqualTo("Azúcar Rubia");
        verify(productoRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCreateDuplicateProduct() {
        when(productoRepository.findByNombreContainingIgnoreCase(anyString()))
                .thenReturn(List.of(producto));

        assertThatThrownBy(() -> productoService.create(newDto))
                .isInstanceOf(ValidacionException.class)
                .hasMessageContaining("Ya existe un producto");
    }


    @Test
    void shouldUpdateProductWhenExists() {
        // Given
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any())).thenReturn(producto);
        doReturn(requestDto).when(modelMapper).map(any(Producto.class), eq(ProductoRequestDTO.class));

        // When
        var result = productoService.update(1L, newDto);

        // Then
        assertThat(result.getNombre()).isEqualTo("Azúcar Rubia");
        verify(productoRepository).findById(1L);
        verify(productoRepository).save(any());

    }

    @Test
    void shouldThrowExceptionWhenUpdateNotFound() {
        when(productoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.update(1L, newDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void shouldMarkProductInactiveWhenDeleteById() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        productoService.deleteById(1L);

        assertThat(producto.getActivo()).isFalse();
        verify(productoRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenDeleteNotFound() {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.deleteById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void shouldUpdateFullProductWhenAllFieldsPresent() {
        // Given
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any())).thenReturn(producto);
        doReturn(requestDto).when(modelMapper).map(any(Producto.class), eq(ProductoRequestDTO.class));

        // When
        var result = productoService.updateFull(1L, newDto);

        // Then
        assertThat(result.getNombre()).isEqualTo("Azúcar Rubia");
        verify(productoRepository).findById(1L);
        verify(productoRepository).save(any());

    }

    @Test
    void shouldThrowBadRequestWhenUpdateFullMissingFields() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        newDto.setDescripcion(null);

        assertThatThrownBy(() -> productoService.updateFull(1L, newDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("obligatorios");
    }


    @Test
    void shouldUpdatePartialWhenValidFieldsProvided() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any())).thenReturn(producto);
        when(modelMapper.map(producto, ProductoRequestDTO.class)).thenReturn(requestDto);

        newDto.setDescripcion("Actualizada");
        var result = productoService.updatePartial(1L, newDto);

        assertThat(result.getNombre()).isEqualTo("Azúcar Rubia");
        verify(productoRepository).save(any());
    }


    @Test
    void shouldFilterProductsByCategoriaAndActivo() {
        when(productoRepository.findByCategoriaIgnoreCaseAndActivo("Granos", true))
                .thenReturn(List.of(producto));
        when(modelMapper.map(any(), eq(ProductoRequestDTO.class))).thenReturn(requestDto);

        var result = productoService.filtrar("Granos", true);

        assertThat(result).hasSize(1);
        verify(productoRepository).findByCategoriaIgnoreCaseAndActivo("Granos", true);
    }
}
