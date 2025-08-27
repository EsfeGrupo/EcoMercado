package org.esfe.controladores;

import org.esfe.modelos.Producto;
import org.esfe.modelos.Venta;
import org.esfe.modelos.DetalleVenta;
import org.esfe.servicios.interfaces.IVentaService;
import org.esfe.servicios.interfaces.IDetalleVentaService;
import org.esfe.repositorios.IProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/carrito")
public class DetalleVentaController {
    @Autowired
    private IVentaService ventaService;
    @Autowired
    private IDetalleVentaService detalleVentaService;
    @Autowired
    private IProductoRepository productoRepository;

    @GetMapping
    public String verCarrito(@RequestParam(value = "ventaId", required = false) Integer ventaId, Model model) {
        if (ventaId != null) {
            Venta venta = ventaService.obtenerPorId(ventaId).orElse(null);
            List<DetalleVenta> detalles = ventaId != null ? detalleVentaService.obtenerPorVentaId(ventaId) : List.of();
            model.addAttribute("venta", venta);
            model.addAttribute("detalles", detalles);
            model.addAttribute("productos", productoRepository.findAll());
            model.addAttribute("detalleNuevo", new DetalleVenta());
            double total = detalles.stream().mapToDouble(d -> d.getPrecioUnitario() * d.getCantidad()).sum();
            model.addAttribute("total", total);
        } else {
            model.addAttribute("venta", null);
            model.addAttribute("detalles", List.of());
            model.addAttribute("productos", productoRepository.findAll());
            model.addAttribute("detalleNuevo", new DetalleVenta());
            model.addAttribute("total", 0);
        }
        return "venta/detalleVenta";
    }

    @PostMapping("/add")
    public String addDetalleCarrito(@RequestParam("idProducto") Integer idProducto,
                                  @RequestParam("cantidad") Integer cantidad,
                                  @RequestParam("ventaId") Integer ventaId,
                                  RedirectAttributes attributes, Model model) {
        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto == null) {
            attributes.addFlashAttribute("error", "Producto no encontrado");
            return "Venta/detalleVenta";
        }
        if (producto.getStock() < cantidad) {
            attributes.addFlashAttribute("error", "Stock insuficiente para el producto seleccionado");
            return "Venta/detalleVenta";
        }
        List<DetalleVenta> detalles = detalleVentaService.obtenerPorVentaId(ventaId);
        DetalleVenta existente = detalles.stream()
            .filter(d -> d.getIdProducto().equals(idProducto))
            .findFirst().orElse(null);
        if (existente != null) {
            int nuevaCantidad = existente.getCantidad() + cantidad;
            if (producto.getStock() < nuevaCantidad) {
                attributes.addFlashAttribute("error", "Stock insuficiente para el producto seleccionado");
                return "Venta/detalleVenta";
            }
            existente.setCantidad(nuevaCantidad);
            existente.setPrecioUnitario(producto.getPrecio().floatValue());
            detalleVentaService.crearOEditar(existente);
        } else {
            DetalleVenta detalleNuevo = new DetalleVenta();
            detalleNuevo.setVenta(ventaService.obtenerPorId(ventaId).get());
            detalleNuevo.setIdProducto(idProducto);
            detalleNuevo.setCantidad(cantidad);
            detalleNuevo.setPrecioUnitario(producto.getPrecio().floatValue());
            detalleVentaService.crearOEditar(detalleNuevo);
        }
        attributes.addFlashAttribute("msg", "Producto añadido correctamente");
        return "Venta/detalleVenta";
    }

    @PostMapping("/updateCantidad")
    public String updateCantidadCarrito(@RequestParam("detalleId") Integer detalleId,
                                             @RequestParam("ventaId") Integer ventaId,
                                             @RequestParam("accion") String accion,
                                             RedirectAttributes attributes, Model model) {
        DetalleVenta detalle = detalleVentaService.obtenerPorId(detalleId);
        if (detalle == null) {
            attributes.addFlashAttribute("error", "Detalle no encontrado");
            return "Venta/detalleVenta";
        }
        Producto producto = productoRepository.findById(detalle.getIdProducto()).orElse(null);
        if (producto == null) {
            attributes.addFlashAttribute("error", "Producto no encontrado");
            return "Venta/detalleVenta";
        }
        int cantidadActual = detalle.getCantidad();
        if ("incrementar".equals(accion)) {
            if (producto.getStock() < cantidadActual + 1) {
                attributes.addFlashAttribute("error", "Stock insuficiente para el producto seleccionado");
                return "Venta/detalleVenta";
            }
            detalle.setCantidad(cantidadActual + 1);
        } else if ("decrementar".equals(accion)) {
            if (cantidadActual > 1) {
                detalle.setCantidad(cantidadActual - 1);
            } else {
                detalleVentaService.eliminarPorId(detalleId);
                attributes.addFlashAttribute("msg", "Producto eliminado del carrito");
                return "Venta/detalleVenta";
            }
        }
        detalle.setPrecioUnitario(producto.getPrecio().floatValue());
        detalleVentaService.crearOEditar(detalle);
        attributes.addFlashAttribute("msg", "Cantidad actualizada");
        return "Venta/detalleVenta";
    }

    @PostMapping("/delete")
    public String deleteDetalleCarrito(@RequestParam("detalleId") Integer detalleId,
                                     @RequestParam("ventaId") Integer ventaId,
                                     RedirectAttributes attributes, Model model) {
        detalleVentaService.eliminarPorId(detalleId);
        attributes.addFlashAttribute("msg", "Producto eliminado del carrito");
        return "Venta/detalleVenta";
    }

    @PostMapping("/guardar")
    public String guardarCarrito(@RequestParam("ventaId") Integer ventaId, RedirectAttributes attributes, Model model) {
        List<DetalleVenta> detalles = detalleVentaService.obtenerPorVentaId(ventaId);
        for (DetalleVenta detalle : detalles) {
            Producto producto = productoRepository.findById(detalle.getIdProducto()).orElse(null);
            if (producto != null) {
                int nuevoStock = producto.getStock() - detalle.getCantidad();
                if (nuevoStock < 0) {
                    attributes.addFlashAttribute("error", "Stock insuficiente para el producto " + producto.getNombre());
                    return "Venta/detalleVenta";
                }
                producto.setStock(nuevoStock);
                productoRepository.save(producto);
            }
        }
        attributes.addFlashAttribute("msg", "Venta guardada y stock actualizado correctamente");
        return "Venta/detalleVenta";
    }
}

