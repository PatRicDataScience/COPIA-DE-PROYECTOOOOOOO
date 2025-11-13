package com.example.stockify.movimiento.dto;

import com.example.stockify.movimiento.domain.TipoMovimiento;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoRequestDTO {

    private Long id;
    private TipoMovimiento tipoMovimiento;
    private Double cantidad;
    private Double costoUnitario;
    private Double costoTotal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaMovimiento;

    private String observacion;
    private String origen;
}
