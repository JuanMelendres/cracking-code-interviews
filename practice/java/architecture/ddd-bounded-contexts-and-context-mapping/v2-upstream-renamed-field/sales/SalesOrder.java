package sales;

/**
 * Sales's real upstream schema change: `customerName` renamed to `buyerName`
 * (a real, ordinary refactor a team might make to align with a new internal naming
 * convention). This is the only change simulated in this v2 scenario -- everything
 * about Fulfillment's OWN model is untouched.
 */
public final class SalesOrder {
    public final String orderId;
    public final String buyerName;
    public final double totalPrice;

    public SalesOrder(String orderId, String buyerName, double totalPrice) {
        this.orderId = orderId;
        this.buyerName = buyerName;
        this.totalPrice = totalPrice;
    }

    public String getBuyerName() {
        return buyerName;
    }
}
