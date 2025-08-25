package org.esfe.servicios.interfaces;

import org.esfe.modelos.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IVentaService {
    Page<Venta> obtenerTodosPaginados(Pageable pageable);

    List<Venta> obtenerTodos();

    Page<Venta> findByCorrelativoContainingIgnoreCaseAndEstadoAndUsuario_IdAndTipoPago_IdAndTarjetaCredito_IdOrderByIdDesc(String correlativo, Byte estado, Integer idUsuario, Integer idTipoPago, Optional<Integer> idTarjetaCredito, Pageable pageable);

    Optional<Venta> obtenerPorId(Integer id);

    Venta crearOEditar(Venta venta);

    void eliminarPorId(Integer id);

    Page<Venta> buscarVentas(String search, Pageable pageable);
}
