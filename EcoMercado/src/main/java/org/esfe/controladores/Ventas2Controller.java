package org.esfe.controladores;

import org.esfe.modelos.DetalleVenta;
import org.esfe.modelos.Producto;
import org.esfe.modelos.Usuario;
import org.esfe.modelos.Venta;
import org.esfe.servicios.interfaces.IDetalleVentaService;
import org.esfe.servicios.interfaces.IProductoService;
import org.esfe.servicios.interfaces.IVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/ventas2")
public class Ventas2Controller {

    @Autowired
    private IVentaService ventaService;

    @Autowired
    private IDetalleVentaService detalleVentaService;

    @Autowired
    private IProductoService productoService;

    // Lista de ventas
    @GetMapping
    public String index(Model model,
                        @RequestParam(required = false) String correlativo,
                        @RequestParam(required = false) String estado,
                        @RequestParam(defaultValue = "0") int page) {

        Page<Venta> ventas = ventaService.buscarVentasConFiltros(
                correlativo,
                estado,
                null, // usuario
                null, // tipoPago
                java.util.Optional.empty(),
                PageRequest.of(page, 6)
        );

        model.addAttribute("ventas", ventas);
        model.addAttribute("pageNumbers", java.util.stream.IntStream.range(0, ventas.getTotalPages()).toArray());
        return "ventas2/index";
    }

    // Detalle de venta y carrito
    @GetMapping("/detalle/{id}")
    public String detalleVenta(@PathVariable Integer id, Model model) {
        Venta venta = ventaService.obtenerPorId(id).orElse(null);
        if (venta == null) {
            model.addAttribute("error", "Venta no encontrada");
            return "ventas2/detalle";
        }

        List<DetalleVenta> detalles = detalleVentaService.obtenerPorVentaId(id);
        List<Producto> productos = productoService.obtenerTodos();

        model.addAttribute("venta", venta);
        model.addAttribute("detalles", detalles);
        model.addAttribute("productos", productos);

        // Usuario simulado (para demo)
        Usuario usuarioActual = venta.getUsuario();
        model.addAttribute("usuarioActual", usuarioActual);

        model.addAttribute("total", detalles.stream()
                .map(d -> BigDecimal.valueOf(d.getPrecioUnitario() * d.getCantidad()))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return "ventas2/detalle";
    }

    // Añadir producto al carrito
    @PostMapping("/carrito")
    public String addProductoCarrito(@RequestParam Integer idProducto,
                                     @RequestParam Integer cantidad,
                                     @RequestParam Float precioUnitario,
                                     @RequestParam(required = false) Integer ventaId) {

        Venta venta = ventaId != null ? ventaService.obtenerPorId(ventaId).orElse(new Venta()) : new Venta();

        DetalleVenta detalle = new DetalleVenta();
        detalle.setVenta(venta);
        detalle.setIdProducto(idProducto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(precioUnitario);

        detalleVentaService.crearOEditar(detalle);

        return "redirect:/ventas2/detalle/" + (ventaId != null ? ventaId : venta.getId());
    }

    // Incrementar/decrementar cantidad
    @PostMapping("/carrito/updateCantidad")
    public String updateCantidad(@RequestParam Integer detalleId,
                                 @RequestParam String accion) {

        DetalleVenta detalle = detalleVentaService.obtenerPorId(detalleId);
        if (detalle != null) {
            if ("incrementar".equals(accion)) {
                detalle.setCantidad(detalle.getCantidad() + 1);
            } else if ("decrementar".equals(accion) && detalle.getCantidad() > 1) {
                detalle.setCantidad(detalle.getCantidad() - 1);
            }
            detalleVentaService.crearOEditar(detalle);
        }

        return "redirect:/ventas2/detalle/" + detalle.getVenta().getId();
    }

    // Eliminar producto del carrito
    @PostMapping("/carrito/delete")
    public String deleteDetalle(@RequestParam Integer detalleId) {
        DetalleVenta detalle = detalleVentaService.obtenerPorId(detalleId);
        if (detalle != null) {
            Integer ventaId = detalle.getVenta().getId();
            detalleVentaService.eliminarPorId(detalleId);
            return "redirect:/ventas2/detalle/" + ventaId;
        }
        return "redirect:/ventas2";
    }

    // Finalizar venta
    @PostMapping("/carrito/guardar")
    public String finalizarVenta(@RequestParam Integer idUsuario,
                                 @RequestParam Integer idTipoPago,
                                 @RequestParam(required = false) Integer idTarjetaCredito,
                                 @RequestParam(required = false) Integer ventaId) {

        Venta venta;
        if (ventaId != null) {
            venta = ventaService.obtenerPorId(ventaId).orElse(new Venta());
        } else {
            venta = new Venta();
        }

        venta.setUsuario(new Usuario());
        venta.getUsuario().setId(idUsuario);
        venta.setTipoPago(null); // Aquí deberías obtener el TipoPago desde su servicio si lo tienes
        // venta.setTarjetaCredito(...); // Similar para TarjetaCredito
        venta.setFecha(LocalDateTime.now());
        venta.setEstado("Pendiente");

        // Calcular total
        List<DetalleVenta> detalles = detalleVentaService.obtenerPorVentaId(venta.getId());
        BigDecimal total = detalles.stream()
                .map(d -> BigDecimal.valueOf(d.getCantidad() * d.getPrecioUnitario()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        venta.setTotal(total);

        ventaService.save(venta);

        return "redirect:/ventas2/detalle/" + venta.getId();
    }

}
