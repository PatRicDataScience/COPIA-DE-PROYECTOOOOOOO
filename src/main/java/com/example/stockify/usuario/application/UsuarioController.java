package com.example.stockify.usuario.application;

import com.example.stockify.usuario.domain.UsuarioService;
import com.example.stockify.usuario.dto.UsuarioNewDTO;
import com.example.stockify.usuario.dto.UsuarioRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/foto-perfil")
    public ResponseEntity<Map<String, String>> subirFotoPerfil(
            @PathVariable Long id,
            @RequestParam("foto") MultipartFile foto) {

        String urlFoto = usuarioService.actualizarFotoPerfil(id, foto);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Foto de perfil actualizada correctamente");
        response.put("urlFoto", urlFoto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/foto-perfil")
    public ResponseEntity<byte[]> obtenerFotoPerfil(@PathVariable Long id) {
        byte[] imagen = usuarioService.obtenerFotoPerfil(id);
        String tipoContenido = usuarioService.obtenerTipoFotoPerfil(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(tipoContenido))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"perfil.jpg\"")
                .body(imagen);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}/foto-perfil")
    public ResponseEntity<Map<String, String>> eliminarFotoPerfil(@PathVariable Long id) {
        usuarioService.eliminarFotoPerfil(id);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Foto de perfil eliminada correctamente");

        return ResponseEntity.ok(response);
    }
}