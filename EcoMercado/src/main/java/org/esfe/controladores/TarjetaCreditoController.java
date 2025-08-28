package org.esfe.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.esfe.modelos.Usuario;

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
    public String index(Model model, HttpSession session,
                       @RequestParam("page") Optional<Integer> page,
                       @RequestParam("size") Optional<Integer> size,
                       @RequestParam("nombreTitular") Optional<String> nombreTitular,
                       @RequestParam("banco") Optional<String> banco) {
                           
        // Obtener el usuario de la sesión
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioSession");
        if (usuarioActual == null) {
            return "redirect:/login";
        }

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);
        String nombreTitularSearch = nombreTitular.orElse("");
        String bancoSearch = banco.orElse("");

        // Buscar solo las tarjetas del usuario actual
        Page<TarjetaCredito> tarjetas = tarjetaCreditoService.findByUsuarioIdAndNombreTitularContainingIgnoreCaseAndBancoContainingIgnoreCaseOrderByIdDesc(
            usuarioActual.getId(), nombreTitularSearch, bancoSearch, pageable);

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
    public String create(Model model, HttpSession session) {
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioSession");
        if (usuarioActual == null) {
            return "redirect:/login";
        }

        TarjetaCredito tarjetaCredito = new TarjetaCredito();
        tarjetaCredito.setUsuario(usuarioActual);
        model.addAttribute("tarjetaCredito", tarjetaCredito);
        return "tarjetaCredito/create";
    }

    @PostMapping("/save")
    public String save(@Valid TarjetaCredito tarjetaCredito, BindingResult result, 
                      Model model, RedirectAttributes attributes, HttpSession session) {
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioSession");
        if (usuarioActual == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            model.addAttribute("tarjetaCredito", tarjetaCredito);
            attributes.addFlashAttribute("error", "No se pudo guardar debido a un error.");
            return "tarjetaCredito/create";
        }

        // Asignar el usuario actual a la tarjeta
        tarjetaCredito.setUsuario(usuarioActual);

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
    public String details(@PathVariable("id") Integer id, Model model, HttpSession session) {
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioSession");
        if (usuarioActual == null) {
            return "redirect:/login";
        }

        TarjetaCredito tarjetaCredito = tarjetaCreditoService.obtenerPorId(id);
        
        // Verificar que la tarjeta pertenezca al usuario actual
        if (tarjetaCredito == null || !tarjetaCredito.getUsuario().getId().equals(usuarioActual.getId())) {
            return "redirect:/tarjetaCredito";
        }
        
        // Mask the credit card number for display
        if (tarjetaCredito.getNumeroEncriptado() != null && tarjetaCredito.getNumeroEncriptado().length() >= 4) {
            tarjetaCredito.setNumero("**** **** **** " + 
                tarjetaCredito.getNumeroEncriptado().substring(tarjetaCredito.getNumeroEncriptado().length() - 4));
        }
        
        model.addAttribute("tarjetaCredito", tarjetaCredito);
        return "tarjetaCredito/details";
}

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model, HttpSession session) {
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioSession");
        if (usuarioActual == null) {
            return "redirect:/login";
        }

        TarjetaCredito tarjetaCredito = tarjetaCreditoService.obtenerPorId(id);
        
        // Verificar que la tarjeta pertenezca al usuario actual
        if (tarjetaCredito == null || !tarjetaCredito.getUsuario().getId().equals(usuarioActual.getId())) {
            return "redirect:/tarjetaCredito";
        }
        
        model.addAttribute("tarjetaCredito", tarjetaCredito);
        return "tarjetaCredito/edit";
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") Integer id, Model model, HttpSession session) {
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioSession");
        if (usuarioActual == null) {
            return "redirect:/login";
        }

        TarjetaCredito tarjetaCredito = tarjetaCreditoService.obtenerPorId(id);
        
        // Verificar que la tarjeta pertenezca al usuario actual
        if (tarjetaCredito == null || !tarjetaCredito.getUsuario().getId().equals(usuarioActual.getId())) {
            return "redirect:/tarjetaCredito";
        }
        
        model.addAttribute("tarjetaCredito", tarjetaCredito);
        return "tarjetaCredito/delete";
    }

    @PostMapping("/delete")
    public String delete(TarjetaCredito tarjetaCredito, RedirectAttributes attributes, HttpSession session) {
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioSession");
        if (usuarioActual == null) {
            return "redirect:/login";
        }

        try {
            // Verificar que la tarjeta pertenezca al usuario actual antes de eliminar
            TarjetaCredito tarjetaExistente = tarjetaCreditoService.obtenerPorId(tarjetaCredito.getId());
            if (tarjetaExistente == null || !tarjetaExistente.getUsuario().getId().equals(usuarioActual.getId())) {
                attributes.addFlashAttribute("error", "No tiene permisos para eliminar esta tarjeta.");
                return "redirect:/tarjetaCredito";
            }

            tarjetaCreditoService.eliminarPorId(tarjetaCredito.getId());
            attributes.addFlashAttribute("msg", "Tarjeta de crédito eliminada correctamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "No se puede eliminar la tarjeta de crédito porque ya está vinculada a una venta.");
        }
        return "redirect:/tarjetaCredito";
    }
}