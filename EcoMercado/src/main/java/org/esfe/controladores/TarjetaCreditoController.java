package org.esfe.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.esfe.modelos.TarjetaCredito;
import org.esfe.servicios.interfaces.ITarjetaCreditoService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/tarjetasCredito")
public class TarjetaCreditoController {
    @Autowired
    private ITarjetaCreditoService tarjetaCreditoService;

    
    /* @GetMapping
    public String index(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);
        Page<TarjetaCredito> tarjetas = tarjetaCreditoService.obtenerTodos(pageable);
        model.addAttribute("tarjetas", tarjetas);

        // Verificar expiración de tarjetas
        boolean expirada = tarjetas.stream().anyMatch(tc -> tc.getFechaExpiracion().isBefore(LocalDate.now()));
        if (expirada) {
            model.addAttribute("msg", "La tarjeta de crédito expiró");
        }

        int totalPages = tarjetas.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = java.util.stream.IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "tarjetaCredito/index";
    }
        */
    @GetMapping
    public String index(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam("numero") Optional<String> numero, @RequestParam("nombreTitular") Optional<String> nombreTitular, @RequestParam("banco") Optional<String> banco) {
        int currentPage = page.orElse(1) - 1; // si no está seteado se asigna 0
        int pageSize = size.orElse(5); // tamaño de la página, se asigna 5
        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize,sortByIdDesc);
        String numeroSearch = numero.orElse("");
        String nombreTitularSearch = nombreTitular.orElse("");
        String bancoSearch = banco.orElse("");
        Page<TarjetaCredito> tarjetas = tarjetaCreditoService.findByNumeroContainingIgnoreCaseAndNombreTitularContainingIgnoreCaseAndBancoContainingIgnoreCaseOrderByIdDesc(numeroSearch, nombreTitularSearch, bancoSearch,pageable);
        model.addAttribute("tarjetas", tarjetas);

        // Verificar expiración de tarjetas
        boolean expirada = tarjetas.stream().anyMatch(tc -> tc.getFechaExpiracion().isBefore(LocalDate.now()));
        if (expirada) {
            model.addAttribute("msg", "La tarjeta de crédito expiró");
        }

        int totalPages = tarjetas.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "tarjetaCredito/index";
    }

    @GetMapping("/create")
    public String create(TarjetaCredito tarjetaCredito) {
        return "tarjetaCredito/create";
    }

    @PostMapping("/save")
    public String save(TarjetaCredito tarjetaCredito, BindingResult result, Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute(tarjetaCredito);
            attributes.addFlashAttribute("error", "No se pudo guardar debido a un error.");
            return "tarjetaCredito/create";
        }
        tarjetaCreditoService.crearOEditar(tarjetaCredito);
        attributes.addFlashAttribute("msg", "Tarjeta de crédito creada correctamente");
        return "redirect:/tarjetasCredito";
    }

}
