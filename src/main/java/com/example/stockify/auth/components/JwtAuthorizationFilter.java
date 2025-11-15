package com.example.stockify.auth.components;

import com.example.stockify.usuario.domain.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final UsuarioService userDetailsService;
    private final JwtService jwtService;

    public JwtAuthorizationFilter(JwtService jwtService, UsuarioService userDetailsService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = header.substring(7);
            // validar token (usa tu util existente)
            if (!jwtUtil.isValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtUtil.getUsernameFromToken(token);
            // extraer roles desde el token; ejemplo: claim "roles" -> ["ADMIN"]
            List<String> roles = jwtUtil.getRolesFromToken(token); // implementa según tu JWT
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(r -> {
                        // si el token ya trae ROLE_ evita duplicar
                        return r.startsWith("ROLE_") ? new SimpleGrantedAuthority(r) :
                                new SimpleGrantedAuthority("ROLE_" + r);
                    })
                    .collect(Collectors.toList());

            // opcional: obtener userDetails si lo necesitas
            // UserDetails userDetails = userService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);

            // DEBUG temporal: eliminar en producción
            logger.debug("Authenticated user: {} with authorities: {}", username, authorities);

        } catch (Exception ex) {
            logger.warn("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }
}