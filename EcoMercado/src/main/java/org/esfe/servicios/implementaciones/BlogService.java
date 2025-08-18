package org.esfe.servicios.implementaciones;

import org.esfe.modelos.Blog;
import org.esfe.repositorios.IBlogRepository;
import org.esfe.servicios.interfaces.IBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlogService implements IBlogService  {

    @Autowired
    private IBlogRepository blogRepository;

    @Override
    public Page<Blog> obtenerTodosPaginados(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Override
    public List<Blog> obtenerTodos() {
        return List.of();
    }

    @Override
    public Optional<Blog> obtenerPorId(Integer id) {
        return blogRepository.findById(id);
    }

    @Override
    public Blog crearOEditar(Blog blog) {
        return blogRepository.save(blog);
    }

    @Override
    public void eliminarPorId(Integer id) {
        blogRepository.deleteById(id);
    }

    @Override
    public Page<Blog> findByAutorContainingIgnoreCaseAndDescripcionContainingIgnoreCase(String autor, String descripcion, Pageable pageable) {
        return blogRepository.findByAutorContainingIgnoreCaseAndDescripcionContainingIgnoreCase(autor, descripcion, pageable);
    }
}
