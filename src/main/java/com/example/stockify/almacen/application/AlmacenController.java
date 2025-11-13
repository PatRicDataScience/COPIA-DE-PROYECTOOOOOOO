package com.example.stockify.almacen.application;

import com.example.stockify.almacen.domain.AlmacenService;
import com.example.stockify.almacen.dto.AlmacenNewDTO;
import com.example.stockify.almacen.dto.AlmacenRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/almacenes")
@CrossOrigin(origins = "*")
public class AlmacenController {

    private final AlmacenService almacenService;

    public AlmacenController(AlmacenService almacenService) {
        this.almacenService = almacenService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AlmacenRequestDTO> crear(@RequestBody AlmacenNewDTO dto) {
        return ResponseEntity.ok(almacenService.create(dto));
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<?> listarTodos() {
        return ResponseEntity.ok(almacenService.findAll());
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AlmacenRequestDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(almacenService.findById(id));
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @GetMapping("/activos")
    public ResponseEntity<List<AlmacenRequestDTO>> listarActivos() {
        return ResponseEntity.ok(almacenService.listarActivos());
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @GetMapping("/buscar")
    public ResponseEntity<List<AlmacenRequestDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(almacenService.buscarPorNombre(nombre));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<AlmacenRequestDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam Boolean activo) {
        AlmacenRequestDTO updated = almacenService.actualizarEstado(id, activo);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @PatchMapping("/{id}")
    public ResponseEntity<AlmacenRequestDTO> actualizarParcial(
            @PathVariable Long id,
            @RequestBody AlmacenNewDTO dto) {
        AlmacenRequestDTO updated = almacenService.updatePartial(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        almacenService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}