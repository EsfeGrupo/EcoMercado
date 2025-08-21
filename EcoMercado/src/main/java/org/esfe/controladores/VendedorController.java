package org.esfe.controladores;

import jakarta.validation.Valid;
import org.esfe.modelos.Vendedor;
import org.esfe.servicios.interfaces.IVendedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vendedores")
public class VendedorController {

    @Autowired
    private IVendedorService vendedorService;

    // Listar con paginación
    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "5") int size,
                         Model model) {
        Page<Vendedor> vendedores = vendedorService.obtenerTodosPaginados(PageRequest.of(page, size));
        model.addAttribute("vendedores", vendedores);
        return "vendedores/lista"; // templates/vendedores/lista.html
    }

    // Formulario para crear
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("vendedor", new Vendedor());
        return "vendedores/form"; // templates/vendedores/form.html
    }

    // Guardar (crear o editar)
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Vendedor vendedor,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            return "vendedores/form";
        }
        vendedorService.crearOEditar(vendedor);
        return "redirect:/vendedores";
    }

    // Editar
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Vendedor vendedor = vendedorService.obtenerPorId(id);
        if (vendedor == null) {
            return "redirect:/vendedores";
        }
        model.addAttribute("vendedor", vendedor);
        return "vendedores/form";
    }

    // Eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        vendedorService.eliminarPorId(id);
        return "redirect:/vendedores";
    }

    // Buscar por nombre y ubicación
    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) String nombre,
                         @RequestParam(required = false) String ubicacion,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "5") int size,
                         Model model) {
        Page<Vendedor> vendedores = vendedorService.findByNombreContainingIgnoreCaseAndUbicacionContainingIgnoreCase(
                nombre != null ? nombre : "",
                ubicacion != null ? ubicacion : "",
                PageRequest.of(page, size)
        );
        model.addAttribute("vendedores", vendedores);
        return "Vendedor/Vendedor";

    }
}
