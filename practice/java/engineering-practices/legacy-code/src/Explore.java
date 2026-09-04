/**
 * Step 1 of characterization testing: probe the legacy class's ACTUAL behavior
 * across a spread of inputs, before writing a single assertion. Nothing here
 * is an assumption about what the code "should" do -- every printed value is
 * exactly what LegacyOrderPricer really returns, captured before any test exists.
 */
public class Explore {
    public static void main(String[] args) {
        int[][] cases = {
            {1, 0}, {9, 0}, {10, 0}, {11, 0}, {100, 0}
        };
        double[] unitPrices = {19.99, 10.00, 3.33, 0.01, 1000.00};
        for (double unitPrice : unitPrices) {
            for (int[] c : cases) {
                int qty = c[0];
                double result = LegacyOrderPricer.price(qty, unitPrice);
                System.out.printf("price(qty=%d, unitPrice=%.2f) = %s%n", qty, unitPrice, result);
            }
        }
    }
}
