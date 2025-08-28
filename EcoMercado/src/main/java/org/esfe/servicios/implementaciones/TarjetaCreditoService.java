package org.esfe.servicios.implementaciones;


import org.esfe.modelos.TarjetaCredito;
import org.esfe.repositorios.ITarjetaCreditoRepository;
import org.esfe.servicios.interfaces.ITarjetaCreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class TarjetaCreditoService implements ITarjetaCreditoService {


    @Autowired
    private ITarjetaCreditoRepository tarjetaCreditoRepository;


    @Override
    public Page<TarjetaCredito> obtenerTodosPaginados(Pageable pageable) {
        return tarjetaCreditoRepository.findAll(pageable);
    }


    @Override
    public List<TarjetaCredito> obtenerTodos() {
        return tarjetaCreditoRepository.findAll();
    }


    @Override
    public Page<TarjetaCredito> findByUsuarioId(Integer usuarioId, Pageable pageable) {
        return tarjetaCreditoRepository.findByUsuarioId(usuarioId, pageable);
    }

    @Override
    public Page<TarjetaCredito> findByUsuarioIdAndNombreTitularContainingIgnoreCaseAndBancoContainingIgnoreCaseOrderByIdDesc(
            Integer usuarioId, String nombreTitular, String banco, Pageable pageable) {
        return tarjetaCreditoRepository.findByUsuarioIdAndNombreTitularContainingIgnoreCaseAndBancoContainingIgnoreCaseOrderByIdDesc(
            usuarioId, nombreTitular, banco, pageable);
    }

    @Override
    public TarjetaCredito obtenerPorId(Integer id) {
        Optional<TarjetaCredito> tarjeta = tarjetaCreditoRepository.findById(id);
        return tarjeta.orElse(null);
    }


    @Override
    public TarjetaCredito crearOEditar(TarjetaCredito tarjetaCredito) {
        return tarjetaCreditoRepository.save(tarjetaCredito);
    }


    @Override
    public void eliminarPorId(Integer id) {
        tarjetaCreditoRepository.deleteById(id);
    }


    @Override
    public List<TarjetaCredito> findByFechaExpiracionBefore(LocalDate fechaActual) {
        return tarjetaCreditoRepository.findByFechaExpiracionBefore(fechaActual);
    }
}
