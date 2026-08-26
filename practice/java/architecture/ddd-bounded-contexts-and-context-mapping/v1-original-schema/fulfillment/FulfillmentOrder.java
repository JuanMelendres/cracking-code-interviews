package fulfillment;

/**
 * Fulfillment's own "Order" -- a physical shipment, not a commercial transaction.
 * This file is byte-identical between v1-original-schema and v2-upstream-renamed-field
 * (verify with `diff`): Fulfillment's own model never has to change just because
 * Sales renamed a field, which is the entire point of owning your own bounded-context
 * model instead of consuming an upstream type directly.
 */
public final class FulfillmentOrder {
    public final String orderId;
    public final String recipientName;
    public final double weightKg;

    public FulfillmentOrder(String orderId, String recipientName, double weightKg) {
        this.orderId = orderId;
        this.recipientName = recipientName;
        this.weightKg = weightKg;
    }
}
