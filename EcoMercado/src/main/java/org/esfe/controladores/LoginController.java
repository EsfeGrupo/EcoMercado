package org.esfe.controladores;

import org.esfe.modelos.Usuario;
import org.esfe.modelos.Rol;
import org.esfe.servicios.interfaces.IUsuarioService;
import org.esfe.servicios.interfaces.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IRolService rolService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // LOGIN
    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        Usuario usuarioSession = (Usuario) session.getAttribute("usuarioSession");
        if (usuarioSession != null) {
            return redirectByRole(usuarioSession);
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("correo") String correo,
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

            Usuario usuario = usuarioService.obtenerPorCorreo(correo.trim());
            if (usuario == null || !passwordEncoder.matches(password, usuario.getPassword())) {
                model.addAttribute("error", "Correo o contraseña incorrectos");
                return "auth/login";
            }

            session.setAttribute("usuarioSession", usuario);
            session.setAttribute("nombreUsuario", usuario.getNombre());
            session.setAttribute("rolUsuario", usuario.getRol().getNombre());
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Bienvenido, " + usuario.getNombre() + "!");
            return redirectByRole(usuario);

        } catch (Exception e) {
            model.addAttribute("error", "Error interno del servidor. Intente nuevamente.");
            return "auth/login";
        }
    }

    // REGISTRO USUARIO NORMAL
    @GetMapping("/registro")
    public String registerForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolService.obtenerTodos());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String processRegister(Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuarioService.guardar(usuario);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Registro exitoso! Ahora puedes iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el usuario: " + e.getMessage());
            return "redirect:/registro";
        }
    }

    // REGISTRO VENDEDOR
    @GetMapping("/registro-vendedor")
    public String registerVendedorForm(Model model) {
        Usuario usuario = new Usuario();
        Rol rolVendedor = rolService.obtenerPorId(3); // ID 2 = Vendedor
        usuario.setRol(rolVendedor);
        model.addAttribute("usuario", usuario);
        return "auth/registro-vendedor";
    }

    @PostMapping("/registro-vendedor")
    public String processRegisterVendedor(Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            Rol rolVendedor = rolService.obtenerPorId(3);
            usuario.setRol(rolVendedor);
            usuarioService.guardar(usuario);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Registro de vendedor exitoso! Ahora puedes iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el vendedor: " + e.getMessage());
            return "redirect:/registro-vendedor";
        }
    }

    // LOGOUT
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
        String nombreUsuario = (usuario != null) ? usuario.getNombre() : "Usuario";
        session.invalidate();
        redirectAttributes.addFlashAttribute("mensajeInfo", "Hasta luego, " + nombreUsuario + ". Sesión cerrada correctamente.");
        return "redirect:/login";
    }

    private String redirectByRole(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null) return "redirect:/login";
        return "redirect:/";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "error/403";
    }
}
