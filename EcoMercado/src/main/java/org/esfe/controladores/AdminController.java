package org.esfe.controladores;

import org.esfe.modelos.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");

        if (usuario == null || !usuario.getRol().getNombre().equalsIgnoreCase("admin")) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("titulo", "Panel de Administración");

        return "admin/home"; // apunta a templates/admin/home.html
    }

    @GetMapping("/usuarios")
    public String usuarios(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");

        if (usuario == null || !usuario.getRol().getNombre().equalsIgnoreCase("admin")) {
            return "redirect:/acceso-denegado";
        }

        return "admin/usuarios"; // apunta a templates/admin/usuarios.html
    }

    @GetMapping("/productos")
    public String productos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");

        if (usuario == null || !usuario.getRol().getNombre().equalsIgnoreCase("admin")) {
            return "redirect:/acceso-denegado";
        }

        return "admin/productos"; // apunta a templates/admin/productos.html
    }

    @GetMapping("/roles")
    public String roles(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");

        if (usuario == null || !usuario.getRol().getNombre().equalsIgnoreCase("admin")) {
            return "redirect:/acceso-denegado";
        }

        return "admin/roles"; // apunta a templates/admin/roles.html
    }

    @GetMapping("/ventas")
    public String ventas(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");

        if (usuario == null || !usuario.getRol().getNombre().equalsIgnoreCase("admin")) {
            return "redirect:/acceso-denegado";
        }

        return "admin/ventas"; // apunta a templates/admin/ventas.html
    }
}
