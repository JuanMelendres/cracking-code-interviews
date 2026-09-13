import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

public class ReadOnlyDemo {

    @Configuration
    @EnableTransactionManagement
    static class Config {
        @Bean
        DataSource dataSource() {
            DriverManagerDataSource ds = new DriverManagerDataSource();
            ds.setDriverClassName("org.h2.Driver");
            ds.setUrl("jdbc:h2:mem:readonly;DB_CLOSE_DELAY=-1");
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
        ReportService reportService(JdbcTemplate jdbcTemplate) {
            return new ReportService(jdbcTemplate);
        }
    }

    static class ReportService {
        private final JdbcTemplate jdbc;
        ReportService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

        void createTable() {
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
                System.out.println("Write inside @Transactional(readOnly=true) SUCCEEDED (no exception).");
                System.out.println("On this driver (H2), readOnly=true set connection.setReadOnly(true) but H2 did not reject the write.");
                System.out.println("RESULT: readOnly is a HINT here, not an enforced constraint -- driver-dependent behavior, exactly as documented.");
            } catch (Exception e) {
                System.out.println("Write inside @Transactional(readOnly=true) FAILED: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                System.out.println("RESULT: this driver (H2) DOES enforce connection.setReadOnly(true) at the JDBC level.");
            }
        }
    }
}
