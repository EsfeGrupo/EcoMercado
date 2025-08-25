package org.esfe.dto;

public class ChargeRequest {
    private String paymentMethodId;
    private Integer ventaId;

    // Getters y Setters
    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }
    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }
}
