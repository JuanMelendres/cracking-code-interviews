import java.util.NoSuchElementException;

/**
 * Real, executed ScopedValue basics (JDK 21 preview, second preview --
 * requires --enable-preview to compile and run). Proves the core contract:
 * a ScopedValue is bound only for the DYNAMIC EXTENT of the run()/call()
 * that binds it -- readable inside, genuinely unbound (throws) outside,
 * and with no set() method at all (immutable within its binding).
 */
public class ScopedValueBasicsDemo {

    static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    public static void main(String[] args) {
        System.out.println("== Reading REQUEST_ID before any binding exists ==");
        try {
            REQUEST_ID.get();
            System.out.println("get() succeeded (unexpected)");
        } catch (NoSuchElementException e) {
            System.out.println("get() threw real NoSuchElementException: no binding exists yet");
        }
        System.out.println("isBound() before binding: " + REQUEST_ID.isBound());

        System.out.println("\n== Real binding, visible only for the dynamic extent of run() ==");
        ScopedValue.where(REQUEST_ID, "req-42").run(() -> {
            System.out.println("Inside run(): REQUEST_ID.get() = " + REQUEST_ID.get() + ", isBound()=" + REQUEST_ID.isBound());
            callNestedMethod(); // real proof the binding propagates down the call stack, not just the lambda itself
        });

        System.out.println("\n== After run() returns: binding is real gone ==");
        System.out.println("isBound() after run() returned: " + REQUEST_ID.isBound());
        try {
            REQUEST_ID.get();
            System.out.println("get() succeeded (unexpected)");
        } catch (NoSuchElementException e) {
            System.out.println("get() threw real NoSuchElementException again: the binding's dynamic extent genuinely ended");
        }

        System.out.println("\n== Nested rebinding: real, structured shadowing ==");
        ScopedValue.where(REQUEST_ID, "outer").run(() -> {
            System.out.println("Outer binding: " + REQUEST_ID.get());
            ScopedValue.where(REQUEST_ID, "inner").run(() -> {
                System.out.println("Inner binding (shadows outer): " + REQUEST_ID.get());
            });
            System.out.println("Back in outer scope, real restoration: " + REQUEST_ID.get());
        });
    }

    static void callNestedMethod() {
        // No parameter passing needed -- the binding is visible down the
        // real call stack, exactly like a dynamically-scoped variable.
        System.out.println("  Inside a nested method call (no parameter passed): REQUEST_ID.get() = " + REQUEST_ID.get());
    }
}
