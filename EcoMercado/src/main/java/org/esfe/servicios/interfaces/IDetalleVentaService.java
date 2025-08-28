package org.esfe.servicios.interfaces;


import org.esfe.modelos.DetalleVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IDetalleVentaService {

    Page<DetalleVenta> obtenerTodosPaginados(Pageable pageable);

    List<DetalleVenta> obtenerTodos();

    Page<DetalleVenta> findByIdProducto(Integer idProducto, Pageable pageable);

    DetalleVenta obtenerPorId(Integer id);

    DetalleVenta crearOEditar(DetalleVenta detalleVenta);

    void eliminarPorId(Integer id);

    List<DetalleVenta> obtenerPorVentaId(Integer idVenta);

    // Nuevo metodo para eliminar todos los detalles de una venta
    void eliminarPorVentaId(Integer ventaId);
}
