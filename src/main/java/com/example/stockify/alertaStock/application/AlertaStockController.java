package com.example.stockify.alertaStock.application;

import com.example.stockify.alertaStock.domain.AlertaStockService;
import com.example.stockify.alertaStock.dto.AlertaStockNewDTO;
import com.example.stockify.alertaStock.dto.AlertaStockRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alertas")
@CrossOrigin(origins = "*")
public class AlertaStockController {

    private final AlertaStockService alertaStockService;

    public AlertaStockController(AlertaStockService alertaStockService) {
        this.alertaStockService = alertaStockService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<AlertaStockRequestDTO>> listarTodas() {
        return ResponseEntity.ok(alertaStockService.findAll());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/pendientes")
    public ResponseEntity<List<AlertaStockRequestDTO>> listarPendientes() {
        return ResponseEntity.ok(alertaStockService.findPendientes());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<AlertaStockRequestDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alertaStockService.findById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/atendidas")
    public ResponseEntity<List<AlertaStockRequestDTO>> listarAtendidas() {
        return ResponseEntity.ok(alertaStockService.findAtendidas());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/prioridad/{prioridad}")
    public ResponseEntity<List<AlertaStockRequestDTO>> listarPorPrioridad(@PathVariable String prioridad) {
        return ResponseEntity.ok(alertaStockService.findByPrioridad(prioridad));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<AlertaStockRequestDTO> crear(@Valid @RequestBody AlertaStockNewDTO dto) {
        return ResponseEntity.ok(alertaStockService.crear(dto));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/atender")
    public ResponseEntity<AlertaStockRequestDTO> marcarComoAtendida(@PathVariable Long id) {
        return ResponseEntity.ok(alertaStockService.marcarComoAtendida(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<AlertaStockRequestDTO> editar(@PathVariable Long id, @Valid @RequestBody AlertaStockNewDTO dto) {
        return ResponseEntity.ok(alertaStockService.editar(id, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alertaStockService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/limpiar")
    public ResponseEntity<Void> eliminarAtendidas() {
        alertaStockService.eliminarAtendidas();
        return ResponseEntity.noContent().build();
    }
}