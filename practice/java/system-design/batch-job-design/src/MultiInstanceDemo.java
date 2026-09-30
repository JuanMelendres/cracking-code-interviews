import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Part 1: what happens when the same scheduled job is deployed on three
 * instances. Three threads stand in for three pods, all starting at the same
 * instant because their cron expressions are identical.
 */
final class MultiInstanceDemo {

    static void unguarded(int instances, int invoices) throws Exception {
        Db.reset();
        Db.seedInvoices(invoices);
        runAll(instances, worker -> processAll(worker));
        report("  Unguarded (@Scheduled on every instance)", invoices);
    }

    static void lockGuarded(int instances, int invoices) throws Exception {
        Db.reset();
        Db.seedInvoices(invoices);
        runAll(instances, worker -> {
            if (acquireLock("nightly-invoice-job", worker)) {
                processAll(worker);
            }
        });
        report("  Guarded by a database lock", invoices);
    }

    /**
     * The whole lock, in one statement. A conditional UPDATE either matches the
     * free row and returns 1, or matches nothing and returns 0. The database's
     * own row-level locking makes it atomic across every instance; no
     * coordination service is required for a job that runs once a night.
     */
    static boolean acquireLock(String jobName, String worker) {
        try (Connection c = Db.open()) {
            try (var ins = c.prepareStatement(
                    "INSERT INTO job_lock (name, held_by) VALUES (?, NULL)")) {
                ins.setString(1, jobName);
                ins.executeUpdate();
                c.commit();
            } catch (SQLException duplicateKey) {
                c.rollback();   // another instance created the row first; fine
            }
            try (var upd = c.prepareStatement(
                    "UPDATE job_lock SET held_by = ?, locked_at = CURRENT_TIMESTAMP "
                            + "WHERE name = ? AND held_by IS NULL")) {
                upd.setString(1, worker);
                upd.setString(2, jobName);
                int rows = upd.executeUpdate();
                c.commit();
                return rows == 1;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    static void processAll(String worker) {
        try (Connection c = Db.open();
             var select = c.prepareStatement("SELECT id FROM invoice ORDER BY id");
             var effect = c.prepareStatement(
                     "INSERT INTO side_effect (invoice_id, worker) VALUES (?, ?)")) {
            var rs = select.executeQuery();
            while (rs.next()) {
                effect.setInt(1, rs.getInt("id"));
                effect.setString(2, worker);
                effect.addBatch();
            }
            effect.executeBatch();
            c.commit();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    interface Job {
        void run(String worker);
    }

    static void runAll(int instances, Job job) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(instances);
        CountDownLatch gate = new CountDownLatch(1);
        for (int i = 1; i <= instances; i++) {
            String worker = "pod-" + i;
            pool.submit(() -> {
                gate.await();
                job.run(worker);
                return null;
            });
        }
        gate.countDown();
        pool.shutdown();
        pool.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS);
    }

    static void report(String label, int invoices) throws SQLException {
        int effects = Db.count("SELECT COUNT(*) FROM side_effect");
        int distinct = Db.count("SELECT COUNT(DISTINCT worker) FROM side_effect");
        System.out.printf("%-42s invoices=%d  side_effect rows=%-6d workers that ran=%d  -> %s%n",
                label, invoices, effects, distinct,
                effects == invoices ? "correct" : "EVERY INVOICE PROCESSED " + (effects / invoices) + "x");
    }
}
