package org.esfe.repositorios;

import org.esfe.modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductoRepository extends JpaRepository <Producto, Integer> {

    // Filtrar por nombre
    Page<Producto> findByNombreContainingIgnoreCaseAndPrecioIn(
            String nombre,
            List<Float> precio,
            Pageable pageable
    );

}
