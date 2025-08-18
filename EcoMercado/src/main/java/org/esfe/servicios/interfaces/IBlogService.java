package org.esfe.servicios.interfaces;

import org.esfe.modelos.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IBlogService {
    Page<Blog> obtenerTodosPaginados(Pageable pageable);

    List<Blog> obtenerTodos();

    Optional<Blog> obtenerPorId(Integer id);

    Blog crearOEditar(Blog blog);

    void eliminarPorId(Integer id);

    Page<Blog> findByAutorContainingIgnoreCaseAndDescripcionContainingIgnoreCase(String autor, String descripcion, Pageable pageable);
}
