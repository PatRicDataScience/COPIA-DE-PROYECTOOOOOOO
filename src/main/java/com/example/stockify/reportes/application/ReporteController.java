package com.example.stockify.reportes.application;

import com.example.stockify.reportes.domain.ReporteService;
import com.example.stockify.reportes.util.PdfGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.Map;

@RestController
@RequestMapping("/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @PreAuthorize("hasRole('SUPERVISOR')")
    @GetMapping("/pdf")
    public ResponseEntity<?> generarReportePDF(@RequestParam String periodo, @RequestParam Long usuarioId) {
        try {
            Map<String, Object> data = reporteService.generarResumenGeneral(periodo, usuarioId);
            ByteArrayInputStream bis = PdfGenerator.generarReportePDF(data);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=reporte-inventario-" + periodo + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(bis.readAllBytes());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}