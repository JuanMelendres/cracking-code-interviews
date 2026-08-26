import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A real, deliberately inefficient workload for a real JDK Flight Recorder capture
 * to profile. Two distinctly-named hotspots run concurrently for a fixed duration:
 * {@code quadraticStringBuild} (a real O(n^2) CPU hotspot, string concatenation in a
 * loop instead of StringBuilder) and {@code allocateManyShortLivedRecords} (a real
 * allocation hotspot, boxing and discarding objects continuously). A third,
 * genuinely fast baseline method runs alongside both, so the profile has to
 * distinguish a real hotspot from real, unremarkable work -- not just report
 * "everything is slow."
 */
public final class HotspotWorkload {

    static volatile long sink; // prevents dead-code elimination of the "fast" path

    public static void main(String[] args) throws Exception {
        long durationSeconds = args.length > 0 ? Long.parseLong(args[0]) : 8;
        System.out.println("Running workload for " + durationSeconds + " real seconds...");

        CountDownLatch stop = new CountDownLatch(1);
        AtomicLong cpuIterations = new AtomicLong();
        AtomicLong allocIterations = new AtomicLong();
        AtomicLong fastIterations = new AtomicLong();

        Thread cpuThread = new Thread(() -> {
            while (stop.getCount() > 0) {
                quadraticStringBuild(400);
                cpuIterations.incrementAndGet();
            }
        }, "cpu-hotspot-thread");

        Thread allocThread = new Thread(() -> {
            while (stop.getCount() > 0) {
                allocateManyShortLivedRecords(5000);
                allocIterations.incrementAndGet();
            }
        }, "alloc-hotspot-thread");

        Thread fastThread = new Thread(() -> {
            while (stop.getCount() > 0) {
                fastChecksum(10000);
                fastIterations.incrementAndGet();
            }
        }, "fast-baseline-thread");

        cpuThread.start();
        allocThread.start();
        fastThread.start();

        TimeUnit.SECONDS.sleep(durationSeconds);
        stop.countDown();
        cpuThread.join();
        allocThread.join();
        fastThread.join();

        System.out.println("Done. cpuIterations=" + cpuIterations.get()
                + " allocIterations=" + allocIterations.get()
                + " fastIterations=" + fastIterations.get());
    }

    /** Deliberate CPU hotspot: O(n^2) via String concatenation in a loop. */
    private static void quadraticStringBuild(int n) {
        String s = "";
        for (int i = 0; i < n; i++) {
            s = s + i; // each += reallocates and copies the whole string -- O(n^2)
        }
        sink += s.length();
    }

    /** Deliberate allocation hotspot: many short-lived boxed objects and Lists. */
    private static void allocateManyShortLivedRecords(int n) {
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(Long.valueOf(i)); // real boxing allocation each iteration
        }
        sink += list.size();
    }

    /** Fast baseline: genuinely cheap, O(n), no allocation -- the "not a hotspot." */
    private static long fastChecksum(int n) {
        long sum = 0;
        for (int i = 0; i < n; i++) {
            sum += i;
        }
        sink += sum;
        return sum;
    }
}
