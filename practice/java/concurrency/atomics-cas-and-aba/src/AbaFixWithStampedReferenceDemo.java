import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * The same exact interleaving as AbaProblemDemo, but the stack's top is now
 * an AtomicStampedReference<Node> -- every mutation bumps an integer stamp,
 * so Thread 1's stale CAS is REJECTED even though the reference itself is
 * still identity-equal to A. This is the real, standard JDK fix for ABA.
 */
public class AbaFixWithStampedReferenceDemo {

    static class Node<T> {
        final T value;
        Node<T> next;

        Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    static class StampedTreiberStack<T> {
        final AtomicStampedReference<Node<T>> top = new AtomicStampedReference<>(null, 0);

        void push(T value) {
            int[] stampHolder = new int[1];
            Node<T> oldHead;
            Node<T> newHead;
            int oldStamp;
            do {
                oldHead = top.get(stampHolder);
                oldStamp = stampHolder[0];
                newHead = new Node<>(value, oldHead);
            } while (!top.compareAndSet(oldHead, newHead, oldStamp, oldStamp + 1));
        }

        Node<T> pop() {
            int[] stampHolder = new int[1];
            Node<T> oldHead;
            Node<T> newHead;
            int oldStamp;
            do {
                oldHead = top.get(stampHolder);
                if (oldHead == null) return null;
                oldStamp = stampHolder[0];
                newHead = oldHead.next;
            } while (!top.compareAndSet(oldHead, newHead, oldStamp, oldStamp + 1));
            return oldHead;
        }

        void pushExistingNode(Node<T> node) {
            int[] stampHolder = new int[1];
            Node<T> oldHead;
            int oldStamp;
            do {
                oldHead = top.get(stampHolder);
                oldStamp = stampHolder[0];
                node.next = oldHead;
            } while (!top.compareAndSet(oldHead, node, oldStamp, oldStamp + 1));
        }

        String contentsTopToBottom() {
            StringBuilder sb = new StringBuilder("[");
            Node<T> n = top.getReference();
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
        StampedTreiberStack<String> stack = new StampedTreiberStack<>();
        stack.push("C");
        stack.push("B");
        stack.push("A");
        System.out.println("Initial stack (top to bottom): " + stack.contentsTopToBottom());

        // --- Thread 1 begins pop(): captures reference AND stamp, then is "paused" ---
        int[] t1_stampHolder = new int[1];
        Node<String> t1_oldTop = stack.top.get(t1_stampHolder); // A
        int t1_capturedStamp = t1_stampHolder[0];
        Node<String> t1_capturedNext = t1_oldTop.next;          // B
        System.out.println("Thread 1 read oldTop=" + t1_oldTop.value + ", stamp=" + t1_capturedStamp
                + ", capturedNext=" + t1_capturedNext.value + " -- then is preempted before its CAS runs");

        // --- Thread 2 runs to completion, identically to AbaProblemDemo ---
        Node<String> t2_poppedA = stack.pop();
        System.out.println("Thread 2 popped: " + t2_poppedA.value + " -- stack now " + stack.contentsTopToBottom());
        Node<String> t2_poppedB = stack.pop();
        System.out.println("Thread 2 popped: " + t2_poppedB.value + " -- stack now " + stack.contentsTopToBottom());
        stack.pushExistingNode(t2_poppedA); // pushes the SAME A object back
        int[] currentStampHolder = new int[1];
        stack.top.get(currentStampHolder);
        System.out.println("Thread 2 pushed A back (same object) -- stack now " + stack.contentsTopToBottom()
                + ", real current stamp=" + currentStampHolder[0] + " (bumped 3 times: pop, pop, push)");

        // --- Thread 1 resumes: CAS now checks stamp too, not just reference identity ---
        boolean casSucceeded = stack.top.compareAndSet(t1_oldTop, t1_capturedNext, t1_capturedStamp, t1_capturedStamp + 1);
        System.out.println("\nThread 1's CAS(expected=A, new=B, expectedStamp=" + t1_capturedStamp
                + ") succeeded: " + casSucceeded
                + " -- reference IS still == A, but the stamp moved from " + t1_capturedStamp
                + " to " + currentStampHolder[0] + ", so the stamped CAS correctly detects the interleaving and rejects it");
        System.out.println("Stack after Thread 1's (rejected) CAS: " + stack.contentsTopToBottom()
                + "  <-- CORRECT: unchanged from what Thread 2 legitimately produced; Thread 1 must retry its pop from scratch");
    }
}
