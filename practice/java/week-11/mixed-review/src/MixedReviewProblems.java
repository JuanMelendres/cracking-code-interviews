import java.util.*;

/** T-1419-style mixed review, timed -- 15 problems spanning patterns already
 * built across Weeks 1-10, drilled quickly rather than studied from scratch. */
public class MixedReviewProblems {

    /** LC 1: Two Sum. Hash map, one pass. */
    static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (seen.containsKey(complement)) return new int[]{seen.get(complement), i};
            seen.put(nums[i], i);
        }
        throw new IllegalArgumentException("no solution");
    }

    /** LC 20: Valid Parentheses. Stack. */
    static boolean isValidParentheses(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        Map<Character, Character> pairs = Map.of(')', '(', ']', '[', '}', '{');
        for (char c : s.toCharArray()) {
            if (pairs.containsValue(c)) stack.push(c);
            else if (pairs.containsKey(c)) {
                if (stack.isEmpty() || stack.pop() != pairs.get(c)) return false;
            }
        }
        return stack.isEmpty();
    }

    /** LC 704: Binary Search. */
    static int binarySearch(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return mid;
            if (nums[mid] < target) lo = mid + 1; else hi = mid - 1;
        }
        return -1;
    }

    /** LC 53: Maximum Subarray (Kadane's). */
    static int maxSubArray(int[] nums) {
        int best = nums[0], current = nums[0];
        for (int i = 1; i < nums.length; i++) {
            current = Math.max(nums[i], current + nums[i]);
            best = Math.max(best, current);
        }
        return best;
    }

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /** LC 206: Reverse Linked List. Iterative. */
    static ListNode reverseList(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }
        return prev;
    }

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** LC 102: Binary Tree Level Order Traversal. BFS. */
    static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
            result.add(level);
        }
        return result;
    }

    /** LC 200: Number of Islands. BFS/DFS flood fill. */
    static int numIslands(char[][] grid) {
        int count = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    sinkIsland(grid, r, c);
                }
            }
        }
        return count;
    }

    static void sinkIsland(char[][] grid, int r, int c) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != '1') return;
        grid[r][c] = '0';
        sinkIsland(grid, r + 1, c);
        sinkIsland(grid, r - 1, c);
        sinkIsland(grid, r, c + 1);
        sinkIsland(grid, r, c - 1);
    }

    /** LC 33: Search in Rotated Sorted Array. Modified binary search. */
    static int searchRotated(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return mid;
            if (nums[lo] <= nums[mid]) { // left half is sorted
                if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
                else lo = mid + 1;
            } else { // right half is sorted
                if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
                else hi = mid - 1;
            }
        }
        return -1;
    }

    /** LC 155: Min Stack. Auxiliary stack tracking running minimum. */
    static class MinStack {
        private final Deque<Integer> stack = new ArrayDeque<>();
        private final Deque<Integer> minStack = new ArrayDeque<>();

        void push(int val) {
            stack.push(val);
            minStack.push(minStack.isEmpty() ? val : Math.min(val, minStack.peek()));
        }
        void pop() { stack.pop(); minStack.pop(); }
        int top() { return stack.peek(); }
        int getMin() { return minStack.peek(); }
    }

    /** LC 121: Best Time to Buy and Sell Stock. Single pass, track min-so-far. */
    static int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE, best = 0;
        for (int p : prices) {
            minPrice = Math.min(minPrice, p);
            best = Math.max(best, p - minPrice);
        }
        return best;
    }

    /** LC 15: 3Sum. Sort + two pointers. */
    static List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue; // skip duplicate anchors
            int lo = i + 1, hi = nums.length - 1;
            while (lo < hi) {
                int sum = nums[i] + nums[lo] + nums[hi];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[lo], nums[hi]));
                    while (lo < hi && nums[lo] == nums[lo + 1]) lo++;
                    while (lo < hi && nums[hi] == nums[hi - 1]) hi--;
                    lo++; hi--;
                } else if (sum < 0) lo++; else hi--;
            }
        }
        return result;
    }

    /** LC 236: Lowest Common Ancestor of a Binary Tree. */
    static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);
        if (left != null && right != null) return root; // p and q found in different subtrees
        return left != null ? left : right;
    }

    /** LC 76: Minimum Window Substring. Sliding window with a need/have count map. */
    static String minWindow(String s, String t) {
        if (t.isEmpty()) return "";
        Map<Character, Integer> need = new HashMap<>();
        for (char c : t.toCharArray()) need.merge(c, 1, Integer::sum);
        Map<Character, Integer> have = new HashMap<>();
        int required = need.size(), formed = 0;
        int left = 0, bestLen = Integer.MAX_VALUE, bestStart = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            have.merge(c, 1, Integer::sum);
            if (need.containsKey(c) && have.get(c).intValue() == need.get(c).intValue()) formed++;
            while (formed == required) {
                if (right - left + 1 < bestLen) {
                    bestLen = right - left + 1;
                    bestStart = left;
                }
                char leftChar = s.charAt(left);
                have.put(leftChar, have.get(leftChar) - 1);
                if (need.containsKey(leftChar) && have.get(leftChar) < need.get(leftChar)) formed--;
                left++;
            }
        }
        return bestLen == Integer.MAX_VALUE ? "" : s.substring(bestStart, bestStart + bestLen);
    }

    /** LC 42: Trapping Rain Water. Two pointers, track running max from each side. */
    static int trap(int[] height) {
        int left = 0, right = height.length - 1;
        int leftMax = 0, rightMax = 0, water = 0;
        while (left < right) {
            if (height[left] < height[right]) {
                leftMax = Math.max(leftMax, height[left]);
                water += leftMax - height[left];
                left++;
            } else {
                rightMax = Math.max(rightMax, height[right]);
                water += rightMax - height[right];
                right--;
            }
        }
        return water;
    }

    /** LC 208: Implement Trie (Prefix Tree). */
    static class Trie {
        private final Trie[] children = new Trie[26];
        private boolean isWord = false;

        void insert(String word) {
            Trie node = this;
            for (char c : word.toCharArray()) {
                int i = c - 'a';
                if (node.children[i] == null) node.children[i] = new Trie();
                node = node.children[i];
            }
            node.isWord = true;
        }

        boolean search(String word) {
            Trie node = find(word);
            return node != null && node.isWord;
        }

        boolean startsWith(String prefix) {
            return find(prefix) != null;
        }

        private Trie find(String s) {
            Trie node = this;
            for (char c : s.toCharArray()) {
                int i = c - 'a';
                if (node.children[i] == null) return null;
                node = node.children[i];
            }
            return node;
        }
    }
}
