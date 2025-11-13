package com.example.stockify.recetaDetalle.domain;

import com.example.stockify.excepciones.ResourceNotFoundException;
import com.example.stockify.producto.domain.Producto;
import com.example.stockify.recetaBase.domain.RecetaBase;
import com.example.stockify.recetaDetalle.infrastructure.RecetaDetalleRepository;
import com.example.stockify.recetaDetalle.dto.RecetaDetalleRequestDTO;
import com.example.stockify.recetaDetalle.dto.RecetaDetalleNewDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecetaDetalleService {
    private final RecetaDetalleRepository recetaDetalleRepository;
    private final ModelMapper modelMapper;
    private final com.example.stockify.recetaBase.infrastructure.RecetaBaseRepository recetaBaseRepository;
    private final com.example.stockify.producto.infrastructure.ProductoRepository productoRepository;

    public RecetaDetalleService(
            ModelMapper modelMapper,
            RecetaDetalleRepository recetaDetalleRepository,
            com.example.stockify.recetaBase.infrastructure.RecetaBaseRepository recetaBaseRepository,
            com.example.stockify.producto.infrastructure.ProductoRepository productoRepository
    ) {
        this.modelMapper = modelMapper;
        this.recetaDetalleRepository = recetaDetalleRepository;
        this.recetaBaseRepository = recetaBaseRepository;
        this.productoRepository = productoRepository;
    }

    public List<RecetaDetalleRequestDTO> findAll() {
        return recetaDetalleRepository.findAll()
                .stream()
                .map(e -> modelMapper.map(e, RecetaDetalleRequestDTO.class))
                .collect(Collectors.toList());
    }

    public RecetaDetalleRequestDTO findById(Long id) {
        RecetaDetalle e = recetaDetalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado con ID: " + id));
        return modelMapper.map(e, RecetaDetalleRequestDTO.class);
    }

    public RecetaDetalleRequestDTO create(RecetaDetalleNewDTO dto) {
        RecetaBase recetaBase = recetaBaseRepository.findById(dto.getRecetaBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Receta base no encontrada con ID: " + dto.getRecetaBaseId()));

        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + dto.getProductoId()));

        RecetaDetalle detalle = new RecetaDetalle();
        detalle.setCantidadNecesaria(dto.getCantidadNecesaria());
        detalle.setUnidadMedida(dto.getUnidadMedida());
        detalle.setRecetaBase(recetaBase);
        detalle.setProducto(producto);

        detalle = recetaDetalleRepository.save(detalle);
        return modelMapper.map(detalle, RecetaDetalleRequestDTO.class);
    }

    public void deleteById(Long id) {
        if (!recetaDetalleRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Detalle no encontrado con ID: " + id);
        }
        recetaDetalleRepository.deleteById(id);
    }

    public List<RecetaDetalleRequestDTO> createBulk(List<RecetaDetalleNewDTO> ingredientes) {
        return ingredientes.stream()
                .map(this::create)
                .collect(Collectors.toList());
    }
    public RecetaDetalleRequestDTO patchUpdate(Long id, RecetaDetalleNewDTO dto) {
        RecetaDetalle detalle = recetaDetalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado con ID: " + id));

        if (dto.getCantidadNecesaria() != null) detalle.setCantidadNecesaria(dto.getCantidadNecesaria());
        if (dto.getUnidadMedida() != null) detalle.setUnidadMedida(dto.getUnidadMedida());
        if (dto.getProductoId() != null) {
            Producto producto = productoRepository.findById(dto.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + dto.getProductoId()));
            detalle.setProducto(producto);
        }
        if (dto.getRecetaBaseId() != null) {
            RecetaBase recetaBase = recetaBaseRepository.findById(dto.getRecetaBaseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Receta base no encontrada con ID: " + dto.getRecetaBaseId()));
            detalle.setRecetaBase(recetaBase);
        }

        detalle = recetaDetalleRepository.save(detalle);
        return modelMapper.map(detalle, RecetaDetalleRequestDTO.class);
    }

    public List<RecetaDetalleRequestDTO> findByRecetaBaseId(Long recetaBaseId) {
        return recetaDetalleRepository.findByRecetaBase_Id(recetaBaseId)
                .stream()
                .map(d -> modelMapper.map(d, RecetaDetalleRequestDTO.class))
                .collect(Collectors.toList());
    }
}
