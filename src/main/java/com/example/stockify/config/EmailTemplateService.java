package com.example.stockify.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class EmailTemplateService {


    public String cargarPlantilla(String templateName, Map<String, String> variables) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email/" + templateName);
            
            String html;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                html = reader.lines().collect(Collectors.joining("\n"));
            }

            // Reemplazar todos los placeholders {{VARIABLE}} con sus valores
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                String value = entry.getValue() != null ? entry.getValue() : "";
                html = html.replace(placeholder, escapeHtml(value));
            }

            return html;
        } catch (Exception e) {
            log.error("Error al cargar plantilla de email {}: {}", templateName, e.getMessage());
            return "<html><body><p>Error al cargar plantilla de email</p></body></html>";
        }
    }


    private String escapeHtml(String text) {
        if (text == null) return "";
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}

