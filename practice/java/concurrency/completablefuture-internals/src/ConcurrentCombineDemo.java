import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Real, measured proof of why thenCombine (fully concurrent) beats calling
 * get() sequentially on two independent Futures (accidentally serialized),
 * even though both approaches "use CompletableFuture."
 */
public class ConcurrentCombineDemo {

    public static void main(String[] args) throws Exception {
        long sequential = sequentialGetBlocksEachCallInTurn();
        long combined = thenCombineRunsBothConcurrently();

        System.out.println("\n== Real measured wall-clock time ==");
        System.out.println("Sequential .get()/.get(): " + sequential + "ms (two ~300ms calls, one after another)");
        System.out.println("thenCombine():            " + combined + "ms (two ~300ms calls, running concurrently)");
    }

    static String slowRemoteCall(String label, long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return label + "-result";
    }

    // The real, common mistake: call B isn't even SUBMITTED until call A's
    // get() has already returned, so the two ~300ms calls run one after the
    // other instead of concurrently -- a sequential dependency that was
    // never actually required by the data.
    static long sequentialGetBlocksEachCallInTurn() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        CompletableFuture<String> callA = CompletableFuture.supplyAsync(() -> slowRemoteCall("A", 300));
        String resultA = callA.get(); // blocks here; call B hasn't started yet

        CompletableFuture<String> callB = CompletableFuture.supplyAsync(() -> slowRemoteCall("B", 300));
        String resultB = callB.get();

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Sequential result: " + resultA + " + " + resultB + " (elapsed=" + elapsed + "ms)");
        return elapsed;
    }

    static long thenCombineRunsBothConcurrently() {
        long start = System.currentTimeMillis();

        CompletableFuture<String> callA = CompletableFuture.supplyAsync(() -> slowRemoteCall("A", 300));
        CompletableFuture<String> callB = CompletableFuture.supplyAsync(() -> slowRemoteCall("B", 300));

        CompletableFuture<String> combined = callA.thenCombine(callB, (a, b) -> a + " + " + b);
        String result = combined.join();

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("thenCombine result: " + result + " (elapsed=" + elapsed + "ms)");
        return elapsed;
    }
}
