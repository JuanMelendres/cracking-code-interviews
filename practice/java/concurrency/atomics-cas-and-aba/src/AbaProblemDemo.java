import java.util.concurrent.atomic.AtomicReference;

/**
 * Real, deterministic (single-threaded, manually interleaved) reproduction
 * of the ABA problem on a lock-free Treiber stack backed by a plain
 * AtomicReference<Node>. No real thread race is needed to demonstrate ABA --
 * it's a logical blind spot in reference-identity CAS, reproducible by hand:
 *
 *   1. "Thread 1" reads the top (A) and A's successor (B), intending to pop A.
 *   2. Before Thread 1's CAS runs, "Thread 2" fully executes: pops A, pops B,
 *      then pushes A back -- the SAME A object, so top is reference-equal to
 *      what Thread 1 already read.
 *   3. Thread 1's CAS(expected=A, new=B) succeeds, because top really is
 *      still == A by reference -- but B was already popped in step 2 and is
 *      stale. The stack is now corrupted: B is resurrected as top, and the
 *      node Thread 2 legitimately pushed (A) is lost.
 */
public class AbaProblemDemo {

    static class Node<T> {
        final T value;
        Node<T> next;

        Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    static class TreiberStack<T> {
        final AtomicReference<Node<T>> top = new AtomicReference<>();

        void push(T value) {
            Node<T> newHead;
            Node<T> oldHead;
            do {
                oldHead = top.get();
                newHead = new Node<>(value, oldHead);
            } while (!top.compareAndSet(oldHead, newHead));
        }

        // Real pop, used to simulate "Thread 2" running to completion.
        Node<T> pop() {
            Node<T> oldHead;
            Node<T> newHead;
            do {
                oldHead = top.get();
                if (oldHead == null) return null;
                newHead = oldHead.next;
            } while (!top.compareAndSet(oldHead, newHead));
            return oldHead;
        }

        // Re-push an EXISTING node object (simulates Thread 2 pushing A back
        // -- the same object identity, which is exactly what makes ABA possible).
        void pushExistingNode(Node<T> node) {
            Node<T> oldHead;
            do {
                oldHead = top.get();
                node.next = oldHead;
            } while (!top.compareAndSet(oldHead, node));
        }

        String contentsTopToBottom() {
            StringBuilder sb = new StringBuilder("[");
            Node<T> n = top.get();
            boolean first = true;
            while (n != null) {
                if (!first) sb.append(", ");
                sb.append(n.value);
                first = false;
                n = n.next;
            }
            return sb.append("]").toString();
        }
    }

    public static void main(String[] args) {
        TreiberStack<String> stack = new TreiberStack<>();
        stack.push("C");
        stack.push("B");
        stack.push("A");
        System.out.println("Initial stack (top to bottom): " + stack.contentsTopToBottom());

        // --- Thread 1 begins pop(): reads top and its successor, then is "paused" ---
        Node<String> t1_oldTop = stack.top.get();          // A
        Node<String> t1_capturedNext = t1_oldTop.next;      // B, captured BEFORE any ABA mutation
        System.out.println("Thread 1 read oldTop=" + t1_oldTop.value + ", capturedNext=" + t1_capturedNext.value
                + " -- then is preempted before its CAS runs");

        // --- Thread 2 runs to completion in the meantime ---
        Node<String> t2_poppedA = stack.pop();
        System.out.println("Thread 2 popped: " + t2_poppedA.value + " -- stack now " + stack.contentsTopToBottom());
        Node<String> t2_poppedB = stack.pop();
        System.out.println("Thread 2 popped: " + t2_poppedB.value + " -- stack now " + stack.contentsTopToBottom());
        stack.pushExistingNode(t2_poppedA); // pushes the SAME A object back
        System.out.println("Thread 2 pushed A back (same object) -- stack now " + stack.contentsTopToBottom());

        // --- Thread 1 resumes: its CAS only checks reference identity of top ---
        boolean casSucceeded = stack.top.compareAndSet(t1_oldTop, t1_capturedNext);
        System.out.println("\nThread 1's CAS(expected=A, new=B) succeeded: " + casSucceeded
                + " -- top IS reference-equal to A, so plain AtomicReference CAS cannot tell anything happened in between");
        System.out.println("Stack after Thread 1's CAS: " + stack.contentsTopToBottom()
                + "  <-- CORRUPTED: B is resurrected at the top (it was already popped), and Thread 2's real push of A is lost");
    }
}
