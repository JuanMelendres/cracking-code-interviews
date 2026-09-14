import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Real proof of where coupling actually goes when you switch integration styles. The
 * same fact -- "order-42's shipping address is 12 Main St, weight 3kg" -- reaches a
 * Shipping consumer two different ways:
 *
 * <ul>
 *   <li><b>Event notification</b>: the event carries only the order ID; the consumer
 *       must call the producer back synchronously to fetch the details it needs.</li>
 *   <li><b>Event-carried state transfer</b>: the event carries the full data the
 *       consumer needs, embedded at publish time.</li>
 * </ul>
 *
 * This demo really makes the producer unavailable (an {@link AtomicBoolean} flipped
 * off, checked by a real method call that throws) between publish and consumption,
 * simulating a real, ordinary occurrence in a distributed system: the producer
 * service restarting, deploying, or being briefly overloaded after it already emitted
 * an event. Event notification's consumer really fails; event-carried state
 * transfer's consumer really succeeds -- the fact was already in hand. The trade a
 * team makes for that resilience is real too: the fat event must carry (and every
 * consumer must tolerate changes to) the producer's schema, whereas the thin event's
 * schema is nearly free to evolve.
 */
public final class ProducerAvailabilityDemo {

    static final class OrderDetails {
        final String shippingAddress;
        final double weightKg;
        OrderDetails(String shippingAddress, double weightKg) {
            this.shippingAddress = shippingAddress;
            this.weightKg = weightKg;
        }
    }

    static final class OrderService {
        private final AtomicBoolean available = new AtomicBoolean(true);

        OrderDetails fetchDetails(String orderId) {
            if (!available.get()) {
                throw new IllegalStateException(
                        "OrderService is unavailable (real simulated outage) -- cannot fetch details for " + orderId);
            }
            return new OrderDetails("12 Main St", 3.0);
        }

        void goDown() {
            available.set(false);
        }
    }

    // Event notification: thin event, consumer must call back for details.
    static final class ThinOrderPlaced {
        final String orderId;
        ThinOrderPlaced(String orderId) { this.orderId = orderId; }
    }

    // Event-carried state transfer: fat event, consumer needs nothing further.
    static final class FatOrderPlaced {
        final String orderId;
        final String shippingAddress;
        final double weightKg;
        FatOrderPlaced(String orderId, String shippingAddress, double weightKg) {
            this.orderId = orderId;
            this.shippingAddress = shippingAddress;
            this.weightKg = weightKg;
        }
    }

    public static void main(String[] args) {
        OrderService producer = new OrderService();

        System.out.println("=== Event notification (thin event) ===");
        ThinOrderPlaced thinEvent = new ThinOrderPlaced("order-42");
        System.out.println("Published: order=" + thinEvent.orderId + " (no details embedded)");

        System.out.println("Real simulated outage: OrderService.goDown()");
        producer.goDown();

        System.out.println("Shipping consumer now processes the thin event and calls back for details...");
        try {
            OrderDetails details = producer.fetchDetails(thinEvent.orderId);
            System.out.println("Got details: " + details.shippingAddress + ", " + details.weightKg + "kg");
        } catch (IllegalStateException e) {
            System.out.println("REAL FAILURE: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Event-carried state transfer (fat event) ===");
        OrderService producer2 = new OrderService();
        FatOrderPlaced fatEvent = new FatOrderPlaced("order-42", "12 Main St", 3.0);
        System.out.println("Published: order=" + fatEvent.orderId
                + " (details embedded: " + fatEvent.shippingAddress + ", " + fatEvent.weightKg + "kg)");

        System.out.println("Real simulated outage: OrderService.goDown()");
        producer2.goDown();

        System.out.println("Shipping consumer now processes the fat event -- no callback needed...");
        System.out.println("Got details: " + fatEvent.shippingAddress + ", " + fatEvent.weightKg
                + "kg (producer's real availability was never checked)");
        System.out.println("REAL SUCCESS -- producer being down did not matter.");

        System.out.println();
        System.out.println("=== What this proves and what it costs ===");
        System.out.println("Event notification: real runtime/availability coupling -- consumer depends on the");
        System.out.println("producer being reachable AT CONSUMPTION TIME, not just at publish time.");
        System.out.println("Event-carried state transfer: that coupling is gone, but replaced by real SCHEMA");
        System.out.println("coupling -- every consumer now depends on the producer's OrderDetails shape, and a");
        System.out.println("field the producer adds, renames, or removes must be a compatible change for every");
        System.out.println("consumer that embedded it, not just the ones that use it today.");
    }
}
