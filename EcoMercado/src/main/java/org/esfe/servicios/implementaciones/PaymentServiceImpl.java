package org.esfe.servicios.implementaciones;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import org.esfe.servicios.interfaces.IPaymentService;

@Service
public class PaymentServiceImpl implements IPaymentService {
    @Value("${stripe.api.secretKey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public PaymentIntent processPayment(String paymentMethodId, BigDecimal amount) throws StripeException {
        // ... (El mismo código que ya tenías)
        long amountInCents = amount.multiply(new BigDecimal("100")).longValue();

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amountInCents)
                        .setCurrency("usd")
                        .setPaymentMethod(paymentMethodId)
                        .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.AUTOMATIC)
                        .setConfirm(true)
                        .build();

        return PaymentIntent.create(params);
    }
}