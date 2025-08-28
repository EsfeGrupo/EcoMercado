package org.esfe.controladores;

import org.esfe.modelos.Producto;
import org.esfe.servicios.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/usuario/productos")
public class UsuarioProductoController {

    @Autowired
    private IProductoService productoService;

    // --- LISTAR PRODUCTOS PARA USUARIO CON BÚSQUEDA Y PAGINACIÓN ---
    @GetMapping
    public String listarProductos(Model model,
                                  @RequestParam("page") Optional<Integer> page,
                                  @RequestParam("size") Optional<Integer> size,
                                  @RequestParam("nombre") Optional<String> nombre,
                                  @RequestParam("precio") Optional<Double> precio) {

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(9); // Más productos por página para usuarios
        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);

        String nombreSearch = nombre.orElse("");
        Double precioSearch = precio.orElse(null);

        Page<Producto> productos;

        try {
            if (!nombreSearch.isEmpty() && precioSearch != null) {
                productos = productoService.findByNombreContainingIgnoreCaseAndPrecio(nombreSearch, precioSearch, pageable);
            } else if (!nombreSearch.isEmpty()) {
                productos = productoService.findByNombreContainingIgnoreCase(nombreSearch, pageable);
            } else {
                productos = productoService.obtenerTodosPaginados(pageable);
            }
        } catch (Exception e) {
            productos = productoService.obtenerTodosPaginados(pageable);
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

        return "usuario/productos";
    }

    // --- VER DETALLES DEL PRODUCTO ---
    @GetMapping("/detalles/{id}")
    public String verDetalles(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/usuario/productos";
            }
            model.addAttribute("producto", producto);
            return "usuario/producto-detalles";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar los detalles del producto");
            return "redirect:/usuario/productos";
        }
    }

    // --- MOSTRAR IMAGEN ---
    @GetMapping("/imagen/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable Integer id) {
        try {
            Producto producto = productoService.obtenerPorId(id);

            if (producto != null && producto.getImg() != null) {
                byte[] imagen = producto.getImg();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
                headers.setContentLength(imagen.length);
                return new ResponseEntity<>(imagen, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}