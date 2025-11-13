package com.example.stockify.usuario.application;

import com.example.stockify.usuario.domain.UsuarioService;
import com.example.stockify.usuario.dto.UsuarioNewDTO;
import com.example.stockify.usuario.dto.UsuarioRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

//    @PostMapping
//    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioNewDTO dto) {
//        return ResponseEntity.ok(usuarioService.create(dto));
//    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UsuarioRequestDTO>> listar() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRequestDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioRequestDTO> actualizarUsuarioCompleto(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioNewDTO dto) {
        UsuarioRequestDTO updated = usuarioService.updateFull(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioRequestDTO> actualizarUsuarioParcial(
            @PathVariable Long id,
            @RequestBody UsuarioNewDTO dto) {
        UsuarioRequestDTO updated = usuarioService.updatePartial(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}