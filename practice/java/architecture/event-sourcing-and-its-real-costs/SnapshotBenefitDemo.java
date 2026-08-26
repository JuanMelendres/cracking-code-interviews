import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Real proof of the standard mitigation for replay cost: a snapshot. This demo
 * writes 200,000 real events to a real file, then compares two real approaches to
 * rebuilding current state: (1) replay every event from the beginning, and (2)
 * take a real snapshot at event 190,000, then replay only the real remaining 10,000
 * events by seeking directly to that snapshot's real byte offset in the file --
 * never reading the snapshotted prefix from disk at all.
 */
public final class SnapshotBenefitDemo {

    public static void main(String[] args) throws Exception {
        Path file = Files.createTempFile("event-store-snapshot-", ".log");
        try {
            EventStore store = new EventStore(file);
            store.append(new AccountOpened("alice"));
            for (int i = 0; i < 189_999; i++) {
                store.append(i % 2 == 0 ? new MoneyDeposited(10) : new MoneyWithdrawn(3));
            }
            long snapshotByteOffset = store.sizeInBytes();
            Account snapshot = Account.replay(store.readAll()); // one real, one-time cost to produce the snapshot

            for (int i = 0; i < 10_000; i++) {
                store.append(i % 2 == 0 ? new MoneyDeposited(10) : new MoneyWithdrawn(3));
            }

            System.out.println("=== Real total events: 200,000. Real snapshot taken at event 190,000. ===");
            System.out.println();

            long startFull = System.nanoTime();
            Account fromScratch = Account.replay(store.readAll());
            long fullMs = (System.nanoTime() - startFull) / 1_000_000;
            System.out.println("Full replay from event 0 (no snapshot):  " + fullMs
                    + " ms, real balance=" + fromScratch.getBalance());

            long startSnapshot = System.nanoTime();
            List<Event> tail = store.readFromByteOffset(snapshotByteOffset);
            Account fromSnapshot = Account.replayFrom(snapshot, tail);
            long snapshotMs = (System.nanoTime() - startSnapshot) / 1_000_000;
            System.out.println("Snapshot + replay only the real tail (" + tail.size() + " events): " + snapshotMs
                    + " ms, real balance=" + fromSnapshot.getBalance());

            System.out.println();
            System.out.println("Balances match: " + (fromScratch.getBalance() == fromSnapshot.getBalance())
                    + " -- the snapshot didn't change the real result, only how expensively it was reached.");
            if (snapshotMs > 0) {
                System.out.printf("Real measured speedup: %.1fx%n", (double) fullMs / snapshotMs);
            }
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
