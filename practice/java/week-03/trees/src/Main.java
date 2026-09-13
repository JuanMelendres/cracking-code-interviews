import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Tree:        3
        //            /   \
        //           9     20
        //                /  \
        //               15   7
        TreeNode t1 = new TreeNode(3, new TreeNode(9), new TreeNode(20, new TreeNode(15), new TreeNode(7)));
        Check.eq(3, TreeProblems.maxDepth(t1), "LC104 maxDepth on the canonical example");
        Check.eq(0, TreeProblems.maxDepth(null), "LC104 maxDepth of empty tree");

        TreeNode t2 = new TreeNode(4,
                new TreeNode(2, new TreeNode(1), new TreeNode(3)),
                new TreeNode(7, new TreeNode(6), new TreeNode(9)));
        TreeNode inverted = TreeProblems.invertTree(t2);
        Check.eq(7, inverted.left.val, "LC226 root.left is now the old root.right");
        Check.eq(2, inverted.right.val, "LC226 root.right is now the old root.left");
        Check.eq(9, inverted.left.left.val, "LC226 grandchild swapped correctly");

        TreeNode validBst = new TreeNode(5, new TreeNode(3), new TreeNode(8));
        Check.isTrue(TreeProblems.isValidBST(validBst), "LC98 simple valid BST");

        // The classic trap: locally consistent (2 < 5, 5 < 6 for the immediate
        // parent) but globally invalid, because node 6's LEFT child violates
        // the ancestor bound established by the root (must be < 5, but it's 6).
        TreeNode trapBst = new TreeNode(5,
                new TreeNode(1),
                new TreeNode(6, new TreeNode(3), new TreeNode(7)));
        Check.isTrue(!TreeProblems.isValidBST(trapBst), "LC98 catches the ancestor-bound trap (local check alone would miss this)");

        TreeNode bst = new TreeNode(6,
                new TreeNode(2, new TreeNode(0), new TreeNode(4, new TreeNode(3), new TreeNode(5))),
                new TreeNode(8, new TreeNode(7), new TreeNode(9)));
        TreeNode lca1 = TreeProblems.lowestCommonAncestor(bst, find(bst, 2), find(bst, 8));
        Check.eq(6, lca1.val, "LC235 LCA of 2 and 8 is the root");
        TreeNode lca2 = TreeProblems.lowestCommonAncestor(bst, find(bst, 2), find(bst, 4));
        Check.eq(2, lca2.val, "LC235 LCA of 2 and 4 is 2 itself (ancestor case)");

        List<List<Integer>> levels = TreeProblems.levelOrder(t1);
        Check.eq("[[3], [9, 20], [15, 7]]", levels.toString(), "LC102 level order on the canonical example");

        List<Integer> rightView = TreeProblems.rightSideView(t1);
        Check.eq("[3, 20, 7]", rightView.toString(), "LC199 right side view on the canonical example");

        Check.summary("Week 3 tree suite");
        if (Check.fail > 0) System.exit(1);
    }

    private static TreeNode find(TreeNode root, int val) {
        if (root == null) return null;
        if (root.val == val) return root;
        TreeNode left = find(root.left, val);
        return left != null ? left : find(root.right, val);
    }
}
