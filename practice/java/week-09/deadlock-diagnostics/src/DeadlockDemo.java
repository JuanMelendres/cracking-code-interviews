import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.CountDownLatch;

/**
 * T-409 -- a genuine deadlock (two threads, two locks, acquired in
 * opposite order), detected the same way production diagnostics do:
 * ThreadMXBean.findDeadlockedThreads(), not a description of the
 * classic diagram.
 */
public class DeadlockDemo {
    static final Object lockA = new Object();
    static final Object lockB = new Object();

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch bothHoldFirstLock = new CountDownLatch(2);

        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                bothHoldFirstLock.countDown();
                await(bothHoldFirstLock);
                sleep(100);
                synchronized (lockB) { /* never reached */ }
            }
        }, "thread-1-A-then-B");

        Thread t2 = new Thread(() -> {
            synchronized (lockB) {
                bothHoldFirstLock.countDown();
                await(bothHoldFirstLock);
                sleep(100);
                synchronized (lockA) { /* never reached */ }
            }
        }, "thread-2-B-then-A");

        t1.start();
        t2.start();

        Thread.sleep(1000); // let the deadlock actually form
        System.out.println("== states while deadlocked ==");
        System.out.println(t1.getName() + ": " + t1.getState());
        System.out.println(t2.getName() + ": " + t2.getState());

        System.out.println();
        System.out.println("== ThreadMXBean.findDeadlockedThreads() -- real detection, not a guess ==");
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long[] deadlockedIds = bean.findDeadlockedThreads();
        if (deadlockedIds == null) {
            System.out.println("No deadlock detected (unexpected for this demo)");
        } else {
            ThreadInfo[] infos = bean.getThreadInfo(deadlockedIds, true, true);
            for (ThreadInfo info : infos) {
                System.out.printf("DEADLOCKED: %s is %s, waiting on %s held by %s%n",
                        info.getThreadName(), info.getThreadState(),
                        info.getLockName(), info.getLockOwnerName());
            }
        }

        System.out.println();
        System.out.println("(Process exits via System.exit -- the two deadlocked threads are daemon-equivalent "
                + "for this demo's purposes and would otherwise hang forever, which is the entire point.)");
        System.exit(0);
    }

    static void await(CountDownLatch latch) {
        try { latch.await(); } catch (InterruptedException ignored) { }
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { }
    }
}
