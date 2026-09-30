import java.util.concurrent.atomic.AtomicInteger;

/**
 * Part 2: the same counter, four ways. The numbers are the whole lesson --
 * particularly the volatile one, which is the single most common misconception
 * about Java concurrency.
 */
final class RaceConditionDemo {

    static final int THREADS = 8;
    static final int INCREMENTS = 100_000;
    static final int EXPECTED = THREADS * INCREMENTS;

    static int plainCounter;
    static volatile int volatileCounter;
    static int synchronizedCounter;
    static final AtomicInteger ATOMIC_COUNTER = new AtomicInteger();
    static final Object LOCK = new Object();

    static void runAll(Runnable increment) throws InterruptedException {
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int n = 0; n < INCREMENTS; n++) {
                    increment.run();
                }
            });
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }

    static void report(String label, int actual, String note) {
        int lost = EXPECTED - actual;
        System.out.printf("  %-34s %,9d / %,9d   lost %,7d  %s%n",
                label, actual, EXPECTED, lost, note);
    }

    static void run() throws InterruptedException {
        System.out.println("== 4. A race condition, measured ==");
        System.out.printf("  %d threads x %,d increments = %,d expected%n%n", THREADS, INCREMENTS, EXPECTED);

        plainCounter = 0;
        runAll(() -> plainCounter++);
        report("plain int, counter++", plainCounter, "<- UPDATES LOST");

        volatileCounter = 0;
        runAll(() -> volatileCounter++);
        report("volatile int, counter++", volatileCounter, "<- STILL LOST");

        synchronizedCounter = 0;
        runAll(() -> {
            synchronized (LOCK) {
                synchronizedCounter++;
            }
        });
        report("synchronized block", synchronizedCounter, "correct");

        ATOMIC_COUNTER.set(0);
        runAll(ATOMIC_COUNTER::incrementAndGet);
        report("AtomicInteger.incrementAndGet", ATOMIC_COUNTER.get(), "correct");

        System.out.println();
        System.out.println("  Why volatile does not help here: `counter++` is three operations --");
        System.out.println("  read, add one, write back. volatile guarantees each read sees the");
        System.out.println("  latest value; it does NOT stop another thread from interleaving");
        System.out.println("  between the read and the write. That gap is the lost update.");
        System.out.println();
    }
}
