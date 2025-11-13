package com.example.stockify.alertaStock.domain;

import com.example.stockify.alertaStock.infrastructure.AlertaStockRepository;
import com.example.stockify.alertaStock.dto.AlertaStockRequestDTO;
import com.example.stockify.alertaStock.dto.AlertaStockNewDTO;
import com.example.stockify.excepciones.ResourceNotFoundException;
import com.example.stockify.producto.domain.Producto;
import com.example.stockify.producto.infrastructure.ProductoRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertaStockService {
    private final AlertaStockRepository alertaStockRepository;
    private final ProductoRepository productoRepository;
    private final ModelMapper modelMapper;

    public AlertaStockService(ModelMapper modelMapper,
                              AlertaStockRepository alertaStockRepository,
                              ProductoRepository productoRepository) {
        this.modelMapper = modelMapper;
        this.alertaStockRepository = alertaStockRepository;
        this.productoRepository = productoRepository;
    }

    public List<AlertaStockRequestDTO> findAll() {
        return alertaStockRepository.findAll()
                .stream()
                .map(a -> mapToDTO(a))
                .collect(Collectors.toList());
    }

    public List<AlertaStockRequestDTO> findPendientes() {
        return alertaStockRepository.findByAtendidoFalse()
                .stream()
                .map(a -> mapToDTO(a))
                .collect(Collectors.toList());
    }

    @Transactional
    public AlertaStockRequestDTO crear(AlertaStockNewDTO dto) {
        AlertaStock alerta = modelMapper.map(dto, AlertaStock.class);

        // 🔑 Asegúrate de limpiar el ID antes de guardar
        alerta.setId(null);

        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + dto.getProductoId()));

        alerta.setProducto(producto);
        alerta.setFechaAlerta(dto.getFechaAlerta() != null ? dto.getFechaAlerta() : LocalDateTime.now());
        alerta.setAtendido(false);

        alertaStockRepository.save(alerta);

        return mapToDTO(alerta);
    }

    public AlertaStockRequestDTO findById(Long id) {
        AlertaStock alerta = alertaStockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada con ID: " + id));
        return mapToDTO(alerta);
    }

    public List<AlertaStockRequestDTO> findAtendidas() {
        return alertaStockRepository.findByAtendidoTrue()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AlertaStockRequestDTO> findByPrioridad(String prioridad) {
        return alertaStockRepository.findByPrioridad(Prioridad.valueOf(prioridad))
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AlertaStockRequestDTO editar(Long id, AlertaStockNewDTO dto) {
        AlertaStock alerta = alertaStockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada con ID: " + id));

        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + dto.getProductoId()));

        alerta.setProducto(producto);
        alerta.setFechaAlerta(dto.getFechaAlerta() != null ? dto.getFechaAlerta() : alerta.getFechaAlerta());
        alerta.setPrioridad(dto.getPrioridad());
        alerta.setAtendido(dto.getAtendido() != null ? dto.getAtendido() : alerta.getAtendido());

        alertaStockRepository.save(alerta);

        return mapToDTO(alerta);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!alertaStockRepository.existsById(id)) {
            throw new ResourceNotFoundException("Alerta no encontrada con ID: " + id);
        }
        alertaStockRepository.deleteById(id);
    }

    @Transactional
    public void eliminarAtendidas() {
        alertaStockRepository.deleteByAtendidoTrue();
    }

    @Transactional
    public AlertaStockRequestDTO marcarComoAtendida(Long id) {
        AlertaStock alerta = alertaStockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada con ID: " + id));
        alerta.setAtendido(true);
        alertaStockRepository.save(alerta);
        return mapToDTO(alerta);
    }

    private AlertaStockRequestDTO mapToDTO(AlertaStock alerta) {
        AlertaStockRequestDTO dto = modelMapper.map(alerta, AlertaStockRequestDTO.class);
        if (alerta.getProducto() != null) {
            dto.setProductoId(alerta.getProducto().getId());
            dto.setProductoNombre(alerta.getProducto().getNombre());
        }
        return dto;
    }
}
