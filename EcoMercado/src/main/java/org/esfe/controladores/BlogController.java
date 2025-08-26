package org.esfe.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.esfe.modelos.Blog;
import org.esfe.servicios.interfaces.IBlogService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.esfe.servicios.interfaces.IUsuarioService;

@Controller
@RequestMapping("/blog")
public class BlogController {
    @Autowired
    private IBlogService blogService;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping
    public String index(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam("autor") Optional<String> autor, @RequestParam("descripcion") Optional<String> descripcion) {
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize, sortByIdDesc);
        String autorSearch = autor.orElse("");
        String descripcionSearch = descripcion.orElse("");

        Page<Blog> blogs = blogService.findByAutorContainingIgnoreCaseAndDescripcionContainingIgnoreCase(autorSearch, descripcionSearch, pageable);
        model.addAttribute("blogs", blogs);

        int totalPages = blogs.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "Blog/index";
    }

    @GetMapping("/create")
    public String create(Blog blog, Model model) {
        // Aquí deberías cargar los usuarios para el select
        model.addAttribute("usuarios", usuarioService.obtenerTodos());
        return "Blog/create";
    }

    @PostMapping("/save")
    public String save(Blog blog, BindingResult result, Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute(blog);
            model.addAttribute("usuarios", usuarioService.obtenerTodos()); // Volver a cargar los usuarios
            attributes.addFlashAttribute("error", "No se pudo guardar debido a un error.");
            return "Blog/create";
        }
        blog.setFechaPublicacion(LocalDateTime.now());
        blogService.crearOEditar(blog);
        attributes.addFlashAttribute("msg", "Blog creado correctamente");
        return "redirect:/blog";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model) {
        Blog blog = blogService.obtenerPorId(id).orElse(null);
        model.addAttribute("blog", blog);
        return "Blog/details";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        Blog blog = blogService.obtenerPorId(id).orElse(null);
        model.addAttribute("blog", blog);
        model.addAttribute("usuarios", usuarioService.obtenerTodos());
        return "Blog/edit";
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") Integer id, Model model) {
        Blog blog = blogService.obtenerPorId(id).orElse(null);
        model.addAttribute("blog", blog);
        return "Blog/remove";
    }

    @PostMapping("/remove")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes attributes) {
        blogService.eliminarPorId(id);
        attributes.addFlashAttribute("msg", "Blog eliminado correctamente");
        return "redirect:/blog";
    }
}