/**
 * Real, observable evidence for how Java's primitive numeric types are actually
 * stored as bits -- two's complement for integers, IEEE 754 for floating point --
 * and where that storage leaks through as behavior a "numbers are just numbers"
 * mental model does not predict: silent wraparound, precision loss, and
 * truncating narrowing conversions.
 */
import java.math.BigDecimal;

public class NumberRepresentationDemo {

    public static void main(String[] args) {
        twosComplementBitPatterns();
        integerOverflowWraparound();
        narrowingCastTruncation();
        floatingPointPrecisionLoss();
        floatVsDoubleAccumulatedError();
    }

    static void twosComplementBitPatterns() {
        System.out.println("=== Two's complement bit patterns (32-bit int) ===");
        int[] values = {0, 1, -1, 2, -2, Integer.MAX_VALUE, Integer.MIN_VALUE};
        for (int v : values) {
            System.out.printf("  %12d -> %s%n", v, String.format("%32s", Integer.toBinaryString(v)).replace(' ', '0'));
        }
    }

    static void integerOverflowWraparound() {
        System.out.println("=== Integer overflow: silent wraparound, no exception ===");
        int max = Integer.MAX_VALUE;
        System.out.println("  Integer.MAX_VALUE       = " + max);
        System.out.println("  Integer.MAX_VALUE + 1   = " + (max + 1));
        System.out.println("  Integer.MAX_VALUE + 2   = " + (max + 2));

        int min = Integer.MIN_VALUE;
        System.out.println("  Integer.MIN_VALUE       = " + min);
        System.out.println("  Integer.MIN_VALUE - 1   = " + (min - 1));

        try {
            Math.addExact(max, 1);
            System.out.println("  Math.addExact did not throw (unexpected)");
        } catch (ArithmeticException e) {
            System.out.println("  Math.addExact(MAX_VALUE, 1) threw: " + e.getMessage());
        }
    }

    static void narrowingCastTruncation() {
        System.out.println("=== Narrowing cast: truncation, not rounding or clamping ===");
        int wide = 200;
        byte narrow = (byte) wide;
        System.out.println("  (byte) 200  = " + narrow + "   (200 does not fit in a signed 8-bit byte, range -128..127)");

        int biggerWide = 65536 + 42;
        short narrowShort = (short) biggerWide;
        System.out.println("  (short) " + biggerWide + " = " + narrowShort + "   (low 16 bits kept, high bits silently dropped)");

        long bigLong = 4_294_967_296L + 7; // 2^32 + 7
        int narrowInt = (int) bigLong;
        System.out.println("  (int) " + bigLong + " = " + narrowInt + "   (low 32 bits kept)");
    }

    static void floatingPointPrecisionLoss() {
        System.out.println("=== Floating point: not every decimal has an exact binary representation ===");
        double a = 0.1;
        double b = 0.2;
        double sum = a + b;
        System.out.println("  0.1 + 0.2            = " + sum);
        System.out.println("  0.1 + 0.2 == 0.3 ?   " + (sum == 0.3));
        System.out.println("  Exact difference      = " + (sum - 0.3));
        System.out.printf("  0.1 via printf %%.20f  = %.20f   (misleading -- see below)%n", 0.1);
        System.out.println("  0.1 via new BigDecimal(double) = " + new BigDecimal(0.1) + "   (the real, exact binary value)");
    }

    static void floatVsDoubleAccumulatedError() {
        System.out.println("=== Accumulated rounding error: float vs. double over many additions ===");
        float floatSum = 0f;
        double doubleSum = 0d;
        int iterations = 10_000_000;
        for (int i = 0; i < iterations; i++) {
            floatSum += 0.1f;
            doubleSum += 0.1d;
        }
        double expected = iterations * 0.1;
        System.out.println("  iterations            = " + iterations);
        System.out.println("  expected (mathematical) = " + expected);
        System.out.println("  float  accumulated sum  = " + floatSum + "   (error: " + (expected - floatSum) + ")");
        System.out.println("  double accumulated sum  = " + doubleSum + "   (error: " + (expected - doubleSum) + ")");
    }
}
