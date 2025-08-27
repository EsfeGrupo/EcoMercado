package org.esfe.controladores;

import org.esfe.modelos.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario")
public class UsuariovistasController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");

        if (usuario == null ||
                (!usuario.getRol().getNombre().equalsIgnoreCase("usuario") &&
                        !usuario.getRol().getNombre().equalsIgnoreCase("cliente"))) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("titulo", "Mi Panel");

        return "usuariovistas/home"; // templates/usuario/home.html
    }

    @GetMapping("/carrito")
    public String carrito(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("titulo", "Mi Carrito");
        return "usuariovistas/carrito"; // templates/usuario/carrito.html
    }

    @GetMapping("/compras")
    public String misCompras(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("titulo", "Mis Compras");
        return "usuariovistas/compras"; // templates/usuario/compras.html
    }

    @GetMapping("/pagos")
    public String tiposPago(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("titulo", "Métodos de Pago");
        return "usuariovistas/pagos"; // templates/usuario/pagos.html
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("titulo", "Mi Perfil");
        model.addAttribute("usuario", usuario);
        return "usuariovistas/perfil"; // templates/usuario/perfil.html
    }
}
