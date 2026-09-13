import java.util.concurrent.atomic.AtomicInteger;

/**
 * T-409 -- a race condition, measured, not asserted: `count++` is a
 * read-modify-write, not atomic. 10 threads each incrementing a shared
 * counter 100,000 times should reach 1,000,000. The plain int version
 * reliably falls short (lost updates); AtomicInteger reliably hits it.
 */
public class RaceConditionDemo {
    static int plainCount = 0;
    static AtomicInteger atomicCount = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        int threads = 10;
        int incrementsPerThread = 100_000;
        int expected = threads * incrementsPerThread;

        System.out.println("== plain int, unsynchronized ++ ==");
        runPlain(threads, incrementsPerThread);
        System.out.printf("expected=%d actual=%d lost=%d%n", expected, plainCount, expected - plainCount);

        System.out.println();
        System.out.println("== AtomicInteger.incrementAndGet() ==");
        runAtomic(threads, incrementsPerThread);
        System.out.printf("expected=%d actual=%d lost=%d%n", expected, atomicCount.get(), expected - atomicCount.get());
    }

    static void runPlain(int threads, int increments) throws InterruptedException {
        Thread[] pool = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            pool[i] = new Thread(() -> {
                for (int j = 0; j < increments; j++) plainCount++;
            });
        }
        for (Thread t : pool) t.start();
        for (Thread t : pool) t.join();
    }

    static void runAtomic(int threads, int increments) throws InterruptedException {
        Thread[] pool = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            pool[i] = new Thread(() -> {
                for (int j = 0; j < increments; j++) atomicCount.incrementAndGet();
            });
        }
        for (Thread t : pool) t.start();
        for (Thread t : pool) t.join();
    }
}
