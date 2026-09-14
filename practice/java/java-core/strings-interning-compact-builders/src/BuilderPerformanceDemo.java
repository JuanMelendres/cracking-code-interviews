/**
 * Real, measured proof of two classic String-building cost facts:
 * (1) naive String += concatenation in a loop is genuinely quadratic --
 * each += allocates a whole new String, copying everything before it --
 * versus StringBuilder's genuine amortized-linear append; and
 * (2) StringBuffer's synchronized methods are genuinely slower than
 * StringBuilder's unsynchronized ones, even single-threaded.
 */
public class BuilderPerformanceDemo {

    static final int ITERATIONS = 60_000;

    public static void main(String[] args) {
        System.out.println("== Real measured cost: String += in a loop (quadratic) vs StringBuilder (linear) ==");

        long concatStart = System.currentTimeMillis();
        String concatenated = "";
        for (int i = 0; i < ITERATIONS; i++) {
            concatenated += "x";
        }
        long concatElapsed = System.currentTimeMillis() - concatStart;

        long builderStart = System.currentTimeMillis();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ITERATIONS; i++) {
            builder.append("x");
        }
        String built = builder.toString();
        long builderElapsed = System.currentTimeMillis() - builderStart;

        System.out.println("Both produced equal-length results: " + (concatenated.length() == built.length()));
        System.out.println("String += in a loop, " + ITERATIONS + " iterations: " + concatElapsed + "ms");
        System.out.println("StringBuilder.append, " + ITERATIONS + " iterations:  " + builderElapsed + "ms");
        System.out.println("Real measured ratio: " + String.format("%.2fx", (double) concatElapsed / Math.max(builderElapsed, 1)));

        System.out.println("\n== Real measured cost: StringBuilder (unsynchronized) vs StringBuffer (synchronized) ==");
        long sbStart = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20_000_000; i++) {
            sb.append('x');
            sb.setLength(0); // keep the object small; measuring per-call overhead, not growth
        }
        long sbElapsed = System.currentTimeMillis() - sbStart;

        long bufStart = System.currentTimeMillis();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < 20_000_000; i++) {
            buf.append('x');
            buf.setLength(0);
        }
        long bufElapsed = System.currentTimeMillis() - bufStart;

        System.out.println("StringBuilder (unsynchronized), 20,000,000 append+reset: " + sbElapsed + "ms");
        System.out.println("StringBuffer (synchronized),    20,000,000 append+reset: " + bufElapsed + "ms");
        System.out.println("Real measured ratio: " + String.format("%.2fx", (double) bufElapsed / Math.max(sbElapsed, 1)));
    }
}
