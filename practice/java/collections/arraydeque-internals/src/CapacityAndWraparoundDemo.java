import java.lang.reflect.Field;
import java.util.ArrayDeque;

/**
 * Real, reflective proof of two ArrayDeque internals on OpenJDK 21: (1) its
 * backing array capacity is real, measured requestedCapacity + 1 (one slot
 * always kept empty as a full/empty disambiguator) -- NOT rounded up to a
 * power of two, correcting outdated folklore from older JDK versions that
 * used a bitmask-based modulo requiring a power-of-two array length; and
 * (2) head/tail are real integer indices into a circular buffer that
 * genuinely wrap around (head can end up LARGER than tail after enough
 * mixed head/tail operations), not a simple linear array.
 */
public class CapacityAndWraparoundDemo {

    public static void main(String[] args) throws Exception {
        Field elementsField = ArrayDeque.class.getDeclaredField("elements");
        Field headField = ArrayDeque.class.getDeclaredField("head");
        Field tailField = ArrayDeque.class.getDeclaredField("tail");
        elementsField.setAccessible(true);
        headField.setAccessible(true);
        tailField.setAccessible(true);

        System.out.println("== Real backing-array capacity, requested vs actual ==");
        System.out.println("requested\tactual backing array length");
        for (int requested : new int[]{1, 3, 8, 9, 17, 100}) {
            ArrayDeque<Integer> deque = new ArrayDeque<>(requested);
            Object[] elements = (Object[]) elementsField.get(deque);
            System.out.println(requested + "\t\t" + elements.length);
        }

        System.out.println("\n== Real growth behavior when capacity is exceeded ==");
        ArrayDeque<Integer> growing = new ArrayDeque<>(4); // actual capacity 5
        System.out.println("Initial actual capacity: " + ((Object[]) elementsField.get(growing)).length);
        for (int i = 0; i < 4; i++) growing.addLast(i);
        System.out.println("After filling all 4 usable slots: " + ((Object[]) elementsField.get(growing)).length + " (unchanged)");
        growing.addLast(99); // triggers real growth
        System.out.println("After one more add (triggers grow()): " + ((Object[]) elementsField.get(growing)).length);
        for (int i = 0; i < 20; i++) growing.addLast(i);
        System.out.println("After 20 more adds: " + ((Object[]) elementsField.get(growing)).length);

        System.out.println("\n== Real circular wraparound: head index can exceed tail index ==");
        ArrayDeque<Integer> deque = new ArrayDeque<>(4); // small capacity to force wraparound quickly
        Object[] elements = (Object[]) elementsField.get(deque);
        System.out.println("Initial backing array length: " + elements.length);

        // Push and pop from both ends repeatedly to force the circular indices to wrap.
        for (int round = 0; round < 3; round++) {
            deque.addLast(round * 10);
            deque.addLast(round * 10 + 1);
            System.out.println("After addLast x2 (round " + round + "): head=" + headField.getInt(deque)
                    + " tail=" + tailField.getInt(deque) + " contents=" + deque);
            deque.pollFirst();
            System.out.println("After pollFirst (round " + round + "):  head=" + headField.getInt(deque)
                    + " tail=" + tailField.getInt(deque) + " contents=" + deque);
        }

        int head = headField.getInt(deque);
        int tail = tailField.getInt(deque);
        System.out.println("\nFinal real indices: head=" + head + " tail=" + tail
                + (head > tail ? "  <-- head > tail: REAL proof of circular wraparound (a linear array could never show this)" : ""));
    }
}
