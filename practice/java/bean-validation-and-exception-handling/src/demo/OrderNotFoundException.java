package demo;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(long id) {
        super("Order " + id + " not found");
    }
}
