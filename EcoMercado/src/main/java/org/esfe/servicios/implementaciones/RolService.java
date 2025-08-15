package org.esfe.servicios.implementaciones;

import org.esfe.modelos.Rol;
import org.esfe.repositorios.IRolRepository;
import org.esfe.servicios.interfaces.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class RolService implements IRolService {
    @Autowired
    private IRolRepository rolRepository;

    @Override
    public Page<Rol> obtenerTodosPaginados(Pageable pageable) {
        return rolRepository.findAll(pageable);
    }

    @Override
    public List<Rol> obtenerTodos() {
        return rolRepository.findAll();
    }

    @Override
    public Page<Rol> findByNombreContainingIgnoreCaseAndDescripcionContainingIgnoreCaseOrderByIdDesc(String nombre, String descripcion, Pageable pageable) {
        return rolRepository.findByNombreContainingIgnoreCaseAndDescripcionContainingIgnoreCaseOrderByIdDesc(nombre, descripcion, pageable);
    }

    @Override
    public Rol obtenerPorId(Integer id) {
        Optional<Rol> rolOptional = rolRepository.findById(id);
        return rolOptional.orElse(null);
    }

    @Override
    public Rol crearOEditar(Rol rol) {
        return rolRepository.save(rol);
    }

    @Override
    public void eliminarPorId(Integer id) {
        rolRepository.deleteById(id);
    }
}
