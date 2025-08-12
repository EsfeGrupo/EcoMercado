package org.esfe.repositorios;

import org.esfe.modelos.Rol;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;


public interface IRolRepository extends JpaRepository<Rol, Integer> {
    Page<Rol> findByNombreContainingIgnoreCaseAndDescripcionContainingIgnoreCaseOrderByIdDesc(String nombre, String descripcion, Pageable pageable);
}
