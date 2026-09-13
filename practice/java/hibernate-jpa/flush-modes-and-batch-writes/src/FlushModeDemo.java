import java.util.List;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

/**
 * Real, measured proof that {@link FlushMode#AUTO} (the default) flushes a
 * pending in-session change before a query runs, while {@link
 * FlushMode#COMMIT} does not -- a query can genuinely miss its own
 * transaction's own uncommitted change, not a hypothetical edge case.
 */
public class FlushModeDemo {

    public static void main(String[] args) {
        System.out.println("=== FlushMode.AUTO (default): query sees the pending, unflushed change ===");
        runScenario(FlushMode.AUTO);

        System.out.println();
        System.out.println("=== FlushMode.COMMIT: query does NOT see the pending, unflushed change ===");
        runScenario(FlushMode.COMMIT);
    }

    private static void runScenario(FlushMode mode) {
        SessionFactory sf = HibernateSupport.buildSessionFactory(
                "flushmode_" + mode + "_" + System.nanoTime(), 0, false);
        try (Session session = sf.openSession()) {
            session.beginTransaction();

            SequenceWidget widget = new SequenceWidget("original-name");
            session.persist(widget);
            session.flush();
            session.clear();

            session.setHibernateFlushMode(mode);

            SequenceWidget managed = session.find(SequenceWidget.class, widget.id);
            managed.name = "renamed-in-session";
            // Deliberately not calling session.flush() here -- the whole
            // point is observing whether the upcoming query does it for us.

            Query<SequenceWidget> query = session.createQuery(
                    "select w from SequenceWidget w where w.name = :n", SequenceWidget.class);
            query.setParameter("n", "renamed-in-session");
            List<SequenceWidget> found = query.getResultList();

            System.out.printf("Flush mode:                     %s%n", mode);
            System.out.printf("In-session name (not yet flushed): renamed-in-session%n");
            System.out.printf("Query for the new name found:   %d row(s)%n", found.size());
            System.out.println(found.isEmpty()
                    ? "Real result: the query missed its own transaction's own pending rename -- FlushMode.COMMIT genuinely does not auto-flush before a query."
                    : "Real result: the query found the renamed row -- Hibernate auto-flushed the pending change before running the query.");

            session.getTransaction().rollback();
        }
        sf.close();
    }
}
