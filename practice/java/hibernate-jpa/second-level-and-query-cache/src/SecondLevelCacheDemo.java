import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

public class SecondLevelCacheDemo {

    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateSupport.buildSessionFactory("l2cache_demo_" + System.nanoTime(), true);
        try {
            Long productId;
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                Product product = new Product("Widget", 100);
                session.persist(product);
                session.getTransaction().commit();
                productId = product.getId();
            }

            Statistics stats = sessionFactory.getStatistics();
            stats.clear();

            System.out.println("=== First load, session A: real DB hit expected ===");
            try (Session sessionA = sessionFactory.openSession()) {
                Product loaded = sessionA.get(Product.class, productId);
                System.out.println("Loaded: " + loaded);
            }
            System.out.println("Real L2 cache misses: " + stats.getSecondLevelCacheMissCount() + " (expect 1)");
            System.out.println("Real L2 cache hits: " + stats.getSecondLevelCacheHitCount() + " (expect 0)");
            System.out.println("Real L2 cache puts: " + stats.getSecondLevelCachePutCount() + " (expect 1)");

            System.out.println();
            System.out.println("=== Second load, session B (a DIFFERENT session): real L2 cache hit expected, no new SQL ===");
            try (Session sessionB = sessionFactory.openSession()) {
                Product loaded = sessionB.get(Product.class, productId);
                System.out.println("Loaded: " + loaded);
            }
            System.out.println("Real L2 cache misses: " + stats.getSecondLevelCacheMissCount() + " (expect still 1)");
            System.out.println("Real L2 cache hits: " + stats.getSecondLevelCacheHitCount() + " (expect 1 -- served from L2, no SQL for session B)");
        } finally {
            sessionFactory.close();
        }
    }
}
