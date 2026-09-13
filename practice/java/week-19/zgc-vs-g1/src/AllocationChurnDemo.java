import java.util.ArrayList;
import java.util.List;

// Real, deliberately allocation-heavy workload: continuously allocates
// short-lived 2KB objects (churn) while retaining roughly 10% of them in a
// growing list (so the live set grows over the run, forcing the collector
// to do real work, not just reclaim pure garbage). Same class run under
// both -XX:+UseG1GC and -XX:+UseZGC to compare real pause-time behavior.
public class AllocationChurnDemo {
    public static void main(String[] args) throws Exception {
        List<byte[]> retained = new ArrayList<>();
        long endAt = System.currentTimeMillis() + 3000; // run for 3 seconds
        int i = 0;
        while (System.currentTimeMillis() < endAt) {
            byte[] chunk = new byte[2048];
            chunk[0] = (byte) i;
            if (i % 500 == 0) {
                retained.add(chunk); // retain a small fraction -- live set grows slowly over the run
            }
            i++;
        }
        System.out.println("allocations=" + i + " retained=" + retained.size());
    }
}
