package org.esfe.servicios.interfaces;

import org.esfe.modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

public interface IProductoService {

    Page<Producto> obtenerTodos(Pageable pageable);

    Optional<Producto> obtenerPorId(Integer id);

    Producto guardar(Producto producto);

    void eliminarPorId(Integer id);

    Page<Producto> buscarPorNombre(String nombre, Pageable pageable);

    Page<Producto> buscarPorPrecio(BigDecimal precio, Pageable pageable);

}
