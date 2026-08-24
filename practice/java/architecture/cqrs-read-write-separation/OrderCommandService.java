import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The command side. Owns the normalized write model and is the only thing allowed to mutate it.
 * Every mutation publishes a domain event; the write side never reads its own events back and
 * never knows the read model exists.
 */
final class OrderCommandService {
    private final Map<Long, Order> writeStore = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);
    private final BlockingQueue<DomainEvent> eventBus;

    OrderCommandService(BlockingQueue<DomainEvent> eventBus) {
        this.eventBus = eventBus;
    }

    long createOrder(String customerId) {
        long id = nextId.getAndIncrement();
        writeStore.put(id, new Order(id, customerId));
        publish(new OrderCreated(id, customerId, System.nanoTime()));
        return id;
    }

    void addItem(long orderId, String sku, int quantity, BigDecimal unitPrice) {
        Order order = writeStore.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("no such order: " + orderId);
        }
        order.items.add(new OrderItem(sku, quantity, unitPrice));
        publish(new ItemAdded(orderId, sku, quantity, unitPrice, System.nanoTime()));
    }

    void completeOrder(long orderId) {
        Order order = writeStore.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("no such order: " + orderId);
        }
        order.status = "COMPLETED";
        publish(new OrderCompleted(orderId, System.nanoTime()));
    }

    /** Only exists so demos can compute a "ground truth" straight from the write model for comparison. */
    Order rawOrder(long orderId) {
        return writeStore.get(orderId);
    }

    Map<Long, Order> allOrders() {
        return writeStore;
    }

    private void publish(DomainEvent event) {
        try {
            eventBus.put(event);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
