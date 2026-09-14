import java.sql.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A real idempotency-key mechanism against real PostgreSQL: key storage,
 * concurrent-duplicate behavior, and TTL-based recovery from a crashed
 * in-progress attempt -- the exact four things the interview question asks
 * for ("key, storage, TTL, concurrent-duplicate behaviour").
 *
 * Storage: a table with a UNIQUE key column. The database's own unique
 * constraint is what guarantees exactly one "winner" among concurrent
 * attempts to insert the same key -- no application-level locking needed.
 */
public class IdempotencyDemo {

    static final String URL = "jdbc:postgresql://localhost:55433/week5";
    static final AtomicInteger actualChargesPerformed = new AtomicInteger();
    static final long IN_PROGRESS_TTL_SECONDS = 5;

    public static void main(String[] args) throws Exception {
        try (Connection setup = DriverManager.getConnection(URL, "postgres", "postgres")) {
            setup.createStatement().execute("DROP TABLE IF EXISTS idempotency_keys");
            setup.createStatement().execute(
                "CREATE TABLE idempotency_keys (" +
                "  key TEXT PRIMARY KEY," +
                "  status TEXT NOT NULL," +
                "  result TEXT," +
                "  created_at TIMESTAMP NOT NULL DEFAULT now()" +
                ")");
        }

        System.out.println("=== Part 1: two concurrent requests, SAME idempotency key ===");
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch startGate = new CountDownLatch(1);
        Callable<String> task = () -> {
            startGate.await();
            return chargeIdempotent("charge-key-abc123", "$50.00");
        };
        Future<String> f1 = pool.submit(task);
        Future<String> f2 = pool.submit(task);
        startGate.countDown();
        String result1 = f1.get();
        String result2 = f2.get();
        pool.shutdown();

        System.out.println("Request A result: " + result1);
        System.out.println("Request B result: " + result2);
        System.out.println("Actual charges performed: " + actualChargesPerformed.get() + " (must be exactly 1)");
        System.out.println("Both requests returned the same result: " + result1.equals(result2));

        System.out.println("\n=== Part 2: TTL recovery from a crashed in-progress attempt ===");
        try (Connection conn = DriverManager.getConnection(URL, "postgres", "postgres")) {
            // Simulate a process that started a charge, wrote IN_PROGRESS, then
            // crashed before ever completing it -- backdating created_at past the TTL.
            PreparedStatement stale = conn.prepareStatement(
                "INSERT INTO idempotency_keys (key, status, created_at) VALUES (?, 'IN_PROGRESS', now() - interval '10 seconds')");
            stale.setString(1, "charge-key-crashed");
            stale.executeUpdate();
        }
        System.out.println("A stale IN_PROGRESS row (age 10s, TTL " + IN_PROGRESS_TTL_SECONDS + "s) exists for key 'charge-key-crashed'.");
        String recovered = chargeIdempotent("charge-key-crashed", "$75.00");
        System.out.println("New request with the same key result: " + recovered);
        System.out.println("RESULT: the stale IN_PROGRESS row did not block a fresh attempt -- TTL recovery worked.");
    }

    /**
     * Returns the charge result -- either freshly computed (first request for
     * this key) or the previously stored result (duplicate request).
     */
    static String chargeIdempotent(String idempotencyKey, String amount) throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, "postgres", "postgres")) {
            for (int attempt = 0; attempt < 20; attempt++) {
                try {
                    PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO idempotency_keys (key, status) VALUES (?, 'IN_PROGRESS')");
                    insert.setString(1, idempotencyKey);
                    insert.executeUpdate();

                    // We won the race -- we are the ONE request that actually performs the charge.
                    String chargeResult = performActualCharge(amount);

                    PreparedStatement update = conn.prepareStatement(
                        "UPDATE idempotency_keys SET status = 'COMPLETED', result = ? WHERE key = ?");
                    update.setString(1, chargeResult);
                    update.setString(2, idempotencyKey);
                    update.executeUpdate();

                    return chargeResult;
                } catch (SQLException e) {
                    if (!"23505".equals(e.getSQLState())) throw e; // 23505 = unique_violation

                    // Someone else already holds this key. Check its status.
                    PreparedStatement select = conn.prepareStatement(
                        "SELECT status, result, extract(epoch from (now() - created_at)) as age_seconds " +
                        "FROM idempotency_keys WHERE key = ?");
                    select.setString(1, idempotencyKey);
                    ResultSet rs = select.executeQuery();
                    if (rs.next()) {
                        String status = rs.getString("status");
                        double ageSeconds = rs.getDouble("age_seconds");
                        if ("COMPLETED".equals(status)) {
                            return rs.getString("result"); // duplicate -- return the ORIGINAL result, no re-charge
                        }
                        if (ageSeconds > IN_PROGRESS_TTL_SECONDS) {
                            // The original attempt is presumed dead (crashed before completing).
                            // Reclaim the key and retry the insert on the next loop iteration.
                            PreparedStatement delete = conn.prepareStatement(
                                "DELETE FROM idempotency_keys WHERE key = ? AND status = 'IN_PROGRESS'");
                            delete.setString(1, idempotencyKey);
                            delete.executeUpdate();
                            continue;
                        }
                        // Genuinely concurrent, still in-flight -- brief wait, then re-check.
                        Thread.sleep(50);
                        continue;
                    }
                }
            }
            throw new IllegalStateException("gave up waiting for the in-flight request to complete");
        }
    }

    private static String performActualCharge(String amount) throws InterruptedException {
        actualChargesPerformed.incrementAndGet();
        Thread.sleep(200); // simulate real payment-provider latency
        return "charged " + amount + ", confirmation #" + System.nanoTime();
    }
}
