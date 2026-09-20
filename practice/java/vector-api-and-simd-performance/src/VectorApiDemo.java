import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorSpecies;

import java.util.Random;

/** A real element-wise fused-multiply-add over two large float arrays
 * (c[i] = a[i]*b[i] + a[i]), computed two ways: an ordinary scalar loop,
 * and the incubating Vector API (JEP 460 as of JDK 21), which compiles
 * down to real SIMD (single-instruction-multiple-data) CPU instructions
 * processing several float lanes per instruction instead of one. */
public class VectorApiDemo {

    static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;
    static final int N = 1_000_000;
    static final int REPEATS = 2000;

    public static void main(String[] args) {
        System.out.println("Vector species: " + SPECIES + " (" + SPECIES.length() + " float lanes per instruction)");

        float[] a = new float[N];
        float[] b = new float[N];
        Random random = new Random(42);
        for (int i = 0; i < N; i++) {
            a[i] = random.nextFloat();
            b[i] = random.nextFloat();
        }
        float[] scalarOut = new float[N];
        float[] vectorOut = new float[N];

        System.out.println("Warmup...");
        for (int r = 0; r < 3; r++) {
            scalarFma(a, b, scalarOut);
            vectorFma(a, b, vectorOut);
        }

        System.out.println("\n=== Real measurement, N=" + N + ", " + REPEATS + " passes (cache-resident, compute-bound) ===");

        long scalarStart = System.nanoTime();
        for (int r = 0; r < REPEATS; r++) scalarFma(a, b, scalarOut);
        long scalarMs = (System.nanoTime() - scalarStart) / 1_000_000;

        long vectorStart = System.nanoTime();
        for (int r = 0; r < REPEATS; r++) vectorFma(a, b, vectorOut);
        long vectorMs = (System.nanoTime() - vectorStart) / 1_000_000;

        boolean identical = true;
        for (int i = 0; i < N; i++) {
            if (scalarOut[i] != vectorOut[i]) { identical = false; break; }
        }

        System.out.println("Scalar loop:  " + scalarMs + "ms");
        System.out.println("Vector API:   " + vectorMs + "ms");
        System.out.printf("Speedup:      %.2fx%n", (double) scalarMs / vectorMs);
        System.out.println("Every one of " + N + " outputs bit-for-bit identical: " + identical);
    }

    static void scalarFma(float[] a, float[] b, float[] out) {
        // Math.fma, not a[i]*b[i]+a[i]: a real FMA is a single rounding
        // step (IEEE 754), while separate multiply-then-add is two
        // roundings -- using Math.fma here makes this a true apples-to-
        // apples comparison against the Vector API's own fma() below,
        // rather than comparing against a numerically different scalar
        // computation.
        for (int i = 0; i < a.length; i++) {
            out[i] = Math.fma(a[i], b[i], a[i]);
        }
    }

    static void vectorFma(float[] a, float[] b, float[] out) {
        int upperBound = SPECIES.loopBound(a.length);
        int i = 0;
        for (; i < upperBound; i += SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(SPECIES, a, i);
            FloatVector vb = FloatVector.fromArray(SPECIES, b, i);
            va.fma(vb, va).intoArray(out, i);
        }
        // Tail: remaining elements that don't fill a full vector lane
        for (; i < a.length; i++) {
            out[i] = a[i] * b[i] + a[i];
        }
    }
}
