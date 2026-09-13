package conformist;

import sales.SalesOrder;

/**
 * The Conformist relationship: Fulfillment gives up on having its own model and
 * consumes Sales's upstream type directly. This file is byte-identical between
 * v1-original-schema and v2-upstream-renamed-field (verify with `diff`) -- the
 * consumer code did not change. Whether it still COMPILES did.
 */
public final class ConformistFulfillmentService {

    public void prepareShipment(SalesOrder order) {
        System.out.println("Preparing shipment for " + order.getCustomerName()
                + " (order " + order.orderId + ")");
    }
}
