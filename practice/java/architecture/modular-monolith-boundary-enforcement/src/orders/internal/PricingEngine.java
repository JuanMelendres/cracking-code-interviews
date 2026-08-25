package orders.internal;

/**
 * A real, deliberately internal-only implementation detail of the orders module --
 * how prices are actually computed, never meant to be a stable contract for other
 * modules to depend on directly.
 */
public class PricingEngine {
    public double computeInternalPrice(String orderId) {
        return orderId.length() * 10.0;
    }
}
