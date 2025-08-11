package org.esfe.repositorios;

import org.esfe.modelos.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
c

public interface IRolRepository extends JpaRepository<Rol, Integer> {
    Page<Rol> findByNombreContainingIgnoreCaseAndDescripcionContainingIgnoreCase(String nombre, String descripcion, Pageable pageable);
}
