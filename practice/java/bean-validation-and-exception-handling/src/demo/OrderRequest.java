package demo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@ValidPayment
public record OrderRequest(
        @NotBlank(message = "customerEmail must not be blank")
        @Email(message = "customerEmail must be a valid email address")
        String customerEmail,

        @NotNull(message = "quantity is required")
        @Positive(message = "quantity must be positive")
        Integer quantity,

        @NotNull(message = "paymentMethod is required")
        PaymentMethod paymentMethod,

        String cardNumber // required only when paymentMethod == CREDIT_CARD; enforced by @ValidPayment
) {}
