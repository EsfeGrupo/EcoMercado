package org.esfe.repositorios;

import org.esfe.modelos.DetalleVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IDetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

    // Buscar por idProducto
    Page<DetalleVenta> findByIdProducto(Integer idProducto, Pageable pageable);

    List<DetalleVenta> findByVenta_Id(Integer ventaId);

    // Nuevo metodo para eliminar por ID de venta
    @Modifying
    @Query("DELETE FROM DetalleVenta d WHERE d.venta.id = :ventaId")
    void deleteByVentaId(@Param("ventaId") Integer ventaId);
}
