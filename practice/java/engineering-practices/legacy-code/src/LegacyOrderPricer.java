/**
 * Deliberately "legacy": no author comments, no existing tests, an undocumented
 * discount threshold, and a real floating-point rounding quirk nobody flagged.
 * This class exists to be characterization-tested, not read for style.
 */
public class LegacyOrderPricer {
    static double price(int quantity, double unitPrice) {
        double total = quantity * unitPrice;
        if (quantity >= 10) {
            total = total * 0.9;
        }
        return Math.round(total * 100) / 100.0;
    }
}
