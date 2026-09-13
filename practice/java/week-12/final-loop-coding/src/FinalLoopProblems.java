import java.util.*;

/** Week 12 -- Full Loop Simulation. No new topics: 8 problems spanning
 * patterns already taught across Weeks 1-11, none repeating a problem
 * number already solved earlier in the program. */
public class FinalLoopProblems {

    /** LC 3: Longest Substring Without Repeating Characters. Sliding window. */
    static int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> lastSeen = new HashMap<>();
        int left = 0, best = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) {
                left = lastSeen.get(c) + 1;
            }
            lastSeen.put(c, right);
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    /** LC 207: Course Schedule. Topological sort via Kahn's algorithm (cycle detection). */
    static boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
        int[] indegree = new int[numCourses];
        for (int[] p : prerequisites) {
            adj.get(p[1]).add(p[0]);
            indegree[p[0]]++;
        }
        Deque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < numCourses; i++) if (indegree[i] == 0) queue.add(i);
        int visited = 0;
        while (!queue.isEmpty()) {
            int course = queue.poll();
            visited++;
            for (int next : adj.get(course)) {
                if (--indegree[next] == 0) queue.add(next);
            }
        }
        return visited == numCourses; // if fewer visited, a cycle blocked the rest
    }

    /** LC 56: Merge Intervals. Sort by start, merge overlapping. */
    static int[][] mergeIntervals(int[][] intervals) {
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
        List<int[]> merged = new ArrayList<>();
        for (int[] interval : intervals) {
            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
                merged.add(interval);
            } else {
                merged.get(merged.size() - 1)[1] = Math.max(merged.get(merged.size() - 1)[1], interval[1]);
            }
        }
        return merged.toArray(new int[0][]);
    }

    /** LC 139: Word Break. DP -- dp[i] = can s[0..i) be segmented using the dictionary. */
    static boolean wordBreak(String s, List<String> wordDict) {
        Set<String> dict = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && dict.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[s.length()];
    }

    /** LC 128: Longest Consecutive Sequence. Hash set, only start counting from a run's true start. */
    static int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int n : nums) set.add(n);
        int best = 0;
        for (int n : set) {
            if (set.contains(n - 1)) continue; // not the start of a run -- skip, avoids O(n^2)
            int length = 1;
            while (set.contains(n + length)) length++;
            best = Math.max(best, length);
        }
        return best;
    }

    /** LC 973: K Closest Points to Origin. Max-heap of size k. */
    static int[][] kClosest(int[][] points, int k) {
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
                (a, b) -> (b[0] * b[0] + b[1] * b[1]) - (a[0] * a[0] + a[1] * a[1]));
        for (int[] p : points) {
            maxHeap.offer(p);
            if (maxHeap.size() > k) maxHeap.poll();
        }
        return maxHeap.toArray(new int[0][]);
    }

    /** LC 55: Jump Game. Greedy -- track the furthest reachable index. */
    static boolean canJump(int[] nums) {
        int furthest = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > furthest) return false; // this index is unreachable
            furthest = Math.max(furthest, i + nums[i]);
        }
        return true;
    }

    /** LC 127: Word Ladder. BFS over the word graph, shortest transformation sequence length. */
    static int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> dict = new HashSet<>(wordList);
        if (!dict.contains(endWord)) return 0;
        Deque<String> queue = new ArrayDeque<>();
        queue.add(beginWord);
        Set<String> visited = new HashSet<>();
        visited.add(beginWord);
        int steps = 1;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String word = queue.poll();
                if (word.equals(endWord)) return steps;
                char[] chars = word.toCharArray();
                for (int pos = 0; pos < chars.length; pos++) {
                    char original = chars[pos];
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == original) continue;
                        chars[pos] = c;
                        String candidate = new String(chars);
                        if (dict.contains(candidate) && !visited.contains(candidate)) {
                            visited.add(candidate);
                            queue.add(candidate);
                        }
                    }
                    chars[pos] = original;
                }
            }
            steps++;
        }
        return 0;
    }
}
