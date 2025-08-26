package org.esfe.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/blog")
public class BlogController {
    @GetMapping
    public String blog(){
        return "Blog/blog";
    }
}
// Falta plantear bien el controlador del blog, por ahora se le dio prioridad a al controlador de Ventas ya que es una tabla maestro-detalle.