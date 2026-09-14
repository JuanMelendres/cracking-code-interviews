import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A minimal, real key-value store standing in for either the legacy system or the
 * new system during a migration. Two independent instances of this class represent
 * two genuinely separate systems of record -- writing to one never implicitly writes
 * to the other, which is exactly what makes dual-write an explicit choice a migration
 * has to make, not something that happens for free.
 */
final class OrderStore {
    private final String name;
    private final Map<String, Order> orders = new LinkedHashMap<>();

    OrderStore(String name) {
        this.name = name;
    }

    void save(Order order) {
        orders.put(order.orderId, order);
    }

    Order find(String orderId) {
        return orders.get(orderId);
    }

    boolean contains(String orderId) {
        return orders.containsKey(orderId);
    }

    String name() {
        return name;
    }
}
