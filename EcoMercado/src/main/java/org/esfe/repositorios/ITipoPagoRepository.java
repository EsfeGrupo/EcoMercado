package org.esfe.repositorios;

import org.esfe.modelos.TipoPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITipoPagoRepository extends JpaRepository<TipoPago, Integer> {
    Page<TipoPago> findBymetodoPagoContainingAndDescripcionContaining(String metodoPago, String descripcion, Pageable pageable);
}
