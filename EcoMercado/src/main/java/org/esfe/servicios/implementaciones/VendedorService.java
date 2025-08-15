package org.esfe.servicios.implementaciones;

import org.esfe.modelos.Vendedor;
import org.esfe.repositorios.IVendedorRepository;
import org.esfe.servicios.interfaces.IVendedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendedorService implements IVendedorService {

    @Autowired
    private IVendedorRepository vendedorRepository;

    @Override
    public Page<Vendedor> obtenerTodosPaginados(Pageable pageable) {
        return vendedorRepository.findAll(pageable);
    }

    @Override
    public List<Vendedor> obtenerTodos() {
        return vendedorRepository.findAll();
    }

    @Override
    public Page<Vendedor> findByNombreContainingIgnoreCaseAndUbicacionContainingIgnoreCase(
            String nombre,
            String ubicacion,
            Pageable pageable
    ) {
        return vendedorRepository.findByNombreContainingIgnoreCaseAndUbicacionContainingIgnoreCase(nombre, ubicacion, pageable);
    }

    @Override
    public Vendedor obtenerPorId(Integer id) {
        return vendedorRepository.findById(id).orElse(null);
    }

    @Override
    public Vendedor crearOEditar(Vendedor vendedor) {
        return vendedorRepository.save(vendedor);
    }

    @Override
    public void eliminarPorId(Integer id) {
        vendedorRepository.deleteById(id);
    }
}
