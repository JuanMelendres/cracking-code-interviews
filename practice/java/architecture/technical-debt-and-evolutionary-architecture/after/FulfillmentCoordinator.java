package after;

/**
 * Second remediation step: extract the physical-fulfillment collaborators
 * (inventory, shipping, notification) behind their own coordinator.
 */
public final class FulfillmentCoordinator {
    private final InventoryClient inventory = new InventoryClient();
    private final ShippingClient shipping = new ShippingClient();
    private final NotificationClient notification = new NotificationClient();

    public void fulfill(String orderId) {
        inventory.reserve(orderId);
        shipping.ship(orderId);
        notification.notifyCustomer(orderId);
    }
}
