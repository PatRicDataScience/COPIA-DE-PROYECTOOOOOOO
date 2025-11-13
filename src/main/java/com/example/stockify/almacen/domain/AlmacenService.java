package com.example.stockify.almacen.domain;

import com.example.stockify.almacen.infrastructure.AlmacenRepository;
import com.example.stockify.almacen.dto.AlmacenRequestDTO;
import com.example.stockify.almacen.dto.AlmacenNewDTO;
import com.example.stockify.excepciones.BadRequestException;
import com.example.stockify.excepciones.ResourceNotFoundException;
import com.example.stockify.excepciones.ValidacionException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlmacenService {

    private final AlmacenRepository almacenRepository;
    private final ModelMapper modelMapper;

    public AlmacenService(ModelMapper modelMapper, AlmacenRepository almacenRepository) {
        this.modelMapper = modelMapper;
        this.almacenRepository = almacenRepository;
    }

    public AlmacenRequestDTO findById(Long id) {
        Almacen e = almacenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén no encontrado con ID: " + id));
        return modelMapper.map(e, AlmacenRequestDTO.class);
    }

    public AlmacenRequestDTO create(AlmacenNewDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new ValidacionException("El nombre del almacén es obligatorio.");
        }

        Almacen e = modelMapper.map(dto, Almacen.class);

        if (e.getActivo() == null) {
            e.setActivo(true);
        }

        e = almacenRepository.save(e);
        return modelMapper.map(e, AlmacenRequestDTO.class);
    }


    public void deleteById(Long id) {
        if (!almacenRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Almacén no encontrado con ID: " + id);
        }
        almacenRepository.deleteById(id);
    }

    public AlmacenRequestDTO updatePartial(Long id, AlmacenNewDTO dto) {
        Almacen existing = almacenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén con ID " + id + " no encontrado."));

        if (dto == null) {
            throw new BadRequestException("No se ha enviado información para actualizar.");
        }

        if (dto.getNombre() != null) existing.setNombre(dto.getNombre());
        if (dto.getUbicacion() != null) existing.setUbicacion(dto.getUbicacion());
        if (dto.getResponsable() != null) existing.setResponsable(dto.getResponsable());
        if (dto.getCapacidadMaxima() != null) existing.setCapacidadMaxima(dto.getCapacidadMaxima());
        if (dto.getActivo() != null) existing.setActivo(dto.getActivo());

        existing = almacenRepository.save(existing);
        return modelMapper.map(existing, AlmacenRequestDTO.class);
    }

    public List<AlmacenRequestDTO> findAll() {
        return almacenRepository.findAll()
                .stream()
                .map(a -> modelMapper.map(a, AlmacenRequestDTO.class))
                .toList();
    }
    public List<AlmacenRequestDTO> listarActivos() {
        List<Almacen> activos = almacenRepository.findByActivo(true);
        if (activos.isEmpty()) {
            throw new ResourceNotFoundException("No hay almacenes activos actualmente.");
        }
        return activos.stream()
                .map(a -> modelMapper.map(a, AlmacenRequestDTO.class))
                .toList();
    }

    public List<AlmacenRequestDTO> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new BadRequestException("Debe proporcionar un nombre para realizar la búsqueda.");
        }

        List<Almacen> resultado = almacenRepository.findByNombreContainingIgnoreCase(nombre);
        if (resultado.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron almacenes con el nombre: " + nombre);
        }

        return resultado.stream()
                .map(a -> modelMapper.map(a, AlmacenRequestDTO.class))
                .toList();
    }

    public AlmacenRequestDTO actualizarEstado(Long id, Boolean nuevoEstado) {
        Almacen almacen = almacenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén con ID " + id + " no encontrado."));

        if (nuevoEstado == null) {
            throw new BadRequestException("Debe especificar el nuevo estado del almacén (true/false).");
        }

        almacen.setActivo(nuevoEstado);
        almacen = almacenRepository.save(almacen);

        return modelMapper.map(almacen, AlmacenRequestDTO.class);
    }
}