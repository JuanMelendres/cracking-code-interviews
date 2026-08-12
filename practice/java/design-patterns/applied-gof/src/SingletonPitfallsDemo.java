// Java 21. Singleton: guarantee exactly one instance of a class exists. This
// demo measures the classic pitfall directly -- a naive, unsynchronized lazy
// singleton is NOT thread-safe, and under real concurrent first access, more
// than one instance genuinely gets constructed. A tiny artificial delay is
// added inside the constructor purely to widen the race window enough to
// reproduce this reliably on a fast, single-run demo; the underlying bug is
// real and timing-dependent without it, exactly like this handbook's Java
// Memory Model visibility demos widen their own races the same way.

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

class NaiveLazySingleton {
    private static NaiveLazySingleton instance; // no `volatile`, no synchronization at all
    static final AtomicInteger constructedCount = new AtomicInteger(0);
    private final int id;

    private NaiveLazySingleton() {
        try {
            Thread.sleep(2); // widen the race window so the bug reproduces reliably in this demo
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        id = constructedCount.incrementAndGet();
    }

    static NaiveLazySingleton getInstance() {
        if (instance == null) {          // many threads can pass this check concurrently
            instance = new NaiveLazySingleton(); // -- and each one constructs its own object
        }
        return instance;
    }

    int id() { return id; }

    static void reset() { instance = null; constructedCount.set(0); } // test-only reset between runs
}

// A plain holder class, deliberately NOT a field of SafeSingleton itself: an
// enum constant is constructed before any of its own class's other static
// fields are initialized, so referencing a same-class static field from
// inside the constant's constructor is an illegal forward reference (JLS
// 8.3.3) even when qualified. Counting construction from a separate class
// sidesteps that restriction entirely.
final class SafeSingletonCounter {
    static final AtomicInteger constructedCount = new AtomicInteger(0);
}

enum SafeSingleton {
    INSTANCE;

    private final int id;

    SafeSingleton() {
        try {
            Thread.sleep(2); // same artificial delay -- proves the fix, not just a faster race
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        id = SafeSingletonCounter.constructedCount.incrementAndGet();
    }

    int id() { return id; }
}

class SingletonPitfallsDemo {

    private static int runConcurrentAccessTest(int threadCount, java.util.function.IntSupplier idSupplier) throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);
        Set<Integer> observedIds = Collections.synchronizedSet(new HashSet<>());

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                ready.countDown();
                try {
                    start.await(); // all threads release at (approximately) the same instant
                    observedIds.add(idSupplier.getAsInt());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }).start();
        }

        ready.await();
        start.countDown(); // fire every thread's first getInstance() call at once
        done.await();
        return observedIds.size();
    }

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 30;

        System.out.println("== Naive lazy singleton, " + threadCount + " threads racing on the FIRST call to getInstance() ==");
        NaiveLazySingleton.reset();
        int distinctIds = runConcurrentAccessTest(threadCount, () -> NaiveLazySingleton.getInstance().id());
        System.out.println("Distinct instance ids observed across all threads: " + distinctIds);
        System.out.println("Total NaiveLazySingleton objects actually constructed: " + NaiveLazySingleton.constructedCount.get());
        System.out.println("RESULT: " + (NaiveLazySingleton.constructedCount.get() > 1
            ? "CONFIRMED -- more than one instance was constructed under real concurrent first access."
            : "not reproduced this run (a genuine race -- see the chapter's retrospective on why this is inherently timing-dependent)."));

        System.out.println();
        System.out.println("== Enum-based singleton, same " + threadCount + "-thread race ==");
        int distinctSafeIds = runConcurrentAccessTest(threadCount, () -> SafeSingleton.INSTANCE.id());
        System.out.println("Distinct instance ids observed across all threads: " + distinctSafeIds);
        System.out.println("Total SafeSingleton objects actually constructed: " + SafeSingletonCounter.constructedCount.get());
        System.out.println("RESULT: " + (SafeSingletonCounter.constructedCount.get() == 1
            ? "CONFIRMED -- exactly one instance constructed, guaranteed by the JLS's class-initialization lock for enums."
            : "UNEXPECTED -- this should never happen for an enum singleton."));
    }
}
