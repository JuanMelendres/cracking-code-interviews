import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

// A real, honest correction made while building this demo: an UPDATE issued
// through Hibernate's OWN native query API (session.createNativeQuery(...))
// does NOT reproduce a genuine "Hibernate has no idea this changed" scenario
// -- Hibernate conservatively invalidates the affected L2 cache region for
// any DML it executes itself, even native SQL, specifically to avoid this
// exact staleness bug. The real gotcha only appears when a write happens
// through a channel Hibernate has ZERO visibility into: a completely
// separate JDBC connection, opened directly via DriverManager, bypassing
// Hibernate's SessionFactory, connection pool, and query API entirely --
// standing in for a different microservice or a DBA writing straight to the
// database.
public class StaleCacheAfterDirectSqlDemo {

    public static void main(String[] args) throws Exception {
        String dbName = "stale_cache_demo_" + System.nanoTime();
        SessionFactory sessionFactory = HibernateSupport.buildSessionFactory(dbName, true);
        Statistics stats = sessionFactory.getStatistics();
        try {
            Long productId;
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                Product product = new Product("Widget", 100);
                session.persist(product);
                session.getTransaction().commit();
                productId = product.getId();
            }

            // Populate the L2 cache with the initial state.
            try (Session session = sessionFactory.openSession()) {
                session.get(Product.class, productId);
            }
            stats.clear();

            System.out.println("=== A write through a REAL, completely separate JDBC connection -- ");
            System.out.println("    not through Hibernate's SessionFactory, connection pool, or query API at all ===");
            try (Connection rawConnection = DriverManager.getConnection(HibernateSupport.jdbcUrl(dbName), "sa", "");
                 Statement statement = rawConnection.createStatement()) {
                statement.executeUpdate("UPDATE product SET stock = 5 WHERE id = " + productId);
            }
            System.out.println("Real row updated via a real, independent JDBC connection -- Hibernate's L2 cache was never told.");

            System.out.println();
            System.out.println("=== A NEW Hibernate session loads the same entity ===");
            try (Session session = sessionFactory.openSession()) {
                Product loaded = session.get(Product.class, productId);
                System.out.println("Real loaded stock: " + loaded.getStock() + " (real row value is 5; stale L2 value would be 100)");
            }
            System.out.println("Real L2 cache hits: " + stats.getSecondLevelCacheHitCount()
                    + ", misses: " + stats.getSecondLevelCacheMissCount()
                    + " (a hit with stale=100 proves the bug; a miss means the cache genuinely had no stale data to serve)");
        } finally {
            sessionFactory.close();
        }
    }
}
