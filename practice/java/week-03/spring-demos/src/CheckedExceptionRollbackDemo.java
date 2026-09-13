import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

public class CheckedExceptionRollbackDemo {

    static class OrderPlacementFailedException extends Exception {
        OrderPlacementFailedException(String msg) { super(msg); }
    }

    @Configuration
    @EnableTransactionManagement
    static class Config {
        @Bean
        DataSource dataSource() {
            DriverManagerDataSource ds = new DriverManagerDataSource();
            ds.setDriverClassName("org.h2.Driver");
            ds.setUrl("jdbc:h2:mem:checkedex;DB_CLOSE_DELAY=-1");
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
        OrderService orderService(JdbcTemplate jdbcTemplate) {
            return new OrderService(jdbcTemplate);
        }
    }

    static class OrderService {
        private final JdbcTemplate jdbc;
        OrderService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

        void createTable() {
            jdbc.execute("CREATE TABLE orders (id INT PRIMARY KEY, note VARCHAR(100))");
        }

        // DEFAULT Spring rollback rule: rolls back on RuntimeException/Error,
        // but NOT on a checked exception, unless rollbackFor says otherwise.
        @Transactional
        public void placeOrderDefaultRollbackRule(int id, boolean throwChecked) throws OrderPlacementFailedException {
            jdbc.update("INSERT INTO orders (id, note) VALUES (?, ?)", id, "default-rule");
            if (throwChecked) {
                throw new OrderPlacementFailedException("simulated downstream failure, checked exception");
            }
        }

        // Same method, but explicitly configured to roll back on ANY exception,
        // checked or not -- the fix for the default-rule surprise above.
        @Transactional(rollbackFor = Exception.class)
        public void placeOrderExplicitRollbackForAll(int id, boolean throwChecked) throws OrderPlacementFailedException {
            jdbc.update("INSERT INTO orders (id, note) VALUES (?, ?)", id, "rollbackFor-all");
            if (throwChecked) {
                throw new OrderPlacementFailedException("simulated downstream failure, checked exception");
            }
        }

        int countRows() {
            Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM orders", Integer.class);
            return c == null ? 0 : c;
        }
    }

    public static void main(String[] args) throws Exception {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class)) {
            OrderService service = ctx.getBean(OrderService.class);
            service.createTable();

            boolean defaultRuleRowSurvived;
            try {
                service.placeOrderDefaultRollbackRule(1, true);
            } catch (OrderPlacementFailedException e) {
                // expected -- the checked exception still propagates to the caller
            }
            defaultRuleRowSurvived = service.countRows() == 1;
            System.out.println("Default rollback rule, checked exception thrown -> row count: " + service.countRows()
                    + " (row survived the exception: " + defaultRuleRowSurvived + ")");

            boolean explicitRuleRowGone;
            try {
                service.placeOrderExplicitRollbackForAll(2, true);
            } catch (OrderPlacementFailedException e) {
                // expected
            }
            int afterExplicit = service.countRows();
            explicitRuleRowGone = afterExplicit == 1; // still 1, meaning row 2 did NOT persist
            System.out.println("rollbackFor=Exception.class, checked exception thrown -> row count: " + afterExplicit
                    + " (row 2 correctly rolled back: " + explicitRuleRowGone + ")");

            if (defaultRuleRowSurvived && explicitRuleRowGone) {
                System.out.println("RESULT: CONFIRMED -- default rule does NOT roll back on a checked exception; rollbackFor fixes it.");
            } else {
                System.out.println("RESULT: UNEXPECTED");
                System.exit(1);
            }
        }
    }
}
