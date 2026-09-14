import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Real, measured proof that ReadWriteLock lets multiple readers hold the
 * lock CONCURRENTLY, while a plain ReentrantLock serializes every acquirer
 * -- reader or not. Four "readers" each hold their lock for ~150ms; under a
 * real ReadWriteLock this costs ~150ms total (they overlap); under a plain
 * ReentrantLock it costs ~600ms (they queue).
 */
public class ReadWriteLockConcurrencyDemo {

    static final int READERS = 4;
    static final long HOLD_MILLIS = 150;

    public static void main(String[] args) throws InterruptedException {
        long rwElapsed = runWithReadWriteLock();
        long exclusiveElapsed = runWithPlainReentrantLock();

        System.out.println("\n== Real measured wall-clock time, " + READERS + " readers x " + HOLD_MILLIS + "ms hold each ==");
        System.out.println("ReadWriteLock (concurrent reads):     " + rwElapsed + "ms");
        System.out.println("Plain ReentrantLock (serialized):     " + exclusiveElapsed + "ms");
    }

    static long runWithReadWriteLock() throws InterruptedException {
        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        Thread[] threads = new Thread[READERS];
        long start = System.currentTimeMillis();
        for (int i = 0; i < READERS; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                rwLock.readLock().lock();
                try {
                    long enter = System.currentTimeMillis() - start;
                    sleep(HOLD_MILLIS);
                    long exit = System.currentTimeMillis() - start;
                    System.out.println("  reader-" + id + " held read lock from t=" + enter + "ms to t=" + exit + "ms");
                } finally {
                    rwLock.readLock().unlock();
                }
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("ReadWriteLock total elapsed: " + elapsed + "ms (readers overlapped)");
        return elapsed;
    }

    static long runWithPlainReentrantLock() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Thread[] threads = new Thread[READERS];
        long start = System.currentTimeMillis();
        for (int i = 0; i < READERS; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                lock.lock();
                try {
                    long enter = System.currentTimeMillis() - start;
                    sleep(HOLD_MILLIS);
                    long exit = System.currentTimeMillis() - start;
                    System.out.println("  reader-" + id + " held exclusive lock from t=" + enter + "ms to t=" + exit + "ms");
                } finally {
                    lock.unlock();
                }
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Plain ReentrantLock total elapsed: " + elapsed + "ms (readers serialized)");
        return elapsed;
    }

    static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
