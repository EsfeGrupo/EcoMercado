package org.esfe.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.esfe.modelos.TipoPago;
import org.esfe.servicios.interfaces.ITipoPagoService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/tipopagos")
public class TipoPagoController {
    @Autowired
    private ITipoPagoService tipoPagoService;

    @GetMapping
    public String index(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam("metodoPago") Optional<String> metodoPago, @RequestParam("descripcion") Optional<String> descripcion ){
        int currentPage = page.orElse(1) - 1; // si no está seteado se asigna 0
        int pageSize = size.orElse(5); // tamaño de la página, se asigna 5
        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize,sortByIdDesc);
        String metodoPagoSearch = metodoPago.orElse("");
        String descripcionSearch = descripcion.orElse("");
        Page<TipoPago> tipoPagos = tipoPagoService.findByMetodoPagoContainingAndDescripcionContaining(metodoPagoSearch,descripcionSearch,pageable);
        model.addAttribute("tipoPagos", tipoPagos);

        int totalPages = tipoPagos.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "tipopago/index";
    }

    @GetMapping("/create")
    public String create(TipoPago tipoPago){
        return "tipopago/create";
    }

    @PostMapping("/save")
    public String save(TipoPago tipoPago, BindingResult result, Model model, RedirectAttributes attributes){
        if(result.hasErrors()){
            model.addAttribute(tipoPago);
            attributes.addFlashAttribute("error", "No se pudo guardar debido a un error.");
            return "tipopago/create";
        }
        tipoPagoService.crearOEditar(tipoPago);
        attributes.addFlashAttribute("msg", "Tipo de pago creado correctamente");
        return "redirect:/tipopagos";
    }

      @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model){
        TipoPago tipoPago = tipoPagoService.obtenerPorId(id).get();
        model.addAttribute("tipoPago", tipoPago);
        return "tipopago/details";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model){
        TipoPago tipoPago = tipoPagoService.obtenerPorId(id).get();
        model.addAttribute("tipoPago", tipoPago);
        return "tipopago/edit";
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") Integer id, Model model){
        TipoPago tipoPago = tipoPagoService.obtenerPorId(id).get();
        model.addAttribute("tipoPago", tipoPago);
        return "tipopago/delete";
    }

    @PostMapping("/delete")
    public String delete(TipoPago tipoPago, RedirectAttributes attributes) {
    try {
        tipoPagoService.eliminarPorId(tipoPago.getId());
        attributes.addFlashAttribute("msg", "Tipo de pago eliminado correctamente");
    } catch (Exception e) {
        attributes.addFlashAttribute("error", "No se puede eliminar el tipo de pago porque está vinculado a un pago.");
    }
    return "redirect:/tipopagos";
}
}
