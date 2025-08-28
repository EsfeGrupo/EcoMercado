package org.esfe.repositorios;

import org.esfe.modelos.TarjetaCredito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ITarjetaCreditoRepository extends JpaRepository<TarjetaCredito, Integer> {
    List<TarjetaCredito> findByFechaExpiracionBefore(LocalDate fechaActual);

    // Metodo para buscar tarjetas por ID de usuario (retorna List para el carrito)
    List<TarjetaCredito> findByUsuarioId(Integer usuarioId);

    Page<TarjetaCredito> findByUsuarioId(Integer usuarioId, Pageable pageable);
    
    Page<TarjetaCredito> findByUsuarioIdAndNombreTitularContainingIgnoreCaseAndBancoContainingIgnoreCaseOrderByIdDesc(
        Integer usuarioId, String nombreTitular, String banco, Pageable pageable);
}
