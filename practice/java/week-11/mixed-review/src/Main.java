import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("== LC 1: Two Sum ==");
        Check.eq("[0, 1]", Arrays.toString(MixedReviewProblems.twoSum(new int[]{2, 7, 11, 15}, 9)), "twoSum([2,7,11,15], 9) = [0,1]");

        System.out.println("\n== LC 20: Valid Parentheses ==");
        Check.isTrue(MixedReviewProblems.isValidParentheses("()[]{}"), "\"()[]{}\" is valid");
        Check.isTrue(!MixedReviewProblems.isValidParentheses("(]"), "\"(]\" is invalid");

        System.out.println("\n== LC 704: Binary Search ==");
        Check.eq(4, MixedReviewProblems.binarySearch(new int[]{-1, 0, 3, 5, 9, 12}, 9), "binarySearch finds 9 at index 4");
        Check.eq(-1, MixedReviewProblems.binarySearch(new int[]{-1, 0, 3, 5, 9, 12}, 2), "binarySearch: 2 not present");

        System.out.println("\n== LC 53: Maximum Subarray ==");
        Check.eq(6, MixedReviewProblems.maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}), "maxSubArray = 6 ([4,-1,2,1])");

        System.out.println("\n== LC 206: Reverse Linked List ==");
        MixedReviewProblems.ListNode head = buildList(1, 2, 3, 4, 5);
        Check.eq(List.of(5, 4, 3, 2, 1), toList(MixedReviewProblems.reverseList(head)), "reverseList([1,2,3,4,5]) = [5,4,3,2,1]");

        System.out.println("\n== LC 102: Binary Tree Level Order Traversal ==");
        MixedReviewProblems.TreeNode root = new MixedReviewProblems.TreeNode(3);
        root.left = new MixedReviewProblems.TreeNode(9);
        root.right = new MixedReviewProblems.TreeNode(20);
        root.right.left = new MixedReviewProblems.TreeNode(15);
        root.right.right = new MixedReviewProblems.TreeNode(7);
        Check.eq(List.of(List.of(3), List.of(9, 20), List.of(15, 7)), MixedReviewProblems.levelOrder(root), "levelOrder = [[3],[9,20],[15,7]]");

        System.out.println("\n== LC 200: Number of Islands ==");
        char[][] grid = {
                {'1', '1', '0', '0', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '1', '0', '0'},
                {'0', '0', '0', '1', '1'}
        };
        Check.eq(3, MixedReviewProblems.numIslands(grid), "numIslands = 3");

        System.out.println("\n== LC 33: Search in Rotated Sorted Array ==");
        Check.eq(4, MixedReviewProblems.searchRotated(new int[]{4, 5, 6, 7, 0, 1, 2}, 0), "searchRotated finds 0 at index 4");
        Check.eq(-1, MixedReviewProblems.searchRotated(new int[]{4, 5, 6, 7, 0, 1, 2}, 3), "searchRotated: 3 not present");

        System.out.println("\n== LC 155: Min Stack ==");
        MixedReviewProblems.MinStack minStack = new MixedReviewProblems.MinStack();
        minStack.push(-2); minStack.push(0); minStack.push(-3);
        Check.eq(-3, minStack.getMin(), "getMin() = -3");
        minStack.pop();
        Check.eq(0, minStack.top(), "top() = 0 after pop");
        Check.eq(-2, minStack.getMin(), "getMin() = -2 after pop");

        System.out.println("\n== LC 121: Best Time to Buy and Sell Stock ==");
        Check.eq(5, MixedReviewProblems.maxProfit(new int[]{7, 1, 5, 3, 6, 4}), "maxProfit = 5 (buy at 1, sell at 6)");
        Check.eq(0, MixedReviewProblems.maxProfit(new int[]{7, 6, 4, 3, 1}), "maxProfit = 0 (never profitable)");

        System.out.println("\n== LC 15: 3Sum ==");
        List<List<Integer>> triples = MixedReviewProblems.threeSum(new int[]{-1, 0, 1, 2, -1, -4});
        Check.eq(2, triples.size(), "threeSum([-1,0,1,2,-1,-4]) finds 2 unique triples");
        Check.isTrue(triples.contains(List.of(-1, -1, 2)), "threeSum finds [-1,-1,2]");
        Check.isTrue(triples.contains(List.of(-1, 0, 1)), "threeSum finds [-1,0,1]");

        System.out.println("\n== LC 236: Lowest Common Ancestor ==");
        MixedReviewProblems.TreeNode lcaRoot = new MixedReviewProblems.TreeNode(3);
        MixedReviewProblems.TreeNode n5 = new MixedReviewProblems.TreeNode(5);
        MixedReviewProblems.TreeNode n1 = new MixedReviewProblems.TreeNode(1);
        lcaRoot.left = n5; lcaRoot.right = n1;
        MixedReviewProblems.TreeNode n6 = new MixedReviewProblems.TreeNode(6);
        MixedReviewProblems.TreeNode n2 = new MixedReviewProblems.TreeNode(2);
        n5.left = n6; n5.right = n2;
        Check.eq(lcaRoot, MixedReviewProblems.lowestCommonAncestor(lcaRoot, n5, n1), "LCA(5,1) = root(3)");
        Check.eq(n5, MixedReviewProblems.lowestCommonAncestor(lcaRoot, n6, n2), "LCA(6,2) = 5");

        System.out.println("\n== LC 76: Minimum Window Substring ==");
        Check.eq("BANC", MixedReviewProblems.minWindow("ADOBECODEBANC", "ABC"), "minWindow(\"ADOBECODEBANC\",\"ABC\") = \"BANC\"");
        Check.eq("", MixedReviewProblems.minWindow("a", "aa"), "minWindow(\"a\",\"aa\") = \"\" (impossible)");

        System.out.println("\n== LC 42: Trapping Rain Water ==");
        Check.eq(6, MixedReviewProblems.trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}), "trap(...) = 6 units");

        System.out.println("\n== LC 208: Implement Trie ==");
        MixedReviewProblems.Trie trie = new MixedReviewProblems.Trie();
        trie.insert("apple");
        Check.isTrue(trie.search("apple"), "trie contains \"apple\" after insert");
        Check.isTrue(!trie.search("app"), "trie does NOT contain \"app\" as a full word");
        Check.isTrue(trie.startsWith("app"), "trie DOES have \"app\" as a prefix");

        Check.summary("Week 11 mixed-review suite (15 problems)");
        if (Check.fail > 0) System.exit(1);
    }

    static MixedReviewProblems.ListNode buildList(int... vals) {
        MixedReviewProblems.ListNode dummy = new MixedReviewProblems.ListNode(0);
        MixedReviewProblems.ListNode tail = dummy;
        for (int v : vals) {
            tail.next = new MixedReviewProblems.ListNode(v);
            tail = tail.next;
        }
        return dummy.next;
    }

    static List<Integer> toList(MixedReviewProblems.ListNode head) {
        List<Integer> result = new ArrayList<>();
        while (head != null) { result.add(head.val); head = head.next; }
        return result;
    }
}
