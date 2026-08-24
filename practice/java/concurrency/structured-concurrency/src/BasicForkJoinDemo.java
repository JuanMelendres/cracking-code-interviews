import java.util.concurrent.StructuredTaskScope;

/**
 * Real, executed StructuredTaskScope basics (JDK 21 preview, JEP 453):
 * fork two independent, real, concurrent subtasks; join() blocks until BOTH
 * complete (or one fails); combine their real results after the scope
 * closes. Requires --enable-preview to compile and run on JDK 21.
 */
public class BasicForkJoinDemo {

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var userTask = scope.fork(() -> fetchUser(200));
            var ordersTask = scope.fork(() -> fetchOrders(250));

            scope.join();
            scope.throwIfFailed();

            long elapsed = System.currentTimeMillis() - start;
            System.out.println("user=" + userTask.get() + ", orders=" + ordersTask.get());
            System.out.println("Real elapsed: " + elapsed + "ms -- both ~200ms and ~250ms calls ran CONCURRENTLY,"
                    + " not sequentially (sequential would be ~450ms)");
        }
    }

    static String fetchUser(long millis) throws InterruptedException {
        Thread.sleep(millis);
        return "user-42";
    }

    static String fetchOrders(long millis) throws InterruptedException {
        Thread.sleep(millis);
        return "orders-[7,8,9]";
    }
}
