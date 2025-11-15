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

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "password", nullable = false)
    private String password;

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

    @Column(name = "foto_perfil_path")
    private String fotoPerfilPath;

    @Column(name = "foto_perfil_nombre")
    private String fotoPerfilNombre;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    public String getUsername() {
        return this.email;
    }


    @OneToMany(mappedBy = "usuario")
    private List<Movimiento> movimientos;

    @OneToMany(mappedBy = "usuario")
    private List<ValorizacionPeriodo> valorizaciones; // 1:n

}
