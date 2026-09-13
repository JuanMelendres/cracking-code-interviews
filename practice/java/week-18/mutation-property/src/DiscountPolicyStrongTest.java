import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Same two tests, PLUS the boundary case the weak suite was missing.
public class DiscountPolicyStrongTest {
    @Test
    void wellAboveThresholdIsEligible() {
        assertTrue(DiscountPolicy.isEligibleForDiscount(150));
    }

    @Test
    void wellBelowThresholdIsNotEligible() {
        assertFalse(DiscountPolicy.isEligibleForDiscount(50));
    }

    @Test
    void exactlyAtThresholdIsEligible() {
        assertTrue(DiscountPolicy.isEligibleForDiscount(100));
    }
}
