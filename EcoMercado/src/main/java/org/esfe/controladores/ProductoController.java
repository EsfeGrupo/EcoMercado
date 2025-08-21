package org.esfe.controladores;

import org.esfe.modelos.Producto;
import org.esfe.servicios.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private IProductoService productoService;

    // Listar con paginación
    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "5") int size,
                         Model model) {
        Page<Producto> productos = productoService.obtenerTodosPaginados(PageRequest.of(page, size));
        model.addAttribute("productos", productos);
        return "productos/lista"; // Vista en templates/productos/lista.html
    }

    // Mostrar formulario de creación
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("producto", new Producto());
        return "productos/form"; // Vista en templates/productos/form.html
    }

    // Guardar (crear o editar)
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Producto producto,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            return "productos/form";
        }
        productoService.crearOEditar(producto);
        return "redirect:/productos";
    }

    // Editar
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Producto producto = productoService.obtenerPorId(id);
        if (producto == null) {
            return "redirect:/productos";
        }
        model.addAttribute("producto", producto);
        return "productos/form";
    }

    // Eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        productoService.eliminarPorId(id);
        return "redirect:/productos";
    }

    // Búsqueda por nombre y precio
    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) String nombre,
                         @RequestParam(required = false) Double precio,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "5") int size,
                         Model model) {
        Page<Producto> productos = productoService.findByNombreContainingIgnoreCaseAndPrecio(
                nombre != null ? nombre : "",
                precio != null ? precio : 0.0,
                PageRequest.of(page, size)
        );
        model.addAttribute("productos", productos);
        return "Producto/Producto";
    }
}
