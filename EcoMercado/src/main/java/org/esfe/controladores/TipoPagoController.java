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

@Controller
@RequestMapping("/tipopagos")
public class TipoPagoController {
    @Autowired
    private ITipoPagoService tipoPagoService;

    @GetMapping
    public String index(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size){
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Page<TipoPago> tipoPagos = tipoPagoService.obtenerTodosPaginados(pageable);
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

}
