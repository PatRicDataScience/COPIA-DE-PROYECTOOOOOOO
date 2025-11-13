package com.example.stockify.producto.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequestDTO {
    @NotNull(message = "El ID del producto es obligatorio para la actualización")
    private Long id;

    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Size(min = 5, max = 255, message = "La descripción debe tener entre 5 y 255 caracteres")
    private String descripcion;

    @Size(max = 30, message = "La unidad de medida no puede superar los 30 caracteres")
    private String unidadMedida;

    @Size(max = 50, message = "La categoría no puede superar los 50 caracteres")
    private String categoria;

    @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
    private Double stockMinimo;

    @PositiveOrZero(message = "El stock actual no puede ser negativo")
    private Double stockActual;

    private Boolean activo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;
}
