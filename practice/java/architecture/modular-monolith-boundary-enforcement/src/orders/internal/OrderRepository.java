package orders.internal;

import orders.api.OrderLookup;

/** The orders module's real implementation detail. Not part of the public contract. */
public class OrderRepository implements OrderLookup {
    @Override
    public String find(String orderId) {
        return "order-" + orderId + "-details";
    }
}
