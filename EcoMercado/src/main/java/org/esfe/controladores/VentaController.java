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
        @RequestParam("tarjetaCredito") Optional<String> tarjetaNumero) {
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);

        String correlativoSearch = correlativo.orElse("");
        String estadoSearch = estado.orElse("");
        String usuarioSearch = usuarioNombre.orElse("");
        String tipoPagoSearch = tipoPagoNombre.orElse("");
        String tarjetaSearch = tarjetaNumero.orElse("");

        // Buscar los IDs correspondientes a los valores legibles
        Integer idUsuarioSearch = null;
        if (!usuarioSearch.isEmpty()) {
            idUsuarioSearch = usuarioRepository.findAll().stream()
                .filter(u -> u.getNombre().equalsIgnoreCase(usuarioSearch))
                .map(u -> u.getId())
                .findFirst().orElse(null);
        }
        Integer idTipoPagoSearch = null;
        if (!tipoPagoSearch.isEmpty()) {
            idTipoPagoSearch = tipoPagoRepository.findAll().stream()
                .filter(tp -> tp.getMetodoPago().equalsIgnoreCase(tipoPagoSearch))
                .map(tp -> tp.getId())
                .findFirst().orElse(null);
        }
        Optional<Integer> idTarjetaCreditoSearch = Optional.empty();
        if (!tarjetaSearch.isEmpty()) {
            idTarjetaCreditoSearch = tarjetaCreditoRepository.findAll().stream()
                .filter(tc -> tc.getNumero().equalsIgnoreCase(tarjetaSearch))
                .map(tc -> tc.getId())
                .findFirst();
        }
        Byte estadoByte = null;
        if (estadoSearch.equalsIgnoreCase("Pendiente")) estadoByte = 1;
        else if (estadoSearch.equalsIgnoreCase("Cancelado")) estadoByte = 2;

        Page<Venta> ventas = ventaService.findByCorrelativoContainingIgnoreCaseAndEstadoAndUsuario_IdAndTipoPago_IdAndTarjetaCredito_IdOrderByIdDesc(
            correlativoSearch,
            estadoByte,
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

                @GetMapping("/reportegeneral/{visualizacion}")
    public ResponseEntity<byte[]> reporteGeneralVentas(@PathVariable("visualizacion") String visualizacion) {
        try {
            List<Venta> ventas = ventaService.obtenerTodos();
            byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtml("reportes/rpVentas", "ventas", ventas);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", visualizacion+"; filename=reporte_general_ventas.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
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
            headers.add("Content-Disposition", visualizacion+"; filename=reporte_venta_"+id+".pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    }
