import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.fail;

// Hand-rolled property-based test -- no jqwik/QuickCheck dependency needed
// to demonstrate the technique. The property under test: for ANY two sorted
// int arrays, merge(a, b) must equal the sorted concatenation of a and b.
// Unlike the two example tests, array lengths (and therefore which array
// runs out first) are randomized on every run, with no human bias toward
// "a is always the longer array."
public class MergeSortedPropertyTest {

    static int[] randomSortedArray(Random r, int maxLen) {
        int len = r.nextInt(maxLen + 1);
        int[] arr = new int[len];
        int v = r.nextInt(5);
        for (int i = 0; i < len; i++) {
            v += r.nextInt(4); // strictly non-decreasing -> already sorted
            arr[i] = v;
        }
        return arr;
    }

    @Test
    void mergeMatchesSortedConcatenationForRandomInputs() {
        Random r = new Random(42); // fixed seed: reproducible counterexample
        for (int trial = 0; trial < 2000; trial++) {
            int[] a = randomSortedArray(r, 8);
            int[] b = randomSortedArray(r, 8);

            int[] actual = MergeSorted.merge(a, b);

            int[] expected = new int[a.length + b.length];
            System.arraycopy(a, 0, expected, 0, a.length);
            System.arraycopy(b, 0, expected, a.length, b.length);
            Arrays.sort(expected);

            if (!Arrays.equals(expected, actual)) {
                fail(String.format(
                    "property violated on trial %d%n  a=%s%n  b=%s%n  expected=%s%n  actual=%s",
                    trial, Arrays.toString(a), Arrays.toString(b),
                    Arrays.toString(expected), Arrays.toString(actual)));
            }
        }
    }
}
