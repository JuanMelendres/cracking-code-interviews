package demo;

/** Plain in-memory order record used by every demo endpoint in this pack. */
public class Order {
    public final long id;
    public final OrderStatus status;
    public final double amount;

    public Order(long id, OrderStatus status, double amount) {
        this.id = id;
        this.status = status;
        this.amount = amount;
    }
}
