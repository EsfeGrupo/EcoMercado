package org.esfe.servicios.implementaciones;

import org.esfe.modelos.Tipo;
import org.esfe.repositorios.ITipoRepository;
import org.esfe.servicios.interfaces.ITipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class TipoService implements ITipoService {

    @Autowired
    private ITipoRepository tipoRepository;

    @Override
    public Page<Tipo> obtenerTodosPaginados(Pageable pageable) {
        return tipoRepository.findAll(pageable);
    }

    @Override
    public List<Tipo> obtenerTodos() {
        return List.of();
    }

    @Override
    public Page<Tipo> findByNombreContainingIgnoreCase(String nombre, Pageable pageable) {
        return tipoRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    @Override
    public Optional<Tipo> obtenerPorId(Integer id) {
        return tipoRepository.findById(id);
    }

    @Override
    public Tipo crearOEditar(Tipo tipo) {
        return tipoRepository.save(tipo);
    }

    @Override
    public void eliminarPorId(Integer id) {
        tipoRepository.deleteById(id);
    }
}
