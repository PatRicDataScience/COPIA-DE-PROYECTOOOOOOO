package com.example.stockify.movimiento.application;

import com.example.stockify.movimiento.domain.MovimientoService;
import com.example.stockify.movimiento.dto.MovimientoNewDTO;
import com.example.stockify.movimiento.dto.MovimientoRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
@CrossOrigin(origins = "*")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @PreAuthorize("hasRole('SUPERVISOR')")
    @PostMapping("/entrada")
    public ResponseEntity<MovimientoRequestDTO> registrarEntrada(@RequestBody MovimientoNewDTO dto) {
        MovimientoRequestDTO movimiento = movimientoService.registrarEntrada(dto);
        return ResponseEntity.ok(movimiento);
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'COCINERO')")
    @PostMapping("/salida-manual")
    public ResponseEntity<MovimientoRequestDTO> registrarSalidaManual(@RequestBody MovimientoNewDTO dto) {
        MovimientoRequestDTO movimiento = movimientoService.registrarSalidaManual(dto);
        return ResponseEntity.ok(movimiento);
    }

    @PreAuthorize("hasRole('COCINERO')")
    @PostMapping("/salida-receta/{recetaBaseId}")
    public ResponseEntity<List<MovimientoRequestDTO>> registrarSalidaPorReceta(
            @PathVariable Long recetaBaseId,
            @RequestParam int porciones) {

        List<MovimientoRequestDTO> movimientos = movimientoService.registrarSalidaPorReceta(recetaBaseId, porciones);
        return ResponseEntity.ok(movimientos);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoRequestDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.findById(id));
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @GetMapping("/filtrar")
    public ResponseEntity<List<MovimientoRequestDTO>> filtrarMovimientos(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) Long almacenId,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta) {
        return ResponseEntity.ok(movimientoService.filtrarMovimientos(tipo, productoId, almacenId, desde, hasta));
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @GetMapping("/lote/{loteId}")
    public ResponseEntity<List<MovimientoRequestDTO>> listarPorLote(@PathVariable Long loteId) {
        return ResponseEntity.ok(movimientoService.listarPorLote(loteId));
    }

    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    @PutMapping("/anular/{id}")
    public ResponseEntity<MovimientoRequestDTO> anularMovimiento(@PathVariable Long id) {
        MovimientoRequestDTO movimiento = movimientoService.anularMovimiento(id);
        return ResponseEntity.ok(movimiento);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<MovimientoRequestDTO>> listarTodos() {
        return ResponseEntity.ok(movimientoService.findAll());
    }

}