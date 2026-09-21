import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** LC 54 -- Spiral Matrix. Four shrinking boundaries, not a direction-vector walk with turn detection. */
public class SpiralTraversalDemo {

    static List<Integer> spiralOrder(int[][] m) {
        List<Integer> result = new ArrayList<>();
        if (m.length == 0 || m[0].length == 0) return result;
        int top = 0, bottom = m.length - 1, left = 0, right = m[0].length - 1;
        while (top <= bottom && left <= right) {
            for (int c = left; c <= right; c++) result.add(m[top][c]);
            top++;
            for (int r = top; r <= bottom; r++) result.add(m[r][right]);
            right--;
            if (top <= bottom) {
                for (int c = right; c >= left; c--) result.add(m[bottom][c]);
                bottom--;
            }
            if (left <= right) {
                for (int r = bottom; r >= top; r--) result.add(m[r][left]);
                left++;
            }
        }
        return result;
    }

    static void check(int[][] input, List<Integer> expected) {
        List<Integer> actual = spiralOrder(input);
        boolean match = actual.equals(expected);
        System.out.println((match ? "PASS " : "FAIL ") + Arrays.deepToString(input) + " -> " + actual);
        if (!match) throw new IllegalStateException("expected " + expected + " got " + actual);
    }

    public static void main(String[] args) {
        // Square matrix.
        check(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}},
              List.of(1, 2, 3, 6, 9, 8, 7, 4, 5));

        // Non-square, more rows than columns.
        check(new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}},
              List.of(1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7));

        // Single row.
        check(new int[][]{{1, 2, 3, 4, 5}},
              List.of(1, 2, 3, 4, 5));

        // Single column.
        check(new int[][]{{1}, {2}, {3}},
              List.of(1, 2, 3));

        // Single cell.
        check(new int[][]{{42}},
              List.of(42));

        System.out.println("All spiral-traversal correctness checks passed.");
    }
}
