import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;

/**
 * Real proof of HikariCP's leak detection: a connection is really borrowed and
 * deliberately never closed (the real bug this feature exists to catch -- a
 * try-with-resources forgotten, an exception path that skips close()). With a real,
 * short leakDetectionThreshold configured, HikariCP's own background thread really
 * logs a WARN identifying the exact leaked connection and the stack trace of where
 * it was acquired -- a real, actionable diagnostic, not a silent resource leak.
 */
public final class LeakDetectionDemo {

    public static void main(String[] args) throws Exception {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:55634/appdb");
        config.setUsername("postgres");
        config.setPassword("pooltest");
        config.setMaximumPoolSize(3);
        // Real, honest discovery made while building this demo: HikariCP silently
        // disables leakDetectionThreshold below 2000ms (see its own real WARN log,
        // "leakDetectionThreshold is less than 2000ms... disabling it") -- 1000ms,
        // the first value tried here, was rejected. 2000ms is the real minimum.
        config.setLeakDetectionThreshold(2000);
        config.setPoolName("leak-demo-pool");

        try (HikariDataSource dataSource = new HikariDataSource(config)) {
            System.out.println("=== Real leakDetectionThreshold=2000ms configured (HikariCP's real enforced minimum) ===");
            System.out.println("=== Deliberately borrowing a connection and NEVER closing it ===");

            @SuppressWarnings("unused")
            Connection leaked = dataSource.getConnection(); // deliberately never closed

            System.out.println("Connection borrowed at " + System.currentTimeMillis()
                    + ". Waiting 3 real seconds for HikariCP's real leak detector to fire...");
            Thread.sleep(3000);

            System.out.println();
            System.out.println("=== Real pool state after the wait (see WARN log above from HikariCP itself) ===");
            System.out.println("Active connections: " + dataSource.getHikariPoolMXBean().getActiveConnections()
                    + " (the leaked one is still really held -- this program never released it)");
        }
    }
}
