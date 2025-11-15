package com.example.stockify.config;

import com.example.stockify.usuario.domain.Rol;
import com.example.stockify.usuario.domain.Usuario;
import com.example.stockify.usuario.infrastructure.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Crear usuario ADMIN si no existe
        if (!usuarioRepository.existsByEmail("admin@stockify.com")) {
            Usuario admin = new Usuario();
            admin.setEmail("admin@stockify.com");
            admin.setPassword(passwordEncoder.encode("Admin123"));
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setRol(Rol.ADMIN);
            admin.setTelefono("999999999");
            admin.setSede("Sede Principal");
            admin.setActivo(true);
            admin.setFechaRegistro(ZonedDateTime.now());

            usuarioRepository.save(admin);
            System.out.println("✓ Usuario ADMIN creado exitosamente");
            System.out.println("  Email: admin@stockify.com");
            System.out.println("  Password: Admin123");
        }
    }
}

