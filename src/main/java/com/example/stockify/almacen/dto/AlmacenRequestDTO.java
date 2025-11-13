package com.example.stockify.almacen.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlmacenRequestDTO {
    @NotNull(message = "El ID del almacén es obligatorio para la actualización")
    private Long id;

    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Size(min = 2, max = 200, message = "La ubicación debe tener entre 2 y 200 caracteres")
    private String ubicacion;

    @Size(max = 100, message = "El nombre del responsable no puede superar los 100 caracteres")
    private String responsable;

    @PositiveOrZero(message = "La capacidad máxima no puede ser negativa")
    private Double capacidadMaxima;

    private Boolean activo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ultimoActualizado;

}
