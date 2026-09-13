/**
 * "Before" version: a long method mixing three unrelated concerns (weight-tier
 * lookup, region surcharge, and express-fee logic) in one nested-conditional
 * block. Deliberately realistic legacy shape, not a strawman -- this compiles
 * and produces correct-for-its-spec output; the problem is maintainability,
 * not correctness.
 */
public class ShippingCostBefore {
    static double cost(double weightKg, String region, boolean express) {
        double base;
        if (weightKg <= 1.0) {
            base = 5.00;
        } else if (weightKg <= 5.0) {
            base = 9.00;
        } else if (weightKg <= 20.0) {
            base = 18.00;
        } else {
            base = 18.00 + (weightKg - 20.0) * 0.75;
        }

        double regionMultiplier;
        if (region.equals("domestic")) {
            regionMultiplier = 1.0;
        } else if (region.equals("continental")) {
            regionMultiplier = 1.5;
        } else if (region.equals("international")) {
            regionMultiplier = 2.5;
        } else {
            throw new IllegalArgumentException("unknown region: " + region);
        }

        double total = base * regionMultiplier;

        if (express) {
            if (region.equals("domestic")) {
                total += 10.00;
            } else {
                total += 25.00;
            }
        }

        return Math.round(total * 100) / 100.0;
    }
}
