package com.example.stockify.common.service;

import com.example.stockify.config.FileStorageConfig;
import com.example.stockify.excepciones.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    
    private final FileStorageConfig config;
    private final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");
    
    public String guardarArchivo(MultipartFile file, String prefijo) {
        if (file.isEmpty()) {
            throw new BadRequestException("El archivo está vacío");
        }

        // Validar tipo de archivo
        String extension = obtenerExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase().replace(".", ""))) {
            throw new BadRequestException("Tipo de archivo no permitido. Solo se permiten: " + ALLOWED_EXTENSIONS);
        }

        // Validar tamaño
        if (file.getSize() > config.getMaxFileSize()) {
            throw new BadRequestException("El archivo excede el tamaño máximo permitido de 5MB");
        }

        // Crear directorio si no existe
        Path fileStorageLocation = Paths.get(config.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de almacenamiento", e);
        }

        // Generar nombre único
        String nombreArchivo = prefijo + "_" + UUID.randomUUID() + extension;

        try {
            Path targetLocation = fileStorageLocation.resolve(nombreArchivo);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return nombreArchivo;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage(), e);
        }
    }

    public void eliminarArchivo(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            return;
        }
        
        try {
            Path fileStorageLocation = Paths.get(config.getUploadDir()).toAbsolutePath().normalize();
            Path filePath = fileStorageLocation.resolve(nombreArchivo).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log pero no fallar
            System.err.println("Error al eliminar archivo: " + e.getMessage());
        }
    }

    public Resource cargarArchivo(String nombreArchivo) {
        try {
            Path fileStorageLocation = Paths.get(config.getUploadDir()).toAbsolutePath().normalize();
            Path filePath = fileStorageLocation.resolve(nombreArchivo).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("Archivo no encontrado: " + nombreArchivo);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Archivo no encontrado: " + nombreArchivo, e);
        }
    }

    private String obtenerExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg";
        }
        int lastDot = filename.lastIndexOf('.');
        return filename.substring(lastDot);
    }
}

