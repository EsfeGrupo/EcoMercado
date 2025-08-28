package org.esfe.controladores;

import org.esfe.modelos.Producto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.esfe.modelos.Venta;
import org.esfe.modelos.DetalleVenta;
import org.esfe.servicios.interfaces.IVentaService;
import org.esfe.servicios.interfaces.IDetalleVentaService;
import org.esfe.repositorios.IProductoRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.esfe.servicios.utilerias.PdfGeneratorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/ventas")
public class VentaController {
    @Autowired
    private IVentaService ventaService;
    @Autowired
    private IDetalleVentaService detalleVentaService;
    @Autowired
    private IProductoRepository productoRepository;
    @Autowired
    private org.esfe.repositorios.IUsuarioRepository usuarioRepository;
    @Autowired
    private org.esfe.repositorios.ITipoPagoRepository tipoPagoRepository;
    @Autowired
    private org.esfe.repositorios.ITarjetaCreditoRepository tarjetaCreditoRepository;
    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @GetMapping
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> page,
                        @RequestParam("size") Optional<Integer> size,
                        @RequestParam("correlativo") Optional<String> correlativo,
                        @RequestParam("estado") Optional<String> estado,
                        @RequestParam("usuario") Optional<String> usuarioNombre,
                        @RequestParam("tipoPago") Optional<String> tipoPagoNombre,
                        @RequestParam("tarjetaCredito") Optional<String> tarjetaBanco) { // Cambiado de tarjetaNumero a tarjetaBanco

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);

        String correlativoSearch = correlativo.orElse("");
        String estadoSearch = estado.orElse("");
        String usuarioSearch = usuarioNombre.orElse("");
        String tipoPagoSearch = tipoPagoNombre.orElse("");
        String tarjetaSearch = tarjetaBanco.orElse(""); // Cambiado nombre de variable

        // Buscar ID de Usuario - Corregido para manejar nombres parciales
        Integer idUsuarioSearch = null;
        if (!usuarioSearch.isEmpty()) {
            idUsuarioSearch = usuarioRepository.findAll().stream()
                    .filter(u -> u.getNombre() != null && u.getNombre().toLowerCase().contains(usuarioSearch.toLowerCase()))
                    .map(u -> u.getId())
                    .findFirst().orElse(null);
        }

        // Buscar ID de Tipo de Pago - Corregido para manejar nombres parciales
        Integer idTipoPagoSearch = null;
        if (!tipoPagoSearch.isEmpty()) {
            idTipoPagoSearch = tipoPagoRepository.findAll().stream()
                    .filter(tp -> tp.getMetodoPago() != null && tp.getMetodoPago().toLowerCase().contains(tipoPagoSearch.toLowerCase()))
                    .map(tp -> tp.getId())
                    .findFirst().orElse(null);
        }

        // Buscar ID de Tarjeta de Crédito - Corregido para buscar por banco y manejar nulls
        Optional<Integer> idTarjetaCreditoSearch = Optional.empty();
        if (!tarjetaSearch.isEmpty()) {
            idTarjetaCreditoSearch = tarjetaCreditoRepository.findAll().stream()
                    .filter(tc -> tc.getBanco() != null && tc.getBanco().toLowerCase().contains(tarjetaSearch.toLowerCase()))
                    .map(tc -> tc.getId())
                    .findFirst();
        }

        // Usar directamente el estado como String
        String estadoFinal = estadoSearch.isEmpty() ? null : estadoSearch;

        Page<Venta> ventas = ventaService.buscarVentasConFiltros(
                correlativoSearch,
                estadoFinal,
                idUsuarioSearch,
                idTipoPagoSearch,
                idTarjetaCreditoSearch,
                pageable);

        model.addAttribute("ventas", ventas);

        int totalPages = ventas.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("tiposPago", tipoPagoRepository.findAll());
        model.addAttribute("tarjetasCredito", tarjetaCreditoRepository.findAll());

        return "venta/index";
    }

    @GetMapping("/create")
    public String create(Venta venta, Model model){
        venta.setUsuario(new org.esfe.modelos.Usuario());
        venta.setTipoPago(new org.esfe.modelos.TipoPago());
        venta.setTarjetaCredito(new org.esfe.modelos.TarjetaCredito());
        model.addAttribute("venta", venta);
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("tiposPago", tipoPagoRepository.findAll());
        model.addAttribute("tarjetasCredito", tarjetaCreditoRepository.findAll());
        return "venta/create";
    }

    @PostMapping("/save")
    public String save(Venta venta, BindingResult result, Model model, RedirectAttributes attributes){
        if(result.hasErrors()){
            model.addAttribute("venta", venta);
            model.addAttribute("detalles", venta.getDetalleventas());
            attributes.addFlashAttribute("error", "No se pudo guardar la venta debido a un error.");
            return "venta/create";
        }
        //venta.setEstado("Pendiente"); // Estado pendiente por defecto
        Venta ventaGuardada = ventaService.crearOEditar(venta);
        if(venta.getDetalleventas() != null){
            for(DetalleVenta detalle : venta.getDetalleventas()){
                detalle.setVenta(ventaGuardada);
                detalleVentaService.crearOEditar(detalle);
            }
        }
        attributes.addFlashAttribute("msg", "Venta y detalles creados correctamente");
        return "redirect:/ventas";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model){
        Venta venta = ventaService.obtenerPorId(id).get();
        List<DetalleVenta> detalles = detalleVentaService.obtenerPorVentaId(id);
        List<Producto> productos = productoRepository.findAll(); // ← LÍNEA AÑADIDA

        model.addAttribute("venta", venta);
        model.addAttribute("detalles", detalles);
        model.addAttribute("productos", productos); // ← LÍNEA AÑADIDA
        model.addAttribute("detalleNuevo", new DetalleVenta());
        return "venta/details";
    }

    // 2. MÉTODO edit - MODIFICADO
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model){
        Venta venta = ventaService.obtenerPorId(id).get();
        List<DetalleVenta> detalles = detalleVentaService.obtenerPorVentaId(id);
        List<Producto> productos = productoRepository.findAll(); // ← LÍNEA AÑADIDA

        model.addAttribute("venta", venta);
        model.addAttribute("detalles", detalles);
        model.addAttribute("productos", productos); // ← LÍNEA AÑADIDA
        model.addAttribute("detalleNuevo", new DetalleVenta());

        // Agregar las listas necesarias para los selects
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("tiposPago", tipoPagoRepository.findAll());
        model.addAttribute("tarjetasCredito", tarjetaCreditoRepository.findAll());

        return "venta/edit";
    }
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes){
        try {
            Optional<Venta> ventaOpt = ventaService.obtenerPorId(id);
            if (ventaOpt.isEmpty()) {
                attributes.addFlashAttribute("error", "La venta no existe o no fue encontrada");
                return "redirect:/ventas";
            }

            Venta venta = ventaOpt.get();
            model.addAttribute("venta", venta);
            return "venta/delete";

        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al buscar la venta: " + e.getMessage());
            return "redirect:/ventas";
        }
    }

    // POST: Realizar la eliminación
    @PostMapping("/delete")
    @Transactional
    public String delete(Venta venta, RedirectAttributes attributes){
        try {
            // Verificar que la venta existe
            Optional<Venta> ventaExistente = ventaService.obtenerPorId(venta.getId());
            if (ventaExistente.isEmpty()) {
                attributes.addFlashAttribute("error", "La venta no existe o ya fue eliminada");
                return "redirect:/ventas";
            }

            // Eliminar todos los detalles de venta asociados de una vez (más eficiente)
            detalleVentaService.eliminarPorVentaId(venta.getId());

            // Eliminar la venta principal
            ventaService.eliminarPorId(venta.getId());

            attributes.addFlashAttribute("msg", "Venta y detalles eliminados correctamente");

        } catch (Exception e) {
            // En caso de error, la transacción se revierte automáticamente
            attributes.addFlashAttribute("error", "Error al eliminar la venta: " + e.getMessage());
            return "redirect:/ventas";
        }

        return "redirect:/ventas";
    }

    @GetMapping("/reportegeneral/{visualizacion}")
    public ResponseEntity<byte[]> reporteGeneralVentas(@PathVariable("visualizacion") String visualizacion) {
        try {
            List<Venta> ventas = ventaService.obtenerTodos();

            // Validar que la lista no esté vacía
            if (ventas == null || ventas.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }

            byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtml("reportes/rpVentas", "ventas", ventas);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", visualizacion + "; filename=reporte_general_ventas.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace(); // Para debug - puedes usar logger en producción
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/reporte/{id}/{visualizacion}")
    public ResponseEntity<byte[]> reporteIndividualVenta(@PathVariable("id") Integer id, @PathVariable("visualizacion") String visualizacion) {
        try {
            Optional<Venta> ventaOpt = ventaService.obtenerPorId(id);
            if (ventaOpt.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }

            Venta venta = ventaOpt.get();
            List<Venta> ventas = List.of(venta); // Para reutilizar la plantilla

            byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtml("reportes/rpVentas", "ventas", ventas);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", visualizacion + "; filename=reporte_venta_" + id + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace(); // Para debug - puedes usar logger en producción
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Redirigir /carrito a la vista de detalleVenta (ahora llamada carrito)
    // @GetMapping("/ventas/detalleVenta")
    // public String verCarrito(...) {...}
    // @PostMapping("/carrito/add")
    // public String addDetalleCarrito(...) {...}
    // @PostMapping("/carrito/updateCantidad")
    // public String updateCantidadCarrito(...) {...}
    // @PostMapping("/carrito/delete")
    // public String deleteDetalleCarrito(...) {...}
    // @PostMapping("/carrito/guardar")
    // public String guardarCarrito(...) {...}
}
