package com.example.stockify.reportes.domain;

import com.example.stockify.alertaStock.infrastructure.AlertaStockRepository;
import com.example.stockify.excepciones.ResourceNotFoundException;
import com.example.stockify.lote.infrastructure.LoteRepository;
import com.example.stockify.movimiento.infrastructure.MovimientoRepository;
import com.example.stockify.producto.infrastructure.ProductoRepository;
import com.example.stockify.reportes.infrastructure.ReporteRepository;
import com.example.stockify.usuario.domain.Usuario;
import com.example.stockify.usuario.infrastructure.UsuarioRepository;
import com.example.stockify.valorizacionPeriodo.infrastructure.ValorizacionPeriodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReporteService {

    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;
    private final MovimientoRepository movimientoRepository;
    private final AlertaStockRepository alertaStockRepository;
    private final ValorizacionPeriodoRepository valorizacionPeriodoRepository;
    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;

    public ReporteService(ProductoRepository productoRepository,
                          LoteRepository loteRepository,
                          MovimientoRepository movimientoRepository,
                          AlertaStockRepository alertaStockRepository,
                          ValorizacionPeriodoRepository valorizacionPeriodoRepository,
                          ReporteRepository reporteRepository,
                          UsuarioRepository usuarioRepository) {
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
        this.movimientoRepository = movimientoRepository;
        this.alertaStockRepository = alertaStockRepository;
        this.valorizacionPeriodoRepository = valorizacionPeriodoRepository;
        this.reporteRepository = reporteRepository;
        this.usuarioRepository = usuarioRepository;
    }


    @Transactional
    public Map<String, Object> generarResumenGeneral(String periodo, Long usuarioId) {
        if (periodo == null || !periodo.matches("\\d{4}-\\d{2}")) {
            throw new IllegalArgumentException("El formato del periodo debe ser YYYY-MM");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        boolean existeValorizacion = valorizacionPeriodoRepository.findAll().stream()
                .anyMatch(v -> v.getPeriodo().equals(periodo));
        if (!existeValorizacion) {
            throw new ResourceNotFoundException("No existe valorización registrada para el periodo: " + periodo);
        }

        double valorInventario = loteRepository.findAll().stream()
                .mapToDouble(l -> l.getCantidadDisponible() * l.getCostoUnitario())
                .sum();

        double costoVentas = valorizacionPeriodoRepository.findAll().stream()
                .filter(v -> v.getPeriodo().equals(periodo))
                .mapToDouble(v -> v.getCostoVentas())
                .sum();

        Map<String, Object> data = new HashMap<>();
        data.put("fechaReporte", LocalDate.now());
        data.put("valorInventario", valorInventario);
        data.put("costoVentas", costoVentas);
        data.put("productos", productoRepository.findAll());
        data.put("lotes", loteRepository.findAll());
        data.put("movimientos", movimientoRepository.findAll());
        data.put("alertas", alertaStockRepository.findByAtendidoFalse());

        Reporte nuevoReporte = Reporte.builder()
                .periodo(periodo)
                .formato(FormatoReporte.PDF)
                .nombreArchivo("reporte-inventario-" + periodo + ".pdf")
                .observaciones("Reporte generado automáticamente")
                .usuario(usuario)
                .build();

        reporteRepository.save(nuevoReporte);

        return data;
    }
}