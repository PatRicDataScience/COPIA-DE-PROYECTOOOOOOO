package com.example.stockify.movimiento.domain;

import com.example.stockify.alertaStock.infrastructure.AlertaStockRepository;
import com.example.stockify.almacen.domain.Almacen;
import com.example.stockify.almacen.infrastructure.AlmacenRepository;
import com.example.stockify.excepciones.StockInsuficienteException;
import com.example.stockify.lote.domain.Lote;
import com.example.stockify.lote.dto.Estado;
import com.example.stockify.lote.infrastructure.LoteRepository;
import com.example.stockify.movimiento.dto.MovimientoNewDTO;
import com.example.stockify.movimiento.dto.MovimientoRequestDTO;
import com.example.stockify.movimiento.infrastructure.MovimientoRepository;
import com.example.stockify.producto.domain.Producto;
import com.example.stockify.producto.infrastructure.ProductoRepository;
import com.example.stockify.recetaBase.infrastructure.RecetaBaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock private ProductoRepository productoRepository;
    @Mock private LoteRepository loteRepository;
    @Mock private AlmacenRepository almacenRepository;
    @Mock private MovimientoRepository movimientoRepository;
    @Mock private RecetaBaseRepository recetaBaseRepository;
    @Mock private AlertaStockRepository alertaStockRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private MovimientoService movimientoService;

    private Producto producto;
    private Almacen almacen;
    private Lote lote;
    private Movimiento movimiento;
    private MovimientoNewDTO newDTO;
    private MovimientoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Harina");
        producto.setStockMinimo(10.0);
        producto.setStockActual(50.0);

        almacen = new Almacen();
        almacen.setId(1L);
        almacen.setNombre("Almacén Central");

        lote = Lote.builder()
                .id(1L)
                .producto(producto)
                .almacen(almacen)
                .cantidadInicial(10.0)
                .cantidadDisponible(10.0)
                .estado(Estado.ACTIVO)
                .fechaCompra(LocalDateTime.now())
                .build();

        movimiento = Movimiento.builder()
                .id(1L)
                .producto(producto)
                .almacen(almacen)
                .cantidad(10.0)
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .anulado(false)
                .build();

        newDTO = new MovimientoNewDTO();
        newDTO.setProductoId(1L);
        newDTO.setAlmacenId(1L);
        newDTO.setCantidad(10.0);
        newDTO.setCostoUnitario(5.0);
        newDTO.setOrigen("Compra Proveedor");
        newDTO.setObservacion("Ingreso por compra");


        requestDTO = new MovimientoRequestDTO();
        requestDTO.setId(1L);
        requestDTO.setCantidad(10.0);
        requestDTO.setCostoUnitario(5.0);
        requestDTO.setCostoTotal(50.0);
        requestDTO.setOrigen("Compra Proveedor");
        requestDTO.setObservacion("Ingreso por compra");
    }


    @Test
    void shouldReturnAllMovimientosWhenFindAll() {
        when(movimientoRepository.findAll()).thenReturn(List.of(movimiento));
        when(modelMapper.map(any(Movimiento.class), eq(MovimientoRequestDTO.class))).thenReturn(requestDTO);

        var result = movimientoService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getCantidad()).isEqualTo(10.0);
        verify(movimientoRepository).findAll();
    }


    @Test
    void shouldRegisterEntradaWhenProductoAndAlmacenExist() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(almacenRepository.findById(1L)).thenReturn(Optional.of(almacen));
        when(loteRepository.save(any())).thenReturn(lote);
        when(productoRepository.save(any())).thenReturn(producto);
        when(movimientoRepository.save(any())).thenReturn(movimiento);
        when(modelMapper.map(any(Movimiento.class), eq(MovimientoRequestDTO.class))).thenReturn(requestDTO);

        var result = movimientoService.registrarEntrada(newDTO);

        assertThat(result).isNotNull();
        assertThat(result.getCantidad()).isEqualTo(10.0);
        verify(productoRepository).save(any());
        verify(loteRepository).save(any());
        verify(movimientoRepository).save(any());
    }


    @Test
    void shouldThrowStockInsuficienteExceptionWhenSalidaExceedsStock() {
        producto.setStockActual(5.0); // menor al solicitado
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        newDTO.setCantidad(20.0); // intenta sacar más de lo disponible

        assertThatThrownBy(() -> movimientoService.registrarSalidaManual(newDTO))
                .isInstanceOf(StockInsuficienteException.class)
                .hasMessageContaining("Stock insuficiente");
    }


    @Test
    void shouldAnularMovimientoWhenExists() {
        movimiento.setTipoMovimiento(TipoMovimiento.ENTRADA);
        movimiento.setCantidad(5.0);
        movimiento.setLote(lote);

        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(movimiento));
        when(movimientoRepository.save(any())).thenReturn(movimiento);
        when(productoRepository.save(any())).thenReturn(producto);
        when(loteRepository.save(any())).thenReturn(lote);
        when(modelMapper.map(any(Movimiento.class), eq(MovimientoRequestDTO.class))).thenReturn(requestDTO);

        var result = movimientoService.anularMovimiento(1L);

        assertThat(result).isNotNull();
        assertThat(movimiento.getAnulado()).isTrue();
        verify(movimientoRepository).save(any());
        verify(productoRepository).save(any());
        verify(loteRepository).save(any());
    }
}
