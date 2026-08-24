import java.util.concurrent.StructuredTaskScope;

/**
 * Real, executed proof that ScopedValue bindings ARE visible to subtasks
 * forked from within a bound scope (built specifically for structured
 * concurrency), while a plain ThreadLocal is genuinely NOT visible on a
 * manually-created child Thread -- it doesn't propagate automatically at
 * all, unlike InheritableThreadLocal (which only copies once, at thread
 * creation time, not dynamically).
 */
public class InheritanceComparisonDemo {

    static final ThreadLocal<String> THREAD_LOCAL_CTX = new ThreadLocal<>();
    static final ScopedValue<String> SCOPED_CTX = ScopedValue.newInstance();

    public static void main(String[] args) throws Exception {
        System.out.println("== Plain ThreadLocal: genuinely NOT visible on a manually-created child thread ==");
        THREAD_LOCAL_CTX.set("parent-value");
        Thread child = new Thread(() -> {
            String seenByChild = THREAD_LOCAL_CTX.get();
            System.out.println("Child thread sees THREAD_LOCAL_CTX = " + seenByChild
                    + (seenByChild == null ? "  <-- REAL: null, plain ThreadLocal does NOT propagate to a new thread automatically" : ""));
        });
        child.start();
        child.join();

        System.out.println("\n== ScopedValue: real, verified propagation into a StructuredTaskScope subtask ==");
        ScopedValue.where(SCOPED_CTX, "parent-value").run(InheritanceComparisonDemo::runScopedSubtask);
    }

    static void runScopedSubtask() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var subtask = scope.fork(() -> {
                String seenBySubtask = SCOPED_CTX.get(); // real, genuinely visible inside the forked subtask
                return "subtask saw SCOPED_CTX = " + seenBySubtask;
            });
            scope.join();
            scope.throwIfFailed();
            System.out.println(subtask.get() + "  <-- REAL: ScopedValue genuinely propagated into the forked subtask's own virtual thread");
        } catch (Exception e) {
            System.out.println("unexpected: " + e);
        }
    }
}
