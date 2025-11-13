package com.example.stockify.recetaDetalle.application;

import com.example.stockify.recetaDetalle.domain.RecetaDetalleService;
import com.example.stockify.recetaDetalle.dto.RecetaDetalleBulkDTO;
import com.example.stockify.recetaDetalle.dto.RecetaDetalleNewDTO;
import com.example.stockify.recetaDetalle.dto.RecetaDetalleRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recetas-detalle")
@CrossOrigin(origins = "*")
public class RecetaDetalleController {
    private final RecetaDetalleService recetaDetalleService;

    public RecetaDetalleController(RecetaDetalleService recetaDetalleService) {
        this.recetaDetalleService = recetaDetalleService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<RecetaDetalleRequestDTO> crear(@Valid @RequestBody RecetaDetalleNewDTO dto) {
        return ResponseEntity.ok(recetaDetalleService.create(dto));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/bulk")
    public ResponseEntity<List<RecetaDetalleRequestDTO>> crearVarios(@RequestBody RecetaDetalleBulkDTO dto) {
        List<RecetaDetalleRequestDTO> detalles = recetaDetalleService.createBulk(dto.getIngredientes());
        return ResponseEntity.ok(detalles);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/receta/{recetaBaseId}")
    public ResponseEntity<List<RecetaDetalleRequestDTO>> listarPorReceta(@PathVariable Long recetaBaseId) {
        List<RecetaDetalleRequestDTO> detalles = recetaDetalleService.findByRecetaBaseId(recetaBaseId);
        return ResponseEntity.ok(detalles);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<RecetaDetalleRequestDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recetaDetalleService.findById(id));
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<RecetaDetalleRequestDTO> actualizarParcial(@PathVariable Long id,
                                                                     @RequestBody RecetaDetalleNewDTO dto) {
        RecetaDetalleRequestDTO detalle = recetaDetalleService.patchUpdate(id, dto);
        return ResponseEntity.ok(detalle);
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recetaDetalleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}