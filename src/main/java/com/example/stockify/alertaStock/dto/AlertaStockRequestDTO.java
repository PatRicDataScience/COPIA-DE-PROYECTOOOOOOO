package com.example.stockify.alertaStock.dto;

import com.example.stockify.alertaStock.domain.Prioridad;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaStockRequestDTO {

    private Long id;
    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 255, message = "El mensaje no puede exceder 255 caracteres")
    private String mensaje;
    @PastOrPresent(message = "La fecha de alerta no puede ser futura")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaAlerta;
    private Boolean atendido;

    private Prioridad prioridad;

    private Long productoId;

    private String productoNombre;

}
