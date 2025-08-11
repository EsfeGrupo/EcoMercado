package org.esfe.repositorios;

import org.esfe.modelos.DetalleVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

    // Buscar por idProducto exacto
    Page<DetalleVenta> findByIdProducto(Integer idProducto, Pageable pageable);

    // Buscar por cantidad exacta
    Page<DetalleVenta> findByCantidad(Integer cantidad, Pageable pageable);

    // Buscar por precioUnitario exacto
    Page<DetalleVenta> findByPrecioUnitario(Float precioUnitario, Pageable pageable);

    // Búsqueda combinada: idProducto y precioUnitario
    Page<DetalleVenta> findByIdProductoAndPrecioUnitario(Integer idProducto, Float precioUnitario, Pageable pageable);
}
