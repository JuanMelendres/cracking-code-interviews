import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Real, measured proof of the classic orElse() vs orElseGet() gotcha:
 * orElse(x) ALWAYS evaluates its argument eagerly, even when the Optional
 * is already present and the fallback value will be discarded. orElseGet()
 * only evaluates its Supplier lazily, when actually needed. Proven with a
 * real side-effecting counter, not a comment claiming this is true.
 */
public class OrElseVsOrElseGetDemo {

    public static void main(String[] args) {
        Optional<String> present = Optional.of("already-have-a-value");

        AtomicInteger orElseCallCount = new AtomicInteger(0);
        String r1 = present.orElse(expensiveFallback(orElseCallCount));
        System.out.println("orElse() on a PRESENT Optional: returned \"" + r1 + "\" (the real, present value)"
                + " -- but the fallback argument was STILL evaluated " + orElseCallCount.get()
                + " time(s) before orElse() was even called, its result silently discarded -- EAGER evaluation, real and measurable");

        AtomicInteger orElseGetCallCount = new AtomicInteger(0);
        String r2 = present.orElseGet(() -> expensiveFallback(orElseGetCallCount));
        System.out.println("orElseGet() on a PRESENT Optional: fallback was called " + orElseGetCallCount.get()
                + " time(s) -- LAZY evaluation, real and measurable, the Supplier is never even invoked");

        // Same comparison, but on an EMPTY Optional, where both should call the fallback exactly once.
        Optional<String> empty = Optional.empty();
        AtomicInteger orElseEmptyCount = new AtomicInteger(0);
        empty.orElse(expensiveFallback(orElseEmptyCount));
        AtomicInteger orElseGetEmptyCount = new AtomicInteger(0);
        empty.orElseGet(() -> expensiveFallback(orElseGetEmptyCount));
        System.out.println("\nOn an EMPTY Optional: orElse() called fallback " + orElseEmptyCount.get()
                + " time(s), orElseGet() called it " + orElseGetEmptyCount.get()
                + " time(s) -- identical when the Optional is actually empty, the difference ONLY shows up when present");

        System.out.println("\n== Real measured cost of the eager evaluation, with a genuinely expensive fallback ==");
        Optional<String> alreadyPresent = Optional.of("cached-value");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 5_000_000; i++) {
            alreadyPresent.orElse(genuinelyExpensiveComputation());
        }
        long orElseElapsed = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        for (int i = 0; i < 5_000_000; i++) {
            alreadyPresent.orElseGet(OrElseVsOrElseGetDemo::genuinelyExpensiveComputation);
        }
        long orElseGetElapsed = System.currentTimeMillis() - start;

        System.out.println("orElse(expensive computation), 5,000,000 calls, value already present: " + orElseElapsed + "ms");
        System.out.println("orElseGet(expensive computation), 5,000,000 calls, value already present: " + orElseGetElapsed + "ms");
        System.out.println("Real measured cost of the eager-evaluation bug: "
                + String.format("%.2fx", (double) orElseElapsed / Math.max(orElseGetElapsed, 1)));
    }

    static String expensiveFallback(AtomicInteger callCounter) {
        callCounter.incrementAndGet();
        return "expensive-fallback-result";
    }

    static String genuinelyExpensiveComputation() {
        // A real, non-trivial amount of work -- not a no-op -- to make the
        // eager-vs-lazy timing difference real and measurable.
        double x = 0;
        for (int i = 0; i < 200; i++) x += Math.sqrt(i) * Math.sin(i);
        return "computed-" + x;
    }
}
