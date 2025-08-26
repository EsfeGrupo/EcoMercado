package org.esfe.modelos;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;

@Entity
public class TarjetaCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    
    @Column(name = "numero_encriptado")
    private String numeroEncriptado;
    
    @Transient // This field won't be persisted to the
    private String numero;

    @NotBlank(message = "El correo de usuario es requerido")
    private String nombreTitular;

    @NotNull(message = "La fecha de expiración es requerida")
    private LocalDate fechaExpiracion;

    @NotBlank(message = "El codigo postal es requerido")
    private String codigoPostal;

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

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
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

    public String getNumeroEncriptado() {
        return numeroEncriptado;
    }

    public void setNumeroEncriptado(String numeroEncriptado) {
        this.numeroEncriptado = numeroEncriptado;
    }
}
