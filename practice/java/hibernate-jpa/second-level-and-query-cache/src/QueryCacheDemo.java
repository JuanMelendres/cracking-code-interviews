import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

public class QueryCacheDemo {

    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateSupport.buildSessionFactory("query_cache_demo_" + System.nanoTime(), true);
        try {
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                session.persist(new Product("Widget", 100));
                session.persist(new Product("Gadget", 50));
                session.getTransaction().commit();
            }

            Statistics stats = sessionFactory.getStatistics();
            stats.clear();

            String hql = "from Product p where p.stock > 10";

            System.out.println("=== First run, session A: real query cache miss + real DB query ===");
            try (Session sessionA = sessionFactory.openSession()) {
                List<Product> results = sessionA.createQuery(hql, Product.class)
                        .setCacheable(true)
                        .list();
                System.out.println("Real results: " + results.size());
            }
            System.out.println("Real query cache misses: " + stats.getQueryCacheMissCount() + " (expect 1)");
            System.out.println("Real query cache hits: " + stats.getQueryCacheHitCount() + " (expect 0)");

            System.out.println();
            System.out.println("=== Second run, session B: real query cache hit -- no SQL re-issued for the query itself ===");
            try (Session sessionB = sessionFactory.openSession()) {
                List<Product> results = sessionB.createQuery(hql, Product.class)
                        .setCacheable(true)
                        .list();
                System.out.println("Real results: " + results.size());
            }
            System.out.println("Real query cache misses: " + stats.getQueryCacheMissCount() + " (expect still 1)");
            System.out.println("Real query cache hits: " + stats.getQueryCacheHitCount() + " (expect 1)");
        } finally {
            sessionFactory.close();
        }
    }
}
