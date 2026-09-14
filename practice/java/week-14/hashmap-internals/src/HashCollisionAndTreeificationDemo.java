import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Objects;

/**
 * Requires: java --add-opens java.base/java.util=ALL-UNNAMED HashCollisionAndTreeificationDemo
 */
public class HashCollisionAndTreeificationDemo {

    // A deliberately bad hashCode(): every instance hashes to the SAME value,
    // forcing every entry into the same bucket regardless of table size.
    static final class BadHashKey {
        final int id;
        BadHashKey(int id) { this.id = id; }

        @Override
        public boolean equals(Object o) {
            return o instanceof BadHashKey k && k.id == id;
        }

        @Override
        public int hashCode() { return 42; } // constant -- every key collides
    }

    static final class GoodHashKey {
        final int id;
        GoodHashKey(int id) { this.id = id; }

        @Override
        public boolean equals(Object o) {
            return o instanceof GoodHashKey k && k.id == id;
        }

        @Override
        public int hashCode() { return Objects.hashCode(id); } // well-distributed
    }

    public static void main(String[] args) throws Exception {
        int n = 50_000;

        System.out.println("== Lookup cost: well-distributed hash vs. a constant (all-collide) hash ==");
        HashMap<GoodHashKey, Integer> goodMap = new HashMap<>();
        for (int i = 0; i < n; i++) goodMap.put(new GoodHashKey(i), i);

        HashMap<BadHashKey, Integer> badMap = new HashMap<>();
        for (int i = 0; i < n; i++) badMap.put(new BadHashKey(i), i);

        long goodStart = System.nanoTime();
        for (int i = 0; i < n; i++) goodMap.get(new GoodHashKey(i));
        long goodNanos = System.nanoTime() - goodStart;

        long badStart = System.nanoTime();
        for (int i = 0; i < n; i++) badMap.get(new BadHashKey(i));
        long badNanos = System.nanoTime() - badStart;

        System.out.printf("Well-distributed hash: %,d lookups in %,d ns (%.1f ns/lookup)%n",
                n, goodNanos, (double) goodNanos / n);
        System.out.printf("Constant hash (all collide): %,d lookups in %,d ns (%.1f ns/lookup)%n",
                n, badNanos, (double) badNanos / n);
        System.out.printf("Slowdown factor: %.1fx%n", (double) badNanos / goodNanos);
        System.out.println("(every BadHashKey lands in the same bucket -- JDK 8+ treeifies bins this large,");
        System.out.println(" giving O(log n) instead of the pre-JDK-8 O(n) linked-list worst case, but still");
        System.out.println(" far slower than the O(1)-average case a well-distributed hash gives every bucket)");

        System.out.println();
        System.out.println("== Proving treeification: bin node type after forcing 8+ collisions into one bucket ==");
        Field tableField = HashMap.class.getDeclaredField("table");
        tableField.setAccessible(true);
        Object[] table = (Object[]) tableField.get(badMap);

        // Find the (single) non-null bucket -- every BadHashKey collided into it.
        Object bucketHead = null;
        for (Object bucket : table) {
            if (bucket != null) { bucketHead = bucket; break; }
        }
        System.out.println("Bucket node's actual runtime class: " + bucketHead.getClass().getSimpleName()
                + (bucketHead.getClass().getSimpleName().equals("TreeNode")
                    ? "  (TREEIFIED -- this bin holds >= 8 nodes in a table with capacity >= 64, so HashMap"
                      + " converted it from a linked list to a red-black tree for O(log n) worst-case lookup)"
                    : "  (still a linked list -- treeification threshold or minimum table capacity not yet met)"));
    }
}
