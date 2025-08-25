package org.esfe.servicios.interfaces;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import java.math.BigDecimal;

public interface IPaymentService {
    PaymentIntent processPayment(String paymentMethodId, BigDecimal amount) throws StripeException;
}