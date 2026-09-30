import java.util.concurrent.atomic.AtomicInteger;

/**
 * Counts how many times each of a Collector's four functions is actually
 * invoked by the JDK. The whole demo rests on these numbers being real
 * invocation counts rather than assertions about what "should" happen.
 */
final class LifecycleCounters {

    final AtomicInteger supplier = new AtomicInteger();
    final AtomicInteger accumulator = new AtomicInteger();
    final AtomicInteger combiner = new AtomicInteger();
    final AtomicInteger finisher = new AtomicInteger();

    void reset() {
        supplier.set(0);
        accumulator.set(0);
        combiner.set(0);
        finisher.set(0);
    }

    @Override
    public String toString() {
        return String.format("supplier=%-4d accumulator=%-6d combiner=%-4d finisher=%d",
                supplier.get(), accumulator.get(), combiner.get(), finisher.get());
    }
}
