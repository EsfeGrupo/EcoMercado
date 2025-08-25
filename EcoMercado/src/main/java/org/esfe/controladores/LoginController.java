package org.esfe.controladores;

import org.esfe.modelos.Usuario;
import org.esfe.servicios.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private IUsuarioService usuarioService;

    // Mostrar formulario de login
    @GetMapping("/login")
    public String loginForm() {
        return "login"; // nombre del html: login.html
    }

    // Procesar login
    @PostMapping("/login")
    public String login(
            @RequestParam("correo") String correo,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        Usuario usuario = usuarioService.login(correo, password);

        if (usuario == null) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
        }

        // Guardar usuario en sesión
        session.setAttribute("usuarioSession", usuario);

        // Redirigir según rol
        String rol = usuario.getRol().getNombre();
        switch (rol.toLowerCase()) {
            case "admin":
                return "redirect:/admin/home";
            case "vendedor":
                return "redirect:/vendedor/home";
            default: // usuario normal
                return "redirect:/usuario/home";
        }
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
