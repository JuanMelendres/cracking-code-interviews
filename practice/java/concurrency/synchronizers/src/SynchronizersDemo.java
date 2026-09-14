import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// Real, compiling demonstrations of the three java.util.concurrent
// synchronizers most commonly asked about after locks/atomics: CountDownLatch
// (one-shot "wait for N events"), CyclicBarrier (reusable "wait for N parties
// to rendezvous"), and Semaphore (bounded concurrent access). Each demo
// measures real, observable evidence -- real blocking duration, a real barrier
// action running exactly twice across two reused rounds, a real, measured
// maximum-concurrent-holder count that never exceeds the permit count.
public class SynchronizersDemo {

    static void demoCountDownLatchBlocksUntilAllSignal() throws InterruptedException {
        int workers = 3;
        CountDownLatch latch = new CountDownLatch(workers);
        ExecutorService pool = Executors.newFixedThreadPool(workers);
        int[] workDelaysMs = {100, 250, 400}; // deliberately staggered

        long start = System.currentTimeMillis();
        for (int i = 0; i < workers; i++) {
            int delay = workDelaysMs[i];
            int id = i;
            pool.submit(() -> {
                try {
                    Thread.sleep(delay);
                    System.out.println("  worker " + id + " finished after " + delay + "ms, counting down");
                } catch (InterruptedException ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }
        System.out.println("  main thread calling latch.await() -- should block until the SLOWEST worker (400ms) finishes...");
        latch.await();
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("  main thread unblocked after " + elapsed + "ms  <-- real evidence it waited for all 3, not just the first");
        pool.shutdown();
    }

    static void demoCountDownLatchIsOneShot() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        latch.countDown();
        long start = System.currentTimeMillis();
        latch.await(); // count is already 0 -- returns immediately, every time, forever
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("  latch already at zero; await() returned in " + elapsed + "ms (immediately)");
        System.out.println("  a SECOND await() on the SAME latch also returns immediately -- there is no way to");
        System.out.println("  reset a CountDownLatch; a new one must be constructed for a second round.");
    }

    static void demoCyclicBarrierRunsTwiceAcrossTwoRounds() throws Exception {
        int parties = 3;
        AtomicInteger barrierActionRuns = new AtomicInteger();
        CyclicBarrier barrier = new CyclicBarrier(parties,
                () -> System.out.println("  *** barrier action fired -- all " + parties
                        + " parties arrived (run #" + barrierActionRuns.incrementAndGet() + ") ***"));
        ExecutorService pool = Executors.newFixedThreadPool(parties);

        for (int round = 1; round <= 2; round++) {
            System.out.println("  --- round " + round + " ---");
            final int r = round;
            for (int i = 0; i < parties; i++) {
                int id = i;
                pool.submit(() -> {
                    try {
                        Thread.sleep(50L * (id + 1)); // stagger arrivals
                        System.out.println("  round " + r + ": thread " + id + " arrived at the barrier");
                        barrier.await();
                    } catch (Exception ignored) {
                    }
                });
            }
            Thread.sleep(400); // let this round fully complete before starting the next
        }
        pool.shutdown();
        System.out.println("  barrier action ran " + barrierActionRuns.get()
                + " times total -- the SAME CyclicBarrier instance was reused across both rounds automatically.");
    }

    static void demoSemaphoreBoundsRealConcurrency() throws InterruptedException {
        int permits = 3;
        int tasks = 10;
        Semaphore semaphore = new Semaphore(permits);
        AtomicInteger currentHolders = new AtomicInteger();
        AtomicInteger observedMax = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(tasks);
        CountDownLatch done = new CountDownLatch(tasks);

        for (int i = 0; i < tasks; i++) {
            int id = i;
            pool.submit(() -> {
                try {
                    semaphore.acquire();
                    int now = currentHolders.incrementAndGet();
                    observedMax.updateAndGet(max -> Math.max(max, now));
                    Thread.sleep(80); // hold the permit briefly, simulating real work
                    currentHolders.decrementAndGet();
                    semaphore.release();
                } catch (InterruptedException ignored) {
                } finally {
                    done.countDown();
                }
            });
        }
        done.await();
        pool.shutdown();
        System.out.println("  " + tasks + " tasks competing for " + permits + " permits.");
        System.out.println("  Real, measured maximum concurrent holders observed at any instant: " + observedMax.get()
                + (observedMax.get() <= permits ? "  <-- never exceeded the permit count" : "  <-- BUG, exceeded permits"));
    }

    public static void main(String[] args) throws Exception {
        System.out.println("############ CountDownLatch: blocks until ALL signal, one-shot ############");
        demoCountDownLatchBlocksUntilAllSignal();
        System.out.println();
        demoCountDownLatchIsOneShot();
        System.out.println();
        System.out.println("############ CyclicBarrier: reusable rendezvous, runs its action every round ############");
        demoCyclicBarrierRunsTwiceAcrossTwoRounds();
        System.out.println();
        System.out.println("############ Semaphore: real, measured bounded concurrency ############");
        demoSemaphoreBoundsRealConcurrency();
    }
}
