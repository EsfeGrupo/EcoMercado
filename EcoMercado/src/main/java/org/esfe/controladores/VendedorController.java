package org.esfe.controladores;

import org.esfe.modelos.Vendedor;
import org.esfe.repositorios.IVendedorRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vendedores")
public class VendedorController {

    private final IVendedorRepository vendedorRepository;

    // Inyección de dependencias por constructor
    public VendedorController(IVendedorRepository vendedorRepository) {
        this.vendedorRepository = vendedorRepository;
    }

    // --- LISTAR ---
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("vendedores", vendedorRepository.findAll());
        return "vendedores/lista"; // Vista: src/main/resources/templates/vendedores/lista.html
    }

    // --- DETALLES ---
    @GetMapping("/detalles/{id}")
    public String verDetalles(@PathVariable Integer id, Model model) {
        Vendedor vendedor = vendedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de vendedor no válido: " + id));
        model.addAttribute("vendedor", vendedor);
        return "vendedores/detalles"; // Vista: src/main/resources/templates/vendedores/detalles.html
    }

    // --- CREAR ---
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("vendedor", new Vendedor());
        return "vendedores/crear"; // Vista: src/main/resources/templates/vendedores/crear.html
    }

    @PostMapping("/crear")
    public String guardar(@ModelAttribute Vendedor vendedor) {
        vendedorRepository.save(vendedor);
        return "redirect:/vendedores";
    }

    // --- EDITAR ---
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Vendedor vendedor = vendedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de vendedor no válido: " + id));
        model.addAttribute("vendedor", vendedor);
        return "vendedores/editar"; // Vista: src/main/resources/templates/vendedores/editar.html
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Integer id, @ModelAttribute Vendedor vendedorActualizado) {
        Vendedor vendedor = vendedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de vendedor no válido: " + id));

        // Actualizar campos
        vendedor.setNombre(vendedorActualizado.getNombre());
        vendedor.setUbicacion(vendedorActualizado.getUbicacion());
        vendedor.setCorreo(vendedorActualizado.getCorreo());

        vendedorRepository.save(vendedor);
        return "redirect:/vendedores";
    }

    // --- ELIMINAR ---
    // Mostrar confirmación de eliminación
    @GetMapping("/eliminar/{id}")
    public String confirmarEliminar(@PathVariable Integer id, Model model) {
        Vendedor vendedor = vendedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de vendedor no válido: " + id));
        model.addAttribute("vendedor", vendedor);
        return "vendedores/eliminar"; // Vista: eliminar.html
    }

    // Procesar eliminación
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Vendedor vendedor = vendedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de vendedor no válido: " + id));
        vendedorRepository.delete(vendedor);
        return "redirect:/vendedores";
    }

}
