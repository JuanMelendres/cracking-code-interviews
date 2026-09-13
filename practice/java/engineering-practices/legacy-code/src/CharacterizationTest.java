/**
 * Step 2 of characterization testing: pin down the ACTUAL behavior observed
 * in Explore.java as a real, runnable safety net -- not the behavior we wish
 * the code had. These assertions exist to fail loudly if a future refactor
 * accidentally changes behavior, not to declare the behavior "correct."
 */
public class CharacterizationTest {

    private static int failures = 0;

    public static void main(String[] args) {
        check("no discount below threshold", LegacyOrderPricer.price(9, 19.99), 179.91);
        check("discount applies at threshold", LegacyOrderPricer.price(10, 19.99), 179.91);
        check("discount applies above threshold", LegacyOrderPricer.price(11, 19.99), 197.9);

        // Real, captured finding: at this unit price, buying the 10th unit is
        // effectively free -- qty=9 and qty=10 produce the IDENTICAL price,
        // because the 10% discount at the threshold exactly cancels the cost
        // of the extra unit. This is not a bug being fixed here -- it's real,
        // current behavior a characterization test locks in so nobody removes
        // it by accident while refactoring, without a deliberate decision.
        double atNine = LegacyOrderPricer.price(9, 19.99);
        double atTen = LegacyOrderPricer.price(10, 19.99);
        check("qty=9 and qty=10 produce the identical price at this unit price (real discount-cliff behavior)",
            atTen, atNine);

        check("small unit price, no discount", LegacyOrderPricer.price(9, 0.01), 0.09);
        check("small unit price, discount applies", LegacyOrderPricer.price(10, 0.01), 0.09);
        check("single unit, no discount possible", LegacyOrderPricer.price(1, 1000.00), 1000.0);

        if (failures == 0) {
            System.out.println("All characterization assertions passed -- current behavior is now pinned.");
        } else {
            System.out.println(failures + " characterization assertion(s) FAILED -- behavior changed.");
            System.exit(1);
        }
    }

    private static void check(String label, double actual, double expected) {
        if (Double.compare(actual, expected) == 0) {
            System.out.println("  PASS  " + label + " -> " + actual);
        } else {
            System.out.println("  FAIL  " + label + " -> expected " + expected + ", got " + actual);
            failures++;
        }
    }
}
