import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

final class HibernateSupport {

    static SessionFactory buildSessionFactory(String dbName, int jdbcBatchSize, boolean showSql) {
        String url = "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1";
        Configuration cfg = new Configuration();
        cfg.getStandardServiceRegistryBuilder().addService(
                org.hibernate.engine.jdbc.connections.spi.ConnectionProvider.class,
                new CountingConnectionProvider(url));
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        cfg.setProperty("hibernate.show_sql", String.valueOf(showSql));
        cfg.setProperty("hibernate.format_sql", "false");
        cfg.setProperty("hibernate.generate_statistics", "true");
        if (jdbcBatchSize > 0) {
            cfg.setProperty("hibernate.jdbc.batch_size", String.valueOf(jdbcBatchSize));
            cfg.setProperty("hibernate.order_inserts", "true");
        }
        cfg.addAnnotatedClass(SequenceWidget.class);
        cfg.addAnnotatedClass(IdentityWidget.class);
        return cfg.buildSessionFactory();
    }
}
