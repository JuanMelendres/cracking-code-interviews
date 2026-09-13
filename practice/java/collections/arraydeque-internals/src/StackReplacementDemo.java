import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Real, measured proof of the "ArrayDeque replaces the legacy Stack/Vector"
 * recommendation: Stack extends the legacy, synchronized Vector, paying
 * real lock-acquisition cost on every single push/pop even in genuinely
 * single-threaded code. ArrayDeque used as a stack (push/pop via the Deque
 * interface) pays none of that cost.
 *
 * Also real proof of a genuine behavioral gotcha: ArrayDeque disallows
 * null elements (throws NullPointerException) because null is used
 * internally as the "empty slot" sentinel -- LinkedList permits null fine.
 */
public class StackReplacementDemo {

    static final int OPERATIONS = 20_000_000;

    public static void main(String[] args) {
        long stackElapsed = measureLegacyStack();
        long arrayDequeElapsed = measureArrayDequeAsStack();
        long linkedListElapsed = measureLinkedListAsDeque();

        System.out.println("\n== Real measured wall-clock time, " + OPERATIONS + " push+pop pairs ==");
        System.out.println("java.util.Stack (legacy, synchronized): " + stackElapsed + "ms");
        System.out.println("ArrayDeque (via Deque push/pop):        " + arrayDequeElapsed + "ms");
        System.out.println("LinkedList (via Deque push/pop):        " + linkedListElapsed + "ms");
        System.out.println("Real measured ArrayDeque vs Stack speedup: "
                + String.format("%.2fx", (double) stackElapsed / arrayDequeElapsed));

        System.out.println("\n== Real null-handling difference ==");
        Deque<String> arrayDeque = new ArrayDeque<>();
        try {
            arrayDeque.addFirst(null);
            System.out.println("ArrayDeque.addFirst(null): did NOT throw (unexpected)");
        } catch (NullPointerException e) {
            System.out.println("ArrayDeque.addFirst(null): threw real NullPointerException"
                    + " (null is reserved internally as the empty-slot sentinel)");
        }

        Deque<String> linkedList = new LinkedList<>();
        linkedList.addFirst(null);
        System.out.println("LinkedList.addFirst(null):  succeeded, contents=" + linkedList
                + " (LinkedList has no such internal sentinel restriction)");
    }

    static long measureLegacyStack() {
        Stack<Integer> stack = new Stack<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < OPERATIONS; i++) {
            stack.push(i);
            stack.pop();
        }
        return System.currentTimeMillis() - start;
    }

    static long measureArrayDequeAsStack() {
        Deque<Integer> deque = new ArrayDeque<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < OPERATIONS; i++) {
            deque.push(i);
            deque.pop();
        }
        return System.currentTimeMillis() - start;
    }

    static long measureLinkedListAsDeque() {
        Deque<Integer> deque = new LinkedList<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < OPERATIONS; i++) {
            deque.push(i);
            deque.pop();
        }
        return System.currentTimeMillis() - start;
    }
}
