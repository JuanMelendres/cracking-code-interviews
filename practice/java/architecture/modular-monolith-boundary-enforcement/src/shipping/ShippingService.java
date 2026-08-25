package shipping;

import orders.api.OrderLookup;

/** The correct way to depend on the orders module: only through its public API. */
public class ShippingService {
    private final OrderLookup orderLookup;

    public ShippingService(OrderLookup orderLookup) {
        this.orderLookup = orderLookup;
    }

    public String prepareShipment(String orderId) {
        String order = orderLookup.find(orderId);
        return "shipment for " + order;
    }
}
