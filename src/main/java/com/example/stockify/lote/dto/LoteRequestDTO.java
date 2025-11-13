package com.example.stockify.lote.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoteRequestDTO {
    @NotBlank(message = "El código de lote es obligatorio")
    private String codigoLote;

    @NotNull(message = "El costo unitario no puede ser nulo")
    @Positive(message = "El costo unitario debe ser mayor que 0")
    private Double costoUnitario;

    @NotNull(message = "El costo total no puede ser nulo")
    @PositiveOrZero(message = "El costo total no puede ser negativo")
    private Double costoTotal;

    @NotNull(message = "La cantidad inicial no puede ser nula")
    @Positive(message = "La cantidad inicial debe ser mayor que 0")
    private Double cantidadInicial;

    @NotNull(message = "La cantidad disponible no puede ser nula")
    @PositiveOrZero(message = "La cantidad disponible no puede ser negativa")
    private Double cantidadDisponible;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCompra;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaVencimiento;
}
