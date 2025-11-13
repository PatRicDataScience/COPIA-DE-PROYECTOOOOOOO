package com.example.stockify.valorizacionPeriodo.dto;

import com.example.stockify.valorizacionPeriodo.domain.MetodoValorizacion;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValorizacionPeriodoRequestDTO {
    private Long id;
    private String periodo;
    private MetodoValorizacion metodoValorizacion;
    private Double valorInventario;
    private Double costoVentas;
    private String observaciones;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaValorizacion;

    private Long usuarioId;
}
