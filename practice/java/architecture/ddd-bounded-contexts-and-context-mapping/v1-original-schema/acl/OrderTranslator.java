package acl;

import fulfillment.FulfillmentOrder;
import sales.SalesOrder;

/**
 * The Anti-Corruption Layer: the ONE place allowed to know about Sales's upstream
 * model. This file DOES differ between v1-original-schema and v2-upstream-renamed-field
 * (verify with `diff` -- it's the only one of the five files that does) -- it exists
 * specifically to absorb that kind of upstream change so nothing downstream has to.
 */
public final class OrderTranslator {

    public FulfillmentOrder toFulfillmentOrder(SalesOrder order) {
        return new FulfillmentOrder(order.orderId, order.getCustomerName(), estimateWeightKg(order));
    }

    private double estimateWeightKg(SalesOrder order) {
        return order.totalPrice / 100.0; // placeholder domain rule, not the point of this demo
    }
}
