package com.example.stockify.alertaStock.domain;

import com.example.stockify.alertaStock.dto.AlertaStockNewDTO;
import com.example.stockify.alertaStock.dto.AlertaStockRequestDTO;
import com.example.stockify.alertaStock.infrastructure.AlertaStockRepository;
import com.example.stockify.excepciones.ResourceNotFoundException;
import com.example.stockify.producto.domain.Producto;
import com.example.stockify.producto.infrastructure.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaStockServiceTest {

    @Mock private AlertaStockRepository alertaStockRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private AlertaStockService alertaStockService;

    private Producto producto;
    private AlertaStock alerta;
    private AlertaStockNewDTO newDTO;
    private AlertaStockRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setStockActual(5.0);
        producto.setStockMinimo(10.0);

        alerta = new AlertaStock();
        alerta.setId(1L);
        alerta.setMensaje("Stock bajo de Arroz");
        alerta.setPrioridad(Prioridad.ALTA);
        alerta.setProducto(producto);
        alerta.setAtendido(false);
        alerta.setFechaAlerta(LocalDateTime.now());

        newDTO = new AlertaStockNewDTO();
        newDTO.setProductoId(1L);
        newDTO.setMensaje("Stock bajo de Arroz");
        newDTO.setPrioridad(Prioridad.ALTA);
        newDTO.setFechaAlerta(LocalDateTime.now());

        requestDTO = new AlertaStockRequestDTO();
        requestDTO.setId(1L);
        requestDTO.setMensaje("Stock bajo de Arroz");
        requestDTO.setPrioridad(Prioridad.ALTA);
        requestDTO.setAtendido(false);
        requestDTO.setProductoNombre("Arroz");
    }


    @Test
    void shouldCreateAlertaWhenProductoExists() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(modelMapper.map(any(AlertaStockNewDTO.class), eq(AlertaStock.class))).thenReturn(alerta);
        when(alertaStockRepository.save(any(AlertaStock.class))).thenReturn(alerta);
        when(modelMapper.map(any(AlertaStock.class), eq(AlertaStockRequestDTO.class))).thenReturn(requestDTO);

        var result = alertaStockService.crear(newDTO);

        assertThat(result).isNotNull();
        assertThat(result.getMensaje()).contains("Arroz");
        verify(alertaStockRepository).save(any());
    }


    @Test
    void shouldThrowExceptionWhenProductoNotFoundOnCreate() {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());
        when(modelMapper.map(any(AlertaStockNewDTO.class), eq(AlertaStock.class)))
                .thenReturn(new AlertaStock());

        assertThatThrownBy(() -> alertaStockService.crear(newDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Producto no encontrado");

    }


    @Test
    void shouldEditAlertaWhenExists() {
        when(alertaStockRepository.findById(1L)).thenReturn(Optional.of(alerta));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(alertaStockRepository.save(any())).thenReturn(alerta);
        when(modelMapper.map(any(AlertaStock.class), eq(AlertaStockRequestDTO.class))).thenReturn(requestDTO);

        var result = alertaStockService.editar(1L, newDTO);

        assertThat(result).isNotNull();
        assertThat(result.getMensaje()).isEqualTo("Stock bajo de Arroz");
        verify(alertaStockRepository).save(any());
    }


    @Test
    void shouldThrowExceptionWhenAlertaNotFoundOnEdit() {
        when(alertaStockRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alertaStockService.editar(1L, newDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Alerta no encontrada");
    }


    @Test
    void shouldMarkAlertaAsAtendidaWhenExists() {
        when(alertaStockRepository.findById(1L)).thenReturn(Optional.of(alerta));
        when(alertaStockRepository.save(any())).thenReturn(alerta);
        when(modelMapper.map(any(AlertaStock.class), eq(AlertaStockRequestDTO.class))).thenReturn(requestDTO);

        var result = alertaStockService.marcarComoAtendida(1L);

        assertThat(result).isNotNull();
        assertThat(alerta.getAtendido()).isTrue();
        verify(alertaStockRepository).save(any());
    }


    @Test
    void shouldThrowExceptionWhenMarkAtendidaNotFound() {
        when(alertaStockRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alertaStockService.marcarComoAtendida(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Alerta no encontrada");
    }


    @Test
    void shouldDeleteAlertaWhenExists() {
        when(alertaStockRepository.existsById(1L)).thenReturn(true);

        alertaStockService.eliminar(1L);

        verify(alertaStockRepository).deleteById(1L);
    }


    @Test
    void shouldThrowExceptionWhenDeleteNotFound() {
        when(alertaStockRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> alertaStockService.eliminar(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Alerta no encontrada");
    }
}
