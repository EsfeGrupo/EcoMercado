package org.esfe.servicios.interfaces;

import org.esfe.modelos.TarjetaCredito;
import org.esfe.modelos.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ITarjetaCreditoService {
    Page<TarjetaCredito> obtenerTodosPaginados(Pageable pageable);

    List<TarjetaCredito> obtenerTodos();

    Page<TarjetaCredito> findByNumeroContainingIgnoreCaseAndNombreTitularContainingIgnoreCaseAndBancoContainingIgnoreCaseOrderByIdDesc(String numero, String nombreTitular, String banco, Pageable pageable);

    TarjetaCredito obtenerPorId(Integer id);

    TarjetaCredito crearOEditar(TarjetaCredito tarjetaCredito);

    void eliminarPorId(Integer id);

    List<TarjetaCredito> findByFechaExpiracionBefore(LocalDate fechaActual);
}
