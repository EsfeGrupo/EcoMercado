package org.esfe.repositorios;

import org.esfe.modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductoRepository extends JpaRepository<Producto, Integer> {

    // Buscar productos por nombre parcial y precio opcional
    Page<Producto> findByNombreContainingIgnoreCaseAndPrecio(
            String nombre,
            Double precio,
            Pageable pageable
    );

    // Alternativa si quieres solo nombre y paginación:
    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
