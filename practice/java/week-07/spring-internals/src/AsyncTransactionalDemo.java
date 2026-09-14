import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Real, observed behavior of stacking @Async and @Transactional on the same
 * method -- the specific "unexpected behavior" the interview question asks
 * about. The transaction itself still works correctly (it starts on the
 * executor thread @Async switches to), but a void @Async method returns to
 * its caller immediately, BEFORE the transactional work even runs -- so an
 * exception thrown inside is never visible to the caller at all, and the
 * caller has no way to know whether the operation it "called" succeeded,
 * failed, or is still running.
 */
public class AsyncTransactionalDemo {

    @Configuration
    @EnableAsync
    @EnableTransactionManagement
    static class Config {
        @Bean
        DataSource dataSource() {
            DriverManagerDataSource ds = new DriverManagerDataSource();
            ds.setDriverClassName("org.h2.Driver");
            ds.setUrl("jdbc:h2:mem:asynctx;DB_CLOSE_DELAY=-1");
            ds.setUsername("sa");
            ds.setPassword("");
            return ds;
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
        WorkService workService(JdbcTemplate jdbcTemplate) {
            return new WorkService(jdbcTemplate);
        }
    }

    static class WorkService {
        private final JdbcTemplate jdbc;
        static volatile boolean workCompleted = false;
        static final CountDownLatch workDone = new CountDownLatch(1);

        WorkService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

        void createTable() {
            jdbc.execute("CREATE TABLE work_log (id INT PRIMARY KEY)");
        }

        @Async
        @Transactional
        public void doWorkAndFail() {
            jdbc.update("INSERT INTO work_log (id) VALUES (1)");
            try {
                throw new RuntimeException("simulated failure INSIDE the async+transactional method");
            } finally {
                workCompleted = true;
                workDone.countDown();
            }
        }

        int rowCount() {
            Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM work_log", Integer.class);
            return c == null ? 0 : c;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class)) {
            WorkService service = ctx.getBean(WorkService.class);
            service.createTable();

            System.out.println("Calling the @Async @Transactional method...");
            long callStart = System.currentTimeMillis();
            boolean exceptionVisibleToCaller = false;
            try {
                service.doWorkAndFail(); // returns immediately -- void @Async method
            } catch (RuntimeException e) {
                exceptionVisibleToCaller = true;
            }
            long callReturnedAfter = System.currentTimeMillis() - callStart;

            System.out.println("Call returned after " + callReturnedAfter + "ms. "
                    + "Exception visible to caller: " + exceptionVisibleToCaller);
            System.out.println("(At this point the caller has NO idea whether the operation succeeded, "
                    + "failed, or is still running on the async executor thread.)");

            WorkService.workDone.await(3, TimeUnit.SECONDS); // wait for the background thread, for OUR test's sake only
            System.out.println("\n[test observer, not something the real caller could see] "
                    + "async work actually completed: " + WorkService.workCompleted);
            System.out.println("[test observer] row count after the exception: " + service.rowCount()
                    + " (0 means the transaction correctly rolled back, even though it ran on a different thread)");
        }
    }
}
