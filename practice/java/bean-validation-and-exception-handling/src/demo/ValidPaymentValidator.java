package demo;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPaymentValidator implements ConstraintValidator<ValidPayment, OrderRequest> {
    @Override
    public boolean isValid(OrderRequest req, ConstraintValidatorContext ctx) {
        if (req == null || req.paymentMethod() != PaymentMethod.CREDIT_CARD) {
            return true; // PayPal (or null, caught separately by @NotNull) needs no card number
        }
        return req.cardNumber() != null && req.cardNumber().matches("\\d{16}");
    }
}
