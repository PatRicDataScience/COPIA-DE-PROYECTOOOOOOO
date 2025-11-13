package com.example.stockify.alertaStock.domain;

import com.example.stockify.producto.domain.Producto;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "alertas_stock")
public class AlertaStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String mensaje;

    @Column(name = "fecha_alerta", nullable = false)
    private LocalDateTime fechaAlerta = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean atendido = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Prioridad prioridad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
}
