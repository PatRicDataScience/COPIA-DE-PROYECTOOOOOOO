package com.example.stockify.alertaStock.domain;

import com.example.stockify.config.EmailService;
import com.example.stockify.config.EmailTemplateService;
import com.example.stockify.producto.domain.Producto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Listener que escucha eventos de creación de alertas de stock
 * y envía notificaciones por email HTML usando plantillas externas
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AlertaStockEmailListener {

    private final EmailService emailService;
    private final EmailTemplateService templateService;


    @EventListener
    @Async
    public void handleAlertaStockCreada(AlertaStockCreadaEvent event) {
        AlertaStock alerta = event.getAlertaStock();
        log.info("Procesando evento de alerta de stock - ID: {}", alerta.getId());

        try {
            String asunto = construirAsunto(alerta);
            String html = construirEmailDesdeTemplate(alerta);

            emailService.enviarEmailHtmlAlAdmin(asunto, html);

            log.info("✓ Notificación de alerta (HTML) enviada - ID: {}", alerta.getId());
        } catch (Exception e) {
            log.error("✗ Error al procesar evento de alerta - ID: {}, Error: {}",
                     alerta.getId(), e.getMessage());
        }
    }


    private String construirAsunto(AlertaStock alerta) {
        String prioridad = alerta.getPrioridad() != null ? alerta.getPrioridad().name() : "NORMAL";
        return String.format("[STOCKIFY - %s] Nueva Alerta de Stock", prioridad);
    }


    private String construirEmailDesdeTemplate(AlertaStock alerta) {
        Producto producto = alerta.getProducto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String colorPrioridad = obtenerColorPrioridad(alerta.getPrioridad());

        Map<String, String> variables = new HashMap<>();
        variables.put("COLOR_PRIORIDAD", colorPrioridad);
        variables.put("PRODUCTO_NOMBRE", producto != null ? producto.getNombre() : "N/A");
        variables.put("PRODUCTO_ID", producto != null ? String.valueOf(producto.getId()) : "N/A");
        variables.put("MENSAJE", alerta.getMensaje() != null ? alerta.getMensaje() : "Sin mensaje");
        variables.put("PRIORIDAD", alerta.getPrioridad() != null ? alerta.getPrioridad().name() : "NORMAL");
        variables.put("FECHA", alerta.getFechaAlerta().format(formatter));
        variables.put("ESTADO", Boolean.TRUE.equals(alerta.getAtendido()) ? "Atendido" : "Pendiente");

        // Cargar y procesar la plantilla HTML
        return templateService.cargarPlantilla("alerta-stock.html", variables);
    }


    private String obtenerColorPrioridad(Prioridad prioridad) {
        if (prioridad == null) return "#0ea5e9"; // Azul por defecto

        return switch (prioridad) {
            case ALTA -> "#f59e0b";   // Ámbar/Naranja
            case MEDIA -> "#f97316";  // Naranja
            case BAJA -> "#22c55e";   // Verde
        };
    }
}

