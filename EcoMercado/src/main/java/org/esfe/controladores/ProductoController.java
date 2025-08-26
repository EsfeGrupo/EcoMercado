package org.esfe.controladores;

import org.esfe.modelos.Producto;
import org.esfe.repositorios.IProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final IProductoRepository productoRepository;

    public ProductoController(IProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Listar todos
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "productos/index";
    }

    // Mostrar formulario de creación
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("producto", new Producto());
        return "productos/crear";
    }

    // Guardar producto (crear o editar) con imagen en la base de datos
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto,
                          @RequestParam("imagenFile") MultipartFile imagenFile) {

        // Si se subió una imagen
        if (!imagenFile.isEmpty()) {
            try {
                producto.setImg(imagenFile.getBytes()); // Guardar bytes en el campo img
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        productoRepository.save(producto);
        return "redirect:/productos";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de producto no válido: " + id));
        model.addAttribute("producto", producto);
        return "productos/editar";
    }

    // Ver detalles
    @GetMapping("/detalles/{id}")
    public String verDetalles(@PathVariable Integer id, Model model) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de producto no válido: " + id));
        model.addAttribute("producto", producto);
        return "productos/detalles";
    }

    // Confirmar eliminación
    @GetMapping("/eliminar/{id}")
    public String confirmarEliminar(@PathVariable Integer id, Model model) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de producto no válido: " + id));
        model.addAttribute("producto", producto);
        return "productos/eliminar";
    }

    // Procesar eliminación
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de producto no válido: " + id));
        productoRepository.delete(producto);
        return "redirect:/productos";
    }


}
