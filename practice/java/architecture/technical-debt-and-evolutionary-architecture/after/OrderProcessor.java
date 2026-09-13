package after;

/**
 * The same behavior as before.OrderProcessor, after three incremental extraction
 * steps (Pricing, Fulfillment, Compliance coordinators). No single step was a
 * rewrite -- each extracted one cohesive group of collaborators behind a coordinator
 * with its own name and responsibility, exactly the kind of small, reviewable,
 * individually-shippable step evolutionary architecture is built around.
 */
public final class OrderProcessor {
    private final PaymentClient payment = new PaymentClient();
    private final PricingCoordinator pricing = new PricingCoordinator();
    private final FulfillmentCoordinator fulfillment = new FulfillmentCoordinator();
    private final ComplianceCoordinator compliance = new ComplianceCoordinator();

    public void process(String orderId, double price) {
        if (compliance.isSuspicious(orderId)) {
            compliance.recordRejection(orderId);
            return;
        }
        double finalPrice = pricing.priceOrder(price);
        fulfillment.fulfill(orderId);
        payment.charge(orderId);
        pricing.awardLoyalty(orderId);
        compliance.recordCompletion(orderId);
        System.out.println("[OrderProcessor] processed " + orderId + " for $" + finalPrice);
    }
}
