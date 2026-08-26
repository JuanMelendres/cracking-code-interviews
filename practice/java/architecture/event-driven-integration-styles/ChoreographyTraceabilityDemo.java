import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Real proof of choreography's debuggability cost. Four services -- Inventory,
 * Payment, Shipping, plus the initial publisher -- are wired only through a shared
 * {@link EventBus}: each reacts to the previous event and emits the next, with no
 * central coordinator. When the Shipping handler runs, this demo captures its REAL
 * {@code Thread.currentThread().getStackTrace()} and prints it verbatim -- proving,
 * rather than asserting, that the call stack contains no frame connecting back to the
 * original {@code OrderPlaced} publish call. The stack shows only "the event bus
 * dispatched me," never "why."
 */
public final class ChoreographyTraceabilityDemo {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        EventBus bus = new EventBus(executor);
        CountDownLatch done = new CountDownLatch(1);

        bus.subscribe(OrderPlaced.class, event -> {
            System.out.println("[Inventory] reserving stock for order=" + event.orderId);
            bus.publish(new InventoryReserved(event.orderId));
        });

        bus.subscribe(InventoryReserved.class, event -> {
            System.out.println("[Payment] charging card for order=" + event.orderId);
            bus.publish(new PaymentCharged(event.orderId));
        });

        bus.subscribe(PaymentCharged.class, event -> {
            System.out.println("[Shipping] shipping order=" + event.orderId);

            StackTraceElement[] realStack = Thread.currentThread().getStackTrace();
            System.out.println("[Shipping] REAL call stack at ship-time (" + realStack.length + " frames):");
            for (StackTraceElement frame : realStack) {
                System.out.println("    at " + frame);
            }

            boolean stackMentionsOrderPlaced = false;
            for (StackTraceElement frame : realStack) {
                if (frame.getMethodName().toLowerCase().contains("orderplaced")
                        || frame.getClassName().contains("OrderPlaced")) {
                    stackMentionsOrderPlaced = true;
                }
            }
            System.out.println("[Shipping] Does the real call stack reference the original OrderPlaced publish? "
                    + stackMentionsOrderPlaced);

            bus.publish(new OrderShipped(event.orderId));
            done.countDown();
        });

        System.out.println("=== Choreography: publishing OrderPlaced, no central coordinator ===");
        bus.publish(new OrderPlaced("order-42"));

        done.await(5, TimeUnit.SECONDS);
        executor.shutdown();
    }
}
