package com.example.stockify.valorizacionPeriodo.dto;

import com.example.stockify.valorizacionPeriodo.domain.MetodoValorizacion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValorizacionPeriodoNewDTO {
    @NotBlank(message = "El periodo es obligatorio (formato: YYYY-MM)")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "El periodo debe tener el formato YYYY-MM")
    private String periodo;

    @NotNull(message = "El método de valorización es obligatorio")
    private MetodoValorizacion metodoValorizacion;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser positivo")
    private Long usuarioId;

    @NotNull(message = "El valor del inventario es obligatorio")
    @Positive(message = "El valor del inventario debe ser positivo")
    private Double valorInventario;

    @NotNull(message = "El costo de ventas es obligatorio")
    @Positive(message = "El costo de ventas debe ser positivo")
    private Double costoVentas;

    @Size(max = 255, message = "Las observaciones no pueden superar los 255 caracteres")
    private String observaciones;
}
