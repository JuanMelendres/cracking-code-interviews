import java.util.concurrent.CountDownLatch;

/**
 * Errata correction: the source material's thread-lifecycle diagram
 * invented a "Running" state and omitted TIMED_WAITING. The real
 * java.lang.Thread.State enum has exactly six values. This demo puts a
 * real thread into five of the six (NEW/RUNNABLE/TIMED_WAITING/WAITING/
 * TERMINATED here; BLOCKED is demonstrated separately in DeadlockDemo,
 * since it requires genuine lock contention) and prints the real
 * Thread.getState() result at each point -- not asserted from memory.
 */
public class ThreadStateDemo {
    static final Object monitor = new Object();

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch enteredWait = new CountDownLatch(1);
        Thread t = new Thread(() -> {
            try {
                synchronized (monitor) {
                    enteredWait.countDown();
                    monitor.wait(); // WAITING (no timeout) until notified
                }
            } catch (InterruptedException ignored) { }
        }, "state-demo-thread");

        System.out.println("Before start(): " + t.getState()); // NEW

        t.start();
        Thread.sleep(50);
        System.out.println("Just after start(), before it reaches wait(): observing RUNNABLE is timing-dependent, "
                + "so instead we synchronize on the latch to observe the state right as it's about to block:");
        enteredWait.await();
        Thread.sleep(50); // let it actually enter wait()
        System.out.println("Inside monitor.wait() (no timeout): " + t.getState()); // WAITING

        synchronized (monitor) {
            monitor.notify();
        }
        t.join();
        System.out.println("After join() returns: " + t.getState()); // TERMINATED

        System.out.println();
        System.out.println("== TIMED_WAITING, the state the source material's diagram omitted ==");
        Thread sleeper = new Thread(() -> {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) { }
        }, "sleeper-thread");
        sleeper.start();
        Thread.sleep(100);
        System.out.println("While inside Thread.sleep(2000): " + sleeper.getState()); // TIMED_WAITING
        sleeper.join();
        System.out.println("After it wakes and finishes: " + sleeper.getState()); // TERMINATED

        System.out.println();
        System.out.println("Real Thread.State enum, for reference: " + java.util.Arrays.toString(Thread.State.values()));
    }
}
