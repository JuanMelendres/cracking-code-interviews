import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Two "obviously reasonable" example tests -- neither happens to test the
// boundary value itself (100). This is the single most common real gap
// mutation testing exists to surface.
public class DiscountPolicyWeakTest {
    @Test
    void wellAboveThresholdIsEligible() {
        assertTrue(DiscountPolicy.isEligibleForDiscount(150));
    }

    @Test
    void wellBelowThresholdIsNotEligible() {
        assertFalse(DiscountPolicy.isEligibleForDiscount(50));
    }
}
