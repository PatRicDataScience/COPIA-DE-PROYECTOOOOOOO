package com.example.stockify.recetaBase.dto;

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
public class RecetaBaseNewDTO {
    @NotBlank(message = "El nombre del plato es obligatorio")
    @Size(max = 100, message = "El nombre del plato no puede superar los 100 caracteres")
    private String nombrePlato;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 150, message = "La descripción no puede superar los 150 caracteres")
    private String descripcion;

    @NotNull(message = "Las porciones base son obligatorias")
    @Positive(message = "Las porciones base deben ser mayores a 0")
    private Integer porcionesBase;

    @NotBlank(message = "La unidad de porción es obligatoria")
    @Size(max = 50, message = "La unidad de porción no puede superar los 50 caracteres")
    private String unidadPorcion;

}
