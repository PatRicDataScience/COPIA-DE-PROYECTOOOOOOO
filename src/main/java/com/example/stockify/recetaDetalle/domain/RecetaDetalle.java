package com.example.stockify.recetaDetalle.domain;

import com.example.stockify.producto.domain.Producto;
import com.example.stockify.recetaBase.domain.RecetaBase;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "receta_detalles")
public class RecetaDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cantidad_necesaria", nullable = false)
    private Double cantidadNecesaria;

    @Column(name = "unidad_medida", nullable = false, length = 20)
    private String unidadMedida;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receta_base_id", nullable = false)
    private RecetaBase recetaBase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
}
