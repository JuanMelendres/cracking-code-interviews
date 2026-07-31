import java.util.*;

/**
 * Demonstrates that G1's remembered-set (RSet) machinery has a REAL, measurable
 * cost tied directly to cross-region reference writes -- not to allocation volume.
 *
 * Every store of a reference field is intercepted by a write barrier, which marks
 * ("dirties") the enclosing card if the write is a candidate for creating a
 * cross-region pointer. During the next young collection, G1 must merge and scan
 * those dirty cards to keep each region's remembered set (the record of "who
 * points into me from other regions") accurate before it can safely evacuate.
 *
 * Run with `-Xlog:gc+phases=debug` and grep for "Dirty Cards" / "Scanned Cards" /
 * "Merged Cards" to see the real per-pause card-table activity these two
 * scenarios produce.
 *
 * Usage:
 *   java -Xmx128m -Xlog:gc+phases=debug:file=low.log  RememberedSetCostDemo low
 *   java -Xmx128m -Xlog:gc+phases=debug:file=high.log RememberedSetCostDemo high
 */
public class RememberedSetCostDemo {

    // A long-lived array in old-gen (once promoted) -- the write TARGET for the
    // "high" scenario. Every store into a slot here is a candidate cross-region
    // write once this array itself has been promoted out of the young generation.
    static Object[] longLivedTable = new Object[200_000];

    public static void main(String[] args) throws Exception {
        String mode = args.length > 0 ? args[0] : "low";
        int iterations = 30_000_000;

        // Force longLivedTable to actually get promoted to old-gen before the
        // measured loop starts, so subsequent writes into it are genuinely
        // cross-region (old array slot -> young object) once GC runs again.
        for (int warm = 0; warm < 5; warm++) {
            byte[] pressure = new byte[4 * 1024 * 1024];
            pressure[0] = 1;
        }
        System.gc();

        Random r = new Random(42);

        if (mode.equals("low")) {
            // LOW cross-region writes: allocate and immediately drop short-lived
            // objects. longLivedTable is never touched after warmup, so it
            // generates essentially no new dirty cards.
            long sink = 0;
            for (int i = 0; i < iterations; i++) {
                int[] shortLived = new int[8];
                shortLived[0] = i;
                sink += shortLived[0];
            }
            System.out.println("low-cross-region scenario complete, sink=" + sink);

        } else {
            // HIGH cross-region writes: every iteration STORES a freshly allocated
            // (young-gen) object's reference into a slot of the long-lived
            // (old-gen, post-promotion) table. Each such store is exactly the
            // write-barrier-triggering pattern RSets exist to track.
            for (int i = 0; i < iterations; i++) {
                Integer freshObject = i; // young-gen allocation (boxing)
                longLivedTable[i % longLivedTable.length] = freshObject; // cross-region store
            }
            System.out.println("high-cross-region scenario complete, table[0]=" + longLivedTable[0]);
        }
    }
}
