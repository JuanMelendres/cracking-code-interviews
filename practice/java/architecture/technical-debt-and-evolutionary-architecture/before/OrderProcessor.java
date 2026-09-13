package before;

/**
 * Ten collaborators accumulated one at a time, over what a real codebase's history
 * would show as ten separate, individually-reasonable pull requests -- "just add the
 * fraud check here too, it's a small addition" -- none of which looked like a
 * problem in isolation. This class is the real, compiling result of that accumulation:
 * a single class now coupled to every concern in the checkout flow.
 */
public final class OrderProcessor {
    private final InventoryClient inventory = new InventoryClient();
    private final PaymentClient payment = new PaymentClient();
    private final ShippingClient shipping = new ShippingClient();
    private final NotificationClient notification = new NotificationClient();
    private final AuditLogger audit = new AuditLogger();
    private final DiscountEngine discount = new DiscountEngine();
    private final TaxCalculator tax = new TaxCalculator();
    private final FraudCheck fraud = new FraudCheck();
    private final LoyaltyService loyalty = new LoyaltyService();
    private final AnalyticsTracker analytics = new AnalyticsTracker();

    public void process(String orderId, double price) {
        if (fraud.isSuspicious(orderId)) {
            audit.log(orderId);
            return;
        }
        double finalPrice = tax.addTax(discount.apply(price));
        inventory.reserve(orderId);
        payment.charge(orderId);
        shipping.ship(orderId);
        notification.notifyCustomer(orderId);
        loyalty.addPoints(orderId);
        analytics.track(orderId);
        audit.log(orderId);
        System.out.println("[OrderProcessor] processed " + orderId + " for $" + finalPrice);
    }
}
