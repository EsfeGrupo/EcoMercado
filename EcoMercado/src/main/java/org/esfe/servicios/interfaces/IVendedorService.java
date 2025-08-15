package org.esfe.servicios.interfaces;


import org.esfe.modelos.Vendedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IVendedorService {

    Page<Vendedor> obtenerTodosPaginados(Pageable pageable);

    List<Vendedor> obtenerTodos();

    Page<Vendedor> findByNombreContainingIgnoreCaseAndUbicacionContainingIgnoreCase(
            String nombre,
            String ubicacion,
            Pageable pageable
    );

    Vendedor obtenerPorId(Integer id);

    Vendedor crearOEditar(Vendedor vendedor);

    void eliminarPorId(Integer id);
}
