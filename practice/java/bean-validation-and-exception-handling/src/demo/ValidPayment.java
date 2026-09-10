package demo;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

// A real, custom, class-level Bean Validation constraint -- cross-field
// validation Jakarta's built-in annotations (@NotNull, @Positive, etc.) can't
// express alone: "if paymentMethod is CREDIT_CARD, cardNumber must look like
// a real card number." Applied at the class level (not a single field) since
// it depends on TWO fields together.
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPaymentValidator.class)
public @interface ValidPayment {
    String message() default "cardNumber is required and must be 16 digits when paymentMethod is CREDIT_CARD";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
