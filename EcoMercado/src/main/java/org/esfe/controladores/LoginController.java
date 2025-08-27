package org.esfe.controladores;

import org.esfe.modelos.Usuario;
import org.esfe.servicios.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private IUsuarioService usuarioService;

    // Mostrar formulario de login
    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        // Si ya hay una sesión activa, redirigir según el rol
        Usuario usuarioSession = (Usuario) session.getAttribute("usuarioSession");
        if (usuarioSession != null) {
            return redirectByRole(usuarioSession);
        }
        return "auth/login"; // busca en templates/auth/login.html
    }

    // Procesar login
    @PostMapping("/login")
    public String login(
            @RequestParam("correo") String correo,
            @RequestParam("password") String password,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            if (correo == null || correo.trim().isEmpty()) {
                model.addAttribute("error", "El correo electrónico es requerido");
                return "auth/login";
            }

            if (password == null || password.trim().isEmpty()) {
                model.addAttribute("error", "La contraseña es requerida");
                return "auth/login";
            }

            Usuario usuario = usuarioService.login(correo.trim(), password);

            if (usuario == null) {
                model.addAttribute("error", "Correo o contraseña incorrectos");
                return "auth/login";
            }

            session.setAttribute("usuarioSession", usuario);
            session.setAttribute("nombreUsuario", usuario.getNombre());
            session.setAttribute("rolUsuario", usuario.getRol().getNombre());

            redirectAttributes.addFlashAttribute("mensajeExito",
                    "¡Bienvenido, " + usuario.getNombre() + "!");

            return redirectByRole(usuario);

        } catch (Exception e) {
            System.err.println("Error en login: " + e.getMessage());
            model.addAttribute("error", "Error interno del servidor. Intente nuevamente.");
            return "auth/login";
        }
    }

    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String registerForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    // Procesar registro
    @PostMapping("/registro")
    public String processRegister(Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.guardar(usuario);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Registro exitoso! Ahora puedes iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el usuario: " + e.getMessage());
            return "redirect:/registro";
        }
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
            String nombreUsuario = (usuario != null) ? usuario.getNombre() : "Usuario";

            session.invalidate();

            redirectAttributes.addFlashAttribute("mensajeInfo",
                    "Hasta luego, " + nombreUsuario + ". Sesión cerrada correctamente.");

        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
        }

        return "redirect:/login";
    }

    // MÉTODO CORREGIDO: Redirigir a la página principal
    private String redirectByRole(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null) {
            return "redirect:/login";
        }

        // Tu layout _mainLayout maneja toda la lógica de roles
        // Simplemente redirigir a la página principal
        return "redirect:/";
    }

    // Endpoint para verificar sesión
    @GetMapping("/verificar-sesion")
    public String verificarSesion(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("autenticado", true);
        } else {
            model.addAttribute("autenticado", false);
        }
        return "fragments/sesion-status";
    }

    // Página de acceso denegado
    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "error/403";
    }
}