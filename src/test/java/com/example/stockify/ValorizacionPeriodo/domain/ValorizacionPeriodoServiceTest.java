package com.example.stockify.ValorizacionPeriodo.domain;

import com.example.stockify.excepciones.ResourceNotFoundException;
import com.example.stockify.lote.domain.Lote;
import com.example.stockify.lote.infrastructure.LoteRepository;
import com.example.stockify.movimiento.domain.Movimiento;
import com.example.stockify.movimiento.domain.TipoMovimiento;
import com.example.stockify.movimiento.infrastructure.MovimientoRepository;
import com.example.stockify.usuario.domain.Usuario;
import com.example.stockify.usuario.infrastructure.UsuarioRepository;
import com.example.stockify.valorizacionPeriodo.domain.MetodoValorizacion;
import com.example.stockify.valorizacionPeriodo.domain.ValorizacionPeriodo;
import com.example.stockify.valorizacionPeriodo.domain.ValorizacionPeriodoService;
import com.example.stockify.valorizacionPeriodo.dto.ValorizacionPeriodoRequestDTO;
import com.example.stockify.valorizacionPeriodo.infrastructure.ValorizacionPeriodoRepository;
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
class ValorizacionPeriodoServiceTest {

    @Mock private ValorizacionPeriodoRepository valorizacionPeriodoRepository;
    @Mock private LoteRepository loteRepository;
    @Mock private MovimientoRepository movimientoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private ValorizacionPeriodoService valorizacionPeriodoService;

    private ValorizacionPeriodo valorizacion;
    private ValorizacionPeriodoRequestDTO dto;
    private Usuario usuario;
    private Lote lote;
    private Movimiento movimiento;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Admin");

        lote = new Lote();
        lote.setId(1L);
        lote.setCantidadDisponible(5.0);
        lote.setCostoUnitario(10.0);

        movimiento = new Movimiento();
        movimiento.setId(1L);
        movimiento.setTipoMovimiento(TipoMovimiento.SALIDA);
        movimiento.setCostoTotal(100.0);
        movimiento.setFechaMovimiento(LocalDateTime.of(2025, 9, 10, 10, 0));

        valorizacion = new ValorizacionPeriodo();
        valorizacion.setId(1L);
        valorizacion.setPeriodo("2025-09");
        valorizacion.setMetodoValorizacion(MetodoValorizacion.FIFO);
        valorizacion.setValorInventario(500.0);
        valorizacion.setCostoVentas(100.0);
        valorizacion.setUsuario(usuario);
        valorizacion.setCerrado(false);
        valorizacion.setFechaValorizacion(LocalDateTime.now());

        dto = new ValorizacionPeriodoRequestDTO();
        dto.setId(1L);
        dto.setPeriodo("2025-09");
        dto.setMetodoValorizacion(MetodoValorizacion.FIFO);
        dto.setValorInventario(500.0);
        dto.setCostoVentas(100.0);
    }

    @Test
    void shouldReturnAllValorizacionesWhenFindAll() {
        when(valorizacionPeriodoRepository.findAll()).thenReturn(List.of(valorizacion));
        when(modelMapper.map(any(ValorizacionPeriodo.class), eq(ValorizacionPeriodoRequestDTO.class)))
                .thenReturn(dto);

        var result = valorizacionPeriodoService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPeriodo()).isEqualTo("2025-09");
        verify(valorizacionPeriodoRepository).findAll();
    }


    @Test
    void shouldExecuteValorizacionWhenValidData() {
        when(loteRepository.findAll()).thenReturn(List.of(lote));
        when(movimientoRepository.findAll()).thenReturn(List.of(movimiento));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(valorizacionPeriodoRepository.save(any())).thenReturn(valorizacion);
        when(modelMapper.map(any(ValorizacionPeriodo.class), eq(ValorizacionPeriodoRequestDTO.class)))
                .thenReturn(dto);

        var result = valorizacionPeriodoService.ejecutarValorizacion("2025-09", MetodoValorizacion.FIFO, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getPeriodo()).isEqualTo("2025-09");
        verify(valorizacionPeriodoRepository).save(any());
        verify(loteRepository).findAll();
        verify(movimientoRepository).findAll();
    }


    @Test
    void shouldThrowExceptionWhenFindByIdNotFound() {
        when(valorizacionPeriodoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> valorizacionPeriodoService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Valorización no encontrada");
    }


    @Test
    void shouldClosePeriodoWhenExists() {
        when(valorizacionPeriodoRepository.findById(1L)).thenReturn(Optional.of(valorizacion));
        when(valorizacionPeriodoRepository.save(any())).thenReturn(valorizacion);
        when(modelMapper.map(any(ValorizacionPeriodo.class), eq(ValorizacionPeriodoRequestDTO.class)))
                .thenReturn(dto);

        var result = valorizacionPeriodoService.cerrarPeriodo(1L, "Cierre finalizado");

        assertThat(result).isNotNull();
        verify(valorizacionPeriodoRepository).findById(1L);
        verify(valorizacionPeriodoRepository).save(any());
    }
}
