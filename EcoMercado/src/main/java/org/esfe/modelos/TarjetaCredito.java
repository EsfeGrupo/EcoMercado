package org.esfe.modelos;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class TarjetaCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El numero de la tarejta es requerido")
    private String numero;

    @NotBlank(message = "El correo de usuario es requerido")
    private String nombreTitular;

    @NotBlank(message = "La fecha de expiracion es requerida")
    private LocalDate fechaExpiracion;

    @NotBlank(message = "El codigo de seguridad es requerido")
    private String codigoSeguridad;

    @NotBlank(message = "El banco de la tarjeta es requerido")
    private String banco;

    @ManyToOne(fetch = FetchType.EAGER)// El rol se carga inmediatamente con el usuario
    @JoinColumn(name = "idUsuario", nullable = false) // Relación con Usuario aqui va el id de
    private Usuario usuario;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public LocalDate getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDate fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getCodigoSeguridad() {
        return codigoSeguridad;
    }

    public void setCodigoSeguridad(String codigoSeguridad) {
        this.codigoSeguridad = codigoSeguridad;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    
}
