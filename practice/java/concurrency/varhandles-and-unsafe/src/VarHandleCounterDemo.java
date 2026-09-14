import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;

// Real proof that a VarHandle over a PLAIN int field achieves the identical
// race-free correctness guarantee as an AtomicInteger wrapper object -- with
// no extra heap object needed. Both counters run the identical concurrent
// workload; a correct result requires real, working atomic CAS underneath
// both.
public class VarHandleCounterDemo {

    private volatile int plainCounter; // the field the VarHandle operates on directly
    private static final VarHandle COUNTER_HANDLE;

    static {
        try {
            COUNTER_HANDLE = MethodHandles.lookup()
                    .findVarHandle(VarHandleCounterDemo.class, "plainCounter", int.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final AtomicInteger atomicCounter = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        int threads = 8;
        int incrementsPerThread = 100_000;
        int expected = threads * incrementsPerThread;

        VarHandleCounterDemo demo = new VarHandleCounterDemo();
        CountDownLatch latch = new CountDownLatch(threads);

        System.out.println("=== " + threads + " real threads, " + incrementsPerThread
                + " increments each, racing on both counters concurrently ===");

        for (int t = 0; t < threads; t++) {
            Thread worker = new Thread(() -> {
                for (int i = 0; i < incrementsPerThread; i++) {
                    demo.atomicCounter.incrementAndGet();
                    // The real VarHandle equivalent of AtomicInteger.incrementAndGet() --
                    // a real CAS retry loop over the plain int field, no wrapper object.
                    int current;
                    do {
                        current = (int) COUNTER_HANDLE.getVolatile(demo);
                    } while (!COUNTER_HANDLE.compareAndSet(demo, current, current + 1));
                }
                latch.countDown();
            });
            worker.start();
        }
        latch.await();

        System.out.println("Expected final count: " + expected);
        System.out.println("Real AtomicInteger result: " + demo.atomicCounter.get()
                + (demo.atomicCounter.get() == expected ? "  (correct)" : "  (WRONG -- a real race survived)"));
        System.out.println("Real VarHandle result:     " + demo.plainCounter
                + (demo.plainCounter == expected ? "  (correct)" : "  (WRONG -- a real race survived)"));
    }
}
