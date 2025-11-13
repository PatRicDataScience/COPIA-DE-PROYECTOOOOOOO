package com.example.stockify.recetaDetalle.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaDetalleBulkDTO {
    @NotEmpty(message = "La lista de ingredientes no puede estar vacía")
    @Valid
    private List<RecetaDetalleNewDTO> ingredientes;
}
