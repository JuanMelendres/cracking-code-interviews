/**
 * The real, standard Fibonacci-like planning-poker scale (1, 2, 3, 5, 8, 13,
 * 20, 40, 100) -- deliberately non-linear, spacing further apart at higher
 * values, because estimation uncertainty grows with size: the real
 * difference between a 1-hour and a 2-hour task is meaningful and worth a
 * separate point value, but the difference between a 40-hour and a 41-hour
 * task isn't worth a separate value at all.
 */
public final class FibonacciScale {

    public static final int[] VALUES = {1, 2, 3, 5, 8, 13, 20, 40, 100};

    private FibonacciScale() {
    }

    public static int nearest(double rawEstimate) {
        int nearest = VALUES[0];
        double smallestDiff = Math.abs(rawEstimate - VALUES[0]);
        for (int value : VALUES) {
            double diff = Math.abs(rawEstimate - value);
            if (diff < smallestDiff) {
                smallestDiff = diff;
                nearest = value;
            }
        }
        return nearest;
    }
}
