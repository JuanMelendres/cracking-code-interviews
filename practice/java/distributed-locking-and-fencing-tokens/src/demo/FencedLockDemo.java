package demo;

/**
 * The identical real timeline as UnsafeLockDemo -- same 300ms lease, same
 * real 500ms pause, same legitimate Client-B takeover -- but every write
 * now carries its lease's own real fencing token, and FencedStorage
 * itself (not the lock holder's good behavior) enforces that only a
 * strictly increasing token is ever accepted.
 */
public class FencedLockDemo {
    public static void main(String[] args) throws InterruptedException {
        LockService lockService = new LockService();
        FencedStorage storage = new FencedStorage();

        LockService.Lease leaseA = lockService.acquire("Client-A", 300);
        System.out.println("Client-A acquired lock, token=" + leaseA.token + ", ttl=300ms");

        Thread clientB = new Thread(() -> {
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            LockService.Lease leaseB = lockService.acquire("Client-B", 300);
            System.out.println("Client-B acquired lock (A's lease expired), token=" + leaseB.token);
            storage.write("B-value", leaseB.token, "Client-B");
        });
        clientB.start();

        System.out.println("Client-A pausing for 500ms (simulated GC pause)...");
        Thread.sleep(500);

        System.out.println("Client-A resumed -- attempts write with its now-stale token=" + leaseA.token);
        boolean accepted = storage.write("A-value", leaseA.token, "Client-A");

        clientB.join();

        System.out.println();
        String finalValue = storage.read();
        System.out.println("Final FencedStorage value: " + finalValue);
        System.out.println("Client-A's stale write accepted? " + accepted);
        if (!accepted && "B-value".equals(finalValue)) {
            System.out.println("FIX VERIFIED: stale Client-A's write was rejected; Client-B's authoritative write survived.");
        } else {
            System.out.println("Unexpected result this run (timing-dependent) -- re-run.");
        }
    }
}
