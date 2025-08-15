package org.esfe.servicios.implementaciones;

import org.esfe.modelos.TipoPago;
import org.esfe.repositorios.ITipoPagoRepository;
import org.esfe.servicios.interfaces.ITipoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoPagoService implements ITipoPagoService {

    @Autowired
    private ITipoPagoRepository tipoPagoRepository;

    @Override
    public Page<TipoPago> obtenerTodosPaginados(Pageable pageable) {
        return tipoPagoRepository.findAll(pageable);
    }

    @Override
    public List<TipoPago> obtenerTodos() {
        return List.of();
    }

    @Override
    public Page<TipoPago> findByMetodoPagoContainingAndDescripcionContaining(String metodoPago, String descripcion, Pageable pageable) {
        return tipoPagoRepository.findBymetodoPagoContainingAndDescripcionContaining(metodoPago, descripcion, pageable);
    }

    @Override
    public Optional<TipoPago> obtenerPorId(Integer id) {
        return tipoPagoRepository.findById(id);
    }

    @Override
    public TipoPago crearOEditar(TipoPago tipoPago) {
        return tipoPagoRepository.save(tipoPago);
    }

    @Override
    public void eliminarPorId(Integer id) {
        tipoPagoRepository.deleteById(id);
    }
}
