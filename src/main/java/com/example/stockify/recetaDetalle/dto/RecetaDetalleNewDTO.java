package com.example.stockify.recetaDetalle.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaDetalleNewDTO {

    @NotNull(message = "El ID de la receta base es obligatorio")
    private Long recetaBaseId;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad necesaria es obligatoria")
    @Positive(message = "La cantidad necesaria debe ser positiva")
    private Double cantidadNecesaria;

    @NotBlank(message = "La unidad de medida es obligatoria")
    @Size(max = 20, message = "La unidad de medida no puede superar los 20 caracteres")
    private String unidadMedida;
}