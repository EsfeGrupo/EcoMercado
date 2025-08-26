package org.esfe.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import org.esfe.modelos.TarjetaCredito;
import org.esfe.servicios.interfaces.ITarjetaCreditoService;
import org.esfe.servicios.interfaces.IUsuarioService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
@RequestMapping("/tarjetaCredito")
public class TarjetaCreditoController {
    @Autowired
    private ITarjetaCreditoService tarjetaCreditoService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping
    public String index(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam("nombreTitular") Optional<String> nombreTitular, @RequestParam("banco") Optional<String> banco) {
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);
        String nombreTitularSearch = nombreTitular.orElse("");
        String bancoSearch = banco.orElse("");

        // Note: The search will now be based on the encrypted number in the database.
        // A better approach would be to only search on non-sensitive data, but for this example, we'll continue searching on the number.
        Page<TarjetaCredito> tarjetas = tarjetaCreditoService.findByNombreTitularContainingIgnoreCaseAndBancoContainingIgnoreCaseOrderByIdDesc(nombreTitularSearch, bancoSearch, pageable);

        // Mask the credit card number for display
        tarjetas.getContent().forEach(tarjeta -> {
            if (tarjeta.getNumeroEncriptado() != null && tarjeta.getNumeroEncriptado().length() >= 4) {
                tarjeta.setNumero("**** **** **** " + tarjeta.getNumeroEncriptado().substring(tarjeta.getNumeroEncriptado().length() - 4));
            }
        });

        model.addAttribute("tarjetas", tarjetas);

        boolean expirada = tarjetas.stream().anyMatch(tc -> tc.getFechaExpiracion().isBefore(LocalDate.now()));

        int totalPages = tarjetas.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "tarjetaCredito/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("usuarios", usuarioService.obtenerTodos());
        model.addAttribute("tarjetaCredito", new TarjetaCredito());
        return "tarjetaCredito/create";
    }

    @PostMapping("/save")
    public String save(@Valid TarjetaCredito tarjetaCredito, BindingResult result, Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
        model.addAttribute("tarjetaCredito", tarjetaCredito);
        attributes.addFlashAttribute("error", "No se pudo guardar debido a un error.");
        return "tarjetaCredito/create";
    }

    // Lógica para encriptar
    if (tarjetaCredito.getNumero() != null && !tarjetaCredito.getNumero().isEmpty()) {
        String numeroEncriptado = passwordEncoder.encode(tarjetaCredito.getNumero());
        tarjetaCredito.setNumeroEncriptado(numeroEncriptado);
        
        // Limpiar el campo transitorio
        tarjetaCredito.setNumero(null);
    }
    
    tarjetaCreditoService.crearOEditar(tarjetaCredito);
    attributes.addFlashAttribute("msg", "Tarjeta de crédito creada correctamente");
    return "redirect:/tarjetaCredito";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model) {
    TarjetaCredito tarjetaCredito = tarjetaCreditoService.obtenerPorId(id);
    
    // Mask the credit card number for display
    if (tarjetaCredito.getNumeroEncriptado() != null && tarjetaCredito.getNumeroEncriptado().length() >= 4) {
        tarjetaCredito.setNumero("**** **** **** " + 
            tarjetaCredito.getNumeroEncriptado().substring(tarjetaCredito.getNumeroEncriptado().length() - 4));
    }
    
    model.addAttribute("tarjetaCredito", tarjetaCredito);
    return "tarjetaCredito/details";
}

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        TarjetaCredito tarjetaCredito = tarjetaCreditoService.obtenerPorId(id);
        model.addAttribute("usuarios", usuarioService.obtenerTodos());
        model.addAttribute("tarjetaCredito", tarjetaCredito);
        return "tarjetaCredito/edit";
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") Integer id, Model model) {
        TarjetaCredito tarjetaCredito = tarjetaCreditoService.obtenerPorId(id);
        model.addAttribute("tarjetaCredito", tarjetaCredito);
        return "tarjetaCredito/delete";
    }

    @PostMapping("/delete")
    public String delete(TarjetaCredito tarjetaCredito, RedirectAttributes attributes) {
        tarjetaCreditoService.eliminarPorId(tarjetaCredito.getId());
        attributes.addFlashAttribute("msg", "Tarjeta de crédito eliminada correctamente");
        return "redirect:/tarjetaCredito";
    }
}