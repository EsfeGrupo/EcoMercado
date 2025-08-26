package org.esfe.controladores;

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
import org.esfe.modelos.Producto;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @GetMapping
    public String index(Model model,
        @RequestParam("page") Optional<Integer> page,
        @RequestParam("size") Optional<Integer> size,
        @RequestParam("correlativo") Optional<String> correlativo,
        @RequestParam("estado") Optional<Byte> estado,
        @RequestParam("idUsuario") Optional<Integer> idUsuario,
        @RequestParam("idTipoPago") Optional<Integer> idTipoPago,
        @RequestParam("idTarjetaCredito") Optional<Integer> idTarjetaCredito) {
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);

        String correlativoSearch = correlativo.orElse("");
        Byte estadoSearch = estado.orElse(null);
        Integer idUsuarioSearch = idUsuario.orElse(null);
        Integer idTipoPagoSearch = idTipoPago.orElse(null);
        Integer idTarjetaCreditoSearch = idTarjetaCredito.orElse(null);

        Page<Venta> ventas = ventaService.findByCorrelativoContainingIgnoreCaseAndEstadoAndUsuario_IdAndTipoPago_IdAndTarjetaCredito_IdOrderByIdDesc(
            correlativoSearch,
            estadoSearch,
            idUsuarioSearch,
            idTipoPagoSearch,
            idTarjetaCredito,
            pageable);
        model.addAttribute("ventas", ventas);

        int totalPages = ventas.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
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
    model.addAttribute("venta", venta);
    model.addAttribute("detalles", detalles);
    model.addAttribute("detalleNuevo", new DetalleVenta());
    return "venta/details";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model){
    Venta venta = ventaService.obtenerPorId(id).get();
    List<DetalleVenta> detalles = detalleVentaService.obtenerPorVentaId(id);
    model.addAttribute("venta", venta);
    model.addAttribute("detalles", detalles);
    model.addAttribute("detalleNuevo", new DetalleVenta());
    return "venta/edit";
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") Integer id, Model model){
        Venta venta = ventaService.obtenerPorId(id).get();
        model.addAttribute("venta", venta);
        return "venta/delete";
    }

    @PostMapping("/delete")
    public String delete(Venta venta, RedirectAttributes attributes){
        List<DetalleVenta> detalles = detalleVentaService.obtenerPorVentaId(venta.getId());
        for(DetalleVenta detalle : detalles){
            detalleVentaService.eliminarPorId(detalle.getId());
        }
        ventaService.eliminarPorId(venta.getId());
        attributes.addFlashAttribute("msg", "Venta y detalles eliminados correctamente");
        return "redirect:/ventas";
    }
        @PostMapping("/addDetalle")
        public String addDetalle(@ModelAttribute DetalleVenta detalleNuevo, @RequestParam("ventaId") Integer ventaId, RedirectAttributes attributes){
            Venta venta = ventaService.obtenerPorId(ventaId).get();
            detalleNuevo.setVenta(venta);
            detalleVentaService.crearOEditar(detalleNuevo);
            attributes.addFlashAttribute("msg", "Detalle agregado correctamente");
            return "redirect:/ventas/details/" + ventaId;
        }

            @GetMapping("/editDetalle/{id}")
            public String editDetalle(@PathVariable("id") Integer id, Model model){
                DetalleVenta detalle = detalleVentaService.obtenerPorId(id);
                model.addAttribute("detalle", detalle);
                return "venta/editDetalle";
            }

            @PostMapping("/updateDetalle")
            public String updateDetalle(@ModelAttribute DetalleVenta detalle, RedirectAttributes attributes){
                detalleVentaService.crearOEditar(detalle);
                attributes.addFlashAttribute("msg", "Detalle actualizado correctamente");
                return "redirect:/ventas/details/" + detalle.getVenta().getId();
            }

                @PostMapping("/deleteDetalle")
                public String deleteDetalle(@RequestParam("detalleId") Integer detalleId, @RequestParam("ventaId") Integer ventaId, RedirectAttributes attributes){
                    detalleVentaService.eliminarPorId(detalleId);
                    attributes.addFlashAttribute("msg", "Detalle eliminado correctamente");
                    return "redirect:/ventas/details/" + ventaId;
                }

                @GetMapping("/downloadPdf/{id}")
                public void downloadPdf(@PathVariable("id") Integer id, HttpServletResponse response) throws IOException, DocumentException {
                    Venta venta = ventaService.obtenerPorId(id).orElse(null);
                    if (venta == null) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", "attachment; filename=venta_" + id + ".pdf");
                    Document document = new Document();
                    PdfWriter.getInstance(document, response.getOutputStream());
                    document.open();
                    document.add(new Paragraph("Venta ID: " + venta.getId()));
                    document.add(new Paragraph("Fecha: " + venta.getFecha()));
                    document.add(new Paragraph("Total: " + venta.getTotal()));
                    document.add(new Paragraph("Detalles:"));
                    List<DetalleVenta> detalles = detalleVentaService.obtenerPorVentaId(id);
                    for (DetalleVenta detalle : detalles) {
                        Producto producto = productoRepository.findById(detalle.getIdProducto()).orElse(null);
                        String nombreProducto = producto != null ? producto.getNombre() : "Producto no encontrado";
                        document.add(new Paragraph("Producto: " + nombreProducto + ", Cantidad: " + detalle.getCantidad() + ", Precio: " + detalle.getPrecioUnitario()));
                    }
                    document.close();
                }

                @GetMapping("/previewPdf/{id}")
                public void previewPdf(@PathVariable("id") Integer id, HttpServletResponse response) throws IOException, DocumentException {
                    Venta venta = ventaService.obtenerPorId(id).orElse(null);
                    if (venta == null) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", "inline; filename=venta_" + id + ".pdf");
                    Document document = new Document();
                    PdfWriter.getInstance(document, response.getOutputStream());
                    document.open();
                    document.add(new Paragraph("Venta ID: " + venta.getId()));
                    document.add(new Paragraph("Fecha: " + venta.getFecha()));
                    document.add(new Paragraph("Total: " + venta.getTotal()));
                    document.add(new Paragraph("Detalles:"));
                    List<DetalleVenta> detalles = detalleVentaService.obtenerPorVentaId(id);
                    for (DetalleVenta detalle : detalles) {
                        Producto producto = productoRepository.findById(detalle.getIdProducto()).orElse(null);
                        String nombreProducto = producto != null ? producto.getNombre() : "Producto no encontrado";
                        document.add(new Paragraph("Producto: " + nombreProducto + ", Cantidad: " + detalle.getCantidad() + ", Precio: " + detalle.getPrecioUnitario()));
                    }
                    document.close();
                }
    }
