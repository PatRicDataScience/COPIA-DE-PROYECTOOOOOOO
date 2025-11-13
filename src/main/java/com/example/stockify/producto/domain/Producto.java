package com.example.stockify.producto.domain;

import com.example.stockify.alertaStock.domain.AlertaStock;
import com.example.stockify.lote.domain.Lote;
import com.example.stockify.movimiento.domain.Movimiento;
import com.example.stockify.recetaDetalle.domain.RecetaDetalle;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String descripcion;

    @Column(name = "unidad_medida", nullable = false, length = 30)
    private String unidadMedida;

    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(name = "stock_minimo", nullable = false)
    private Double stockMinimo = 0.0;

    @Column(name = "stock_actual", nullable = false)
    private Double stockActual = 0.0;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "producto")
    private List<Lote> lotes;

    @OneToMany(mappedBy = "producto")
    private List<Movimiento> movimientos;

    @OneToMany(mappedBy = "producto")
    private List<AlertaStock> alertas;

    @OneToMany(mappedBy = "producto")
    private List<RecetaDetalle> detallesReceta;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "ultimo_actualizado")
    private LocalDateTime ultimoActualizado;

    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
        ultimoActualizado = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        ultimoActualizado = LocalDateTime.now();
    }
}
