package org.esfe.controladores;

import org.esfe.modelos.Producto;
import org.esfe.repositorios.IProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Optional;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    private final IProductoRepository productoRepository;

    public ProductoController(IProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // --- LISTAR CON BUSQUEDA Y PAGINACION ---
    @GetMapping
    public String listar(Model model,
                         @RequestParam("page") Optional<Integer> page,
                         @RequestParam("size") Optional<Integer> size,
                         @RequestParam("nombre") Optional<String> nombre,
                         @RequestParam("precio") Optional<Double> precio) {

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(6);
        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);

        String nombreSearch = nombre.orElse("");
        Double precioSearch = precio.orElse(null);

        Page<Producto> productos;

        if (precioSearch != null) {
            productos = productoRepository.findByNombreContainingIgnoreCaseAndPrecio(nombreSearch, precioSearch, pageable);
        } else {
            // Si no se especifica precio, buscamos solo por nombre
            productos = productoRepository.findByNombreContainingIgnoreCase(nombreSearch, pageable);
        }

        model.addAttribute("productos", productos);
        model.addAttribute("nombre", nombreSearch);
        model.addAttribute("precio", precioSearch);

        int totalPages = productos.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "producto/index";
    }

    // --- CREAR PRODUCTO ---
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("producto", new Producto());
        return "producto/crear";
    }

    @PostMapping("/crear")
    public String guardar(@ModelAttribute Producto producto,
                          @RequestParam("imagenFile") MultipartFile imagenFile) {
        if (!imagenFile.isEmpty()) {
            try {
                producto.setImg(imagenFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        productoRepository.save(producto);
        return "redirect:/producto";
    }

    // --- EDITAR PRODUCTO ---
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de producto no válido: " + id));
        model.addAttribute("producto", producto);
        return "producto/editar";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute Producto productoActualizado,
                             @RequestParam("imagenFile") MultipartFile imagenFile) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de producto no válido: " + id));

        producto.setNombre(productoActualizado.getNombre());
        producto.setPrecio(productoActualizado.getPrecio());

        if (!imagenFile.isEmpty()) {
            try {
                producto.setImg(imagenFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        productoRepository.save(producto);
        return "redirect:/producto";
    }

    // --- VER DETALLES ---
    @GetMapping("/detalles/{id}")
    public String verDetalles(@PathVariable Integer id, Model model) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de producto no válido: " + id));
        model.addAttribute("producto", producto);
        return "producto/detalles";
    }

    // --- ELIMINAR PRODUCTO ---
    @GetMapping("/eliminar/{id}")
    public String confirmarEliminar(@PathVariable Integer id, Model model) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de producto no válido: " + id));
        model.addAttribute("producto", producto);
        return "producto/eliminar";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de producto no válido: " + id));
        productoRepository.delete(producto);
        return "redirect:/producto";
    }

}
