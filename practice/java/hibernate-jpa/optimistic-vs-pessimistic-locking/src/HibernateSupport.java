import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * A fresh, real, file-backed H2 database per demo run (not in-memory-per-session --
 * these demos need multiple independent Hibernate sessions/threads to see the SAME
 * underlying rows, which an in-memory-per-connection H2 URL would not guarantee).
 * Schema generated from entity annotations; no external database process required.
 */
final class HibernateSupport {
    static SessionFactory buildSessionFactory(String dbName, boolean showSql) {
        Configuration cfg = new Configuration();
        cfg.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        cfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1");
        cfg.setProperty("hibernate.connection.username", "sa");
        cfg.setProperty("hibernate.connection.password", "");
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        cfg.setProperty("hibernate.show_sql", String.valueOf(showSql));
        cfg.setProperty("hibernate.format_sql", "false");
        cfg.addAnnotatedClass(Account.class);
        cfg.addAnnotatedClass(UnversionedAccount.class);
        return cfg.buildSessionFactory();
    }
}
