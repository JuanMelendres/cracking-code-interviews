import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;

/**
 * Real proof of HikariCP's own sizing guidance: a bigger pool is not automatically
 * faster. The real PostgreSQL container backing this demo is capped at 2 real CPUs
 * (docker-compose.yml: cpus: 2.0), and every query is genuinely CPU-bound on the
 * server (an md5 computation over 300,000 generated rows, not an I/O wait like
 * pg_sleep) -- so once the pool size exceeds what those 2 real cores can actually
 * execute concurrently, adding more connections has nowhere real to spend that
 * concurrency; queries just queue on the server instead of the client-side pool.
 */
public final class PoolSizingThroughputDemo {

    private static final String CPU_BOUND_QUERY =
            "SELECT count(*) FROM generate_series(1, 300000) s WHERE md5(s::text) LIKE '00%'";
    private static final int TOTAL_QUERIES = 40;

    public static void main(String[] args) throws Exception {
        System.out.println("=== Real PostgreSQL container capped at 2 real CPUs (docker-compose.yml) ===");
        System.out.println("=== " + TOTAL_QUERIES + " real, genuinely CPU-bound queries per pool size ===");
        System.out.println();

        for (int poolSize : new int[]{2, 4, 8, 16}) {
            long totalMs = runBatch(poolSize);
            System.out.printf("Pool size %2d: %5d ms real wall time for %d queries (%.1f ms/query average)%n",
                    poolSize, totalMs, TOTAL_QUERIES, (double) totalMs / TOTAL_QUERIES);
        }
    }

    private static long runBatch(int poolSize) throws Exception {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:55634/appdb");
        config.setUsername("postgres");
        config.setPassword("pooltest");
        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(poolSize);
        config.setPoolName("sizing-pool-" + poolSize);

        try (HikariDataSource dataSource = new HikariDataSource(config);
             ExecutorService executor = Executors.newFixedThreadPool(Math.max(poolSize * 2, 8))) {

            long start = System.nanoTime();
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < TOTAL_QUERIES; i++) {
                futures.add(executor.submit(() -> {
                    try (Connection conn = dataSource.getConnection();
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(CPU_BOUND_QUERY)) {
                        rs.next();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
            }
            for (Future<?> f : futures) {
                f.get();
            }
            return (System.nanoTime() - start) / 1_000_000;
        }
    }
}
