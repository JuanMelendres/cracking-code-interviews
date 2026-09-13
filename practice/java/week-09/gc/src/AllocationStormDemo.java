import java.util.ArrayList;
import java.util.List;

/**
 * T-303/T-306 -- generates a real, forced allocation storm so a real GC
 * log can be captured and read, rather than described. Run with:
 *   java -Xmx256m -Xlog:gc*:file=gc.log:time,level,tags -cp out AllocationStormDemo
 * A small heap and a mix of short-lived garbage plus some retained
 * ("survivor") objects forces several young-gen collections and at
 * least one full/major collection to actually appear in the log.
 */
public class AllocationStormDemo {
    public static void main(String[] args) {
        List<byte[]> retained = new ArrayList<>(); // intentionally retained -- creates promotion pressure
        long totalAllocatedBytes = 0;
        for (int i = 0; i < 5_000_000; i++) {
            byte[] garbage = new byte[1024]; // short-lived, dies immediately -- young-gen churn
            totalAllocatedBytes += garbage.length;
            if (i % 2000 == 0) {
                retained.add(new byte[8192]); // occasionally retain something -- survives into old gen
                totalAllocatedBytes += 8192;
            }
        }
        System.out.println("Allocated ~" + (totalAllocatedBytes / 1024 / 1024) + "MB total, retained " + retained.size() + " objects");
        System.out.println("(retained.size() referenced here so the JIT can't dead-code-eliminate the retention list)");
    }
}
