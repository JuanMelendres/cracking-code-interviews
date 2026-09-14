package acl;

import fulfillment.FulfillmentOrder;
import sales.SalesOrder;

/**
 * Updated for Sales's real rename (customerName -> buyerName). This is the ONE file
 * in the entire ACL-protected path that had to change -- and the change is contained
 * here, never reaching acl.AclFulfillmentService.
 */
public final class OrderTranslator {

    public FulfillmentOrder toFulfillmentOrder(SalesOrder order) {
        return new FulfillmentOrder(order.orderId, order.getBuyerName(), estimateWeightKg(order));
    }

    private double estimateWeightKg(SalesOrder order) {
        return order.totalPrice / 100.0; // placeholder domain rule, not the point of this demo
    }
}
