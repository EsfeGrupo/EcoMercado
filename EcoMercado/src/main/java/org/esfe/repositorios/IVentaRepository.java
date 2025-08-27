package org.esfe.repositorios;

import org.esfe.modelos.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IVentaRepository extends JpaRepository<Venta, Integer> {
    Page<Venta> findByCorrelativoContainingIgnoreCaseAndEstadoAndUsuario_IdAndTipoPago_IdAndTarjetaCredito_IdOrderByIdDesc(String correlativo, Byte estado, Integer idUsuario, Integer idTipoPago, Optional<Integer> idTarjetaCredito, Pageable pageable);
    Page<Venta> findByCorrelativoContainingIgnoreCase(String correlativo, Pageable pageable);
    // Buscar ventas por correlativo que comience con un prefijo, ordenadas por correlativo descendente
    List<Venta> findByCorrelativoStartingWithOrderByCorrelativoDesc(String prefijo);

    // Verificar si existe un correlativo específico
    boolean existsByCorrelativo(String correlativo);
}
