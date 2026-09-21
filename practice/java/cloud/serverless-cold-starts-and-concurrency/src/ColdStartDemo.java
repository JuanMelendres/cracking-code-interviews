/**
 * Real, measured comparison of a cold invocation (a fresh
 * ExecutionEnvironment must be constructed -- real INIT-phase work --
 * before the handler can run) versus a warm invocation (an already-live
 * environment's invoke() is called directly). No mocked clock -- real
 * System.nanoTime() around real work.
 */
public class ColdStartDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Cold invocation: construct a fresh ExecutionEnvironment, then invoke once ===");
        long coldStart = System.nanoTime();
        ExecutionEnvironment coldEnv = new ExecutionEnvironment();
        String coldResult = coldEnv.invoke("request-1");
        long coldElapsedMillis = (System.nanoTime() - coldStart) / 1_000_000;
        System.out.println("  " + coldResult);
        System.out.println("  Real elapsed (INIT + first invoke): " + coldElapsedMillis + "ms");

        System.out.println();
        System.out.println("=== Warm invocations: reuse the SAME environment for 10 more calls ===");
        long warmTotalNanos = 0;
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            coldEnv.invoke("request-" + (i + 2));
            warmTotalNanos += (System.nanoTime() - start);
        }
        double warmAvgMillis = (warmTotalNanos / 10.0) / 1_000_000.0;
        System.out.printf("  Real average elapsed per warm invoke: %.3fms%n", warmAvgMillis);

        System.out.println();
        System.out.println("=== Result ===");
        System.out.printf("  Cold invocation (%dms) was %.0fx slower than the average warm invocation (%.3fms).%n",
                coldElapsedMillis, coldElapsedMillis / warmAvgMillis, warmAvgMillis);
        System.out.println("  This is the real, structural reason cold starts exist: a fresh execution");
        System.out.println("  environment must pay its INIT cost once before ANY invocation can proceed,");
        System.out.println("  and that cost is invisible to every invocation after the first.");
    }
}
