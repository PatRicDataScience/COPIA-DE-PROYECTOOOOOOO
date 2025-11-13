package com.example.stockify.almacen.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlmacenNewDTO {

    @NotBlank(message = "El nombre del almacén es obligatorio")
    private String nombre;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @NotBlank(message = "El responsable es obligatorio")
    @Pattern(
            regexp = "^[a-zA-ZÁÉÍÓÚáéíóúÑñ ]+$",
            message = "El nombre del responsable solo puede contener letras y espacios"
    )
    private String responsable;

    @Positive(message = "La capacidad máxima debe ser mayor a cero")
    private Double capacidadMaxima;

    private Boolean activo;

}
