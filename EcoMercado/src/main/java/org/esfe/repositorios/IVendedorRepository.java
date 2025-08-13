package org.esfe.repositorios;

import org.esfe.modelos.Vendedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVendedorRepository extends JpaRepository<Vendedor, Integer> {

    // Buscar vendedores por nombre (parcial, sin importar mayúsculas)
    Page<Vendedor> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    // Buscar vendedores por ubicación (parcial, sin importar mayúsculas)
    Page<Vendedor> findByUbicacionContainingIgnoreCase(String ubicacion, Pageable pageable);
}
