package org.esfe.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Rol")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre del rol es requerido")
    private String nombre;

    @NotBlank(message = "La descripción del rol es requerido")
    private String descripcion;

    @OneToMany(mappedBy = "rol", // "rol" es el nombre del campo en la entidad Usuario que mapea esta relación
            fetch = FetchType.LAZY // Cuando cargues un Rol, los Usuarios no se cargarán hasta que los pidas.
            // LAZY es el valor por defecto para OneToMany y es recomendado para rendimiento.
    )
    private List<Usuario> usuarios = new ArrayList<>();

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
