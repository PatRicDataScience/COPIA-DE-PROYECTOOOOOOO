package com.example.stockify.valorizacionPeriodo.application;

import com.example.stockify.valorizacionPeriodo.domain.ValorizacionPeriodoService;
import com.example.stockify.valorizacionPeriodo.dto.ValorizacionPeriodoNewDTO;
import com.example.stockify.valorizacionPeriodo.dto.ValorizacionPeriodoRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/valorizaciones")
@CrossOrigin(origins = "*")
public class ValorizacionPeriodoController {
    private final ValorizacionPeriodoService valorizacionPeriodoService;

    public ValorizacionPeriodoController(ValorizacionPeriodoService valorizacionPeriodoService) {
        this.valorizacionPeriodoService = valorizacionPeriodoService;
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<ValorizacionPeriodoRequestDTO>> listar() {
        return ResponseEntity.ok(valorizacionPeriodoService.findAll());
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ValorizacionPeriodoRequestDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(valorizacionPeriodoService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ejecutar")
    public ResponseEntity<ValorizacionPeriodoRequestDTO> ejecutar(@Valid @RequestBody ValorizacionPeriodoNewDTO dto) {
        return ResponseEntity.ok(
                valorizacionPeriodoService.ejecutarValorizacion(dto.getPeriodo(), dto.getMetodoValorizacion(), dto.getUsuarioId())
        );
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @GetMapping("/actual")
    public ResponseEntity<ValorizacionPeriodoRequestDTO> obtenerActual() {
        return ResponseEntity.ok(valorizacionPeriodoService.findActual());
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @GetMapping("/metodos")
    public ResponseEntity<List<String>> listarMetodos() {
        return ResponseEntity.ok(valorizacionPeriodoService.obtenerMetodos());
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<ValorizacionPeriodoRequestDTO> obtenerPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(valorizacionPeriodoService.findByProducto(productoId));
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @PutMapping("/{id}/cerrar")
    public ResponseEntity<ValorizacionPeriodoRequestDTO> cerrar(@PathVariable Long id, @RequestBody(required = false) String observaciones) {
        return ResponseEntity.ok(valorizacionPeriodoService.cerrarPeriodo(id, observaciones));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        valorizacionPeriodoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/limpiar")
    public ResponseEntity<Void> limpiarAntiguas() {
        valorizacionPeriodoService.limpiarAntiguas();
        return ResponseEntity.noContent().build();
    }
}
