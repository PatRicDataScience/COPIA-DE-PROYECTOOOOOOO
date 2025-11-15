package com.example.stockify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuración para habilitar procesamiento asíncrono de eventos
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    // La anotación @EnableAsync permite que los métodos marcados con @Async
    // se ejecuten en hilos separados, evitando bloquear el flujo principal
}

