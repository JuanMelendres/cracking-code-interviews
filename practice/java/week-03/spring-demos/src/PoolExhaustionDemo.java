import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class PoolExhaustionDemo {

    static final int POOL_SIZE = 2;
    static final long CONNECTION_TIMEOUT_MS = 2000;
    static final long LONG_TRANSACTION_HOLD_MS = 6000;

    @Configuration
    @EnableTransactionManagement
    static class Config {
        @Bean(destroyMethod = "close")
        DataSource dataSource() {
            HikariConfig cfg = new HikariConfig();
            cfg.setDriverClassName("org.h2.Driver");
            cfg.setJdbcUrl("jdbc:h2:mem:poolexhaustion;DB_CLOSE_DELAY=-1");
            cfg.setUsername("sa");
            cfg.setPassword("");
            cfg.setMaximumPoolSize(POOL_SIZE);
            cfg.setConnectionTimeout(CONNECTION_TIMEOUT_MS);
            return new HikariDataSource(cfg);
        }

        @Bean
        PlatformTransactionManager txManager(DataSource ds) {
            return new DataSourceTransactionManager(ds);
        }

        @Bean
        JdbcTemplate jdbcTemplate(DataSource ds) {
            return new JdbcTemplate(ds);
        }

        @Bean
        SlowService slowService(JdbcTemplate jdbcTemplate) {
            return new SlowService(jdbcTemplate);
        }
    }

    static class SlowService {
        private final JdbcTemplate jdbc;
        SlowService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

        // Simulates a transaction that holds its connection for far longer than
        // it should -- an HTTP call, a slow report, a forgotten Thread.sleep in
        // the middle of a @Transactional method. Every millisecond here is a
        // millisecond that connection is UNAVAILABLE to every other request.
        @Transactional
        public void longRunningWork(long holdMillis) throws InterruptedException {
            jdbc.execute("SELECT 1"); // touches the connection so it's actually checked out
            Thread.sleep(holdMillis);
        }

        // A normal, fast request that just needs a connection briefly.
        @Transactional
        public void fastWork() {
            jdbc.execute("SELECT 1");
        }
    }

    public static void main(String[] args) throws Exception {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class)) {
            SlowService service = ctx.getBean(SlowService.class);

            System.out.println("Pool size = " + POOL_SIZE + ", connectionTimeout = " + CONNECTION_TIMEOUT_MS + "ms");
            System.out.println("Starting " + POOL_SIZE + " long-running transactions (each holding a connection for "
                    + LONG_TRANSACTION_HOLD_MS + "ms), then one more request competing for a connection...\n");

            CountDownLatch bothLongRunningStarted = new CountDownLatch(POOL_SIZE);
            AtomicLong thirdRequestWaitMillis = new AtomicLong(-1);
            AtomicLong thirdRequestErrorClass = new AtomicLong(0);

            Runnable longRunner = () -> {
                bothLongRunningStarted.countDown();
                try {
                    service.longRunningWork(LONG_TRANSACTION_HOLD_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };

            Thread t1 = new Thread(longRunner, "long-runner-1");
            Thread t2 = new Thread(longRunner, "long-runner-2");
            t1.start();
            t2.start();

            bothLongRunningStarted.await(3, TimeUnit.SECONDS);
            Thread.sleep(200); // let both actually acquire their connections

            Thread t3 = new Thread(() -> {
                long start = System.currentTimeMillis();
                try {
                    service.fastWork();
                    thirdRequestWaitMillis.set(System.currentTimeMillis() - start);
                } catch (Exception e) {
                    thirdRequestWaitMillis.set(System.currentTimeMillis() - start);
                    thirdRequestErrorClass.set(1);
                    System.out.println("Third request FAILED after " + (System.currentTimeMillis() - start)
                            + "ms waiting for a connection: " + e.getClass().getSimpleName());
                }
            }, "third-request");
            t3.start();
            t3.join();

            t1.join();
            t2.join();

            long waited = thirdRequestWaitMillis.get();
            boolean timedOutAsExpected = thirdRequestErrorClass.get() == 1
                    && waited >= CONNECTION_TIMEOUT_MS - 200; // allow small scheduling slack

            System.out.println("\nThird request waited " + waited + "ms before failing (configured timeout: " + CONNECTION_TIMEOUT_MS + "ms).");
            if (timedOutAsExpected) {
                System.out.println("RESULT: CONFIRMED -- pool exhaustion under a small pool size with long-held connections"
                        + " causes a real connection-acquisition timeout for a completely unrelated, fast request.");
            } else {
                System.out.println("RESULT: UNEXPECTED");
                System.exit(1);
            }
        }
    }
}
