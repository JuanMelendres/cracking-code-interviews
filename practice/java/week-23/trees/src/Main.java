final class Main {
    public static void main(String[] args) {
        // LC 543
        Problems.TreeNode t1 = new Problems.TreeNode(1,
            new Problems.TreeNode(2, new Problems.TreeNode(4), new Problems.TreeNode(5)),
            new Problems.TreeNode(3));
        Check.eq(3, Problems.diameterOfBinaryTree(t1), "LC543 diameterOfBinaryTree(5-node tree) = 3 (longest path 4-2-1-3, in edges)");
        Problems.TreeNode t2 = new Problems.TreeNode(1, new Problems.TreeNode(2), null);
        Check.eq(1, Problems.diameterOfBinaryTree(t2), "LC543 diameterOfBinaryTree(1,(2)) = 1");

        // LC 297
        Problems.TreeNode t3 = new Problems.TreeNode(1,
            new Problems.TreeNode(2), new Problems.TreeNode(3, new Problems.TreeNode(4), new Problems.TreeNode(5)));
        String serialized = Problems.serialize(t3);
        Problems.TreeNode roundTrip = Problems.deserialize(serialized);
        Check.eq(serialized, Problems.serialize(roundTrip), "LC297 serialize(deserialize(serialize(tree))) round-trips identically");
        Check.eq(1, roundTrip.val, "LC297 round-tripped root val = 1");
        Check.eq(5, roundTrip.right.right.val, "LC297 round-tripped tree structure preserved (root.right.right = 5)");
        Problems.TreeNode nullTree = Problems.deserialize(Problems.serialize(null));
        Check.isTrue(nullTree == null, "LC297 serialize/deserialize(null) round-trips to null");

        // LC 230
        Problems.TreeNode bst = new Problems.TreeNode(5,
            new Problems.TreeNode(3, new Problems.TreeNode(2, new Problems.TreeNode(1), null), new Problems.TreeNode(4)),
            new Problems.TreeNode(6));
        Check.eq(3, Problems.kthSmallest(bst, 3), "LC230 kthSmallest(k=3) = 3");
        Check.eq(1, Problems.kthSmallest(bst, 1), "LC230 kthSmallest(k=1) = 1 (leftmost)");
        Check.eq(6, Problems.kthSmallest(bst, 6), "LC230 kthSmallest(k=6) = 6 (rightmost, largest)");

        // LC 105
        int[] preorder = {3,9,20,15,7};
        int[] inorder = {9,3,15,20,7};
        Problems.TreeNode built = Problems.buildTree(preorder, inorder);
        Check.eq(3, built.val, "LC105 buildTree root = 3");
        Check.eq(9, built.left.val, "LC105 buildTree root.left = 9");
        Check.eq(20, built.right.val, "LC105 buildTree root.right = 20");
        Check.eq(15, built.right.left.val, "LC105 buildTree root.right.left = 15");
        Check.eq(7, built.right.right.val, "LC105 buildTree root.right.right = 7");

        // LC 112
        Problems.TreeNode pathTree = new Problems.TreeNode(5,
            new Problems.TreeNode(4, new Problems.TreeNode(11, new Problems.TreeNode(7), new Problems.TreeNode(2)), null),
            new Problems.TreeNode(8, new Problems.TreeNode(13), new Problems.TreeNode(4, null, new Problems.TreeNode(1))));
        Check.isTrue(Problems.hasPathSum(pathTree, 22), "LC112 hasPathSum(target=22) -> true (5-4-11-2)");
        Check.isTrue(Problems.hasPathSum(pathTree, 26), "LC112 hasPathSum(target=26) -> true (5-8-13)");
        Check.isTrue(!Problems.hasPathSum(pathTree, 100), "LC112 hasPathSum(target=100) -> false (no root-to-leaf path sums that high)");
        Check.isTrue(!Problems.hasPathSum(null, 0), "LC112 hasPathSum(null tree) -> false");

        Check.summary("Week 23 — Trees (LC 543, 297, 230, 105, 112)");
    }
}
