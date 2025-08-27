package org.esfe.controladores;

import org.esfe.modelos.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/vendedor")
public class VendedorvistasController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");

        if (usuario == null || !usuario.getRol().getNombre().equalsIgnoreCase("vendedor")) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("titulo", "Panel de Vendedor");

        return "vendedorvistas/home"; // templates/vendedor/home.html
    }

    @GetMapping("/productos")
    public String productos(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");

        if (usuario == null || !usuario.getRol().getNombre().equalsIgnoreCase("vendedor")) {
            return "redirect:/acceso-denegado";
        }

        model.addAttribute("titulo", "Mis Productos");
        return "vendedorvistas/productos"; // templates/vendedor/productos.html
    }

    @GetMapping("/ventas")
    public String ventas(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");

        if (usuario == null || !usuario.getRol().getNombre().equalsIgnoreCase("vendedor")) {
            return "redirect:/acceso-denegado";
        }

        model.addAttribute("titulo", "Mis Ventas");
        return "vendedorvistas/ventas"; // templates/vendedor/ventas.html
    }
}
