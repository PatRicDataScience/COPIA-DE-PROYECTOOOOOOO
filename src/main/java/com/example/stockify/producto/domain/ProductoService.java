package com.example.stockify.producto.domain;

import com.example.stockify.excepciones.BadRequestException;
import com.example.stockify.producto.infrastructure.ProductoRepository;
import com.example.stockify.producto.dto.ProductoRequestDTO;
import com.example.stockify.producto.dto.ProductoNewDTO;
import com.example.stockify.excepciones.ResourceNotFoundException;
import com.example.stockify.excepciones.ValidacionException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ModelMapper modelMapper;

    public ProductoService(ModelMapper modelMapper, ProductoRepository productoRepository) {
        this.modelMapper = modelMapper;
        this.productoRepository = productoRepository;
    }

    public List<ProductoRequestDTO> findAll() {
        return productoRepository.findAll()
                .stream()
                .map(e -> modelMapper.map(e, ProductoRequestDTO.class))
                .collect(Collectors.toList());
    }

    public ProductoRequestDTO findById(Long id) {
        Producto e = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return modelMapper.map(e, ProductoRequestDTO.class);
    }

    public ProductoRequestDTO create(ProductoNewDTO dto) {
        if (productoRepository.findByNombreContainingIgnoreCase(dto.getNombre()).size() > 0) {
            throw new ValidacionException("Ya existe un producto con este nombre.");
        }
        Producto e = modelMapper.map(dto, Producto.class);
        e = productoRepository.save(e);
        return modelMapper.map(e, ProductoRequestDTO.class);
    }

    public ProductoRequestDTO update(Long id, ProductoNewDTO dto) {
        Producto existing = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        modelMapper.map(dto, existing);
        existing = productoRepository.save(existing);
        return modelMapper.map(existing, ProductoRequestDTO.class);
    }

    public void deleteById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }


    public ProductoRequestDTO updateFull(Long id, ProductoNewDTO dto) {
        Producto existing = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // Validar campos obligatorios
        if (dto.getNombre() == null || dto.getDescripcion() == null || dto.getUnidadMedida() == null ||
                dto.getCategoria() == null || dto.getStockMinimo() == null || dto.getStockActual() == null ||
                dto.getActivo() == null) {
            throw new BadRequestException("Todos los campos son obligatorios para una actualización completa (PUT)");
        }

        modelMapper.map(dto, existing);
        existing = productoRepository.save(existing);
        return modelMapper.map(existing, ProductoRequestDTO.class);
    }



    public ProductoRequestDTO updatePartial(Long id, ProductoNewDTO dto) {
        Producto existing = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        if (dto.getNombre() != null) existing.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) existing.setDescripcion(dto.getDescripcion());
        if (dto.getUnidadMedida() != null) existing.setUnidadMedida(dto.getUnidadMedida());
        if (dto.getCategoria() != null) existing.setCategoria(dto.getCategoria());
        if (dto.getStockMinimo() != null) existing.setStockMinimo(dto.getStockMinimo());
        if (dto.getStockActual() != null) existing.setStockActual(dto.getStockActual());
        if (dto.getActivo() != null) existing.setActivo(dto.getActivo());

        existing = productoRepository.save(existing);
        return modelMapper.map(existing, ProductoRequestDTO.class);
    }



    public List<ProductoRequestDTO> filtrar(String categoria, Boolean activo) {
        List<Producto> productos;

        if (categoria != null && activo != null) {
            productos = productoRepository.findByCategoriaIgnoreCaseAndActivo(categoria, activo);
        } else if (categoria != null) {
            productos = productoRepository.findByCategoriaIgnoreCase(categoria);
        } else if (activo != null) {
            productos = productoRepository.findByActivo(activo);
        } else {
            productos = productoRepository.findAll();
        }

        return productos.stream()
                .map(p -> modelMapper.map(p, ProductoRequestDTO.class))
                .toList();
    }

    public List<ProductoRequestDTO> listarPorActivo(Boolean activo) {
        List<Producto> productos = productoRepository.findByActivo(activo);
        if (productos.isEmpty()) {
            throw new ResourceNotFoundException("No hay productos con el estado activo = " + activo);
        }
        return productos.stream()
                .map(p -> modelMapper.map(p, ProductoRequestDTO.class))
                .toList();
    }
}