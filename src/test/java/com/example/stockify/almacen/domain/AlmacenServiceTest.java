package com.example.stockify.almacen.domain;

import com.example.stockify.almacen.dto.AlmacenNewDTO;
import com.example.stockify.almacen.dto.AlmacenRequestDTO;
import com.example.stockify.almacen.infrastructure.AlmacenRepository;
import com.example.stockify.excepciones.BadRequestException;
import com.example.stockify.excepciones.ResourceNotFoundException;
import com.example.stockify.excepciones.ValidacionException;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlmacenServiceTest {

    @Mock private AlmacenRepository almacenRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private AlmacenService almacenService;

    private Almacen almacen;
    private AlmacenNewDTO newDTO;
    private AlmacenRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        almacen = new Almacen();
        almacen.setId(1L);
        almacen.setNombre("Central Lima");
        almacen.setUbicacion("Av. Colonial 123");
        almacen.setResponsable("Juan Pérez");
        almacen.setCapacidadMaxima(1000.0);
        almacen.setActivo(true);

        newDTO = new AlmacenNewDTO();
        newDTO.setNombre("Central Lima");
        newDTO.setUbicacion("Av. Colonial 123");
        newDTO.setResponsable("Juan Pérez");
        newDTO.setCapacidadMaxima(1000.0);
        newDTO.setActivo(true);

        requestDTO = new AlmacenRequestDTO();
        requestDTO.setId(1L);
        requestDTO.setNombre("Central Lima");
        requestDTO.setUbicacion("Av. Colonial 123");
        requestDTO.setResponsable("Juan Pérez");
        requestDTO.setActivo(true);
    }


    @Test
    void shouldCreateAlmacenWhenValidData() {
        when(modelMapper.map(any(AlmacenNewDTO.class), eq(Almacen.class))).thenReturn(almacen);
        when(almacenRepository.save(any(Almacen.class))).thenReturn(almacen);
        when(modelMapper.map(any(Almacen.class), eq(AlmacenRequestDTO.class))).thenReturn(requestDTO);

        var result = almacenService.create(newDTO);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Central Lima");
        verify(almacenRepository).save(any(Almacen.class));
    }


    @Test
    void shouldThrowExceptionWhenNombreIsEmptyOnCreate() {
        newDTO.setNombre("  ");

        assertThatThrownBy(() -> almacenService.create(newDTO))
                .isInstanceOf(ValidacionException.class)
                .hasMessageContaining("obligatorio");
    }

    @Test
    void shouldUpdatePartialAlmacenWhenExists() {
        when(almacenRepository.findById(1L)).thenReturn(Optional.of(almacen));
        when(almacenRepository.save(any(Almacen.class))).thenReturn(almacen);
        when(modelMapper.map(any(Almacen.class), eq(AlmacenRequestDTO.class))).thenReturn(requestDTO);

        var result = almacenService.updatePartial(1L, newDTO);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Central Lima");
        verify(almacenRepository).save(any(Almacen.class));
    }


    @Test
    void shouldThrowExceptionWhenUpdateWithNullDTO() {
        when(almacenRepository.findById(1L)).thenReturn(Optional.of(almacen));

        assertThatThrownBy(() -> almacenService.updatePartial(1L, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("No se ha enviado información");
    }


    @Test
    void shouldThrowExceptionWhenNotFoundOnDelete() {
        when(almacenRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> almacenService.deleteById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("no encontrado");
    }


    @Test
    void shouldFindActivosWhenExist() {
        when(almacenRepository.findByActivo(true)).thenReturn(List.of(almacen));
        when(modelMapper.map(any(Almacen.class), eq(AlmacenRequestDTO.class))).thenReturn(requestDTO);

        var result = almacenService.listarActivos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Central Lima");
        verify(almacenRepository).findByActivo(true);
    }


    @Test
    void shouldThrowExceptionWhenNoActivosFound() {
        when(almacenRepository.findByActivo(true)).thenReturn(List.of());

        assertThatThrownBy(() -> almacenService.listarActivos())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No hay almacenes activos");
    }


    @Test
    void shouldChangeEstadoWhenValid() {
        when(almacenRepository.findById(1L)).thenReturn(Optional.of(almacen));
        when(almacenRepository.save(any(Almacen.class))).thenReturn(almacen);
        when(modelMapper.map(any(Almacen.class), eq(AlmacenRequestDTO.class))).thenReturn(requestDTO);

        var result = almacenService.actualizarEstado(1L, false);

        assertThat(result).isNotNull();
        verify(almacenRepository).save(any(Almacen.class));
    }


    @Test
    void shouldThrowExceptionWhenEstadoIsNull() {
        when(almacenRepository.findById(1L)).thenReturn(Optional.of(almacen));

        assertThatThrownBy(() -> almacenService.actualizarEstado(1L, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Debe especificar el nuevo estado");
    }
}
