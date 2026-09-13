import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The real asynchronous boundary between write and read sides. Runs on its own thread, drains
 * the event bus, and folds each event into the read model. This is the thing that makes the read
 * model eventually consistent rather than immediately consistent: there is a real queue and a
 * real thread hop between "write committed" and "read model updated".
 */
final class Projector implements Runnable {
    private final BlockingQueue<DomainEvent> eventBus;
    private final Map<Long, OrderSummaryView> readStore = new ConcurrentHashMap<>();
    private final AtomicLong eventsApplied = new AtomicLong();
    private volatile long artificialProcessingDelayNanos = 0;
    private volatile boolean running = true;

    Projector(BlockingQueue<DomainEvent> eventBus) {
        this.eventBus = eventBus;
    }

    /** Lets demos force a visible lag window deterministically instead of racing the scheduler. */
    void setArtificialProcessingDelayNanos(long nanos) {
        this.artificialProcessingDelayNanos = nanos;
    }

    Map<Long, OrderSummaryView> readStore() {
        return readStore;
    }

    long eventsApplied() {
        return eventsApplied.get();
    }

    void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                DomainEvent event = eventBus.poll(200, java.util.concurrent.TimeUnit.MILLISECONDS);
                if (event == null) {
                    continue;
                }
                if (artificialProcessingDelayNanos > 0) {
                    busyWaitNanos(artificialProcessingDelayNanos);
                }
                apply(event);
                eventsApplied.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void apply(DomainEvent event) {
        if (event instanceof OrderCreated created) {
            OrderSummaryView view = new OrderSummaryView(created.orderId(), created.customerId());
            view.lastUpdatedAtNanos = System.nanoTime();
            readStore.put(created.orderId(), view);
        } else if (event instanceof ItemAdded added) {
            OrderSummaryView view = readStore.get(added.orderId());
            if (view != null) {
                view.totalItemCount += added.quantity();
                view.totalAmount = view.totalAmount.add(
                        added.unitPrice().multiply(java.math.BigDecimal.valueOf(added.quantity())));
                view.lastUpdatedAtNanos = System.nanoTime();
            }
        } else if (event instanceof OrderCompleted completed) {
            OrderSummaryView view = readStore.get(completed.orderId());
            if (view != null) {
                view.status = "COMPLETED";
                view.lastUpdatedAtNanos = System.nanoTime();
            }
        }
    }

    private static void busyWaitNanos(long nanos) {
        long deadline = System.nanoTime() + nanos;
        while (System.nanoTime() < deadline) {
            // deliberate: Thread.sleep granularity is too coarse to demonstrate sub-millisecond lag
        }
    }
}
