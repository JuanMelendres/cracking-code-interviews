import java.util.Arrays;
import java.util.Random;

/** LC 48 -- Rotate Image. In-place 90-degree clockwise rotation via transpose + row-reverse. */
public class RotateMatrixDemo {

    static void rotateInPlace(int[][] m) {
        int n = m.length;
        // Transpose: reflect across the main diagonal.
        for (int r = 0; r < n; r++) {
            for (int c = r + 1; c < n; c++) {
                int tmp = m[r][c];
                m[r][c] = m[c][r];
                m[c][r] = tmp;
            }
        }
        // Reverse each row: transpose + row-reverse == 90-degree clockwise rotation.
        for (int r = 0; r < n; r++) {
            for (int lo = 0, hi = n - 1; lo < hi; lo++, hi--) {
                int tmp = m[r][lo];
                m[r][lo] = m[r][hi];
                m[r][hi] = tmp;
            }
        }
    }

    /** Ground truth: builds a brand-new rotated copy, O(n^2) extra space, for correctness checking only. */
    static int[][] rotatedCopy(int[][] m) {
        int n = m.length;
        int[][] out = new int[n][n];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                out[c][n - 1 - r] = m[r][c];
            }
        }
        return out;
    }

    static int[][] randomMatrix(int n, long seed) {
        Random random = new Random(seed);
        int[][] m = new int[n][n];
        for (int r = 0; r < n; r++) for (int c = 0; c < n; c++) m[r][c] = random.nextInt(1000);
        return m;
    }

    public static void main(String[] args) {
        int[] sizes = {1, 2, 3, 4, 7, 20, 200};
        for (int n : sizes) {
            int[][] original = randomMatrix(n, n);
            int[][] expected = rotatedCopy(original);
            int[][] actual = deepCopy(original);
            rotateInPlace(actual);
            boolean match = Arrays.deepEquals(expected, actual);
            System.out.printf("n=%-4d in-place rotation matches brute-force ground truth: %s%n", n, match);
            if (!match) throw new IllegalStateException("rotation mismatch at n=" + n);
        }

        // Explicit worked example, small enough to print and verify by eye too.
        int[][] example = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        rotateInPlace(example);
        System.out.println("3x3 [[1,2,3],[4,5,6],[7,8,9]] rotated 90deg clockwise -> " + Arrays.deepToString(example));
        int[][] expectedExample = {{7, 4, 1}, {8, 5, 2}, {9, 6, 3}};
        if (!Arrays.deepEquals(example, expectedExample)) throw new IllegalStateException("worked example mismatch");
    }

    static int[][] deepCopy(int[][] m) {
        int[][] out = new int[m.length][];
        for (int i = 0; i < m.length; i++) out[i] = m[i].clone();
        return out;
    }
}
