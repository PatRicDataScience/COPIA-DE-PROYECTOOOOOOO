package com.example.stockify.recetaBase.application;

import com.example.stockify.excepciones.ErrorResponseDTO;
import com.example.stockify.excepciones.ResourceNotFoundException;
import com.example.stockify.recetaBase.domain.RecetaBaseService;
import com.example.stockify.recetaBase.dto.RecetaBaseNewDTO;
import com.example.stockify.recetaBase.dto.RecetaBaseRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/recetas-base")
@CrossOrigin(origins = "*")
public class RecetaBaseController {

    private final RecetaBaseService recetaBaseService;

    public RecetaBaseController(RecetaBaseService recetaBaseService) {
        this.recetaBaseService = recetaBaseService;
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<RecetaBaseRequestDTO> crear(@Valid @RequestBody RecetaBaseNewDTO dto) {
        return ResponseEntity.ok(recetaBaseService.create(dto));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<RecetaBaseRequestDTO>> listar() {
        return ResponseEntity.ok(recetaBaseService.findAll());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<RecetaBaseRequestDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recetaBaseService.findById(id));
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizarParcial(@PathVariable Long id, @RequestBody RecetaBaseNewDTO dto) {
        try {
            RecetaBaseRequestDTO recetaActualizada = recetaBaseService.patchUpdate(id, dto);
            return ResponseEntity.ok(recetaActualizada);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(
                            LocalDateTime.now(),
                            HttpStatus.NOT_FOUND.value(),
                            "Not Found",
                            e.getMessage(),
                            "/recetas-base/" + id
                    ));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recetaBaseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}