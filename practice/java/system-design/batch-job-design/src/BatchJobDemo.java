import java.sql.Connection;
import java.sql.SQLException;

/**
 * Batch and scheduled job design, measured against a real H2 database.
 * Java 21. Run: java -cp "out:lib/*" BatchJobDemo
 */
public final class BatchJobDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("== 1. The same @Scheduled job deployed on three instances ==");
        MultiInstanceDemo.unguarded(3, 1_000);
        MultiInstanceDemo.lockGuarded(3, 1_000);
        System.out.println();

        System.out.println("== 2. A crash halfway through, with and without a checkpoint ==");
        RestartDemo.withoutCheckpoint(1_000, 600);
        RestartDemo.withCheckpoint(1_000, 600, 100);
        System.out.println();

        System.out.println("== 3. Chunk size: throughput versus work lost to a crash ==");
        System.out.printf("%-12s %14s %22s%n", "chunk size", "duration", "worst-case rows lost");
        System.out.printf("%-12s %14s %22s%n", "(rows/commit)", "(10,000 rows)", "to a crash");
        for (int chunk : new int[] {1, 10, 100, 1_000, 10_000}) {
            Db.reset();
            Db.seedInvoices(10_000);
            long start = System.nanoTime();
            RestartDemo.runChunked(10_000, Integer.MAX_VALUE, chunk);
            long ms = (System.nanoTime() - start) / 1_000_000;
            System.out.printf("%-12d %11d ms %19d%n", chunk, ms, chunk - 1);
        }
        System.out.println();
        System.out.println("  Commit cost is per-chunk, so bigger chunks are faster -- but the work");
        System.out.println("  at risk in a crash is exactly one chunk. The choice is a durability");
        System.out.println("  budget, not a performance setting.");
        System.out.println();

        System.out.println("== 4. One poison record: fail-fast versus skip ==");
        poisonRecord(true);
        poisonRecord(false);
    }

    /** A single unprocessable record in the middle of an otherwise fine run. */
    static void poisonRecord(boolean failFast) throws SQLException {
        Db.reset();
        Db.seedInvoices(1_000);
        int poisonId = 500;
        int processed = 0;
        int skipped = 0;

        try (Connection c = Db.open();
             var effect = c.prepareStatement(
                     "INSERT INTO side_effect (invoice_id, worker) VALUES (?, 'single')")) {
            for (int id = 1; id <= 1_000; id++) {
                try {
                    if (id == poisonId) {
                        throw new IllegalArgumentException("invoice " + id + " has a null currency");
                    }
                    effect.setInt(1, id);
                    effect.executeUpdate();
                    processed++;
                } catch (IllegalArgumentException bad) {
                    if (failFast) {
                        c.commit();
                        System.out.printf("  fail-fast : stopped at invoice %d. processed=%-5d skipped=%-3d "
                                        + "remaining=%d  -> job must be fixed and rerun%n",
                                id, processed, skipped, 1_000 - id);
                        return;
                    }
                    skipped++;
                }
            }
            c.commit();
            System.out.printf("  skip      : ran to completion.    processed=%-5d skipped=%-3d remaining=%d"
                            + "  -> job \"succeeded\"; the skip is only visible if it is counted%n",
                    processed, skipped, 0);
        }
    }
}
