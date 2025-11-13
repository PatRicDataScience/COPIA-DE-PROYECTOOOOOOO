package com.example.stockify.usuario.infrastructure;

import com.example.stockify.usuario.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String username);

    boolean existsByEmail(String email);
}