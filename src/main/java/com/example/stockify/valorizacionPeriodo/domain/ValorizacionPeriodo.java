package com.example.stockify.valorizacionPeriodo.domain;

import com.example.stockify.usuario.domain.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "valorizacion_periodo")
public class ValorizacionPeriodo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 7)
    private String periodo;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_valorizacion", nullable = false, length = 20)
    private MetodoValorizacion metodoValorizacion;

    @PositiveOrZero(message = "El valor del inventario debe ser mayor o igual a 0")
    @Column(name = "valor_inventario", nullable = false)
    private Double valorInventario;

    @PositiveOrZero(message = "El costo de ventas debe ser mayor o igual a 0")
    @Column(name = "costo_ventas", nullable = false)
    private Double costoVentas;

    @Column(name = "observaciones", length = 255)
    private String observaciones;

    @Column(name = "fecha_valorizacion", nullable = false)
    private LocalDateTime fechaValorizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "cerrado")
    private Boolean cerrado = false;


    @PrePersist
    public void prePersist() {
        if (fechaValorizacion == null) {
            fechaValorizacion = LocalDateTime.now();
        }
    }
}
