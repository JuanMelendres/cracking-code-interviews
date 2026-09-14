package orders.api;

/** The only supported way for another module to ask the orders module about an order. */
public interface OrderLookup {
    String find(String orderId);
}
