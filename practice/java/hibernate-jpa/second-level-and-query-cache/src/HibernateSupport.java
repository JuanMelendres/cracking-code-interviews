import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

final class HibernateSupport {
    // Exposed so a demo can open a SECOND, completely independent JDBC
    // connection to the exact same in-memory database -- outside Hibernate's
    // own connection pool and query API entirely -- to reproduce a genuine
    // "Hibernate has zero visibility into this write" scenario.
    static String jdbcUrl(String dbName) {
        return "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1";
    }

    // A fresh, in-memory H2 database per SessionFactory, real second-level
    // cache backed by Hibernate's own JCache region factory over a real
    // Ehcache 3 provider, and the query cache enabled alongside it.
    static SessionFactory buildSessionFactory(String dbName, boolean showSql) {
        Configuration cfg = new Configuration();
        cfg.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        cfg.setProperty("hibernate.connection.url", jdbcUrl(dbName));
        cfg.setProperty("hibernate.connection.username", "sa");
        cfg.setProperty("hibernate.connection.password", "");
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        cfg.setProperty("hibernate.show_sql", String.valueOf(showSql));
        cfg.setProperty("hibernate.format_sql", "false");
        cfg.setProperty("hibernate.generate_statistics", "true"); // enables the real cache-hit/miss evidence this chapter measures

        cfg.setProperty("hibernate.cache.use_second_level_cache", "true");
        cfg.setProperty("hibernate.cache.use_query_cache", "true");
        cfg.setProperty("hibernate.cache.region.factory_class", "jcache");
        cfg.setProperty("hibernate.javax.cache.provider", "org.ehcache.jsr107.EhcacheCachingProvider");

        cfg.addAnnotatedClass(Product.class);
        return cfg.buildSessionFactory();
    }
}
