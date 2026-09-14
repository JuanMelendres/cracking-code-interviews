package sales;

/**
 * The Sales bounded context's own "Order" -- what it means HERE: a commercial
 * transaction with a customer, a price, and a discount. Fulfillment's "Order" (see
 * fulfillment.FulfillmentOrder) means something genuinely different -- a physical
 * package with a recipient and a weight. Both are correctly called "Order" in their
 * own ubiquitous language; forcing them into one shared class is the mistake this
 * demo exists to disprove as a workable shortcut.
 */
public final class SalesOrder {
    public final String orderId;
    public final String customerName;
    public final double totalPrice;

    public SalesOrder(String orderId, String customerName, double totalPrice) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.totalPrice = totalPrice;
    }

    public String getCustomerName() {
        return customerName;
    }
}
