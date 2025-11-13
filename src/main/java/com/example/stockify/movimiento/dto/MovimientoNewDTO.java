package com.example.stockify.movimiento.dto;

import com.example.stockify.movimiento.domain.TipoMovimiento;
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
public class MovimientoNewDTO {
    @NotNull(message = "El tipoMovimiento es obligatorio")
    private TipoMovimiento tipoMovimiento;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor que 0")
    private Double cantidad;

    @Positive(message = "El costoUnitario debe ser mayor que 0")
    private Double costoUnitario;

    @NotBlank(message = "La observación es obligatoria")
    @Size(max = 150, message = "La observación no debe superar los 150 caracteres")
    private String observacion;

    @NotBlank(message = "El origen es obligatorio")
    @Size(max = 100, message = "El origen no debe superar los 100 caracteres")
    private String origen;

    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;

    @NotNull(message = "El almacenId es obligatorio")
    private Long almacenId;
}
