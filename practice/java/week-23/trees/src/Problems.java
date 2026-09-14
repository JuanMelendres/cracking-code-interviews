import java.util.*;

final class Problems {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    // ---- LC 543: Diameter of Binary Tree ----
    static int diameterOfBinaryTree(TreeNode root) {
        int[] diameter = new int[1];
        depthForDiameter(root, diameter);
        return diameter[0];
    }

    private static int depthForDiameter(TreeNode node, int[] diameter) {
        if (node == null) return 0;
        int leftDepth = depthForDiameter(node.left, diameter);
        int rightDepth = depthForDiameter(node.right, diameter);
        diameter[0] = Math.max(diameter[0], leftDepth + rightDepth);
        return 1 + Math.max(leftDepth, rightDepth);
    }

    // ---- LC 297: Serialize and Deserialize Binary Tree ----
    static String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }

    private static void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) { sb.append("#,"); return; }
        sb.append(node.val).append(",");
        serializeHelper(node.left, sb);
        serializeHelper(node.right, sb);
    }

    static TreeNode deserialize(String data) {
        Deque<String> tokens = new ArrayDeque<>(Arrays.asList(data.split(",")));
        return deserializeHelper(tokens);
    }

    private static TreeNode deserializeHelper(Deque<String> tokens) {
        String token = tokens.poll();
        if (token.equals("#")) return null;
        TreeNode node = new TreeNode(Integer.parseInt(token));
        node.left = deserializeHelper(tokens);
        node.right = deserializeHelper(tokens);
        return node;
    }

    // ---- LC 230: Kth Smallest Element in a BST ----
    static int kthSmallest(TreeNode root, int k) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root;
        int count = 0;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();
            count++;
            if (count == k) return cur.val;
            cur = cur.right;
        }
        throw new IllegalArgumentException("k out of range");
    }

    // ---- LC 105: Construct Binary Tree from Preorder and Inorder Traversal ----
    static TreeNode buildTree(int[] preorder, int[] inorder) {
        Map<Integer, Integer> inorderIndex = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) inorderIndex.put(inorder[i], i);
        int[] preorderPos = {0};
        return buildHelper(preorder, preorderPos, 0, inorder.length - 1, inorderIndex);
    }

    private static TreeNode buildHelper(int[] preorder, int[] preorderPos, int inLo, int inHi,
                                         Map<Integer, Integer> inorderIndex) {
        if (inLo > inHi) return null;
        int rootVal = preorder[preorderPos[0]++];
        TreeNode root = new TreeNode(rootVal);
        int mid = inorderIndex.get(rootVal);
        root.left = buildHelper(preorder, preorderPos, inLo, mid - 1, inorderIndex);
        root.right = buildHelper(preorder, preorderPos, mid + 1, inHi, inorderIndex);
        return root;
    }

    // ---- LC 112: Path Sum ----
    static boolean hasPathSum(TreeNode root, int targetSum) {
        if (root == null) return false;
        if (root.left == null && root.right == null) return root.val == targetSum;
        int remaining = targetSum - root.val;
        return hasPathSum(root.left, remaining) || hasPathSum(root.right, remaining);
    }
}
