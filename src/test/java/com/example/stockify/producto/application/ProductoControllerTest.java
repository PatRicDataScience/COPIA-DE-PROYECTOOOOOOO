package com.example.stockify.producto.application;

import com.example.stockify.producto.domain.ProductoService;
import com.example.stockify.producto.dto.ProductoNewDTO;
import com.example.stockify.producto.dto.ProductoRequestDTO;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(
        controllers = ProductoController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.example\\.stockify\\.auth\\..*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org\\.springframework\\.security\\..*")
        }
)
@AutoConfigureMockMvc(addFilters = false)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductoNewDTO newDTO;
    private ProductoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        newDTO = new ProductoNewDTO();
        newDTO.setNombre("Azúcar Rubia");
        newDTO.setDescripcion("Azúcar de caña natural");
        newDTO.setUnidadMedida("kg");
        newDTO.setCategoria("Granos");
        newDTO.setStockMinimo(10.0);
        newDTO.setStockActual(50.0);
        newDTO.setActivo(true);

        requestDTO = new ProductoRequestDTO();
        requestDTO.setId(1L);
        requestDTO.setNombre("Azúcar Rubia");
        requestDTO.setDescripcion("Azúcar de caña natural");
        requestDTO.setUnidadMedida("kg");
        requestDTO.setCategoria("Granos");
        requestDTO.setStockMinimo(10.0);
        requestDTO.setStockActual(50.0);
        requestDTO.setActivo(true);
    }


    @Test
    void shouldCreateProductoWhenValidData() throws Exception {
        when(productoService.create(any(ProductoNewDTO.class))).thenReturn(requestDTO);

        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre", is("Azúcar Rubia")))
                .andExpect(jsonPath("$.categoria", is("Granos")));

        verify(productoService).create(any(ProductoNewDTO.class));
    }


    @Test
    void shouldReturnListOfProductosWhenGetAll() throws Exception {
        when(productoService.findAll()).thenReturn(List.of(requestDTO));

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Azúcar Rubia")));
    }


    @Test
    void shouldReturnProductoWhenFindById() throws Exception {
        when(productoService.findById(1L)).thenReturn(requestDTO);

        mockMvc.perform(get("/productos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Azúcar Rubia")));
    }


    @Test
    void shouldReturn204WhenDeleteProducto() throws Exception {
        doNothing().when(productoService).deleteById(1L);

        mockMvc.perform(delete("/productos/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(productoService).deleteById(1L);
    }


    @Test
    void shouldUpdateProductoWhenPutRequest() throws Exception {
        when(productoService.updateFull(eq(1L), any(ProductoNewDTO.class))).thenReturn(requestDTO);

        mockMvc.perform(put("/productos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Azúcar Rubia")))
                .andExpect(jsonPath("$.activo", is(true)));

        verify(productoService).updateFull(eq(1L), any(ProductoNewDTO.class));
    }


    @Test
    void shouldUpdateProductoPartiallyWhenPatchRequest() throws Exception {
        when(productoService.updatePartial(eq(1L), any(ProductoNewDTO.class))).thenReturn(requestDTO);

        mockMvc.perform(patch("/productos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Azúcar Rubia")));

        verify(productoService).updatePartial(eq(1L), any(ProductoNewDTO.class));
    }


    @Test
    void shouldFilterProductosByCategoriaAndActivo() throws Exception {
        when(productoService.filtrar(eq("Granos"), eq(true))).thenReturn(List.of(requestDTO));

        mockMvc.perform(get("/productos/filtrar")
                        .param("categoria", "Granos")
                        .param("activo", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].categoria", is("Granos")))
                .andExpect(jsonPath("$[0].activo", is(true)));

        verify(productoService).filtrar(eq("Granos"), eq(true));
    }


    @Test
    void shouldListProductosByActivo() throws Exception {
        when(productoService.listarPorActivo(true)).thenReturn(List.of(requestDTO));

        mockMvc.perform(get("/productos/activo")
                        .param("activo", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].activo", is(true)));

        verify(productoService).listarPorActivo(true);
    }
}
