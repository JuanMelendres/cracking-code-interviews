/**
 * "After" version: same behavior as ShippingCostBefore, reached via three
 * successive Extract Method refactors -- each one small enough to run the
 * shared test suite after, never batching multiple changes before verifying.
 * No new feature, no bug fix -- purely a structure change, proven identical
 * by RefactoringParityTest.java running the exact same inputs through both.
 */
public class ShippingCostAfter {
    static double cost(double weightKg, String region, boolean express) {
        double total = weightTierBaseCost(weightKg) * regionMultiplier(region);
        total += expressSurcharge(region, express);
        return Math.round(total * 100) / 100.0;
    }

    private static double weightTierBaseCost(double weightKg) {
        if (weightKg <= 1.0) return 5.00;
        if (weightKg <= 5.0) return 9.00;
        if (weightKg <= 20.0) return 18.00;
        return 18.00 + (weightKg - 20.0) * 0.75;
    }

    private static double regionMultiplier(String region) {
        return switch (region) {
            case "domestic" -> 1.0;
            case "continental" -> 1.5;
            case "international" -> 2.5;
            default -> throw new IllegalArgumentException("unknown region: " + region);
        };
    }

    private static double expressSurcharge(String region, boolean express) {
        if (!express) return 0.0;
        return region.equals("domestic") ? 10.00 : 25.00;
    }
}
