package orders.internal;

import shipping.ShippingService;

/**
 * A real, plausible way a cycle actually enters a codebase: the orders module
 * decides to notify shipping directly on order creation, instead of publishing an
 * event -- creating a real, complete cycle at the module level, since shipping
 * already depends on orders (via orders.api) for its own normal operation.
 */
public class OrderCreatedNotifier {
    private final ShippingService shippingService;

    public OrderCreatedNotifier(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    public void onOrderCreated(String orderId) {
        shippingService.prepareShipment(orderId);
    }
}
