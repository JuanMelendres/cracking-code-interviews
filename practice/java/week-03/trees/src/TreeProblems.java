import java.util.*;

final class TreeProblems {

    // LC 104 — Maximum Depth of Binary Tree. Invariant: depth(node) = 1 + max(depth(left), depth(right)).
    static int maxDepth(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }

    // LC 226 — Invert Binary Tree. Invariant: invert(node) swaps node's children,
    // after each subtree has already been fully inverted.
    static TreeNode invertTree(TreeNode root) {
        if (root == null) return null;
        TreeNode left = invertTree(root.left);
        TreeNode right = invertTree(root.right);
        root.left = right;
        root.right = left;
        return root;
    }

    // LC 98 — Validate Binary Search Tree. Invariant: every node's value must fall
    // within the (exclusive) bounds established by its ancestors, not just be
    // greater than its immediate left child and less than its immediate right --
    // that local-only check is the classic wrong-but-tempting first instinct.
    static boolean isValidBST(TreeNode root) {
        return validate(root, null, null);
    }

    private static boolean validate(TreeNode node, Integer lowerExclusive, Integer upperExclusive) {
        if (node == null) return true;
        if (lowerExclusive != null && node.val <= lowerExclusive) return false;
        if (upperExclusive != null && node.val >= upperExclusive) return false;
        return validate(node.left, lowerExclusive, node.val) && validate(node.right, node.val, upperExclusive);
    }

    // LC 235 — Lowest Common Ancestor of a BST. Invariant: use BST ordering to
    // decide direction in O(h) instead of a generic O(n) tree search.
    static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode cur = root;
        while (cur != null) {
            if (p.val < cur.val && q.val < cur.val) cur = cur.left;
            else if (p.val > cur.val && q.val > cur.val) cur = cur.right;
            else return cur;
        }
        throw new IllegalArgumentException("p and q must both exist in the tree");
    }

    // LC 102 — Binary Tree Level Order Traversal. BFS, one list per level.
    static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
            result.add(level);
        }
        return result;
    }

    // LC 199 — Binary Tree Right Side View. The last node visited at each BFS
    // level is exactly the one visible from the right side.
    static List<Integer> rightSideView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                if (i == levelSize - 1) result.add(node.val);
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
            }
        }
        return result;
    }
}
