import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;

public class SelfInvocationDemo {

    @Configuration
    @EnableTransactionManagement
    static class Config {
        @Bean
        DataSource dataSource() {
            DriverManagerDataSource ds = new DriverManagerDataSource();
            ds.setDriverClassName("org.h2.Driver");
            ds.setUrl("jdbc:h2:mem:selfinvoke;DB_CLOSE_DELAY=-1");
            ds.setUsername("sa");
            ds.setPassword("");
            return ds;
        }

        @Bean
        PlatformTransactionManager txManager(DataSource ds) {
            return new DataSourceTransactionManager(ds);
        }

        @Bean
        ServiceA serviceA() {
            return new ServiceA();
        }
    }

    static class ServiceA {
        @Transactional
        public boolean transactionalMethod() {
            return TransactionSynchronizationManager.isActualTransactionActive();
        }

        // Calls the @Transactional method via `this` -- the JVM dispatches this
        // directly on the target object, never through the CGLIB proxy Spring
        // created around ServiceA. The proxy is what intercepts the call and
        // starts a transaction; bypassing it means no transaction is ever begun.
        public boolean callViaSelfInvocation() {
            return this.transactionalMethod();
        }
    }

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class)) {
            ServiceA proxy = ctx.getBean(ServiceA.class);

            boolean viaProxy = proxy.transactionalMethod();
            boolean viaSelfInvocation = proxy.callViaSelfInvocation();

            System.out.println("Called through the Spring-managed proxy:      isActualTransactionActive() = " + viaProxy);
            System.out.println("Called via self-invocation (this.method()):   isActualTransactionActive() = " + viaSelfInvocation);

            if (viaProxy && !viaSelfInvocation) {
                System.out.println("RESULT: CONFIRMED -- self-invocation bypasses the transactional proxy.");
            } else {
                System.out.println("RESULT: UNEXPECTED -- did not reproduce the expected proxy-bypass behavior.");
                System.exit(1);
            }
        }
    }
}
