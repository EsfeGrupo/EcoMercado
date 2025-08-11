package org.esfe.repositorios;

import org.esfe.modelos.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {
    Page<Usuario> findByNombreContainingIgnoreCaseAndCorreoContainingIgnoreCaseAndRol_Id(String nombre, String correo, Integer id, Pageable pageable);}
