import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

public class ReadOnlyPostgresDemo {

    @Configuration
    @EnableTransactionManagement
    static class Config {
        @Bean
        DataSource dataSource() {
            DriverManagerDataSource ds = new DriverManagerDataSource();
            ds.setDriverClassName("org.postgresql.Driver");
            ds.setUrl("jdbc:postgresql://localhost:55432/week3");
            ds.setUsername("postgres");
            ds.setPassword("postgres");
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
        ReportService reportService(JdbcTemplate jdbcTemplate) {
            return new ReportService(jdbcTemplate);
        }
    }

    static class ReportService {
        private final JdbcTemplate jdbc;
        ReportService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

        void createTable() {
            jdbc.execute("DROP TABLE IF EXISTS reports");
            jdbc.execute("CREATE TABLE reports (id INT PRIMARY KEY)");
        }

        @Transactional(readOnly = true)
        public void attemptWriteInsideReadOnlyTransaction() {
            jdbc.update("INSERT INTO reports (id) VALUES (1)");
        }
    }

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class)) {
            ReportService service = ctx.getBean(ReportService.class);
            service.createTable();

            try {
                service.attemptWriteInsideReadOnlyTransaction();
                System.out.println("Write inside @Transactional(readOnly=true) SUCCEEDED (no exception) on PostgreSQL.");
                System.out.println("RESULT: readOnly was NOT enforced on this driver either.");
            } catch (Exception e) {
                System.out.println("Write inside @Transactional(readOnly=true) FAILED on PostgreSQL:");
                System.out.println("  " + e.getClass().getSimpleName() + ": " + rootMessage(e));
                System.out.println("RESULT: CONFIRMED -- PostgreSQL's JDBC driver enforces connection.setReadOnly(true) by rejecting the write at the database level.");
            }
        }
    }

    private static String rootMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null) cur = cur.getCause();
        return cur.getMessage();
    }
}
