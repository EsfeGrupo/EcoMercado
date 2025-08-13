package org.esfe.repositorios;

import org.esfe.modelos.DetalleVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

    // Buscar por idProducto
    Page<DetalleVenta> findByIdProducto(Integer idProducto, Pageable pageable);
}
