import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Real, measured proof that {@code hibernate.jdbc.batch_size} batches
 * inserts for a {@code SEQUENCE}-generated entity but is silently ignored
 * for an {@code IDENTITY}-generated entity -- counted via real JDBC
 * {@code executeBatch()}/{@code executeUpdate()} calls (see
 * {@link CountingConnectionProvider}), not inferred from timing or docs.
 */
public class BatchInsertDemo {

    private static final int ROW_COUNT = 40;
    private static final int BATCH_SIZE = 10;

    public static void main(String[] args) {
        System.out.println("=== SEQUENCE-generated entity, hibernate.jdbc.batch_size=" + BATCH_SIZE + " ===");
        runInsertTest(true);

        System.out.println();
        System.out.println("=== IDENTITY-generated entity, identical hibernate.jdbc.batch_size=" + BATCH_SIZE + " ===");
        runInsertTest(false);
    }

    private static void runInsertTest(boolean useSequence) {
        BatchCallCounter.reset();
        SessionFactory sf = HibernateSupport.buildSessionFactory(
                "batchdemo_" + useSequence + "_" + System.nanoTime(), BATCH_SIZE, false);
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            for (int i = 0; i < ROW_COUNT; i++) {
                if (useSequence) {
                    session.persist(new SequenceWidget("widget-" + i));
                } else {
                    session.persist(new IdentityWidget("widget-" + i));
                }
                if (i % BATCH_SIZE == 0) {
                    session.flush();
                    session.clear();
                }
            }
            session.getTransaction().commit();
        }
        sf.close();

        System.out.printf("Rows inserted:            %d%n", ROW_COUNT);
        System.out.printf("Real executeBatch() calls: %d (rows sent via batching: %d)%n",
                BatchCallCounter.executeBatchCalls.get(), BatchCallCounter.totalRowsInsertedViaBatch.get());
        System.out.printf("Real executeUpdate() calls: %d (rows sent one at a time)%n",
                BatchCallCounter.executeUpdateCalls.get());
    }
}
