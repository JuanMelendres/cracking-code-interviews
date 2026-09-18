package demo;

public class OrderNotFoundException extends RuntimeException {
    public final long orderId;

    public OrderNotFoundException(long orderId) {
        super("order " + orderId + " not found");
        this.orderId = orderId;
    }
}
