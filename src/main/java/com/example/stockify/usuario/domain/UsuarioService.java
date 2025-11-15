package com.example.stockify.usuario.domain;

import com.example.stockify.excepciones.BadRequestException;
import com.example.stockify.excepciones.ResourceNotFoundException;
import com.example.stockify.usuario.dto.UsuarioNewDTO;
import com.example.stockify.usuario.dto.UsuarioRequestDTO;
import com.example.stockify.usuario.infrastructure.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;


    public UsuarioService(
            UsuarioRepository usuarioRepository,
            ModelMapper modelMapper,
            @Lazy PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioRequestDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(e -> modelMapper.map(e, UsuarioRequestDTO.class))
                .collect(Collectors.toList());
    }

    public UsuarioRequestDTO findById(Long id) {
        Usuario e = usuarioRepository.findById(id).orElseThrow();
        return modelMapper.map(e, UsuarioRequestDTO.class);
    }

//    public UsuarioResponseDTO create(UsuarioNewDTO dto) {
//        Usuario usuario = modelMapper.map(dto, Usuario.class);
//        usuario.setActivo(true);
//        usuario = usuarioRepository.save(usuario);
//        return modelMapper.map(usuario, UsuarioResponseDTO.class);
//    }

    public UsuarioRequestDTO update(Long id, UsuarioNewDTO dto) {
        Usuario existing = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        modelMapper.map(dto, existing);
        existing = usuarioRepository.save(existing);
        return modelMapper.map(existing, UsuarioRequestDTO.class);
    }

    public UsuarioRequestDTO updateFull(Long id, UsuarioNewDTO dto) {
        Usuario existing = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + id + " no encontrado"));

        if (dto.getNombre() == null || dto.getApellido() == null || dto.getEmail() == null ||
                dto.getTelefono() == null || dto.getRol() == null || dto.getSede() == null ||
                dto.getPassword() == null || dto.getActivo() == null) {
            throw new BadRequestException("Todos los campos son obligatorios para una actualización completa (PUT)");
        }

        modelMapper.map(dto, existing);

        if (dto.getPassword() != null) {
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        existing = usuarioRepository.save(existing);
        return modelMapper.map(existing, UsuarioRequestDTO.class);
    }


    public UsuarioRequestDTO updatePartial(Long id, UsuarioNewDTO dto) {
        Usuario existing = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + id + " no encontrado"));

        if (dto.getNombre() != null) existing.setNombre(dto.getNombre());
        if (dto.getApellido() != null) existing.setApellido(dto.getApellido());
        if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
        if (dto.getTelefono() != null) existing.setTelefono(dto.getTelefono());
        if (dto.getRol() != null) existing.setRol(dto.getRol());
        if (dto.getSede() != null) existing.setSede(dto.getSede());
        if (dto.getPassword() != null) existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getActivo() != null) existing.setActivo(dto.getActivo());

        existing = usuarioRepository.save(existing);
        return modelMapper.map(existing, UsuarioRequestDTO.class);
    }
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));
    }

    public String actualizarFotoPerfil(Long id, MultipartFile foto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + id + " no encontrado"));

        if (foto.isEmpty()) {
            throw new BadRequestException("El archivo está vacío");
        }

        if (foto.getSize() > 5242880) {
            throw new BadRequestException("El archivo excede el tamaño máximo permitido de 5MB");
        }

        String contentType = foto.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("El archivo debe ser una imagen");
        }

        try {
            usuario.setFotoPerfil(foto.getBytes());
            usuario.setFotoPerfilNombre(foto.getOriginalFilename());
            usuario.setFotoPerfilTipo(contentType);
            usuarioRepository.save(usuario);

            return "/usuarios/" + id + "/foto-perfil";
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la foto: " + e.getMessage(), e);
        }
    }

    public byte[] obtenerFotoPerfil(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + id + " no encontrado"));

        if (usuario.getFotoPerfil() == null) {
            throw new ResourceNotFoundException("El usuario no tiene foto de perfil");
        }

        return usuario.getFotoPerfil();
    }

    public String obtenerTipoFotoPerfil(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + id + " no encontrado"));

        return usuario.getFotoPerfilTipo() != null ? usuario.getFotoPerfilTipo() : "image/jpeg";
    }

    public void eliminarFotoPerfil(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + id + " no encontrado"));

        if (usuario.getFotoPerfil() != null) {
            usuario.setFotoPerfil(null);
            usuario.setFotoPerfilNombre(null);
            usuario.setFotoPerfilTipo(null);
            usuarioRepository.save(usuario);
        }
    }

}
