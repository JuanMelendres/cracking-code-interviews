import java.util.concurrent.atomic.AtomicInteger;

/**
 * Real, process-wide counters of actual JDBC {@code executeBatch()} vs
 * {@code executeUpdate()} calls, incremented by {@link CountingConnectionProvider}'s
 * dynamic proxy. This is the mechanical, decisive evidence this pack uses to
 * prove whether Hibernate is really batching inserts or silently falling back
 * to one round trip per row -- not inferred from timing or documentation.
 */
final class BatchCallCounter {
    static final AtomicInteger executeBatchCalls = new AtomicInteger();
    static final AtomicInteger executeUpdateCalls = new AtomicInteger();
    static final AtomicInteger totalRowsInsertedViaBatch = new AtomicInteger();

    static void reset() {
        executeBatchCalls.set(0);
        executeUpdateCalls.set(0);
        totalRowsInsertedViaBatch.set(0);
    }

    private BatchCallCounter() {
    }
}
