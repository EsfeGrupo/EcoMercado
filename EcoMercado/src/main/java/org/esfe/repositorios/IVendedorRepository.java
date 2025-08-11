package org.esfe.repositorios;

import org.esfe.modelos.Vendedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVendedorRepository extends JpaRepository<Vendedor, Integer> {

    // Buscar por idVendedor exacto
    Page<Vendedor> findByIdVendedor(String idVendedor, Pageable pageable);

    // Buscar por dirección (parcial, sin importar mayúsculas)
    Page<Vendedor> findByDireccionContainingIgnoreCase(String direccion, Pageable pageable);

    // Buscar por teléfono exacto
    Page<Vendedor> findByTelefono(String telefono, Pageable pageable);

    // Buscar por DUI exacto
    Page<Vendedor> findByDui(String dui, Pageable pageable);

    // Buscar por ubicación parcial
    Page<Vendedor> findByUbicacionContainingIgnoreCase(String ubicacion, Pageable pageable);

    // Búsqueda combinada por nombre (heredado de Usuario) y ubicación
    Page<Vendedor> findByNombreContainingIgnoreCaseAndUbicacionContainingIgnoreCase(String nombre, String ubicacion, Pageable pageable);
}
