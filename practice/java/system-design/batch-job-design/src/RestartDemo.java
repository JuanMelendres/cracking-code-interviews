import java.sql.Connection;
import java.sql.SQLException;

/**
 * Part 2: what a crash halfway through costs, with and without a checkpoint.
 *
 * <p>The crash is real in the sense that matters — the job throws partway
 * through and the process's in-memory progress is discarded. What differs
 * between the two runs is whether progress was durably recorded as it went.
 */
final class RestartDemo {

    static class Crash extends RuntimeException {
        Crash(String m) { super(m); }
    }

    /** No checkpoint: one transaction for the whole job, or none at all. */
    static void withoutCheckpoint(int invoices, int crashAt) throws SQLException {
        Db.reset();
        Db.seedInvoices(invoices);

        runNoCheckpoint(invoices, crashAt);
        int committedAfter1 = Db.count("SELECT COUNT(*) FROM side_effect");
        int performed1 = crashAt - 1;          // rows inserted before the rollback discarded them

        runNoCheckpoint(invoices, Integer.MAX_VALUE);   // restart, no crash
        int committedAfter2 = Db.count("SELECT COUNT(*) FROM side_effect");

        System.out.printf("  No checkpoint  : attempt 1 inserted %d rows, then crashed at invoice %d%n",
                performed1, crashAt);
        System.out.printf("                   committed after attempt 1: %d  (the whole transaction rolled back)%n",
                committedAfter1);
        System.out.printf("                   attempt 2 restarted from invoice 1 and committed %d%n",
                committedAfter2);
        System.out.printf("                   result: correct, but %d units of work were performed to commit %d%n",
                performed1 + invoices, invoices);
    }

    static int runNoCheckpoint(int invoices, int crashAt) {
        try (Connection c = Db.open();
             var effect = c.prepareStatement(
                     "INSERT INTO side_effect (invoice_id, worker) VALUES (?, 'single')")) {
            int done = 0;
            for (int id = 1; id <= invoices; id++) {
                if (id == crashAt) {
                    c.rollback();
                    return 0;   // the whole transaction is lost
                }
                effect.setInt(1, id);
                effect.executeUpdate();
                done++;
            }
            c.commit();
            return done;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Chunked with a durable checkpoint committed in the same transaction. */
    static void withCheckpoint(int invoices, int crashAt, int chunk) throws SQLException {
        Db.reset();
        Db.seedInvoices(invoices);

        runChunked(invoices, crashAt, chunk);
        int committedAfter1 = Db.count("SELECT COUNT(*) FROM side_effect");
        int performed1 = crashAt - 1;
        int checkpoint = lastDone();
        int resumeFrom = checkpoint + 1;

        runChunked(invoices, Integer.MAX_VALUE, chunk);
        int total = Db.count("SELECT COUNT(*) FROM side_effect");
        int distinct = Db.count("SELECT COUNT(DISTINCT invoice_id) FROM side_effect");

        System.out.printf("  With checkpoint: attempt 1 inserted %d rows, then crashed at invoice %d%n",
                performed1, crashAt);
        System.out.printf("                   committed after attempt 1: %d  (only the open chunk rolled back)%n",
                committedAfter1);
        System.out.printf("                   checkpoint = %d, so attempt 2 resumed at invoice %d%n",
                checkpoint, resumeFrom);
        System.out.printf("                   total committed = %d across %d distinct invoices  -> %s%n",
                total, distinct,
                total == invoices && distinct == invoices
                        ? "every invoice processed exactly once" : "WRONG");
        System.out.printf("                   work performed to commit %d: %d units (%d wasted)%n",
                invoices, performed1 + (invoices - checkpoint), performed1 - checkpoint);
    }

    static int runChunked(int invoices, int crashAt, int chunk) {
        try (Connection c = Db.open();
             var effect = c.prepareStatement(
                     "INSERT INTO side_effect (invoice_id, worker) VALUES (?, 'single')");
             var mark = c.prepareStatement(
                     "MERGE INTO job_checkpoint (name, last_done_id) KEY (name) VALUES ('nightly', ?)")) {

            int start = lastDone() + 1;
            int done = 0;
            int inChunk = 0;
            for (int id = start; id <= invoices; id++) {
                if (id == crashAt) {
                    c.rollback();   // only the UNCOMMITTED chunk is lost
                    return done;
                }
                effect.setInt(1, id);
                effect.executeUpdate();
                inChunk++;
                done++;
                if (inChunk == chunk) {
                    // The checkpoint and the work commit together. If they did
                    // not, a crash between them would either lose work or skip it.
                    mark.setInt(1, id);
                    mark.executeUpdate();
                    c.commit();
                    inChunk = 0;
                }
            }
            if (inChunk > 0) {
                mark.setInt(1, invoices);
                mark.executeUpdate();
                c.commit();
            }
            return done;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    static int lastDone() {
        try {
            return Db.count("SELECT COALESCE(MAX(last_done_id), 0) FROM job_checkpoint WHERE name = 'nightly'");
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
