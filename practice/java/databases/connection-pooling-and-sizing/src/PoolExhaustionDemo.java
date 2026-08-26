import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLTransientConnectionException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Real proof of connection-pool exhaustion: a real HikariCP pool sized to 2
 * connections, 6 real concurrent threads each holding a connection for 1 real second
 * (simulating a slow query via pg_sleep), and a real 500ms connectionTimeout. Only 2
 * threads can ever be "in the database" at once; the rest queue, and any thread that
 * waits past connectionTimeout gets a real, thrown SQLTransientConnectionException --
 * not a hang, not a silent failure, a real, typed exception naming the pool.
 */
public final class PoolExhaustionDemo {

    public static void main(String[] args) throws Exception {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:55634/appdb");
        config.setUsername("postgres");
        config.setPassword("pooltest");
        config.setMaximumPoolSize(2);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(500); // real, short timeout to make exhaustion visible fast
        config.setPoolName("demo-pool");

        int threadCount = 6;
        AtomicInteger succeeded = new AtomicInteger();
        AtomicInteger timedOut = new AtomicInteger();
        CountDownLatch done = new CountDownLatch(threadCount);

        try (HikariDataSource dataSource = new HikariDataSource(config)) {
            System.out.println("=== Real pool: maximumPoolSize=2, connectionTimeout=500ms ===");
            System.out.println("=== " + threadCount + " real concurrent threads, each holding a connection for 1 real second ===");
            System.out.println();

            long start = System.nanoTime();
            for (int i = 0; i < threadCount; i++) {
                final int id = i;
                new Thread(() -> {
                    long waitStart = System.nanoTime();
                    try (Connection conn = dataSource.getConnection()) {
                        long waited = (System.nanoTime() - waitStart) / 1_000_000;
                        System.out.println("Thread " + id + ": acquired connection after " + waited + "ms real wait, running query...");
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute("SELECT pg_sleep(1)");
                        }
                        System.out.println("Thread " + id + ": query done, releasing connection.");
                        succeeded.incrementAndGet();
                    } catch (SQLTransientConnectionException e) {
                        long waited = (System.nanoTime() - waitStart) / 1_000_000;
                        System.out.println("Thread " + id + ": REAL " + e.getClass().getSimpleName()
                                + " after " + waited + "ms -- \"" + e.getMessage() + "\"");
                        timedOut.incrementAndGet();
                    } catch (Exception e) {
                        System.out.println("Thread " + id + ": unexpected error: " + e);
                    } finally {
                        done.countDown();
                    }
                }).start();
            }

            done.await();
            long totalMs = (System.nanoTime() - start) / 1_000_000;

            System.out.println();
            System.out.println("=== Result after " + totalMs + "ms real wall time ===");
            System.out.println(succeeded.get() + " of " + threadCount + " threads succeeded; "
                    + timedOut.get() + " of " + threadCount + " really timed out waiting for a pooled connection.");
        }
    }
}
