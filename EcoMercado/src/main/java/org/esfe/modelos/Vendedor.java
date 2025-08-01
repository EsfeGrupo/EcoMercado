package org.esfe.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Vendedor extends Usuario {

    @Column(nullable = false, unique = true)
    private String idVendedor;

    @NotBlank(message = "La dirección es requerida")
    private String direccion;

    @NotBlank(message = "El teléfono es requerido")
    private String telefono;

    @NotBlank(message = "El DUI es requerido")
    private String dui;

    @NotBlank(message = "La ubicación es requerida")
    private String ubicacion;

    @ManyToMany
    @JoinTable(
            name = "producto_vendedor",
            joinColumns = @JoinColumn(name = "idVendedor"),
            inverseJoinColumns = @JoinColumn(name = "idProducto")
    )
    private Set<Producto> productos = new HashSet<>();

    public String getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Set<Producto> getProductos() {
        return productos;
    }

    public void setProductos(Set<Producto> productos) {
        this.productos = productos;
    }
}
