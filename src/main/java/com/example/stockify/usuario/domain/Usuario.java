package com.example.stockify.usuario.domain;

import com.example.stockify.movimiento.domain.Movimiento;
import com.example.stockify.valorizacionPeriodo.domain.ValorizacionPeriodo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private Rol rol;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "sede")
    private String sede;

    @Column(name = "fecha_registro")
    private ZonedDateTime fechaRegistro = ZonedDateTime.now();

    @Column(name = "activo")
    private Boolean activo;

    @JsonIgnore
    @Column(name = "foto_perfil")
    private byte[] fotoPerfil;

    @Column(name = "foto_perfil_nombre")
    private String fotoPerfilNombre;

    @Column(name = "foto_perfil_tipo")
    private String fotoPerfilTipo;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (rol == null) {
            return List.of(new SimpleGrantedAuthority("ROLE_COCINERO")); // rol por defecto
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    public String getUsername() {
        return this.email;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Movimiento> movimientos;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<ValorizacionPeriodo> valorizaciones; // 1:n

}
