package com.example.stockify.auth.domain;

import com.example.stockify.auth.components.JwtService;
import com.example.stockify.auth.dto.SignUpRequest;
import com.example.stockify.auth.dto.TokenResponse;
import com.example.stockify.excepciones.CredencialesInvalidasException;
import com.example.stockify.excepciones.CuentaInactivaException;
import com.example.stockify.excepciones.EmailYaRegistradoException;
import com.example.stockify.usuario.domain.Usuario;
import com.example.stockify.usuario.infrastructure.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public TokenResponse signUp(SignUpRequest request){
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailYaRegistradoException("El correo ya está en uso");
        }

        // Validar que el rol sea COCINERO o SUPERVISOR (no se puede registrar como ADMIN)
        if (request.getRol() == null || request.getRol().equals(com.example.stockify.usuario.domain.Rol.ADMIN)) {
            throw new com.example.stockify.excepciones.OperacionNoPermitidaException("No se puede registrar un usuario con rol ADMIN");
        }

        // Crear usuario con todos los campos completos
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setRol(request.getRol());
        usuario.setTelefono(request.getTelefono());
        usuario.setSede(request.getSede());
        usuario.setActivo(true);
        usuario.setFechaRegistro(java.time.ZonedDateTime.now());

        usuario = usuarioRepository.save(usuario);
        var token = jwtService.generateTokenFromUsuario(usuario);

        return TokenResponse.builder()
                .token(token)
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol() != null ? usuario.getRol().name() : null)
                .build();
    }

    public TokenResponse signIn(String username, String password){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        var usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario no encontrado"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new CuentaInactivaException("La cuenta está desactivada");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );

        var token = jwtService.generateTokenFromUsuario(usuario);

        return TokenResponse.builder()
                .token(token)
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .rol(usuario.getRol() != null ? usuario.getRol().name() : null)
                .build();
    }
}