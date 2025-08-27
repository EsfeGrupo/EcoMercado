package org.esfe.controladores;

import org.esfe.modelos.Usuario;
import org.esfe.servicios.interfaces.IRolService;
import org.esfe.servicios.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IRolService rolService;

    // INDEX: Listado de usuarios con paginación
    @GetMapping
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> page,
                        @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Page<Usuario> usuarios = usuarioService.obtenerTodosPaginados(pageable);
        model.addAttribute("usuarios", usuarios);

        int totalPages = usuarios.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "usuario/index";
    }

    // CREAR: Mostrar formulario
    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolService.obtenerTodos());
        return "usuario/crear";
    }

    // CREAR: Procesar formulario
    @PostMapping("/crear")
    public String crear(@ModelAttribute("usuario") Usuario usuario,
                        RedirectAttributes redirectAttributes) {
        usuarioService.crearOEditar(usuario);
        redirectAttributes.addFlashAttribute("mensajeExito", "Usuario creado correctamente");
        return "redirect:/usuarios";
    }

    // EDITAR: Mostrar formulario
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable("id") Integer id, Model model,
                             RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.obtenerTodos());
        return "usuario/editar";
    }

    // EDITAR: Procesar formulario
    @PostMapping("/editar/{id}")
    public String editar(@PathVariable("id") Integer id,
                         @ModelAttribute("usuario") Usuario usuario,
                         RedirectAttributes redirectAttributes) {
        Usuario usuarioExistente = usuarioService.obtenerPorId(id);
        if (usuarioExistente == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/usuarios";
        }

        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setCorreo(usuario.getCorreo());
        usuarioExistente.setPassword(usuario.getPassword());
        usuarioExistente.setRol(usuario.getRol());
        usuarioExistente.setEstado(usuario.getEstado());

        usuarioService.crearOEditar(usuarioExistente);
        redirectAttributes.addFlashAttribute("mensajeExito", "Usuario actualizado correctamente");
        return "redirect:/usuarios";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
        } else {
            usuarioService.eliminarPorId(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario eliminado correctamente");
        }
        return "redirect:/usuarios";
    }

    // DETALLES
    @GetMapping("/detalles/{id}")
    public String detalles(@PathVariable("id") Integer id, Model model,
                           RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", usuario);
        return "usuario/detalles";
    }
}
