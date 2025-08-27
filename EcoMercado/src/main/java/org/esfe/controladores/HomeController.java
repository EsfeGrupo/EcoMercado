package org.esfe.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        // Tu layout _mainLayout ya maneja toda la lógica de roles con:
        // th:if="${session.rolUsuario == 'admin'}"
        // th:if="${session.rolUsuario == 'vendedor'}"
        // th:if="${session.rolUsuario == 'usuario'}"

        return "home/index";
    }
}