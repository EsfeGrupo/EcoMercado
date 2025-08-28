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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Optional;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    private IProductoService productoService;

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

        try {
            if (!nombreSearch.isEmpty() && precioSearch != null) {
                productos = productoService.findByNombreContainingIgnoreCaseAndPrecio(nombreSearch, precioSearch, pageable);
            } else if (!nombreSearch.isEmpty()) {
                // Solo buscar por nombre
                productos = productoService.findByNombreContainingIgnoreCase(nombreSearch, pageable);
            } else {
                // Sin filtros, mostrar todos
                productos = productoService.obtenerTodosPaginados(pageable);
            }
        } catch (Exception e) {
            // En caso de error, mostrar todos los productos
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

        return "producto/index";
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

    // --- CREAR PRODUCTO ---
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("producto", new Producto());
        return "producto/crear";
    }

    @PostMapping("/crear")
    public String guardar(@Valid @ModelAttribute Producto producto,
                          BindingResult result,
                          @RequestParam("imagenFile") MultipartFile imagenFile,
                          RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "producto/crear";
        }

        try {
            if (!imagenFile.isEmpty()) {
                // Validar tipo de archivo
                String contentType = imagenFile.getContentType();
                if (contentType == null || (!contentType.startsWith("image/"))) {
                    redirectAttributes.addFlashAttribute("error", "Solo se permiten archivos de imagen");
                    return "redirect:/producto/crear";
                }

                // Validar tamaño (máximo 5MB)
                if (imagenFile.getSize() > 5 * 1024 * 1024) {
                    redirectAttributes.addFlashAttribute("error", "La imagen no puede ser mayor a 5MB");
                    return "redirect:/producto/crear";
                }

                producto.setImg(imagenFile.getBytes());
            }

            productoService.crearOEditar(producto);
            redirectAttributes.addFlashAttribute("success", "Producto creado exitosamente");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al procesar la imagen");
            return "redirect:/producto/crear";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al guardar el producto");
            return "redirect:/producto/crear";
        }

        return "redirect:/producto";
    }

    // --- EDITAR PRODUCTO ---
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/producto";
            }
            model.addAttribute("producto", producto);
            return "producto/editar";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar el producto");
            return "redirect:/producto";
        }
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @Valid @ModelAttribute Producto productoActualizado,
                             BindingResult result,
                             @RequestParam("imagenFile") MultipartFile imagenFile,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (result.hasErrors()) {
            model.addAttribute("producto", productoActualizado);
            return "producto/editar";
        }

        try {
            Producto producto = productoService.obtenerPorId(id);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/producto";
            }

            producto.setNombre(productoActualizado.getNombre());
            producto.setPrecio(productoActualizado.getPrecio());
            producto.setDescripcion(productoActualizado.getDescripcion()); // NUEVA LÍNEA
            producto.setStock(productoActualizado.getStock()); // NUEVA LÍNEA

            if (!imagenFile.isEmpty()) {
                // Validar tipo de archivo
                String contentType = imagenFile.getContentType();
                if (contentType == null || (!contentType.startsWith("image/"))) {
                    redirectAttributes.addFlashAttribute("error", "Solo se permiten archivos de imagen");
                    return "redirect:/producto/editar/" + id;
                }

                // Validar tamaño
                if (imagenFile.getSize() > 5 * 1024 * 1024) {
                    redirectAttributes.addFlashAttribute("error", "La imagen no puede ser mayor a 5MB");
                    return "redirect:/producto/editar/" + id;
                }

                producto.setImg(imagenFile.getBytes());
            }

            productoService.crearOEditar(producto);
            redirectAttributes.addFlashAttribute("success", "Producto actualizado exitosamente");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al procesar la imagen");
            return "redirect:/producto/editar/" + id;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el producto");
            return "redirect:/producto/editar/" + id;
        }

        return "redirect:/producto";
    }

    // --- VER DETALLES ---
    @GetMapping("/detalles/{id}")
    public String verDetalles(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/producto";
            }
            model.addAttribute("producto", producto);
            return "producto/detalles";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar los detalles del producto");
            return "redirect:/producto";
        }
    }

    // --- ELIMINAR PRODUCTO ---
    @GetMapping("/eliminar/{id}")
    public String confirmarEliminar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/producto";
            }
            model.addAttribute("producto", producto);
            return "producto/eliminar";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar el producto");
            return "redirect:/producto";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/producto";
            }

            productoService.eliminarPorId(id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado exitosamente");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el producto");
        }

        return "redirect:/producto";
    }
}