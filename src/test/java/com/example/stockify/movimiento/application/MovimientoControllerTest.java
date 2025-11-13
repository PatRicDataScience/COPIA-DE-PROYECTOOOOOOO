package com.example.stockify.movimiento.application;

import com.example.stockify.movimiento.domain.MovimientoService;
import com.example.stockify.movimiento.dto.MovimientoNewDTO;
import com.example.stockify.movimiento.dto.MovimientoRequestDTO;
import com.example.stockify.movimiento.domain.TipoMovimiento;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests principales para MovimientoController.
 * Adaptado al contenido real de MovimientoNewDTO.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(
        controllers = MovimientoController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.example\\.stockify\\.auth\\..*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org\\.springframework\\.security\\..*")
        }
)
@AutoConfigureMockMvc(addFilters = false)
class MovimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovimientoService movimientoService;

    @Autowired
    private ObjectMapper objectMapper;

    private MovimientoNewDTO newDTO;
    private MovimientoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        newDTO = new MovimientoNewDTO();
        newDTO.setTipoMovimiento(TipoMovimiento.ENTRADA);
        newDTO.setCantidad(10.0);
        newDTO.setCostoUnitario(2.5);
        newDTO.setObservacion("Ingreso por compra");
        newDTO.setOrigen("Proveedor");
        newDTO.setProductoId(1L);
        newDTO.setAlmacenId(1L);

        requestDTO = new MovimientoRequestDTO();
        requestDTO.setId(1L);
        requestDTO.setTipoMovimiento(TipoMovimiento.ENTRADA);
        requestDTO.setCantidad(10.0);
        requestDTO.setCostoUnitario(2.5);
        requestDTO.setObservacion("Ingreso por compra");
        requestDTO.setOrigen("Proveedor");
    }

    @Test
    void shouldRegisterEntradaWhenValidData() throws Exception {
        when(movimientoService.registrarEntrada(any(MovimientoNewDTO.class))).thenReturn(requestDTO);

        mockMvc.perform(post("/movimientos/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoMovimiento", is("ENTRADA")))
                .andExpect(jsonPath("$.cantidad", is(10.0)));
    }


    @Test
    void shouldRegisterSalidaManualWhenValidData() throws Exception {
        requestDTO.setTipoMovimiento(TipoMovimiento.SALIDA);
        when(movimientoService.registrarSalidaManual(any(MovimientoNewDTO.class))).thenReturn(requestDTO);

        mockMvc.perform(post("/movimientos/salida-manual")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoMovimiento", is("SALIDA_MANUAL")));
    }


    @Test
    void shouldReturnMovimientoWhenFindById() throws Exception {
        when(movimientoService.findById(1L)).thenReturn(requestDTO);

        mockMvc.perform(get("/movimientos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.tipoMovimiento", is("ENTRADA")));
    }


    @Test
    void shouldFilterMovimientosByParams() throws Exception {
        when(movimientoService.filtrarMovimientos(eq("ENTRADA"), eq(1L), eq(1L), eq("2025-01-01"), eq("2025-12-31")))
                .thenReturn(List.of(requestDTO));

        mockMvc.perform(get("/movimientos/filtrar")
                        .param("tipo", "ENTRADA")
                        .param("productoId", "1")
                        .param("almacenId", "1")
                        .param("desde", "2025-01-01")
                        .param("hasta", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipoMovimiento", is("ENTRADA")));
    }


    @Test
    void shouldAnularMovimientoWhenValidId() throws Exception {
        when(movimientoService.anularMovimiento(1L)).thenReturn(requestDTO);

        mockMvc.perform(put("/movimientos/anular/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.tipoMovimiento", is("ENTRADA")));
    }


    @Test
    void shouldReturnAllMovimientosWhenGetAll() throws Exception {
        when(movimientoService.findAll()).thenReturn(List.of(requestDTO));

        mockMvc.perform(get("/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cantidad", is(10.0)));
    }
}
