package com.example.stockify.recetaDetalle.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaDetalleRequestDTO {
    @NotNull(message = "El ID del detalle de receta es obligatorio para la actualización")
    private Long id;

    @Positive(message = "La cantidad necesaria debe ser positiva")
    private Double cantidadNecesaria;

    @Size(max = 20, message = "La unidad de medida no puede superar los 20 caracteres")
    private String unidadMedida;

    private Long productoId;
    private Long recetaBaseId;
}
