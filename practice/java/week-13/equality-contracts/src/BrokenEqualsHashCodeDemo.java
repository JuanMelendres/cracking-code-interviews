import java.util.HashSet;
import java.util.Set;

public class BrokenEqualsHashCodeDemo {

    // BROKEN: overrides equals() but not hashCode(). Compiles fine, silently
    // breaks every hash-based collection.
    static class BrokenPoint {
        final int x, y;
        BrokenPoint(int x, int y) { this.x = x; this.y = y; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BrokenPoint p)) return false;
            return x == p.x && y == p.y;
        }
        // hashCode() NOT overridden -- uses Object's identity hash.

        @Override
        public String toString() { return "(" + x + "," + y + ")"; }
    }

    // FIXED: both equals() and hashCode() derived from the same fields.
    static class FixedPoint {
        final int x, y;
        FixedPoint(int x, int y) { this.x = x; this.y = y; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof FixedPoint p)) return false;
            return x == p.x && y == p.y;
        }

        @Override
        public int hashCode() { return java.util.Objects.hash(x, y); }

        @Override
        public String toString() { return "(" + x + "," + y + ")"; }
    }

    public static void main(String[] args) {
        System.out.println("== equals() overridden, hashCode() NOT overridden ==");
        BrokenPoint b1 = new BrokenPoint(1, 1);
        BrokenPoint b2 = new BrokenPoint(1, 1);
        System.out.println("b1.equals(b2) = " + b1.equals(b2) + "  (equal by value)");
        System.out.println("b1.hashCode() = " + b1.hashCode());
        System.out.println("b2.hashCode() = " + b2.hashCode() + "  (different -- identity hash, contract broken)");

        Set<BrokenPoint> brokenSet = new HashSet<>();
        brokenSet.add(b1);
        boolean brokenContains = brokenSet.contains(b2);
        brokenSet.add(b2);
        System.out.println("HashSet.add(b1); HashSet.contains(b2) = " + brokenContains
                + "  (b2 is equals()-equal to b1 but HashSet can't find it -- looked in the wrong bucket)");
        System.out.println("HashSet.add(b2) anyway -- resulting size = " + brokenSet.size()
                + "  (expected 1 if the set correctly recognized a duplicate; got " + brokenSet.size()
                + " because hashCode() sent them to different buckets)");

        System.out.println();
        System.out.println("== Both equals() and hashCode() overridden consistently ==");
        FixedPoint f1 = new FixedPoint(1, 1);
        FixedPoint f2 = new FixedPoint(1, 1);
        System.out.println("f1.equals(f2) = " + f1.equals(f2));
        System.out.println("f1.hashCode() == f2.hashCode() = " + (f1.hashCode() == f2.hashCode()));

        Set<FixedPoint> fixedSet = new HashSet<>();
        fixedSet.add(f1);
        boolean fixedContains = fixedSet.contains(f2);
        fixedSet.add(f2);
        System.out.println("HashSet.add(f1); HashSet.contains(f2) = " + fixedContains);
        System.out.println("HashSet.add(f2) anyway -- resulting size = " + fixedSet.size()
                + "  (correctly deduplicated -- same bucket, equals() confirms it's the same logical value)");
    }
}
