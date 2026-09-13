import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.stat.Statistics;

class EntityLifecycleDemo {
    public static void main(String[] args) {
        SessionFactory sf = HibernateSupport.buildSessionFactory(true);
        Statistics stats = sf.getStatistics();

        Long authorId;
        try (Session session = sf.openSession()) {
            Transaction tx = session.beginTransaction();
            Author author = new Author("Ada Lovelace");
            session.persist(author);
            tx.commit();
            authorId = author.getId();
        }

        System.out.println();
        System.out.println("== 1. The identity map: the SAME managed entity, fetched twice in one session, is the SAME Java object ==");
        try (Session session = sf.openSession()) {
            Author first = session.find(Author.class, authorId);
            Author second = session.find(Author.class, authorId);
            System.out.println("first == second (reference equality): " + (first == second));
            System.out.println("(the second find() did NOT issue a new SELECT -- Hibernate's persistence context returned the already-managed instance)");
        }

        System.out.println();
        System.out.println("== 2. Dirty checking: mutating a managed entity's field, with NO explicit save()/update() call ==");
        stats.clear();
        try (Session session = sf.openSession()) {
            Transaction tx = session.beginTransaction();
            Author author = session.find(Author.class, authorId);
            author.setName("Ada Lovelace, Countess of Lovelace"); // plain setter -- nothing "save"-shaped is called
            tx.commit(); // Hibernate compares the entity's current state to its loaded snapshot and flushes an UPDATE automatically
        }
        System.out.println("Entity update statements issued by Hibernate: " + stats.getEntityUpdateCount() + "  (an UPDATE fired with no explicit save/update call -- this is dirty checking)");

        System.out.println();
        System.out.println("== 3. A detached entity's lazy collection throws when accessed after its session is closed ==");
        Author detached;
        try (Session session = sf.openSession()) {
            detached = session.find(Author.class, authorId);
        } // session closed here -- 'detached' now has no active persistence context behind it
        try {
            int size = detached.getBooks().size(); // triggers lazy initialization -- but there's no session left to do it with
            System.out.println("UNEXPECTED: got books size = " + size);
        } catch (LazyInitializationException e) {
            System.out.println("Accessing the lazy collection threw: " + e.getClass().getSimpleName());
            System.out.println("(\"" + e.getMessage() + "\")");
            System.out.println("This is the single most common real Hibernate production bug: an entity fetched in one layer,");
            System.out.println("read in another after its session/transaction has already closed.");
        }

        sf.close();
    }
}
