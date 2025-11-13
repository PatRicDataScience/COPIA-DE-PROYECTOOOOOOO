package com.example.stockify.lote.domain;

import com.example.stockify.almacen.domain.Almacen;
import com.example.stockify.lote.dto.Estado;
import com.example.stockify.producto.domain.Producto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lotes")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_lote", nullable = false, unique = true)
    private String codigoLote;

    @NotNull
    @Positive
    @Column(name = "costo_unitario", nullable = false)
    private Double costoUnitario;

    @NotNull
    @PositiveOrZero
    @Column(name = "costo_total", nullable = false)
    private Double costoTotal;

    @NotNull
    @Positive
    @Column(name = "cantidad_inicial", nullable = false)
    private Double cantidadInicial;

    @NotNull
    @PositiveOrZero
    @Column(name = "cantidad_disponible", nullable = false)
    private Double cantidadDisponible;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra = LocalDateTime.now();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ACTIVO;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @PrePersist
    public void prePersist() {
        if (codigoLote == null || codigoLote.isBlank()) {
            codigoLote = "L" + System.currentTimeMillis();
        }
        if (fechaCompra == null) {
            fechaCompra = LocalDateTime.now();
        }
    }

    public void reducirCantidadDisponible(double cantidad) {
        if (cantidadDisponible < cantidad) {
            throw new IllegalStateException("No hay suficiente cantidad disponible en el lote.");
        }
        cantidadDisponible -= cantidad;
    }
}
