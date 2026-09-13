/**
 * Proves each thread's stack is its own fixed-size region, sized independently
 * of the heap via -Xss, by measuring the maximum recursion depth reachable
 * before a real StackOverflowError at two different -Xss values on an
 * otherwise-identical heap configuration.
 *
 * Run with:
 *   java -Xmx512m -Xss256k -cp out StackDepthDemo
 *   java -Xmx512m -Xss1m   -cp out StackDepthDemo
 */
public class StackDepthDemo {
    static int depth = 0;

    static void recurse() {
        depth++;
        recurse();
    }

    public static void main(String[] args) {
        try {
            recurse();
        } catch (StackOverflowError e) {
            System.out.println("StackOverflowError at depth:");
            System.out.println(String.valueOf(depth));
        }
    }
}
