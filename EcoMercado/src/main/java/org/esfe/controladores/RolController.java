package org.esfe.controladores;
import org.esfe.servicios.utilerias.PdfGeneratorService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.esfe.modelos.Rol;
import org.esfe.servicios.interfaces.IRolService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.IntStream;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/roles")
public class RolController {
    @Autowired
    private IRolService rolService;
    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @GetMapping
    public String index(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam("nombre") Optional<String> nombre, @RequestParam("descripcion") Optional<String> descripcion ){
        int currentPage = page.orElse(1) - 1; // si no está seteado se asigna 0
        int pageSize = size.orElse(5); // tamaño de la página, se asigna 5
        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(currentPage, pageSize,sortByIdDesc);
        String nombreSearch = nombre.orElse("");
        String descripcionSearch = descripcion.orElse("");
        Page<Rol> roles = rolService.findByNombreContainingIgnoreCaseAndDescripcionContainingIgnoreCaseOrderByIdDesc(nombreSearch,descripcionSearch,pageable);
        model.addAttribute("roles", roles);

        int totalPages = roles.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "rol/index";
    }

    @GetMapping("/create")
    public String create(Rol rol){
        return "rol/create";
    }

    @PostMapping("/save")
    public String save(Rol rol, BindingResult result, Model model, RedirectAttributes attributes){
        if(result.hasErrors()){
            model.addAttribute(rol);
            attributes.addFlashAttribute("error", "No se pudo guardar debido a un error.");
            return "rol/create";
        }

        rolService.crearOEditar(rol);
        attributes.addFlashAttribute("msg", "rol creado correctamente");
        return "redirect:/roles";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model){
        Rol rol = rolService.obtenerPorId(id);
        model.addAttribute("rol", rol);
        return "rol/details";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model){
        Rol rol = rolService.obtenerPorId(id);
        model.addAttribute("rol", rol);
        return "rol/edit";
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") Integer id, Model model){
        Rol rol = rolService.obtenerPorId(id);
        model.addAttribute("rol", rol);
        return "rol/delete";
    }

    @PostMapping("/delete")
    public String delete(Rol rol, RedirectAttributes attributes) {
    try {
        rolService.eliminarPorId(rol.getId());
        attributes.addFlashAttribute("msg", "Rol eliminado correctamente");
    } catch (Exception e) {
        attributes.addFlashAttribute("error", "No se puede eliminar el rol porque ya está vinculado a un usuario.");
    }
    return "redirect:/roles";
    }

    @GetMapping("/reportegeneral/{visualizacion}")
    public ResponseEntity<byte[]> ReporteGeneral(@PathVariable("visualizacion") String visualizacion) {

        try {
            List<Rol> roles = rolService.obtenerTodos();

            // Genera el PDF. Si hay un error aquí, la excepción será capturada.
            byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtml("reportes/rpRoles", "roles", roles);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // inline= vista previa, attachment=descarga el archivo
            headers.add("Content-Disposition", visualizacion+"; filename=reporte_general.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}