package com.example.stockify.movimiento.domain;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.stockify.alertaStock.domain.AlertaStock;
import com.example.stockify.alertaStock.domain.Prioridad;
import com.example.stockify.alertaStock.infrastructure.AlertaStockRepository;
import com.example.stockify.almacen.domain.Almacen;
import com.example.stockify.almacen.infrastructure.AlmacenRepository;
import com.example.stockify.excepciones.OperacionNoPermitidaException;
import com.example.stockify.excepciones.ResourceNotFoundException;
import com.example.stockify.excepciones.StockInsuficienteException;
import com.example.stockify.lote.domain.Lote;
import com.example.stockify.lote.dto.Estado;
import com.example.stockify.lote.infrastructure.LoteRepository;
import com.example.stockify.movimiento.infrastructure.MovimientoRepository;
import com.example.stockify.movimiento.dto.MovimientoRequestDTO;
import com.example.stockify.movimiento.dto.MovimientoNewDTO;
import com.example.stockify.producto.domain.Producto;
import com.example.stockify.producto.infrastructure.ProductoRepository;
import com.example.stockify.recetaBase.domain.RecetaBase;
import com.example.stockify.recetaBase.infrastructure.RecetaBaseRepository;
import com.example.stockify.recetaDetalle.domain.RecetaDetalle;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class MovimientoService {
    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;
    private final AlmacenRepository almacenRepository;
    private final MovimientoRepository movimientoRepository;
    private final RecetaBaseRepository recetaBaseRepository;
    private final AlertaStockRepository alertaStockRepository;
    private final ModelMapper modelMapper;

    public MovimientoService(ModelMapper modelMapper, MovimientoRepository movimientoRepository,
                             ProductoRepository productoRepository, LoteRepository loteRepository,
                             AlmacenRepository almacenRepository,RecetaBaseRepository recetaBaseRepository,
                             AlertaStockRepository alertaStockRepository) {
        this.modelMapper = modelMapper;
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
        this.almacenRepository = almacenRepository;
        this.recetaBaseRepository = recetaBaseRepository;
        this.alertaStockRepository = alertaStockRepository;
    }

    public List<MovimientoRequestDTO> findAll() {
        return movimientoRepository.findAll()
                .stream()
                .map(e -> modelMapper.map(e, MovimientoRequestDTO.class))
                .collect(Collectors.toList());
    }

    public MovimientoRequestDTO findById(Long id) {
        Movimiento e = movimientoRepository.findById(id).orElseThrow();
        return modelMapper.map(e, MovimientoRequestDTO.class);
    }

    public MovimientoRequestDTO create(MovimientoNewDTO dto) {
        Movimiento e = modelMapper.map(dto, Movimiento.class);
        e = movimientoRepository.save(e);
        return modelMapper.map(e, MovimientoRequestDTO.class);
    }


    public void deleteById(Long id) {
        movimientoRepository.deleteById(id);
    }

    @Transactional
    public MovimientoRequestDTO registrarEntrada(MovimientoNewDTO dto) {
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con ID: " + dto.getProductoId()
                ));

        Almacen almacen = almacenRepository.findById(dto.getAlmacenId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Almacén no encontrado con ID: " + dto.getAlmacenId()
                ));

        Lote lote = Lote.builder()
                .codigoLote(generarCodigoLote())
                .producto(producto)
                .almacen(almacen)
                .cantidadInicial(dto.getCantidad())
                .cantidadDisponible(dto.getCantidad())
                .costoUnitario(dto.getCostoUnitario())
                .costoTotal(dto.getCantidad() * dto.getCostoUnitario())
                .estado(Estado.ACTIVO)
                .fechaCompra(LocalDateTime.now())
                .build();

        loteRepository.save(lote);

        Double stockActual = producto.getStockActual();
        if (stockActual == null) stockActual = 0.0;

        producto.setStockActual(stockActual + dto.getCantidad());
        productoRepository.save(producto);

        Movimiento movimiento = Movimiento.builder()
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .cantidad(dto.getCantidad())
                .costoUnitario(dto.getCostoUnitario())
                .costoTotal(dto.getCantidad() * dto.getCostoUnitario())
                .origen(dto.getOrigen())
                .observacion(dto.getObservacion())
                .producto(producto)
                .almacen(almacen)
                .lote(lote)
                .fechaMovimiento(LocalDateTime.now())
                .build();

        movimiento.setAnulado(false);
        movimiento = movimientoRepository.save(movimiento);

        return modelMapper.map(movimiento, MovimientoRequestDTO.class);
    }

    @Transactional
    public MovimientoRequestDTO registrarSalidaManual(MovimientoNewDTO dto) {
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + dto.getProductoId()));

        if (producto.getStockActual() < dto.getCantidad()) {
            throw new StockInsuficienteException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        List<Lote> lotes = loteRepository.findByProductoIdOrderByFechaCompraAsc(producto.getId());
        double cantidadRestante = dto.getCantidad();

        for (Lote lote : lotes) {
            if (cantidadRestante <= 0) break;

            double disponible = lote.getCantidadDisponible();
            double cantidadUsada = Math.min(cantidadRestante, disponible);

            lote.reducirCantidadDisponible(cantidadUsada);
            loteRepository.save(lote);

            Movimiento movimiento = new Movimiento();
            movimiento.setTipoMovimiento(TipoMovimiento.SALIDA);
            movimiento.setCantidad(cantidadUsada);
            movimiento.setCostoUnitario(lote.getCostoUnitario());
            movimiento.setCostoTotal(cantidadUsada * lote.getCostoUnitario());
            movimiento.setFechaMovimiento(LocalDateTime.now());
            movimiento.setObservacion(dto.getObservacion());
            movimiento.setOrigen(dto.getOrigen());
            movimiento.setProducto(producto);
            movimiento.setLote(lote);
            movimiento.setAnulado(false);
            movimiento.setAlmacen(lote.getAlmacen());
            movimientoRepository.save(movimiento);

            cantidadRestante -= cantidadUsada;
        }

        if (cantidadRestante > 0) {
            throw new RuntimeException("No se pudo completar la salida, stock insuficiente en los lotes.");
        }

        producto.setStockActual(producto.getStockActual() - dto.getCantidad());
        productoRepository.save(producto);
        verificarYGenerarAlerta(producto);

        Movimiento ultimoMovimiento = movimientoRepository.findAll().getLast();
        return modelMapper.map(ultimoMovimiento, MovimientoRequestDTO.class);
    }

    @Transactional
    public List<MovimientoRequestDTO> registrarSalidaPorReceta(Long recetaBaseId, int porciones) {
        List<MovimientoRequestDTO> movimientosGenerados = new ArrayList<>();

        RecetaBase recetaBase = recetaBaseRepository.findById(recetaBaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Receta base no encontrada con ID: " + recetaBaseId));

        List<RecetaDetalle> detalles = new ArrayList<>(recetaBase.getDetalles());

        for (RecetaDetalle detalle : detalles) {
            Producto producto = detalle.getProducto();
            double cantidadTotalNecesaria = detalle.getCantidadNecesaria() * porciones;

            if (producto.getStockActual() < cantidadTotalNecesaria) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: "
                        + producto.getNombre() + ". Se requiere " + cantidadTotalNecesaria
                        + " " + detalle.getUnidadMedida() + ".");
            }

            List<Lote> lotes = loteRepository.findByProductoIdOrderByFechaCompraAsc(producto.getId());
            double cantidadRestante = cantidadTotalNecesaria;

            for (Lote lote : lotes) {
                if (cantidadRestante <= 0) break;

                double disponible = lote.getCantidadDisponible();
                double cantidadUsada = Math.min(cantidadRestante, disponible);

                lote.setCantidadDisponible(disponible - cantidadUsada);
                loteRepository.save(lote);

                Movimiento movimiento = new Movimiento();
                movimiento.setTipoMovimiento(TipoMovimiento.SALIDA);
                movimiento.setCantidad(cantidadUsada);
                movimiento.setCostoUnitario(lote.getCostoUnitario());
                movimiento.setCostoTotal(cantidadUsada * lote.getCostoUnitario());
                movimiento.setFechaMovimiento(LocalDateTime.now());
                movimiento.setObservacion("Salida por receta base: " + recetaBase.getNombrePlato());
                movimiento.setOrigen("RecetaBase - " + recetaBase.getNombrePlato());
                movimiento.setProducto(producto);
                movimiento.setLote(lote);
                movimiento.setAlmacen(lote.getAlmacen());
                movimiento.setAnulado(false);
                movimientoRepository.save(movimiento);

                movimientosGenerados.add(modelMapper.map(movimiento, MovimientoRequestDTO.class));

                cantidadRestante -= cantidadUsada;
            }

            if (cantidadRestante > 0) {
                throw new RuntimeException("No se pudo completar la salida por receta. " +
                        "Stock insuficiente del producto: " + producto.getNombre());
            }

            producto.setStockActual(producto.getStockActual() - cantidadTotalNecesaria);
            productoRepository.save(producto);

            verificarYGenerarAlerta(producto);
        }

        return movimientosGenerados;
    }

    private void verificarYGenerarAlerta(Producto producto) {
        if (producto.getStockActual() < producto.getStockMinimo()) {
            AlertaStock alerta = new AlertaStock();
            alerta.setProducto(producto);
            alerta.setMensaje("El producto '" + producto.getNombre() + "' está por debajo del stock mínimo.");
            alerta.setAtendido(false);

            double diferencia = producto.getStockMinimo() - producto.getStockActual();
            if (diferencia <= producto.getStockMinimo() * 0.25) {
                alerta.setPrioridad(Prioridad.BAJA);
            } else if (diferencia <= producto.getStockMinimo() * 0.5) {
                alerta.setPrioridad(Prioridad.MEDIA);
            } else {
                alerta.setPrioridad(Prioridad.ALTA);
            }
            alertaStockRepository.save(alerta);
        }
    }
    private String generarCodigoLote() {
        return "L" + System.currentTimeMillis();
    }

    @Transactional
    public MovimientoRequestDTO anularMovimiento(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con ID: " + id));

        if (movimiento.getAnulado() != null && movimiento.getAnulado()) {
            throw new OperacionNoPermitidaException("El movimiento ya fue anulado.");
        }

        Producto producto = movimiento.getProducto();
        Lote lote = movimiento.getLote();

        if (movimiento.getTipoMovimiento() == TipoMovimiento.ENTRADA) {
            // Revertir entrada → reducir stock y eliminar cantidad del lote
            producto.setStockActual(producto.getStockActual() - movimiento.getCantidad());
            lote.setCantidadDisponible(lote.getCantidadDisponible() - movimiento.getCantidad());
        } else if (movimiento.getTipoMovimiento() == TipoMovimiento.SALIDA) {
            // Revertir salida → aumentar stock y reponer lote
            producto.setStockActual(producto.getStockActual() + movimiento.getCantidad());
            lote.setCantidadDisponible(lote.getCantidadDisponible() + movimiento.getCantidad());
        }

        movimiento.setAnulado(true);
        movimiento.setObservacion((movimiento.getObservacion() != null ? movimiento.getObservacion() + " | " : "") + "Movimiento anulado.");
        movimientoRepository.save(movimiento);
        productoRepository.save(producto);
        loteRepository.save(lote);

        return modelMapper.map(movimiento, MovimientoRequestDTO.class);
    }


    public List<MovimientoRequestDTO> filtrarMovimientos(String tipo, Long productoId, Long almacenId, String desde, String hasta) {
        TipoMovimiento tipoEnum = (tipo != null && !tipo.isBlank()) ? TipoMovimiento.valueOf(tipo.toUpperCase()) : null;
        LocalDateTime fechaDesde = (desde != null && !desde.isBlank()) ? LocalDateTime.parse(desde) : null;
        LocalDateTime fechaHasta = (hasta != null && !hasta.isBlank()) ? LocalDateTime.parse(hasta) : null;

        Specification<Movimiento> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (tipoEnum != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoMovimiento"), tipoEnum));
            }
            if (productoId != null) {
                predicates.add(criteriaBuilder.equal(root.get("producto").get("id"), productoId));
            }
            if (almacenId != null) {
                predicates.add(criteriaBuilder.equal(root.get("almacen").get("id"), almacenId));
            }
            if (fechaDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaMovimiento"), fechaDesde));
            }
            if (fechaHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaMovimiento"), fechaHasta));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        List<Movimiento> movimientos = movimientoRepository.findAll(spec);
        return movimientos.stream()
                .map(m -> modelMapper.map(m, MovimientoRequestDTO.class))
                .collect(Collectors.toList());
    }


    public List<MovimientoRequestDTO> listarPorLote(Long loteId) {
        List<Movimiento> movimientos = movimientoRepository.findByLoteId(loteId);
        if (movimientos.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron movimientos para el lote con ID: " + loteId);
        }
        return movimientos.stream()
                .map(m -> modelMapper.map(m, MovimientoRequestDTO.class))
                .collect(Collectors.toList());
    }
}
