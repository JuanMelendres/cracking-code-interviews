package acl;

import fulfillment.FulfillmentOrder;

/**
 * The ACL-protected consumer: depends ONLY on Fulfillment's own model, never on
 * sales.SalesOrder. This file is byte-identical between v1-original-schema and
 * v2-upstream-renamed-field (verify with `diff`) -- and, unlike
 * conformist.ConformistFulfillmentService, it also still COMPILES unchanged in both,
 * because the upstream rename was absorbed entirely by OrderTranslator.
 */
public final class AclFulfillmentService {

    public void prepareShipment(FulfillmentOrder order) {
        System.out.println("Preparing shipment for " + order.recipientName
                + " (order " + order.orderId + ", " + order.weightKg + "kg)");
    }
}
