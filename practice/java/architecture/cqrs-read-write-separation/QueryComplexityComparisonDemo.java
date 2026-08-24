import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Real, timed comparison of the actual reason CQRS's read model earns its keep: answering a
 * read-heavy query by walking the normalized write model (every order, every item) versus
 * answering the same query from a precomputed, denormalized read model. Both computations
 * produce the identical answer over the identical data; only the cost differs, and the cost
 * difference is really measured, not asserted.
 */
public class QueryComplexityComparisonDemo {
    public static void main(String[] args) throws InterruptedException {
        int orderCount = 50_000;
        int itemsPerOrder = 4;

        BlockingQueue<DomainEvent> eventBus = new ArrayBlockingQueue<>(1_000_000);
        OrderCommandService commandService = new OrderCommandService(eventBus);
        Projector projector = new Projector(eventBus);
        Thread projectorThread = new Thread(projector, "projector");
        projectorThread.start();

        String[] customers = {"cust-A", "cust-B", "cust-C", "cust-D", "cust-E"};
        for (int i = 0; i < orderCount; i++) {
            long orderId = commandService.createOrder(customers[i % customers.length]);
            for (int j = 0; j < itemsPerOrder; j++) {
                commandService.addItem(orderId, "SKU-" + j, 1 + (j % 3), new BigDecimal("9.99"));
            }
            commandService.completeOrder(orderId);
        }

        // Let the read model fully catch up before timing queries — this demo measures query cost,
        // not projection lag (that is EventualConsistencyLagDemo's job).
        int totalEvents = orderCount * (2 + itemsPerOrder);
        while (projector.eventsApplied() < totalEvents) {
            Thread.sleep(10);
        }

        // Query: total spend per customer.

        // (a) Write-side path: walk every order, walk every item inside it, sum.
        long startWrite = System.nanoTime();
        Map<String, BigDecimal> writeSideTotals = new HashMap<>();
        for (Order order : commandService.allOrders().values()) {
            writeSideTotals.merge(order.customerId, order.totalAmount(), BigDecimal::add);
        }
        long writeSideNanos = System.nanoTime() - startWrite;

        // (b) Read-side path: the total is already sitting on each precomputed summary row.
        long startRead = System.nanoTime();
        Map<String, BigDecimal> readSideTotals = new HashMap<>();
        for (OrderSummaryView view : projector.readStore().values()) {
            readSideTotals.merge(view.customerId, view.totalAmount, BigDecimal::add);
        }
        long readSideNanos = System.nanoTime() - startRead;

        boolean identical = writeSideTotals.equals(readSideTotals);

        System.out.printf("Orders: %d, items/order: %d, total events: %d%n", orderCount, itemsPerOrder, totalEvents);
        System.out.printf("Write-side path (walk orders + items, sum per query): %.2fms%n", writeSideNanos / 1_000_000.0);
        System.out.printf("Read-side path (sum precomputed per-order totals):    %.2fms%n", readSideNanos / 1_000_000.0);
        System.out.printf("Speedup: %.1fx%n", (double) writeSideNanos / readSideNanos);
        System.out.printf("Results identical: %s%n", identical);
        writeSideTotals.forEach((customer, total) -> System.out.printf("  %s: %s%n", customer, total));

        System.out.println();
        System.out.println("Both paths produce the exact same numbers off the exact same underlying writes.");
        System.out.println("The read model isn't more 'correct' — it's the same truth, pre-shaped for the");
        System.out.println("query it exists to answer, so answering it doesn't require re-deriving it from");
        System.out.println("the write model's normalized structure every time.");

        projector.stop();
        projectorThread.join();
    }
}
