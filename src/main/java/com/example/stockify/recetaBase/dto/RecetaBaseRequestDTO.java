package com.example.stockify.recetaBase.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaBaseRequestDTO {
    @NotNull(message = "El ID de la receta base es obligatorio para la actualización")
    private Long id;

    @Size(min = 2, max = 100, message = "El nombre del plato debe tener entre 2 y 100 caracteres")
    private String nombrePlato;

    @Size(min = 5, max = 150, message = "La descripción debe tener entre 5 y 150 caracteres")
    private String descripcion;

    @Positive(message = "Las porciones base deben ser mayores a 0")
    private Integer porcionesBase;

    @Size(max = 50, message = "La unidad de porción no puede superar los 50 caracteres")
    private String unidadPorcion;
}
