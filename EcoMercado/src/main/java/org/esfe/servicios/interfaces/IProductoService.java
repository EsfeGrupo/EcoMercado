package org.esfe.servicios.interfaces;

import org.esfe.modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductoService {

    Page<Producto> obtenerTodosPaginados(Pageable pageable);

    List<Producto> obtenerTodos();

    Page<Producto> findByNombreContainingIgnoreCaseAndPrecio(
            String nombre,
            Double precio,
            Pageable pageable
    );

    // Método faltante para buscar solo por nombre
    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Producto obtenerPorId(Integer id);

    Producto crearOEditar(Producto producto);

    void eliminarPorId(Integer id);
}