package com.example.stockify.alertaStock.application;

import com.example.stockify.alertaStock.domain.AlertaStockService;
import com.example.stockify.alertaStock.domain.Prioridad;
import com.example.stockify.alertaStock.dto.AlertaStockNewDTO;
import com.example.stockify.alertaStock.dto.AlertaStockRequestDTO;
import com.example.stockify.auth.components.JwtAuthorizationFilter;
import com.example.stockify.auth.components.JwtService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AlertaStockController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class)
})

@AutoConfigureMockMvc(addFilters = false)
class AlertaStockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertaStockService alertaStockService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private AlertaStockNewDTO newDTO;
    private AlertaStockRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        newDTO = AlertaStockNewDTO.builder()
                .mensaje("Stock bajo en arroz")
                .prioridad(Prioridad.ALTA)
                .productoId(1L)
                .build();

        requestDTO = AlertaStockRequestDTO.builder()
                .id(1L)
                .mensaje("Stock bajo en arroz")
                .fechaAlerta(LocalDateTime.now())
                .atendido(false)
                .prioridad(Prioridad.ALTA)
                .productoId(1L)
                .productoNombre("Arroz Extra")
                .build();
    }

    @Test
    void shouldCreateAlertaWhenValidData() throws Exception {
        when(alertaStockService.crear(any(AlertaStockNewDTO.class))).thenReturn(requestDTO);

        mockMvc.perform(post("/alertas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", is("Stock bajo en arroz")))
                .andExpect(jsonPath("$.prioridad", is("ALTA")));

        verify(alertaStockService).crear(any(AlertaStockNewDTO.class));
    }

    @Test
    void shouldReturnAllAlertasWhenGetAll() throws Exception {
        when(alertaStockService.findAll()).thenReturn(List.of(requestDTO));

        mockMvc.perform(get("/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].mensaje", is("Stock bajo en arroz")));
    }

    @Test
    void shouldReturnPendientesWhenGetPendientes() throws Exception {
        when(alertaStockService.findPendientes()).thenReturn(List.of(requestDTO));

        mockMvc.perform(get("/alertas/pendientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].atendido", is(false)));
    }

    @Test
    void shouldReturnAlertaWhenFindById() throws Exception {
        when(alertaStockService.findById(1L)).thenReturn(requestDTO);

        mockMvc.perform(get("/alertas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.mensaje", is("Stock bajo en arroz")));
    }

    @Test
    void shouldMarkAlertaAsAtendidaWhenPutRequest() throws Exception {
        requestDTO.setAtendido(true);
        when(alertaStockService.marcarComoAtendida(1L)).thenReturn(requestDTO);

        mockMvc.perform(put("/alertas/{id}/atender", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.atendido", is(true)));

        verify(alertaStockService).marcarComoAtendida(1L);
    }

    @Test
    void shouldDeleteAlertaWhenDeleteRequest() throws Exception {
        doNothing().when(alertaStockService).eliminar(1L);

        mockMvc.perform(delete("/alertas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(alertaStockService).eliminar(1L);
    }
}
