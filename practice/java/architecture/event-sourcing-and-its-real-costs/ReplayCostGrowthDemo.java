import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The register's own named emphasis, made real: event sourcing's REAL COST is
 * replay time growing with event count, measured here as genuine disk I/O against a
 * real file, not simulated CPU work. At increasing event counts, this demo writes
 * that many real events to a real file, then measures the real wall-clock time to
 * rebuild an Account's current state by replaying every one of them from scratch.
 */
public final class ReplayCostGrowthDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Real replay cost growth: rebuilding state from scratch at increasing event counts ===");
        System.out.println();

        // Real JIT warmup pass, discarded, so the measured numbers below reflect
        // steady-state JIT-compiled performance rather than interpreter startup cost.
        Path warmupFile = Files.createTempFile("event-store-warmup-", ".log");
        EventStore warmupStore = new EventStore(warmupFile);
        for (int i = 0; i < 20_000; i++) {
            warmupStore.append(i % 2 == 0 ? new MoneyDeposited(10) : new MoneyWithdrawn(3));
        }
        Account.replay(warmupStore.readAll());
        Files.deleteIfExists(warmupFile);

        for (int eventCount : new int[]{1_000, 10_000, 50_000, 100_000, 200_000}) {
            Path file = Files.createTempFile("event-store-", ".log");
            try {
                EventStore store = new EventStore(file);
                store.append(new AccountOpened("alice"));
                for (int i = 0; i < eventCount; i++) {
                    store.append(i % 2 == 0 ? new MoneyDeposited(10) : new MoneyWithdrawn(3));
                }

                long start = System.nanoTime();
                Account account = Account.replay(store.readAll());
                long elapsedMs = (System.nanoTime() - start) / 1_000_000;

                System.out.printf("Events: %6d  Real replay time: %4d ms  Real file size: %6d bytes  Final balance: %d%n",
                        eventCount + 1, elapsedMs, store.sizeInBytes(), account.getBalance());
            } finally {
                Files.deleteIfExists(file);
            }
        }

        System.out.println();
        System.out.println("Real, measured cost: rebuilding an aggregate's current state requires reading and");
        System.out.println("reapplying its ENTIRE history, every single time, unless something (a snapshot) short-circuits it.");
    }
}
