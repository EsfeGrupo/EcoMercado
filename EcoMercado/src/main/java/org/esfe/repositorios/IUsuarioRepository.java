package org.esfe.repositorios;

import org.esfe.modelos.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Método para el login
    Optional<Usuario> findByCorreoAndPassword(String correo, String password);

    // Método para búsquedas con paginación
    Page<Usuario> findByNombreContainingIgnoreCaseAndCorreoContainingIgnoreCaseAndRol_IdOrderByIdDesc(
            String nombre, String correo, Integer idRol, Pageable pageable);
}
