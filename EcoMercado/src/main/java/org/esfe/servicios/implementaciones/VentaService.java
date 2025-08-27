package org.esfe.servicios.implementaciones;

import org.esfe.modelos.Venta;
import org.esfe.repositorios.IVentaRepository;
import org.esfe.servicios.interfaces.IVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService implements IVentaService {
    @Autowired
    private IVentaRepository ventaRepository;

    @Override
    public Page<Venta> obtenerTodosPaginados(Pageable pageable) {
        return ventaRepository.findAll(pageable);
    }

    @Override
    public List<Venta> obtenerTodos() {
        return ventaRepository.findAll();
    }

    @Override
    public Page<Venta> findByCorrelativoContainingIgnoreCaseAndEstadoAndUsuario_IdAndTipoPago_IdAndTarjetaCredito_IdOrderByIdDesc(String correlativo, Byte estado, Integer idUsuario, Integer idTipoPago, Optional<Integer> idTarjetaCredito, Pageable pageable) {
        return ventaRepository.findByCorrelativoContainingIgnoreCaseAndEstadoAndUsuario_IdAndTipoPago_IdAndTarjetaCredito_IdOrderByIdDesc(
                correlativo, estado, idUsuario, idTipoPago, idTarjetaCredito, pageable);
    }

    @Override
    public Optional<Venta> obtenerPorId(Integer id) {
        return ventaRepository.findById(id);
    }

    @Override
    public Venta crearOEditar(Venta venta) {
        return ventaRepository.save(venta);
    }

    @Override
    public void eliminarPorId(Integer id) {
        ventaRepository.deleteById(id);

    }

    @Override
    public Page<Venta> buscarVentas(String search, Pageable pageable) {
        return ventaRepository.findByCorrelativoContainingIgnoreCase(search, pageable);
    }

    @Override
    public Optional<Venta> findById(Integer id) {
        return ventaRepository.findById(id);
    }
    @Override
    public Venta save(Venta venta) {
        return ventaRepository.save(venta);
    }
    @Override
    public BigDecimal getStoredTotal(Integer ventaId) {
        Optional<Venta> ventaOpt = ventaRepository.findById(ventaId);
        if (ventaOpt.isPresent()) {
            return ventaOpt.get().getTotal();
        }
        return BigDecimal.ZERO;
    }
    @Override
    public List<Venta> findByCorrelativoStartingWith(String prefijo) {
        return ventaRepository.findByCorrelativoStartingWithOrderByCorrelativoDesc(prefijo);
    }

    @Override
    public boolean existsByCorrelativo(String correlativo) {
        return ventaRepository.existsByCorrelativo(correlativo);
    }
}
