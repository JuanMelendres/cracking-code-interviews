final class OrderPlaced {
    final String orderId;
    OrderPlaced(String orderId) { this.orderId = orderId; }
}

final class InventoryReserved {
    final String orderId;
    InventoryReserved(String orderId) { this.orderId = orderId; }
}

final class PaymentCharged {
    final String orderId;
    PaymentCharged(String orderId) { this.orderId = orderId; }
}

final class OrderShipped {
    final String orderId;
    OrderShipped(String orderId) { this.orderId = orderId; }
}
