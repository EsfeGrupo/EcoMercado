package org.esfe.servicios.interfaces;

import org.esfe.modelos.Tipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ITipoService {
    Page<Tipo> obtenerTodosPaginados(Pageable pageable);

    List<Tipo> obtenerTodos();

    Page<Tipo> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Optional<Tipo> obtenerPorId(Integer id);

    Tipo crearOEditar(Tipo tipo);

    void eliminarPorId(Integer id);
}
