package after;

/**
 * One remediation step of an incremental modernization: extract the pricing-related
 * collaborators (discount, tax, loyalty) behind a single coordinator with its own
 * clear responsibility, so OrderProcessor no longer needs to know any of them exist
 * individually.
 */
public final class PricingCoordinator {
    private final DiscountEngine discount = new DiscountEngine();
    private final TaxCalculator tax = new TaxCalculator();
    private final LoyaltyService loyalty = new LoyaltyService();

    public double priceOrder(double price) {
        return tax.addTax(discount.apply(price));
    }

    public void awardLoyalty(String orderId) {
        loyalty.addPoints(orderId);
    }
}
