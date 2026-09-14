import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Real measurement of write-to-read-visible lag with no network involved at all: same JVM, same
 * machine. The lag is not simulated — it is the real cost of the async handoff (BlockingQueue
 * put/poll) plus a real thread context switch between the command thread and the projector
 * thread. This is the honest floor of "eventual consistency": even the best case is not zero.
 */
public class EventualConsistencyLagDemo {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<DomainEvent> eventBus = new ArrayBlockingQueue<>(10_000);
        OrderCommandService commandService = new OrderCommandService(eventBus);
        Projector projector = new Projector(eventBus);
        Thread projectorThread = new Thread(projector, "projector");
        projectorThread.start();

        int sampleCount = 5_000;
        long[] lagNanos = new long[sampleCount];

        for (int i = 0; i < sampleCount; i++) {
            long orderId = commandService.createOrder("customer-" + i);
            long writeCommittedAt = System.nanoTime();

            OrderSummaryView view;
            while ((view = projector.readStore().get(orderId)) == null) {
                Thread.onSpinWait();
            }
            long readVisibleAt = view.lastUpdatedAtNanos;
            lagNanos[i] = Math.max(0, readVisibleAt - writeCommittedAt);
        }

        projector.stop();
        projectorThread.join();

        Arrays.sort(lagNanos);
        double minMicros = lagNanos[0] / 1000.0;
        double p50Micros = lagNanos[sampleCount / 2] / 1000.0;
        double p99Micros = lagNanos[(int) (sampleCount * 0.99)] / 1000.0;
        double maxMicros = lagNanos[sampleCount - 1] / 1000.0;
        double avgMicros = Arrays.stream(lagNanos).average().orElse(0) / 1000.0;

        System.out.printf("Samples: %d%n", sampleCount);
        System.out.printf("min=%.1fus  avg=%.1fus  p50=%.1fus  p99=%.1fus  max=%.1fus%n",
                minMicros, avgMicros, p50Micros, p99Micros, maxMicros);
        System.out.printf("Events applied by projector: %d (expected %d)%n",
                projector.eventsApplied(), sampleCount);
        System.out.println();
        System.out.println("This is real, in-process, no-network lag. It exists purely because the write");
        System.out.println("commits synchronously on the caller's thread while the read model update happens");
        System.out.println("asynchronously on the projector's thread, mediated by a queue. A caller that reads");
        System.out.println("its own write from the read model immediately after committing can, and will,");
        System.out.println("sometimes observe a stale or missing result.");
    }
}
