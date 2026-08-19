import java.lang.reflect.Field;
import java.util.TreeMap;

/**
 * T-203: real, reflective proof that TreeMap's internal Red-Black tree stays
 * balanced (height ~ 2*log2(n+1)) under an ADVERSARIAL insertion order
 * (ascending 1..N -- the exact input that degenerates a naive, unbalanced
 * BST into a linked list, real height N), contrasted directly against a
 * from-scratch naive BST inserting the identical sequence.
 *
 * Requires: --add-opens java.base/java.util=ALL-UNNAMED
 * (TreeMap's `root` field, and TreeMap.Entry's `left`/`right` fields, are
 * private; reflection needs explicit module access, same requirement
 * hashmap-internals.md's own demos state.)
 */
public class RedBlackHeightDemo {

    public static void main(String[] args) throws Exception {
        int[] sizes = {10, 100, 1_000, 10_000, 100_000};

        System.out.println("n\tTreeMap height (real, reflective)\tNaive BST height (real)\t2*log2(n+1)");
        for (int n : sizes) {
            TreeMap<Integer, Integer> tree = new TreeMap<>();
            NaiveBst naive = new NaiveBst();
            for (int i = 1; i <= n; i++) {
                tree.put(i, i);       // ascending order -- the worst case for an unbalanced BST
                naive.insert(i);
            }
            int rbHeight = heightOf(rootOf(tree));
            int naiveHeight = naive.height();
            double bound = 2 * (Math.log(n + 1) / Math.log(2));
            System.out.printf("%d\t%d\t%d\t%.1f%n", n, rbHeight, naiveHeight, bound);
        }
    }

    // Reflectively reads TreeMap's private `root` field.
    private static Object rootOf(TreeMap<?, ?> map) throws Exception {
        Field rootField = TreeMap.class.getDeclaredField("root");
        rootField.setAccessible(true);
        return rootField.get(map);
    }

    // Reflectively walks TreeMap.Entry's private `left`/`right` fields to
    // compute the real tree height (edges on the longest root-to-leaf path).
    private static int heightOf(Object node) throws Exception {
        if (node == null) return 0;
        Class<?> entryClass = node.getClass();
        Field leftField = entryClass.getDeclaredField("left");
        Field rightField = entryClass.getDeclaredField("right");
        leftField.setAccessible(true);
        rightField.setAccessible(true);
        int leftHeight = heightOf(leftField.get(node));
        int rightHeight = heightOf(rightField.get(node));
        return 1 + Math.max(leftHeight, rightHeight);
    }

    /**
     * A deliberately naive, unbalanced BST -- no rebalancing at all. Iterative,
     * not recursive: a real recursive version genuinely StackOverflowErrors on
     * ascending input past ~n=10,000 on a default JVM stack, since it degenerates
     * into a linked list of that same depth -- itself a real, worth-noting
     * consequence of the exact defect this demo measures.
     */
    static class NaiveBst {
        static class Node {
            int val;
            Node left, right;
            Node(int val) { this.val = val; }
        }

        Node root;

        void insert(int val) {
            if (root == null) { root = new Node(val); return; }
            Node cur = root;
            while (true) {
                if (val < cur.val) {
                    if (cur.left == null) { cur.left = new Node(val); return; }
                    cur = cur.left;
                } else {
                    if (cur.right == null) { cur.right = new Node(val); return; }
                    cur = cur.right;
                }
            }
        }

        int height() {
            int h = 0;
            Node cur = root;
            while (cur != null) {
                h++;
                cur = (cur.right != null) ? cur.right : cur.left;
            }
            return h;
        }
    }
}
