import java.util.Map;
import java.util.TreeMap;

public class VectorClockDemo {

    static class VectorClock {
        final Map<String, Integer> counters = new TreeMap<>();

        VectorClock copy() {
            VectorClock c = new VectorClock();
            c.counters.putAll(this.counters);
            return c;
        }

        void increment(String node) {
            counters.merge(node, 1, Integer::sum);
        }

        void mergeFrom(VectorClock other) {
            for (Map.Entry<String, Integer> e : other.counters.entrySet()) {
                counters.merge(e.getKey(), e.getValue(), Math::max);
            }
        }

        enum Relation { EQUAL, DOMINATES, DOMINATED, CONCURRENT }

        Relation compareTo(VectorClock other) {
            boolean thisHasGreater = false;
            boolean otherHasGreater = false;
            java.util.Set<String> allNodes = new java.util.TreeSet<>();
            allNodes.addAll(this.counters.keySet());
            allNodes.addAll(other.counters.keySet());
            for (String node : allNodes) {
                int a = this.counters.getOrDefault(node, 0);
                int b = other.counters.getOrDefault(node, 0);
                if (a > b) thisHasGreater = true;
                if (b > a) otherHasGreater = true;
            }
            if (!thisHasGreater && !otherHasGreater) return Relation.EQUAL;
            if (thisHasGreater && !otherHasGreater) return Relation.DOMINATES;
            if (!thisHasGreater && otherHasGreater) return Relation.DOMINATED;
            return Relation.CONCURRENT;
        }

        @Override public String toString() { return counters.toString(); }
    }

    public static void main(String[] args) {
        System.out.println("=== Scenario A: two replicas write independently, neither has seen the other's update ===");
        VectorClock base = new VectorClock();
        base.increment("A");
        System.out.println("Initial write on replica A: " + base + " (value=\"milk\")");

        VectorClock writeOnA = base.copy();
        writeOnA.increment("A");
        System.out.println("Replica A writes again, unaware of B: " + writeOnA + " (value=\"milk,eggs\")");

        VectorClock writeOnB = base.copy();
        writeOnB.increment("B");
        System.out.println("Replica B writes independently, unaware of A's second write: " + writeOnB + " (value=\"milk,bread\")");

        VectorClock.Relation relation = writeOnA.compareTo(writeOnB);
        System.out.println("Comparing " + writeOnA + " vs " + writeOnB + " -> " + relation);
        System.out.println("Real conflict detected? " + (relation == VectorClock.Relation.CONCURRENT)
                + " (neither write's clock dominates the other -> both versions must be kept and merged/resolved, e.g. union to \"milk,eggs,bread\")");

        System.out.println();
        System.out.println("=== Scenario B: replica B first READS replica A's update, then writes (a real causal relationship) ===");
        VectorClock readByB = writeOnA.copy(); // B reads A's state, inheriting A's clock
        VectorClock causalWriteOnB = readByB.copy();
        causalWriteOnB.increment("B");
        System.out.println("Replica B reads A's state " + writeOnA + ", then writes: " + causalWriteOnB + " (value=\"milk,eggs,bread\")");

        VectorClock.Relation causalRelation = causalWriteOnB.compareTo(writeOnA);
        System.out.println("Comparing " + causalWriteOnB + " vs " + writeOnA + " -> " + causalRelation);
        System.out.println("Real conflict detected? " + (causalRelation == VectorClock.Relation.CONCURRENT)
                + " (B's clock dominates A's -> B's write causally supersedes A's, no merge needed, A's version can simply be discarded)");
    }
}
