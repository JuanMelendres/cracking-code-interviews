import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Genuine async work -- a real background thread, a real variable delay,
 * not a fixed, predictable one. Delay ranges 5-60ms, deliberately wide
 * enough that a fixed short sleep in a test will sometimes lose the race. */
public class AsyncWorker {
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Random random = new Random();

    public void runAsync(Runnable onComplete) {
        executor.submit(() -> {
            try {
                Thread.sleep(5 + random.nextInt(56)); // 5-60ms, real variable latency
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            onComplete.run();
        });
    }
}
