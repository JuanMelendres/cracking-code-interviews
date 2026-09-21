package demo;

/** The ordinary, non-buggy case: LockService itself refuses a second
 * acquire while an existing lease is still genuinely valid -- real
 * mutual exclusion, not just the expiry/fencing edge case the other two
 * demos focus on. */
public class LockContentionDemo {
    public static void main(String[] args) {
        LockService lockService = new LockService();

        LockService.Lease leaseA = lockService.acquire("Client-A", 5000);
        System.out.println("Client-A acquired lock, token=" + leaseA.token + " (5s TTL, still valid)");

        try {
            lockService.acquire("Client-B", 5000);
            System.out.println("UNEXPECTED: Client-B acquired the lock while A's lease is still valid");
        } catch (IllegalStateException e) {
            System.out.println("Client-B correctly refused: " + e.getMessage());
        }
    }
}
