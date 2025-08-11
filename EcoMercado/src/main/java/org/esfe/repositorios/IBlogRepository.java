package org.esfe.repositorios;

import org.esfe.modelos.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBlogRepository extends JpaRepository<Blog, String> {
    Page<Blog> findByBlogContainingAndDescripcionContaining(String autor, String descripcion, Pageable pageable);
}
