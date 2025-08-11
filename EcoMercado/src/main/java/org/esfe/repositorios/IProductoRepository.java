package org.esfe.repositorios;

import org.esfe.modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface IProductoRepository extends JpaRepository <Producto, Integer> {

    // Filtrar por nombre
    Page<Producto> findByNombreContainingIgnoreCase(
            String nombre,
            Pageable pageable
    );

    // Filtrar por nombre que contenga y precio mayor que
    Page<Producto> findByPrecioGreaterThan(
            Float precio,
            Pageable pageable
    );
    // Filtrar por nombre que contenga y precio menor que
    Page<Producto> findByPrecioLessThan(
            Float precio,
            Pageable pageable
    );
}
