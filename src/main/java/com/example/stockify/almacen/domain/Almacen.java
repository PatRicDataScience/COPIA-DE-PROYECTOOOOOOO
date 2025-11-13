package com.example.stockify.almacen.domain;

import com.example.stockify.lote.domain.Lote;
import com.example.stockify.movimiento.domain.Movimiento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "almacenes")
public class Almacen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String ubicacion;

    @Column(nullable = false, length = 100)
    private String responsable;

    @Column(name = "capacidad_maxima", nullable = false)
    private Double capacidadMaxima;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime ultimoActualizado = LocalDateTime.now();

    @JsonIgnore
    @OneToMany(mappedBy = "almacen", fetch = FetchType.LAZY)
    private List<Lote> lotes;

    @JsonIgnore
    @OneToMany(mappedBy = "almacen", fetch = FetchType.LAZY)
    private List<Movimiento> movimientos;

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
