import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

public class RequiresNewDemo {

    @Configuration
    @EnableTransactionManagement
    static class Config {
        @Bean
        DataSource dataSource() {
            DriverManagerDataSource ds = new DriverManagerDataSource();
            ds.setDriverClassName("org.h2.Driver");
            ds.setUrl("jdbc:h2:mem:requiresnew;DB_CLOSE_DELAY=-1");
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
        AuditLogService auditLogService(JdbcTemplate jdbcTemplate) {
            return new AuditLogService(jdbcTemplate);
        }

        @Bean
        OrderService orderService(JdbcTemplate jdbcTemplate, AuditLogService auditLogService) {
            return new OrderService(jdbcTemplate, auditLogService);
        }
    }

    // A real use case for REQUIRES_NEW: an audit log entry that must survive
    // even if the business operation it's logging later fails and rolls back.
    static class AuditLogService {
        private final JdbcTemplate jdbc;
        AuditLogService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void recordAttempt(String note) {
            jdbc.update("INSERT INTO audit_log (note) VALUES (?)", note);
            // Returning here COMMITS this independent transaction immediately --
            // it does not wait for the outer transaction to decide anything.
        }
    }

    static class OrderService {
        private final JdbcTemplate jdbc;
        private final AuditLogService auditLogService;
        OrderService(JdbcTemplate jdbc, AuditLogService auditLogService) {
            this.jdbc = jdbc;
            this.auditLogService = auditLogService;
        }

        void createTables() {
            jdbc.execute("CREATE TABLE orders (id INT PRIMARY KEY)");
            jdbc.execute("CREATE TABLE audit_log (id INT AUTO_INCREMENT PRIMARY KEY, note VARCHAR(200))");
        }

        @Transactional
        public void placeOrderThenFail(int id) {
            jdbc.update("INSERT INTO orders (id) VALUES (?)", id);
            auditLogService.recordAttempt("attempted order " + id); // REQUIRES_NEW -- commits on its own
            throw new RuntimeException("simulated failure after the audit entry was recorded");
        }

        int orderCount() {
            Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM orders", Integer.class);
            return c == null ? 0 : c;
        }

        int auditLogCount() {
            Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM audit_log", Integer.class);
            return c == null ? 0 : c;
        }
    }

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class)) {
            OrderService service = ctx.getBean(OrderService.class);
            service.createTables();

            try {
                service.placeOrderThenFail(1);
            } catch (RuntimeException e) {
                // expected
            }

            int orders = service.orderCount();
            int auditEntries = service.auditLogCount();

            System.out.println("orders table row count (outer transaction, should be rolled back): " + orders);
            System.out.println("audit_log table row count (REQUIRES_NEW, should have survived): " + auditEntries);

            if (orders == 0 && auditEntries == 1) {
                System.out.println("RESULT: CONFIRMED -- REQUIRES_NEW committed independently despite the outer rollback.");
            } else {
                System.out.println("RESULT: UNEXPECTED");
                System.exit(1);
            }
        }
    }
}
