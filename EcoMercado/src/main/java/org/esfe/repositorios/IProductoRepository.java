package org.esfe.repositorios;

import org.esfe.modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;

public interface IProductoRepository extends JpaRepository<Producto, Integer> {

    // Buscar por nombre y precio
    Page<Producto> findByNombreContainingIgnoreCaseAndPrecio(
            String nombre,
            Double precio,
            Pageable pageable
    );

}