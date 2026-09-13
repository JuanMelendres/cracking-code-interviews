import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

final class HibernateSupport {
    // A fresh, in-memory H2 database per SessionFactory, schema generated
    // from the entity annotations (hbm2ddl.auto=create-drop) -- no external
    // database process required, and each demo starts from a clean schema.
    static SessionFactory buildSessionFactory(boolean showSql) {
        Configuration cfg = new Configuration();
        cfg.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        cfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:hibernate_demo_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");
        cfg.setProperty("hibernate.connection.username", "sa");
        cfg.setProperty("hibernate.connection.password", "");
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        cfg.setProperty("hibernate.show_sql", String.valueOf(showSql));
        cfg.setProperty("hibernate.format_sql", "false");
        cfg.setProperty("hibernate.generate_statistics", "true"); // enables the real query-count evidence this chapter measures
        cfg.addAnnotatedClass(Author.class);
        cfg.addAnnotatedClass(Book.class);
        return cfg.buildSessionFactory();
    }
}
