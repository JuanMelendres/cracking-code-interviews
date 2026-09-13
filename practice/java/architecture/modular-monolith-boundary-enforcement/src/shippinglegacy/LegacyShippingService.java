package shippinglegacy;

import orders.internal.PricingEngine;

/**
 * A real, common way module boundaries actually erode over time: an older module,
 * written before the orders.api boundary existed (or by an engineer who didn't know
 * about it), reaches directly into orders.internal because the compiler never
 * stopped it -- internal is a package name convention here, not a real access
 * restriction, since everything involved is public.
 */
public class LegacyShippingService {
    private final PricingEngine pricingEngine = new PricingEngine();

    public double quoteShippingCost(String orderId) {
        return pricingEngine.computeInternalPrice(orderId) * 0.1;
    }
}
