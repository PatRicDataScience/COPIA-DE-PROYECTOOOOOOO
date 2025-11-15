package com.example.stockify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file.upload")
@Data
public class FileStorageConfig {
    private String uploadDir = "uploads/perfiles/";
    private long maxFileSize = 5242880; // 5MB
}

