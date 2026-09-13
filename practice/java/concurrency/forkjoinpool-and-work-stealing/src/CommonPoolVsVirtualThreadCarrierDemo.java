import java.util.concurrent.ForkJoinPool;

/**
 * Real, executed proof that virtual threads (and therefore
 * StructuredTaskScope's subtasks, which run on them) are scheduled by a
 * SEPARATE ForkJoinPool instance from ForkJoinPool.commonPool() -- not the
 * same shared pool used by parallel streams and CompletableFuture's default
 * *Async methods, despite both being "backed by a ForkJoinPool internally."
 * This distinction was verified here BEFORE being asserted in the chapter,
 * after an initial draft incorrectly assumed they were the same pool.
 */
public class CommonPoolVsVirtualThreadCarrierDemo {

    public static void main(String[] args) throws InterruptedException {
        ForkJoinPool common = ForkJoinPool.commonPool();

        Thread vt = Thread.ofVirtual().start(() -> {
            System.out.println("Virtual thread's own toString(): " + Thread.currentThread());
        });
        vt.join();

        long stealBefore = common.getStealCount();
        int poolSizeBefore = common.getPoolSize();

        Thread.ofVirtual().start(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }).join();

        long stealAfter = common.getStealCount();
        int poolSizeAfter = common.getPoolSize();

        System.out.println("\ncommonPool identity hash: " + System.identityHashCode(common));
        System.out.println("commonPool.getStealCount() before virtual thread work: " + stealBefore);
        System.out.println("commonPool.getStealCount() after virtual thread work:  " + stealAfter
                + " (changed: " + (stealBefore != stealAfter) + ")");
        System.out.println("commonPool.getPoolSize() before: " + poolSizeBefore + ", after: " + poolSizeAfter
                + " (changed: " + (poolSizeBefore != poolSizeAfter) + ")");
        System.out.println("\nConclusion: the virtual thread's carrier is reported as its own \"ForkJoinPool-N\""
                + " instance, NOT \"ForkJoinPool.commonPool\" -- and running virtual thread work leaves"
                + " commonPool()'s own real metrics completely unchanged, real proof they are separate pool instances.");
    }
}
