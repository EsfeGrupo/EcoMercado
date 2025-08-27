package org.esfe.repositorios;

import org.esfe.modelos.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IVentaRepository extends JpaRepository<Venta, Integer> {
    @Query("SELECT v FROM Venta v WHERE " +
           "(:correlativo IS NULL OR LOWER(v.correlativo) LIKE LOWER(CONCAT('%', :correlativo, '%'))) AND " +
           "(:estado IS NULL OR v.estado = :estado) AND " +
           "(:idUsuario IS NULL OR v.usuario.id = :idUsuario) AND " +
           "(:idTipoPago IS NULL OR v.tipoPago.id = :idTipoPago) AND " +
           "(:idTarjetaCredito IS NULL OR v.tarjetaCredito.id = :idTarjetaCredito) " +
           "ORDER BY v.id DESC")
    Page<Venta> buscarVentasConFiltros(
        @Param("correlativo") String correlativo,
        @Param("estado") String estado,
        @Param("idUsuario") Integer idUsuario,
        @Param("idTipoPago") Integer idTipoPago,
        @Param("idTarjetaCredito") Integer idTarjetaCredito,
        Pageable pageable);
    Page<Venta> findByCorrelativoContainingIgnoreCase(String correlativo, Pageable pageable);
    // Buscar ventas por correlativo que comience con un prefijo, ordenadas por correlativo descendente
    List<Venta> findByCorrelativoStartingWithOrderByCorrelativoDesc(String prefijo);

    // Verificar si existe un correlativo específico
    boolean existsByCorrelativo(String correlativo);
}
