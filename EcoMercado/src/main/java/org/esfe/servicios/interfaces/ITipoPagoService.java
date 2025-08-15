package org.esfe.servicios.interfaces;

import org.esfe.modelos.TipoPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;


public interface ITipoPagoService {
    Page<TipoPago> obtenerTodosPaginados(Pageable pageable);

    List<TipoPago> obtenerTodos();

    Page<TipoPago> findByMetodoPagoContainingAndDescripcionContaining(String metodoPago, String descripcion, Pageable pageable);

    Optional<TipoPago> obtenerPorId(Integer id);

    TipoPago crearOEditar(TipoPago tipoPago);

    void eliminarPorId(Integer id);

}
