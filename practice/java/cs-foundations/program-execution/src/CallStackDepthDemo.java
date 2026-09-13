/**
 * Measures how many nested method calls fit on one thread's call stack before
 * StackOverflowError, and how that number changes with -Xss (thread stack size).
 *
 * Every call adds one frame (return address, local variables, operand references)
 * to the current thread's stack. That stack is a fixed-size region of memory
 * carved out per thread at thread-creation time -- not the heap, and not
 * resizable once the thread exists. This demo makes that limit, and its
 * dependence on -Xss, directly observable instead of asserted.
 */
public class CallStackDepthDemo {

    private static long depth = 0;

    public static void main(String[] args) {
        try {
            recurse();
        } catch (StackOverflowError e) {
            System.out.println("StackOverflowError after " + depth + " nested calls.");
        }
    }

    // No parameters, no local variables beyond the implicit frame overhead --
    // isolates "how many frames fit" from "how big is each frame."
    private static void recurse() {
        depth++;
        recurse();
    }
}
