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

        Usuario usuario = usuarioRepository.save(
                new Usuario(
                        request.getEmail(),
                        passwordEncoder.encode(request.getPassword()),
                        request.getNombre(),
                        request.getApellido()
                )
        );
        var token = jwtService.generateToken(usuario);
        return new TokenResponse(token);
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

        var token = jwtService.generateToken(usuario);
        return new TokenResponse(token);
    }
}