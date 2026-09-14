final class Order {
    final String orderId;
    final String customerName;

    Order(String orderId, String customerName) {
        this.orderId = orderId;
        this.customerName = customerName;
    }

    @Override
    public String toString() {
        return "Order{" + orderId + ", " + customerName + "}";
    }
}
