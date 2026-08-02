// A hot loop that allocates a small, non-escaping object per iteration --
// the object is created, used entirely within the method, and discarded.
// With escape analysis + scalar replacement (JIT default), the JIT can
// prove this object never escapes the method and replace the heap
// allocation with plain register/stack fields, eliminating it entirely for
// compiled invocations. Without it (-XX:-DoEscapeAnalysis), every single
// invocation heap-allocates for real, producing real GC pressure.
public class EscapeAnalysisDemo {

    static class Point {
        final int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
        int manhattan() { return Math.abs(x) + Math.abs(y); }
    }

    // The Point instance here never escapes this method -- it's created,
    // used to compute a primitive result, and immediately discarded.
    static long sumManhattan(int n) {
        long total = 0;
        for (int i = 0; i < n; i++) {
            Point p = new Point(i % 100 - 50, (i * 7) % 100 - 50);
            total += p.manhattan();
        }
        return total;
    }

    public static void main(String[] args) {
        int iterationsPerRound = 20_000_000;
        int rounds = 30;
        long grandTotal = 0;
        for (int r = 0; r < rounds; r++) {
            grandTotal += sumManhattan(iterationsPerRound);
        }
        System.out.println("grandTotal=" + grandTotal
                + " totalAllocatingCalls=" + ((long) iterationsPerRound * rounds));
    }
}
