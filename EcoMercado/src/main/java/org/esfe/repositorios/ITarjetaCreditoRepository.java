package org.esfe.repositorios;

import org.esfe.modelos.TarjetaCredito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ITarjetaCreditoRepository extends JpaRepository<TarjetaCredito, Integer> {
    Page<TarjetaCredito> findByNumeroContainingIgnoreCaseAndNombreTitularContainingIgnoreCaseAndBancoContainingIgnoreCaseOrderByIdDesc(String numero, String nombreTitular, String banco, Pageable pageable);

    List<TarjetaCredito> findByFechaExpiracionBefore(LocalDate fechaActual);
}
