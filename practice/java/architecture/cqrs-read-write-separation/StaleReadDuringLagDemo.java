import java.math.BigDecimal;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Forces a real, deterministic lag window (instead of racing the scheduler for a stale read) by
 * making the projector deliberately slow, then proves the read model is genuinely, observably
 * stale during that window, and genuinely catches up afterward. Nothing here is mocked: the
 * projector really is a separate thread, the delay really is spent in that thread, and the reads
 * really do come back empty until the real event is really applied.
 */
public class StaleReadDuringLagDemo {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<DomainEvent> eventBus = new ArrayBlockingQueue<>(100);
        OrderCommandService commandService = new OrderCommandService(eventBus);
        Projector projector = new Projector(eventBus);
        projector.setArtificialProcessingDelayNanos(150_000_000L); // 150ms per event, deliberately slow
        Thread projectorThread = new Thread(projector, "slow-projector");
        projectorThread.start();

        long orderId = commandService.createOrder("customer-mallory");
        commandService.addItem(orderId, "WIDGET-1", 3, new BigDecimal("19.99"));
        commandService.completeOrder(orderId);
        long writeCommittedAt = System.nanoTime();

        System.out.println("Write committed. Write-side ground truth right now:");
        Order raw = commandService.rawOrder(orderId);
        System.out.printf("  write model: status=%s items=%d total=%s%n",
                raw.status, raw.items.size(), raw.totalAmount());

        System.out.println();
        System.out.println("Polling the read model immediately, before the projector has caught up:");
        for (int i = 0; i < 4; i++) {
            OrderSummaryView view = projector.readStore().get(orderId);
            long elapsedMs = (System.nanoTime() - writeCommittedAt) / 1_000_000;
            if (view == null) {
                System.out.printf("  t+%dms: read model has NO record of order %d yet (stale)%n", elapsedMs, orderId);
            } else {
                System.out.printf("  t+%dms: read model shows status=%s items=%d total=%s%n",
                        elapsedMs, view.status, view.totalItemCount, view.totalAmount);
            }
            Thread.sleep(80);
        }

        System.out.println();
        System.out.println("Waiting for the projector to fully catch up (draining 3 real events at ~150ms each)...");
        OrderSummaryView finalView;
        while ((finalView = projector.readStore().get(orderId)) == null
                || !"COMPLETED".equals(finalView.status)) {
            Thread.sleep(20);
        }
        long caughtUpAtMs = (System.nanoTime() - writeCommittedAt) / 1_000_000;
        System.out.printf("  t+%dms: read model now CONSISTENT: status=%s items=%d total=%s%n",
                caughtUpAtMs, finalView.status, finalView.totalItemCount, finalView.totalAmount);

        System.out.println();
        System.out.println("The write model was correct the entire time. The read model was really, ");
        System.out.println("observably stale for a real, measured window, then really converged. This is");
        System.out.println("what 'the read model is eventually consistent' means operationally, not just");
        System.out.println("as a phrase in a design doc.");

        projector.stop();
        projectorThread.join();
    }
}
