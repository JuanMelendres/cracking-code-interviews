package demo;

/**
 * The real bug, reproduced: Client-A acquires a lock with a 300ms lease,
 * then experiences a real 500ms pause (a real Thread.sleep standing in
 * for a real GC pause, a slow disk I/O, or any other real stall) --
 * longer than its own lease TTL. While Client-A is paused, its lease
 * genuinely expires and Client-B legitimately acquires the lock and
 * writes. Client-A then wakes up, still believing it holds the lock
 * (it never re-checks), and writes anyway -- silently overwriting
 * Client-B's authoritative write. No fencing token involved -- the
 * storage layer accepts whatever the last writer sends, with zero
 * awareness of the lock service's own state.
 */
public class UnsafeLockDemo {
    public static void main(String[] args) throws InterruptedException {
        LockService lockService = new LockService();
        UnsafeStorage storage = new UnsafeStorage();

        LockService.Lease leaseA = lockService.acquire("Client-A", 300);
        System.out.println("Client-A acquired lock, token=" + leaseA.token + ", ttl=300ms");

        Thread clientB = new Thread(() -> {
            try {
                Thread.sleep(350); // waits until A's real 300ms lease has genuinely expired
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            LockService.Lease leaseB = lockService.acquire("Client-B", 300);
            System.out.println("Client-B acquired lock (A's lease expired), token=" + leaseB.token);
            storage.write("B-value", "Client-B");
        });
        clientB.start();

        System.out.println("Client-A pausing for 500ms (simulated GC pause)...");
        Thread.sleep(500);

        System.out.println("Client-A resumed -- writes without ever re-checking its own lease");
        storage.write("A-value", "Client-A");

        clientB.join();

        System.out.println();
        String finalValue = storage.read();
        System.out.println("Final UnsafeStorage value: " + finalValue);
        if ("A-value".equals(finalValue)) {
            System.out.println("BUG REPRODUCED: stale Client-A silently overwrote Client-B's authoritative write.");
        } else {
            System.out.println("Bug did not reproduce this run (timing-dependent) -- re-run.");
        }
    }
}
