import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.StampedLock;

/**
 * Real, deterministic proof of StampedLock's optimistic-read protocol:
 * tryOptimisticRead() never blocks and never takes a lock -- it just hands
 * back a stamp. validate(stamp) tells you, after the fact, whether a writer
 * committed in the meantime. Part 1 forces a real concurrent write between
 * the optimistic read and its validation (via CountDownLatch, not sleep) and
 * shows validate() correctly returning false, requiring a fallback to a real
 * read lock. Part 2 measures the real throughput advantage of the optimistic
 * path when there is no contention.
 */
public class StampedLockOptimisticReadDemo {

    static class Point {
        volatile int x;
        volatile int y;
    }

    public static void main(String[] args) throws InterruptedException {
        optimisticReadInvalidatedByConcurrentWrite();
        System.out.println();
        optimisticReadThroughputVsReadLock();
    }

    static void optimisticReadInvalidatedByConcurrentWrite() throws InterruptedException {
        System.out.println("== Part 1: optimistic read invalidated by a real concurrent write ==");
        StampedLock sl = new StampedLock();
        Point p = new Point();
        p.x = 1;
        p.y = 2;

        CountDownLatch readerHasStamp = new CountDownLatch(1);
        CountDownLatch writerDone = new CountDownLatch(1);

        Thread writer = new Thread(() -> {
            try {
                readerHasStamp.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            long stamp = sl.writeLock();
            try {
                p.x = 100;
                p.y = 200;
            } finally {
                sl.unlockWrite(stamp);
            }
            writerDone.countDown();
        });
        writer.start();

        long stamp = sl.tryOptimisticRead();
        int localX = p.x;
        int localY = p.y;
        System.out.println("  optimistic read (before validation): x=" + localX + " y=" + localY);
        readerHasStamp.countDown();
        writerDone.await();

        boolean valid = sl.validate(stamp);
        System.out.println("  validate(stamp) after a real concurrent write committed: " + valid + " -- correctly detects the write");

        if (!valid) {
            long readStamp = sl.readLock();
            try {
                localX = p.x;
                localY = p.y;
            } finally {
                sl.unlockRead(readStamp);
            }
            System.out.println("  fell back to a real read lock, re-read: x=" + localX + " y=" + localY + " (the real, current values)");
        }
        writer.join();
    }

    static void optimisticReadThroughputVsReadLock() {
        System.out.println("== Part 2: real measured throughput, no contention ==");
        StampedLock sl = new StampedLock();
        Point p = new Point();
        p.x = 42;
        p.y = 7;
        int iterations = 20_000_000;

        long start = System.currentTimeMillis();
        long sinkOptimistic = 0;
        for (int i = 0; i < iterations; i++) {
            long stamp = sl.tryOptimisticRead();
            int x = p.x;
            int y = p.y;
            if (sl.validate(stamp)) {
                sinkOptimistic += x + y;
            }
        }
        long optimisticElapsed = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        long sinkReadLock = 0;
        for (int i = 0; i < iterations; i++) {
            long stamp = sl.readLock();
            try {
                sinkReadLock += p.x + p.y;
            } finally {
                sl.unlockRead(stamp);
            }
        }
        long readLockElapsed = System.currentTimeMillis() - start;

        System.out.println("  " + iterations + " iterations, no contention:");
        System.out.println("  tryOptimisticRead + validate: " + optimisticElapsed + "ms (sink=" + sinkOptimistic + ")");
        System.out.println("  readLock/unlockRead:          " + readLockElapsed + "ms (sink=" + sinkReadLock + ")");
    }
}
