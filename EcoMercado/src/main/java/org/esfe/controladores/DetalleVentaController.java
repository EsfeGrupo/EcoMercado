package org.esfe.controladores;

import org.esfe.modelos.Producto;
import org.esfe.modelos.Venta;
import org.esfe.modelos.DetalleVenta;
import org.esfe.modelos.TipoPago;
import org.esfe.modelos.TarjetaCredito;
import org.esfe.modelos.Usuario;
import org.esfe.servicios.interfaces.IVentaService;
import org.esfe.servicios.interfaces.IDetalleVentaService;
import org.esfe.repositorios.IProductoRepository;
import org.esfe.repositorios.ITipoPagoRepository;
import org.esfe.repositorios.ITarjetaCreditoRepository;
import org.esfe.repositorios.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/carrito")
@SessionAttributes("carritoTemporal")
public class DetalleVentaController {
    @Autowired
    private IVentaService ventaService;

    @Autowired
    private IDetalleVentaService detalleVentaService;

    @Autowired
    private IProductoRepository productoRepository;

    @Autowired
    private ITipoPagoRepository tipoPagoRepository;

    @Autowired
    private ITarjetaCreditoRepository tarjetaCreditoRepository;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @ModelAttribute("carritoTemporal")
    public List<DetalleVenta> carritoTemporal() {
        return new java.util.ArrayList<>();
    }

    @GetMapping
    public String verCarrito(Model model, @ModelAttribute("carritoTemporal") List<DetalleVenta> carrito,
                             HttpSession session) {
        // Obtener el usuario de la sesión
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioSession");
        if (usuarioActual == null) {
            return "redirect:/login";
        }

        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("detalles", carrito);
        model.addAttribute("venta", null); // No hay venta aún

        // Agregar datos para el formulario de finalización de compra
        model.addAttribute("usuarioActual", usuarioActual); // Usuario logueado
        model.addAttribute("tiposPago", tipoPagoRepository.findAll());

        // Filtrar tarjetas solo del usuario logueado
        List<TarjetaCredito> tarjetasUsuario = tarjetaCreditoRepository.findByUsuarioId(usuarioActual.getId());

        // Enmascarar números de tarjeta para mostrar
        tarjetasUsuario.forEach(tarjeta -> {
            if (tarjeta.getNumeroEncriptado() != null && tarjeta.getNumeroEncriptado().length() >= 4) {
                tarjeta.setNumero("**** **** **** " + tarjeta.getNumeroEncriptado().substring(tarjeta.getNumeroEncriptado().length() - 4));
            }
        });

        model.addAttribute("tarjetasCredito", tarjetasUsuario);

        double total = carrito.stream().mapToDouble(d -> d.getPrecioUnitario() * d.getCantidad()).sum();
        model.addAttribute("total", total);
        return "venta/detalleVenta";
    }

    @PostMapping
    public String addProductoAlCarrito(
            @RequestParam("idProducto") Integer idProducto,
            @RequestParam("cantidad") Integer cantidad,
            @RequestParam("precioUnitario") Float precioUnitario,
            @ModelAttribute("carritoTemporal") List<DetalleVenta> carrito,
            RedirectAttributes attributes,
            HttpSession session) {

        // Verificar sesión
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioSession");
        if (usuarioActual == null) {
            return "redirect:/login";
        }

        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto == null) {
            attributes.addFlashAttribute("error", "Producto no encontrado");
            return "redirect:/carrito";
        }
        if (producto.getStock() < cantidad) {
            attributes.addFlashAttribute("error", "Stock insuficiente");
            return "redirect:/carrito";
        }

        // Ver si ya existe el producto en el carrito
        DetalleVenta existente = carrito.stream()
                .filter(d -> d.getIdProducto().equals(idProducto))
                .findFirst()
                .orElse(null);

        if (existente != null) {
            int nuevaCantidad = existente.getCantidad() + cantidad;
            if (producto.getStock() < nuevaCantidad) {
                attributes.addFlashAttribute("error", "Stock insuficiente para esa cantidad");
                return "redirect:/carrito";
            }
            existente.setCantidad(nuevaCantidad);
        } else {
            DetalleVenta nuevo = new DetalleVenta();
            nuevo.setIdProducto(idProducto);
            nuevo.setCantidad(cantidad);
            nuevo.setPrecioUnitario(precioUnitario);
            carrito.add(nuevo);
        }

        attributes.addFlashAttribute("msg", "Producto añadido al carrito");
        return "redirect:/carrito";
    }

    @PostMapping("/guardar")
    public String guardarCarrito(
            @RequestParam("idUsuario") Integer idUsuario,
            @RequestParam("idTipoPago") Integer idTipoPago,
            @RequestParam(value = "idTarjetaCredito", required = false) Integer idTarjetaCredito,
            @ModelAttribute("carritoTemporal") List<DetalleVenta> carrito,
            RedirectAttributes attributes,
            SessionStatus sessionStatus,
            HttpSession session) {

        // Verificar sesión
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioSession");
        if (usuarioActual == null) {
            return "redirect:/login";
        }

        if (carrito.isEmpty()) {
            attributes.addFlashAttribute("error", "El carrito está vacío");
            return "redirect:/carrito";
        }

        // Verificar que el usuario seleccionado sea el usuario logueado
        if (!idUsuario.equals(usuarioActual.getId())) {
            attributes.addFlashAttribute("error", "No puede realizar compras para otros usuarios");
            return "redirect:/carrito";
        }

        try {
            // Log para debug
            System.out.println("=== PROCESANDO VENTA ===");
            System.out.println("Usuario ID: " + idUsuario);
            System.out.println("Tipo Pago ID: " + idTipoPago);
            System.out.println("Tarjeta ID: " + idTarjetaCredito);
            System.out.println("Productos en carrito: " + carrito.size());

            // Validar usuario
            Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
            if (usuario == null) {
                attributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/carrito";
            }

            // Validar tipo de pago
            TipoPago tipoPago = tipoPagoRepository.findById(idTipoPago).orElse(null);
            if (tipoPago == null) {
                attributes.addFlashAttribute("error", "Método de pago no encontrado");
                return "redirect:/carrito";
            }

            // Validar tarjeta (opcional - acepta null o tarjeta válida del usuario)
            TarjetaCredito tarjetaCredito = null;
            if (idTarjetaCredito != null && idTarjetaCredito > 0) {
                tarjetaCredito = tarjetaCreditoRepository.findById(idTarjetaCredito).orElse(null);
                if (tarjetaCredito == null) {
                    attributes.addFlashAttribute("error", "Tarjeta de crédito no encontrada");
                    return "redirect:/carrito";
                }

                // Verificar que la tarjeta pertenezca al usuario logueado
                if (!tarjetaCredito.getUsuario().getId().equals(usuarioActual.getId())) {
                    attributes.addFlashAttribute("error", "No puede usar tarjetas de otros usuarios");
                    return "redirect:/carrito";
                }
            }

            // Verificar stock antes de proceder
            for (DetalleVenta detalle : carrito) {
                Producto producto = productoRepository.findById(detalle.getIdProducto()).orElse(null);
                if (producto == null) {
                    attributes.addFlashAttribute("error", "Producto no encontrado: " + detalle.getIdProducto());
                    return "redirect:/carrito";
                }
                if (producto.getStock() < detalle.getCantidad()) {
                    attributes.addFlashAttribute("error", "Stock insuficiente para: " + producto.getNombre());
                    return "redirect:/carrito";
                }
            }

            // Calcular total
            double totalDouble = carrito.stream()
                    .mapToDouble(d -> d.getPrecioUnitario() * d.getCantidad())
                    .sum();
            BigDecimal total = BigDecimal.valueOf(totalDouble);

            // Crear la venta
            Venta venta = new Venta();
            venta.setUsuario(usuario);
            venta.setTipoPago(tipoPago);
            venta.setTarjetaCredito(tarjetaCredito); // Puede ser null para efectivo
            venta.setFecha(LocalDateTime.now());
            venta.setTotal(total);
            venta.setEstado("PENDIENTE");

            // Generar correlativo simple
            String correlativo = "VENTA-" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            venta.setCorrelativo(correlativo);

            // Guardar la venta
            venta = ventaService.crearOEditar(venta);
            System.out.println("Venta creada con ID: " + venta.getId());

            // Procesar detalles y actualizar stock
            for (DetalleVenta detalle : carrito) {
                Producto producto = productoRepository.findById(detalle.getIdProducto()).get();

                // Reducir stock
                producto.setStock(producto.getStock() - detalle.getCantidad());
                productoRepository.save(producto);

                // Asignar venta al detalle
                detalle.setVenta(venta);
                detalleVentaService.crearOEditar(detalle);
            }

            // Limpiar carrito
            sessionStatus.setComplete();
            attributes.addFlashAttribute("success", "Venta procesada exitosamente. Correlativo: " + correlativo);

            // Redirección según rol
            String rol = usuarioActual.getRol().getNombre();
            if ("ADMIN".equalsIgnoreCase(rol)) {
                return "redirect:/ventas";      // admin
            } else if ("VENDEDOR".equalsIgnoreCase(rol)) {
                return "redirect:/ventas2";     // vendedor
            } else {
                return "redirect:/login";       // cualquier otro caso
            }


        } catch (Exception e) {
            System.err.println("ERROR en guardarCarrito: " + e.getMessage());
            e.printStackTrace();
            attributes.addFlashAttribute("error", "Error inesperado: " + e.getMessage());
            return "redirect:/carrito";
        }
    }

    @PostMapping("/delete")
    public String eliminarProductoDelCarrito(@RequestParam("detalleId") Integer detalleId,
                                             @ModelAttribute("carritoTemporal") List<DetalleVenta> carrito,
                                             RedirectAttributes attributes,
                                             HttpSession session) {
        // Verificar sesión
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioSession");
        if (usuarioActual == null) {
            return "redirect:/login";
        }

        carrito.removeIf(d -> d.getIdProducto().equals(detalleId));
        attributes.addFlashAttribute("msg", "Producto eliminado del carrito");
        return "redirect:/carrito";
    }

    @PostMapping("/updateCantidad")
    public String actualizarCantidad(@RequestParam("detalleId") Integer detalleId,
                                     @RequestParam("accion") String accion,
                                     @ModelAttribute("carritoTemporal") List<DetalleVenta> carrito,
                                     RedirectAttributes attributes,
                                     HttpSession session) {
        // Verificar sesión
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioSession");
        if (usuarioActual == null) {
            return "redirect:/login";
        }

        for (DetalleVenta d : carrito) {
            if (d.getIdProducto().equals(detalleId)) {
                if ("incrementar".equals(accion)) {
                    Producto prod = productoRepository.findById(d.getIdProducto()).orElse(null);
                    if (prod != null && prod.getStock() > d.getCantidad()) {
                        d.setCantidad(d.getCantidad() + 1);
                    } else {
                        attributes.addFlashAttribute("error", "Stock insuficiente");
                    }
                } else if ("decrementar".equals(accion)) {
                    if (d.getCantidad() > 1) {
                        d.setCantidad(d.getCantidad() - 1);
                    } else {
                        carrito.remove(d);
                    }
                }
                break;
            }
        }
        return "redirect:/carrito";
    }
}

    /**
     * Genera un correlativo único para la venta
     * Formato: VENTA-YYYYMMDD-NNNN
     */
    /*
    private String generarCorrelativo() {
        String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefijo = "VENTA-" + fechaActual + "-";

        // Buscar el último correlativo del día
        List<Venta> ventasDelDia = ventaService.findByCorrelativoStartingWith(prefijo);

        int numeroSecuencial = 1;
        if (!ventasDelDia.isEmpty()) {
            // Encontrar el número más alto
            numeroSecuencial = ventasDelDia.stream()
                    .mapToInt(v -> {
                        String corr = v.getCorrelativo();
                        String numStr = corr.substring(corr.lastIndexOf("-") + 1);
                        try {
                            return Integer.parseInt(numStr);
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    })
                    .max()
                    .orElse(0) + 1;
        }

        return prefijo + String.format("%04d", numeroSecuencial);
    }
}*/
/*
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
                producto.setStock(producto.getStock() - detalle.getCantidad());
                productoRepository.save(producto);
            }
        }
        attributes.addFlashAttribute("msg", "Venta guardada y stock actualizado correctamente");
        return "Venta/detalleVenta";
    }
}*/
