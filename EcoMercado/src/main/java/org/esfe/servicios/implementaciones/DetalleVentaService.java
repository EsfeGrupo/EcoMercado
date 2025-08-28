package org.esfe.servicios.implementaciones;


import org.esfe.modelos.DetalleVenta;
import org.esfe.repositorios.IDetalleVentaRepository;
import org.esfe.servicios.interfaces.IDetalleVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class DetalleVentaService implements IDetalleVentaService {

    @Autowired
    private IDetalleVentaRepository detalleVentaRepository;

    @Override
    public Page<DetalleVenta> obtenerTodosPaginados(Pageable pageable) {
        return detalleVentaRepository.findAll(pageable);
    }

    @Override
    public List<DetalleVenta> obtenerTodos() {
        return detalleVentaRepository.findAll();
    }

    @Override
    public Page<DetalleVenta> findByIdProducto(Integer idProducto, Pageable pageable) {
        return detalleVentaRepository.findByIdProducto(idProducto, pageable);
    }

    @Override
    public DetalleVenta obtenerPorId(Integer id) {
        return detalleVentaRepository.findById(id).orElse(null);
    }

    @Override
    public DetalleVenta crearOEditar(DetalleVenta detalleVenta) {
        return detalleVentaRepository.save(detalleVenta);
    }

    @Override
    public void eliminarPorId(Integer id) {
        detalleVentaRepository.deleteById(id);
    }
    
    @Override
    public List<DetalleVenta> obtenerPorVentaId(Integer idVenta) {
        // Llama al metodo del repositorio
        return detalleVentaRepository.findByVenta_Id(idVenta);
    }
    @Override
    @Transactional
    public void eliminarPorVentaId(Integer ventaId) {
        detalleVentaRepository.deleteByVentaId(ventaId);
    }
}
