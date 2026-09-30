import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Measures which of a Collector's four functions the JDK actually calls, and
 * when. Java 21. No dependencies.
 *
 * <p>Three claims are commonly repeated about collectors and are all testable:
 * that the combiner is unused in a sequential stream, that a finisher declared
 * IDENTITY_FINISH is skipped rather than merely cheap, and that a CONCURRENT
 * collector accumulates into one shared container instead of merging partials.
 */
public final class CollectorLifecycleDemo {

    static final LifecycleCounters COUNTERS = new LifecycleCounters();

    /** A Collector equivalent to toList(), but fully instrumented. */
    static Collector<String, List<String>, List<String>> countingToList(
            Collector.Characteristics... characteristics) {
        return Collector.of(
                () -> { COUNTERS.supplier.incrementAndGet(); return new ArrayList<>(); },
                (list, item) -> { COUNTERS.accumulator.incrementAndGet(); list.add(item); },
                (a, b) -> { COUNTERS.combiner.incrementAndGet(); a.addAll(b); return a; },
                list -> { COUNTERS.finisher.incrementAndGet(); return list; },
                characteristics);
    }

    static List<String> input(int size) {
        return IntStream.range(0, size).mapToObj(i -> "item-" + i).toList();
    }

    public static void main(String[] args) {
        List<String> data = input(1000);

        System.out.println("== 1. Is the combiner used in a SEQUENTIAL stream? ==");
        COUNTERS.reset();
        List<String> sequential = data.stream().collect(countingToList());
        System.out.println("  sequential : " + COUNTERS + "  result size=" + sequential.size());

        COUNTERS.reset();
        List<String> parallel = data.parallelStream().collect(countingToList());
        System.out.println("  parallel   : " + COUNTERS + "  result size=" + parallel.size());
        System.out.println("  Available processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println();

        System.out.println("== 2. Does IDENTITY_FINISH actually SKIP the finisher? ==");
        COUNTERS.reset();
        data.stream().collect(countingToList());
        int withoutFlag = COUNTERS.finisher.get();

        COUNTERS.reset();
        data.stream().collect(countingToList(Collector.Characteristics.IDENTITY_FINISH));
        int withFlag = COUNTERS.finisher.get();

        System.out.println("  finisher invocations WITHOUT IDENTITY_FINISH: " + withoutFlag);
        System.out.println("  finisher invocations WITH    IDENTITY_FINISH: " + withFlag);
        System.out.println();

        System.out.println("== 3. What characteristics do the built-in collectors declare? ==");
        printCharacteristics("toList()", Collectors.toList().characteristics());
        printCharacteristics("toUnmodifiableList()", Collectors.toUnmodifiableList().characteristics());
        printCharacteristics("toSet()", Collectors.toSet().characteristics());
        printCharacteristics("joining()", Collectors.joining().characteristics());
        printCharacteristics("counting()", Collectors.counting().characteristics());
        printCharacteristics("groupingBy(f)", Collectors.groupingBy(String::length).characteristics());
        printCharacteristics("groupingByConcurrent(f)",
                Collectors.groupingByConcurrent(String::length).characteristics());
        printCharacteristics("toMap(k,v)",
                Collectors.toMap(s -> s, String::length).characteristics());
        printCharacteristics("toConcurrentMap(k,v)",
                Collectors.toConcurrentMap(s -> s, String::length).characteristics());
        System.out.println();

        System.out.println("== 4. CONCURRENT: one shared container, or merged partials? ==");
        measureGrouping("groupingBy           (parallel)", false);
        measureGrouping("groupingByConcurrent (parallel)", true);
        System.out.println();

        System.out.println("== 5. What a BROKEN combiner costs, sequential vs parallel ==");
        // This combiner is wrong: it drops the second partial result entirely.
        // A sequential stream never calls it, so the bug is invisible there.
        Collector<String, List<String>, List<String>> brokenCombiner = Collector.of(
                ArrayList::new,
                List::add,
                (a, b) -> a);   // <-- silently discards everything accumulated in b

        int size = data.size();
        System.out.println("  input size                     : " + size);
        System.out.println("  sequential with broken combiner: "
                + data.stream().collect(brokenCombiner).size() + "  <- correct, combiner never ran");
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.println("  parallel   with broken combiner: "
                    + data.parallelStream().collect(brokenCombiner).size()
                    + "  <- silent data loss (run " + attempt + ")");
        }
    }

    static void printCharacteristics(String name, Set<Collector.Characteristics> set) {
        System.out.printf("  %-26s %s%n", name, set.isEmpty() ? "(none)" : set);
    }

    /**
     * Counts how many downstream containers get created. With a non-concurrent
     * collector each thread accumulates into its own container and the results
     * are merged; a CONCURRENT collector is allowed to share one.
     */
    static void measureGrouping(String label, boolean concurrent) {
        LifecycleCounters downstream = new LifecycleCounters();
        Collector<String, ?, List<String>> instrumented = Collector.of(
                () -> { downstream.supplier.incrementAndGet(); return new ArrayList<String>(); },
                (list, item) -> { downstream.accumulator.incrementAndGet(); list.add(item); },
                (a, b) -> { downstream.combiner.incrementAndGet(); a.addAll(b); return a; });

        List<String> data = input(10_000);
        int groups;
        if (concurrent) {
            groups = data.parallelStream()
                    .collect(Collectors.groupingByConcurrent(s -> s.length(), instrumented))
                    .size();
        } else {
            groups = data.parallelStream()
                    .collect(Collectors.groupingBy(s -> s.length(), instrumented))
                    .size();
        }
        System.out.printf("  %-34s %s  groups=%d%n", label, downstream, groups);
    }
}
