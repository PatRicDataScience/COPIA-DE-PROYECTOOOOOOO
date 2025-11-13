package com.example.stockify.alertaStock.dto;

import com.example.stockify.alertaStock.domain.Prioridad;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaStockNewDTO {
    @NotBlank(message = "El mensaje de la alerta es obligatorio")
    @Size(max = 255, message = "El mensaje no puede exceder 255 caracteres")
    private String mensaje;

    @PastOrPresent(message = "La fecha de alerta no puede ser futura")
    private LocalDateTime fechaAlerta;

    private Boolean atendido = false;

    @NotNull(message = "La prioridad es obligatoria")
    private Prioridad prioridad;

    @NotNull(message = "Debe asociarse un producto válido")
    private Long productoId;
}
