package org.esfe.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre del producto es requerido")
    private String nombre;

    @NotNull(message = "El precio del producto es requerido")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double precio;

    // NUEVO CAMPO STOCK
    @NotNull(message = "El stock del producto es requerido")
    @Column(nullable = false)
    private Integer stock = 0; // Valor por defecto

    @ManyToMany(mappedBy = "productos")
    private Set<Tipo> tipos = new HashSet<>();

    // CONFIGURACIÓN ESPECÍFICA PARA IMÁGENES GRANDES
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "img", columnDefinition = "LONGBLOB")
    private byte[] img;

    @ManyToMany(mappedBy = "productos")
    private Set<Vendedor> vendedores = new HashSet<>();

    // Getters y Setters existentes
    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public Set<Tipo> getTipos() {
        return tipos;
    }

    public void setTipos(Set<Tipo> tipos) {
        this.tipos = tipos;
    }

    public Set<Vendedor> getVendedores() {
        return vendedores;
    }

    public void setVendedores(Set<Vendedor> vendedores) {
        this.vendedores = vendedores;
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

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    // NUEVOS GETTERS Y SETTERS PARA STOCK
    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}