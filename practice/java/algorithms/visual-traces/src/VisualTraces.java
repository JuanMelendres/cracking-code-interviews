import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates the step-by-step traces used as the visual explanations in the
 * 03-data-structures-algorithms chapters. Every table printed here is real
 * executed output, not a hand-drawn illustration — so the numbers in the
 * chapters' diagrams are verifiable by re-running this file.
 *
 * Java 21. Pure JDK, no dependencies.
 *
 * Compile and run:
 *   javac -d out src/VisualTraces.java
 *   java -cp out VisualTraces
 */
public class VisualTraces {

    public static void main(String[] args) {
        containerWithMostWater(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7});
        shipCapacityBinarySearch(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 5);
        xorSingleNumber(new int[]{4, 1, 2, 1, 2});
        editDistanceGrid("horse", "ros");
        jumpGameII(new int[]{2, 3, 1, 1, 4});
        subarraySumEqualsK(new int[]{1, 2, 3, -3, 1, 1, 1}, 3);
        meetingRoomsII(new int[][]{{0, 30}, {5, 10}, {15, 20}});
        mergeSortTrace(new int[]{38, 27, 43, 3, 9, 82, 10});
        largestRectangleHistogram(new int[]{2, 1, 5, 6, 2, 3});
    }

    // ------------------------------------------------ 1. Two pointers (LC 11)

    static void containerWithMostWater(int[] height) {
        section("LC 11 Container With Most Water — two-pointer walk over " + Arrays.toString(height));
        System.out.println("step  left  right  h[left]  h[right]  width  area  best  action");
        int left = 0;
        int right = height.length - 1;
        int best = 0;
        int step = 0;
        while (left < right) {
            int width = right - left;
            int area = width * Math.min(height[left], height[right]);
            best = Math.max(best, area);
            String action = height[left] < height[right] ? "left++  (shorter side)" : "right-- (shorter side)";
            System.out.printf("%4d  %4d  %5d  %7d  %8d  %5d  %4d  %4d  %s%n",
                    step++, left, right, height[left], height[right], width, area, best, action);
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        System.out.println("Answer: " + best);
    }

    // ------------------------------------------ 2. Search on answer (LC 1011)

    static void shipCapacityBinarySearch(int[] weights, int days) {
        section("LC 1011 Ship Packages in " + days + " days — binary search on the ANSWER, weights "
                + Arrays.toString(weights));
        int lo = Arrays.stream(weights).max().orElseThrow();
        int hi = Arrays.stream(weights).sum();
        System.out.println("Search space: [" + lo + ", " + hi + "]  (max single weight .. total weight)");
        System.out.println("step   lo   hi  mid  daysNeeded(mid)  feasible?  next window");
        int step = 0;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            int needed = daysNeeded(weights, mid);
            boolean feasible = needed <= days;
            String next = feasible ? "hi = mid   -> [" + lo + ", " + mid + "]"
                                   : "lo = mid+1 -> [" + (mid + 1) + ", " + hi + "]";
            System.out.printf("%4d %4d %4d %4d %16d  %-9s  %s%n",
                    step++, lo, hi, mid, needed, feasible ? "yes" : "no", next);
            if (feasible) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        System.out.println("Answer: " + lo);
    }

    private static int daysNeeded(int[] weights, int capacity) {
        int days = 1;
        int load = 0;
        for (int w : weights) {
            if (load + w > capacity) {
                days++;
                load = 0;
            }
            load += w;
        }
        return days;
    }

    // ------------------------------------------------ 3. XOR lanes (LC 136)

    static void xorSingleNumber(int[] nums) {
        section("LC 136 Single Number — XOR cancellation over " + Arrays.toString(nums));
        System.out.println("step  value  value(bits)  running XOR(bits)  running XOR");
        int acc = 0;
        int step = 0;
        System.out.printf("%4d  %5s  %11s  %17s  %11d%n", step++, "-", "-", bits(acc), acc);
        for (int n : nums) {
            acc ^= n;
            System.out.printf("%4d  %5d  %11s  %17s  %11d%n", step++, n, bits(n), bits(acc), acc);
        }
        System.out.println("Answer: " + acc + "  (every duplicate cancelled itself bit by bit)");
    }

    private static String bits(int v) {
        return String.format("%4s", Integer.toBinaryString(v)).replace(' ', '0');
    }

    // ------------------------------------------------ 4. DP grid (LC 72)

    static void editDistanceGrid(String a, String b) {
        section("LC 72 Edit Distance — DP table for \"" + a + "\" -> \"" + b + "\"");
        int n = a.length();
        int m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        StringBuilder header = new StringBuilder("        ''");
        for (int j = 0; j < m; j++) {
            header.append(String.format("%4c", b.charAt(j)));
        }
        System.out.println(header);
        for (int i = 0; i <= n; i++) {
            StringBuilder row = new StringBuilder();
            row.append(String.format("%4s", i == 0 ? "''" : String.valueOf(a.charAt(i - 1))));
            for (int j = 0; j <= m; j++) {
                row.append(String.format("%4d", dp[i][j]));
            }
            System.out.println(row);
        }
        System.out.println("Answer: " + dp[n][m] + "  (bottom-right cell; each cell depends only on its"
                + " left, top, and diagonal neighbour)");
    }

    // ------------------------------------------------ 5. Greedy (LC 45)

    static void jumpGameII(int[] nums) {
        section("LC 45 Jump Game II — greedy level expansion over " + Arrays.toString(nums));
        System.out.println("index  nums[i]  i+nums[i]  farthest  currentEnd  jumps  event");
        int jumps = 0;
        int currentEnd = 0;
        int farthest = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            farthest = Math.max(farthest, i + nums[i]);
            String event = "";
            if (i == currentEnd) {
                jumps++;
                currentEnd = farthest;
                event = "reached end of level -> jump, new level ends at " + currentEnd;
            }
            System.out.printf("%5d  %7d  %9d  %8d  %10d  %5d  %s%n",
                    i, nums[i], i + nums[i], farthest, currentEnd, jumps, event);
        }
        System.out.println("Answer: " + jumps + "  (each 'level' is everything reachable with one more jump)");
    }

    // ------------------------------------------------ 6. Prefix-sum map (LC 560)

    static void subarraySumEqualsK(int[] nums, int k) {
        section("LC 560 Subarray Sum Equals " + k + " — prefix-sum frequency map over " + Arrays.toString(nums));
        System.out.println("index  value  prefix  looking for (prefix-k)  found  count  map after step");
        Map<Integer, Integer> seen = new HashMap<>();
        seen.put(0, 1);
        int prefix = 0;
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            prefix += nums[i];
            int wanted = prefix - k;
            int found = seen.getOrDefault(wanted, 0);
            count += found;
            seen.merge(prefix, 1, Integer::sum);
            System.out.printf("%5d  %5d  %6d  %22d  %5d  %5d  %s%n",
                    i, nums[i], prefix, wanted, found, count, sortedMap(seen));
        }
        System.out.println("Answer: " + count + "  (a subarray sums to k exactly when an earlier prefix"
                + " equals prefix-k)");
    }

    private static String sortedMap(Map<Integer, Integer> m) {
        List<Integer> keys = new ArrayList<>(m.keySet());
        keys.sort(Integer::compareTo);
        StringBuilder sb = new StringBuilder("{");
        for (int key : keys) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(key).append('=').append(m.get(key));
        }
        return sb.append('}').toString();
    }

    // ------------------------------------------------ 7. Sweep line (LC 253)

    static void meetingRoomsII(int[][] intervals) {
        section("LC 253 Meeting Rooms II — sweep line over " + Arrays.deepToString(intervals));
        record Event(int time, int delta, String label) {
        }
        List<Event> events = new ArrayList<>();
        for (int[] in : intervals) {
            events.add(new Event(in[0], +1, "start of [" + in[0] + "," + in[1] + "]"));
            events.add(new Event(in[1], -1, "end   of [" + in[0] + "," + in[1] + "]"));
        }
        events.sort((x, y) -> x.time() != y.time() ? Integer.compare(x.time(), y.time())
                                                  : Integer.compare(x.delta(), y.delta()));
        System.out.println("time  delta  active rooms  peak  event");
        int active = 0;
        int peak = 0;
        for (Event e : events) {
            active += e.delta();
            peak = Math.max(peak, active);
            System.out.printf("%4d  %5d  %12d  %4d  %s%n", e.time(), e.delta(), active, peak, e.label());
        }
        System.out.println("Answer: " + peak + "  (the answer is the PEAK of the running count, never the"
                + " number of intervals)");
    }

    // ------------------------------------------------ 8. Merge sort recursion

    static void mergeSortTrace(int[] input) {
        section("Merge sort — real split/merge order for " + Arrays.toString(input));
        int[] work = input.clone();
        mergeSort(work, 0, work.length - 1, 0);
        System.out.println("Sorted: " + Arrays.toString(work));
    }

    private static void mergeSort(int[] a, int lo, int hi, int depth) {
        if (lo >= hi) {
            return;
        }
        int mid = lo + (hi - lo) / 2;
        System.out.printf("%ssplit  %s -> %s | %s%n", "  ".repeat(depth),
                Arrays.toString(Arrays.copyOfRange(a, lo, hi + 1)),
                Arrays.toString(Arrays.copyOfRange(a, lo, mid + 1)),
                Arrays.toString(Arrays.copyOfRange(a, mid + 1, hi + 1)));
        mergeSort(a, lo, mid, depth + 1);
        mergeSort(a, mid + 1, hi, depth + 1);
        merge(a, lo, mid, hi);
        System.out.printf("%smerge  -> %s%n", "  ".repeat(depth),
                Arrays.toString(Arrays.copyOfRange(a, lo, hi + 1)));
    }

    private static void merge(int[] a, int lo, int mid, int hi) {
        int[] merged = new int[hi - lo + 1];
        int i = lo;
        int j = mid + 1;
        int k = 0;
        while (i <= mid && j <= hi) {
            merged[k++] = a[i] <= a[j] ? a[i++] : a[j++];
        }
        while (i <= mid) {
            merged[k++] = a[i++];
        }
        while (j <= hi) {
            merged[k++] = a[j++];
        }
        System.arraycopy(merged, 0, a, lo, merged.length);
    }

    // ------------------------------------------------ 9. Monotonic stack (LC 84)

    static void largestRectangleHistogram(int[] heights) {
        section("LC 84 Largest Rectangle in Histogram — monotonic stack over " + Arrays.toString(heights));
        System.out.println("index  height  popped (height x width = area)  best  stack after (indices)");
        Deque<Integer> stack = new ArrayDeque<>();
        int best = 0;
        for (int i = 0; i <= heights.length; i++) {
            int current = i == heights.length ? 0 : heights[i];
            StringBuilder popped = new StringBuilder();
            while (!stack.isEmpty() && heights[stack.peek()] >= current) {
                int h = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                int area = h * width;
                best = Math.max(best, area);
                if (popped.length() > 0) {
                    popped.append("; ");
                }
                popped.append(h).append(" x ").append(width).append(" = ").append(area);
            }
            stack.push(i);
            System.out.printf("%5s  %6s  %30s  %4d  %s%n",
                    i == heights.length ? "end" : String.valueOf(i),
                    i == heights.length ? "0*" : String.valueOf(current),
                    popped.length() == 0 ? "-" : popped.toString(),
                    best, stack);
        }
        System.out.println("Answer: " + best + "  (* a sentinel height of 0 at the end forces every"
                + " remaining bar to be popped and measured)");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
