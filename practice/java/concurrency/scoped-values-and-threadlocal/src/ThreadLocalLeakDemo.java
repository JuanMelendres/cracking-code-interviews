import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Real, executed proof of the classic ThreadLocal thread-pool-reuse leak:
 * a value set on a pooled thread and never explicitly removed is genuinely
 * still there for the NEXT, unrelated task that happens to reuse that same
 * physical thread. Contrasted with ScopedValue, which is structurally
 * immune to this class of bug -- its binding's dynamic extent ends when
 * run() returns, with no remove() step to forget.
 */
public class ThreadLocalLeakDemo {

    static final ThreadLocal<String> USER_CONTEXT = new ThreadLocal<>();
    static final ScopedValue<String> SCOPED_USER_CONTEXT = ScopedValue.newInstance();

    public static void main(String[] args) throws Exception {
        System.out.println("== Real ThreadLocal leak across pooled-thread reuse (single-thread pool, forces reuse) ==");
        ExecutorService pool = Executors.newFixedThreadPool(1);

        pool.submit(() -> {
            USER_CONTEXT.set("user-A");
            System.out.println("Task 1 (thread=" + Thread.currentThread().getName() + "): set USER_CONTEXT=user-A, forgot to remove() it");
            // Deliberately NOT calling USER_CONTEXT.remove() -- the real, common bug.
        }).get();

        pool.submit(() -> {
            String leaked = USER_CONTEXT.get();
            System.out.println("Task 2 (thread=" + Thread.currentThread().getName() + "), UNRELATED task, never set its own context: "
                    + "USER_CONTEXT.get() = " + leaked
                    + (leaked != null ? "  <-- REAL LEAK: this is Task 1's stale value, on a reused pooled thread" : ""));
        }).get();

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("\n== ScopedValue: structurally immune to this class of bug ==");
        ExecutorService scopedPool = Executors.newFixedThreadPool(1);

        scopedPool.submit(() -> {
            ScopedValue.where(SCOPED_USER_CONTEXT, "user-A").run(() -> {
                System.out.println("Task 1 (thread=" + Thread.currentThread().getName()
                        + "): bound SCOPED_USER_CONTEXT=user-A for the dynamic extent of this run() only");
            });
            // No remove() call exists to forget -- the binding already ended when run() returned above.
        }).get();

        scopedPool.submit(() -> {
            boolean bound = SCOPED_USER_CONTEXT.isBound();
            System.out.println("Task 2 (thread=" + Thread.currentThread().getName() + "), UNRELATED task, on the SAME reused pooled thread: "
                    + "SCOPED_USER_CONTEXT.isBound() = " + bound
                    + (!bound ? "  <-- REAL: no leak, no stale value, nothing to forget to clean up" : ""));
        }).get();

        scopedPool.shutdown();
        scopedPool.awaitTermination(5, TimeUnit.SECONDS);
    }
}
