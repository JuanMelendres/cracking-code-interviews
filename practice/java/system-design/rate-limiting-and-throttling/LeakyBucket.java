import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Real leaky-bucket limiter, implemented as-a-queue (the common production shape):
 * requests that fit under capacity are enqueued and drained by a background thread
 * at a fixed rate, producing a smoothed, constant-rate outflow regardless of how
 * bursty the inflow was. Requests that don't fit are rejected immediately (leaky
 * bucket as a meter, not the alternative "leaky bucket as a queue with blocking").
 */
final class LeakyBucket {

    private final int capacity;
    private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
    private volatile boolean running = true;
    private final Thread leakThread;

    LeakyBucket(int capacity, double leakPerSecond) {
        this.capacity = capacity;
        long intervalNanos = (long) (1_000_000_000.0 / leakPerSecond);
        this.leakThread = new Thread(() -> {
            while (running) {
                Runnable task = queue.poll();
                if (task != null) {
                    task.run();
                }
                try {
                    Thread.sleep(intervalNanos / 1_000_000, (int) (intervalNanos % 1_000_000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
        this.leakThread.setDaemon(true);
        this.leakThread.start();
    }

    boolean offer(Runnable onProcessed) {
        if (queue.size() >= capacity) {
            return false;
        }
        queue.add(onProcessed);
        return true;
    }

    int queued() {
        return queue.size();
    }

    void shutdown() {
        running = false;
        leakThread.interrupt();
    }
}
