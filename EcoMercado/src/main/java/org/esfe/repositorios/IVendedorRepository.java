package org.esfe.repositorios;

import org.esfe.modelos.Vendedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVendedorRepository extends JpaRepository<Vendedor, Integer> {

    // Buscar por nombre y ubicación al mismo tiempo
    Page<Vendedor> findByNombreContainingIgnoreCaseAndUbicacionContainingIgnoreCase(
            String nombre,
            String ubicacion,
            Pageable pageable
    );
}
