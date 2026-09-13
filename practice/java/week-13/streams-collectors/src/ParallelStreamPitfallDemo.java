import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelStreamPitfallDemo {
    public static void main(String[] args) {
        System.out.println("== A non-thread-safe accumulator corrupted by a parallel stream ==");
        List<Integer> sharedList = new ArrayList<>();
        IntStream.range(0, 100_000).parallel().forEach(sharedList::add);
        System.out.println("Expected size: 100000, actual size: " + sharedList.size()
                + (sharedList.size() != 100_000 ? "  <-- CORRUPTED, lost updates from concurrent ArrayList.add()" : ""));

        System.out.println();
        System.out.println("== The correct, thread-safe way: a proper collector ==");
        List<Integer> collected = IntStream.range(0, 100_000).parallel()
                .boxed()
                .collect(Collectors.toList());
        System.out.println("Collector-based size: " + collected.size() + " (always correct)");

        System.out.println();
        System.out.println("== Parallel streams do not help small or IO-light workloads: measured (with JIT warmup) ==");
        int n = 1000;
        int warmupIters = 20_000;
        int measuredIters = 20_000;

        // Warm up both paths so the JIT has fully compiled both before timing either --
        // a single untuned nanoTime() call is not a reliable benchmark.
        for (int i = 0; i < warmupIters; i++) {
            IntStream.range(0, n).mapToLong(x -> x).sum();
            IntStream.range(0, n).parallel().mapToLong(x -> x).sum();
        }

        long sequentialTotalNanos = 0;
        long sequentialSum = 0;
        for (int i = 0; i < measuredIters; i++) {
            long start = System.nanoTime();
            sequentialSum = IntStream.range(0, n).mapToLong(x -> x).sum();
            sequentialTotalNanos += System.nanoTime() - start;
        }

        long parallelTotalNanos = 0;
        long parallelSum = 0;
        for (int i = 0; i < measuredIters; i++) {
            long start = System.nanoTime();
            parallelSum = IntStream.range(0, n).parallel().mapToLong(x -> x).sum();
            parallelTotalNanos += System.nanoTime() - start;
        }

        System.out.printf("Sequential sum=%d, avg over %,d iters: %,d ns/iter%n",
                sequentialSum, measuredIters, sequentialTotalNanos / measuredIters);
        System.out.printf("Parallel   sum=%d, avg over %,d iters: %,d ns/iter%n",
                parallelSum, measuredIters, parallelTotalNanos / measuredIters);
        System.out.println("(after proper warmup, the fork/join task-splitting and thread-handoff overhead "
                + "of parallel() is visible for a workload this cheap-per-element and this small)");

        AtomicInteger raceCounter = new AtomicInteger(0);
        int[] plainInt = {0};
        IntStream.range(0, 100_000).parallel().forEach(i -> plainInt[0]++);
        IntStream.range(0, 100_000).parallel().forEach(i -> raceCounter.incrementAndGet());
        System.out.println();
        System.out.println("== Non-atomic shared counter vs AtomicInteger under the same parallel stream ==");
        System.out.println("plain int[] counter (data race): " + plainInt[0] + " (expected 100000)");
        System.out.println("AtomicInteger counter (correct): " + raceCounter.get() + " (expected 100000)");
    }
}
