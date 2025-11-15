package com.example.stockify.auth.components;

import com.example.stockify.usuario.domain.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final UsuarioService userDetailsService;
    private final JwtService jwtService;

    public JwtAuthorizationFilter(JwtService jwtService, UsuarioService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");

            // Si no hay token, continuar sin autenticar
            if (header == null || !header.startsWith("Bearer ")) {
                logger.debug("No JWT token found in request headers");
                filterChain.doFilter(request, response);
                return;
            }

            String token = header.substring(7);

            // Validar token
            if (!jwtService.isTokenValid(token)) {
                logger.warn("Invalid JWT token");
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtService.extractUsername(token);
            logger.debug("JWT token is valid for user: " + username);

            // Solo autenticar si no hay autenticación previa
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Cargar detalles del usuario desde la base de datos
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                logger.debug("User authorities: " + userDetails.getAuthorities());

                // Crear el token de autenticación con las autoridades del usuario
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.debug("User authenticated successfully: " + username + " with authorities: " + userDetails.getAuthorities());
            }

        } catch (Exception ex) {
            logger.error("Cannot set user authentication: " + ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
    }
}