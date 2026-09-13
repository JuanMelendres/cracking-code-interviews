// Real, deliberately realistic bug: the tail-copy loop for array `b` was
// forgotten. This is a classic, easy-to-make mistake in the "merge two
// sorted arrays" pattern (LeetCode 88 and relatives).
public class MergeSorted {
    static int[] merge(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];
        int i = 0, j = 0, k = 0;
        while (i < a.length && j < b.length) {
            if (a[i] <= b[j]) result[k++] = a[i++];
            else result[k++] = b[j++];
        }
        while (i < a.length) result[k++] = a[i++];
        // BUG: missing "while (j < b.length) result[k++] = b[j++];"
        return result;
    }
}
