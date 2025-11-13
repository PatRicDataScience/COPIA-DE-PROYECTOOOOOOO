package com.example.stockify.lote.domain;

import com.example.stockify.excepciones.BadRequestException;
import com.example.stockify.excepciones.ResourceNotFoundException;
import com.example.stockify.lote.infrastructure.LoteRepository;
import com.example.stockify.lote.dto.LoteRequestDTO;
import com.example.stockify.lote.dto.LoteNewDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoteService {
    private final LoteRepository loteRepository;
    private final ModelMapper modelMapper;

    public LoteService(ModelMapper modelMapper, LoteRepository loteRepository) {
        this.modelMapper = modelMapper;
        this.loteRepository = loteRepository;
    }

    public List<LoteRequestDTO> findAll() {
        return loteRepository.findAll()
                .stream()
                .map(l -> modelMapper.map(l, LoteRequestDTO.class))
                .collect(Collectors.toList());
    }

    public LoteRequestDTO findByIdPlain(Long id) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote con ID " + id + " no encontrado"));
        return modelMapper.map(lote, LoteRequestDTO.class);
    }

    public LoteRequestDTO create(LoteNewDTO dto) {
        Lote lote = modelMapper.map(dto, Lote.class);

        if (lote.getAlmacen() == null || lote.getAlmacen().getId() == null) {
            throw new BadRequestException("Debe especificar un almacén válido para el lote.");
        }

        if (lote.getProducto() == null || lote.getProducto().getId() == null) {
            throw new BadRequestException("Debe especificar un producto válido para el lote.");
        }

        lote = loteRepository.save(lote);
        return modelMapper.map(lote, LoteRequestDTO.class);
    }

    public LoteRequestDTO update(Long id, LoteRequestDTO dto) {
        Lote existing = loteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote con ID " + id + " no encontrado"));

        if (dto.getCantidadDisponible() != null && dto.getCantidadDisponible() < 0) {
            throw new BadRequestException("La cantidad disponible no puede ser negativa");
        }

        modelMapper.map(dto, existing);
        existing = loteRepository.save(existing);

        return modelMapper.map(existing, LoteRequestDTO.class);
    }

    public List<LoteRequestDTO> obtenerLotesDisponiblesFIFO(Long productoId) {
        List<Lote> lotes = loteRepository.findByProductoIdOrderByFechaCompraAsc(productoId);

        if (lotes.isEmpty()) {
            throw new ResourceNotFoundException("No hay lotes disponibles para el producto con ID " + productoId);
        }

        return lotes.stream()
                .map(l -> modelMapper.map(l, LoteRequestDTO.class))
                .collect(Collectors.toList());
    }

    public List<LoteRequestDTO> obtenerLotesPorProducto(Long productoId) {
        List<Lote> lotes = loteRepository.findByProductoIdOrderByFechaCompraAsc(productoId);

        if (lotes.isEmpty()) {
            throw new ResourceNotFoundException("No hay lotes para el producto con ID " + productoId);
        }

        return lotes.stream()
                .map(l -> modelMapper.map(l, LoteRequestDTO.class))
                .collect(Collectors.toList());
    }

    public List<LoteRequestDTO> obtenerLotesProximosAVencer(int dias) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaFin = ahora.plusDays(dias);
        List<Lote> lotes = loteRepository.findByFechaVencimientoBetween(ahora, fechaFin);

        if (lotes.isEmpty()) {
            throw new ResourceNotFoundException("No hay lotes próximos a vencer en los próximos " + dias + " días");
        }

        return lotes.stream()
                .map(l -> modelMapper.map(l, LoteRequestDTO.class))
                .collect(Collectors.toList());
    }

    public LoteRequestDTO patchUpdate(Long id, LoteRequestDTO dto) {
        Lote existing = loteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote con ID " + id + " no encontrado"));

        if (dto.getCantidadDisponible() != null) {
            if (dto.getCantidadDisponible() < 0) {
                throw new BadRequestException("La cantidad disponible no puede ser negativa");
            }
            existing.setCantidadDisponible(dto.getCantidadDisponible());
        }

        existing = loteRepository.save(existing);
        return modelMapper.map(existing, LoteRequestDTO.class);
    }

}
