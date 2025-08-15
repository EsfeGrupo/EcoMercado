package org.esfe.servicios.implementaciones;

import org.esfe.modelos.Venta;
import org.esfe.repositorios.IVentaRepository;
import org.esfe.servicios.interfaces.IVentaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public class VentaService implements IVentaService {

    private IVentaRepository ventaRepository;

    @Override
    public Page<Venta> obtenerTodosPaginados(Pageable pageable) {
        return ventaRepository.findAll(pageable);
    }

    @Override
    public List<Venta> obtenerTodos() {
        return List.of();
    }

    @Override
    public Page<Venta> findByCorrelativoContainingIgnoreCaseAndEstadoAndUsuario_IdAndTipoPago_IdAndTarjetaCredito_IdOrderByIdDesc(String correlativo, Byte estado, Integer idUsuario, Integer idTipoPago, Integer idTarjetaCredito, Pageable pageable) {
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
}
