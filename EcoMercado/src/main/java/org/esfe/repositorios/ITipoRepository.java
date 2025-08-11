package org.esfe.repositorios;

import org.esfe.modelos.Tipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITipoRepository extends JpaRepository<Tipo, Integer> {
    Page<Tipo> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
