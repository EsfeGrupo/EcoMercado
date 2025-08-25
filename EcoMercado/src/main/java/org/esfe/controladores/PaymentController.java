package org.esfe.controladores;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.esfe.dto.ChargeRequest;
import org.esfe.modelos.Venta;
import org.esfe.servicios.interfaces.IPaymentService;
import org.esfe.servicios.interfaces.IVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    // We only need these two services now
    @Autowired private IPaymentService paymentService;
    @Autowired private IVentaService ventaService;

    @PostMapping("/charge")
    public ResponseEntity<?> chargeCard(@RequestBody ChargeRequest chargeRequest) {
        try {
            // 1. Get the sale and calculate the total securely from the database.
            Venta venta = ventaService.findById(chargeRequest.getVentaId())
                    .orElseThrow(() -> new RuntimeException("Venta no encontrada."));
            BigDecimal totalCalculado = ventaService.getStoredTotal(venta.getId());

            // 2. Process the payment using the secure total.
            PaymentIntent paymentIntent = paymentService.processPayment(chargeRequest.getPaymentMethodId(), totalCalculado);

            // 3. Update the sale with the payment details. We no longer save a separate card object.
            venta.setEstado("pagada");
            venta.setStripePaymentIntentId(paymentIntent.getId());
            venta.setTotal(totalCalculado);
            ventaService.save(venta);

            return ResponseEntity.ok("Pago procesado exitosamente. Venta " + venta.getId() + " pagada. ID de Stripe: " + paymentIntent.getId());
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en el pago con Stripe: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
        }
    }
}