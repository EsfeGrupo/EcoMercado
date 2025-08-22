package org.esfe.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalleventas = new ArrayList<>();

    private String correlativo;

    private LocalDateTime fecha;

    private Float total;

    @ManyToOne
    @JoinColumn(name = "idTipoPago", nullable = false)
    private TipoPago tipoPago;

    private Byte estado;
    

    private LocalDateTime fechaEmision;

    @ManyToOne(fetch = FetchType.EAGER) // El rol se carga inmediatamente con el usuario
    @JoinColumn(name = "idTarjeta", nullable = false)
    private TarjetaCredito tarjetaCredito;
    // Getters y setters

    public void addDetalle(DetalleVenta detalle) {
        this.detalleventas.add(detalle);
        detalle.setVenta(this);
    }
    
    public void removeDetalle(DetalleVenta detalle) {
        this.detalleventas.remove(detalle);
        detalle.setVenta(null);
    }

     public List<DetalleVenta> getDetalleventas() {
        return detalleventas;
    }

    public void setDetalleventas(List<DetalleVenta> detalleventas) {
        this.detalleventas = detalleventas;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getIdUsuario() {
        return usuario;
    }

    public void setIdUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getCorrelativo() {
        return correlativo;
    }

    public void setCorrelativo(String correlativo) {
        this.correlativo = correlativo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public TipoPago getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(TipoPago tipoPago) {
        this.tipoPago = tipoPago;
    }

    public Byte getEstado() {
        return estado;
    }

    public void setEstado(Byte estado) {
        this.estado = estado;
    }

    public TarjetaCredito getIdTarjeta() {
        return tarjetaCredito;
    }

    public void setIdTarjeta(TarjetaCredito tarjetaCredito) {
        this.tarjetaCredito = tarjetaCredito;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
}
