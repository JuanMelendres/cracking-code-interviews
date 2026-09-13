import java.util.ArrayDeque;
import java.util.Deque;

/** LC 1188: Design Bounded Blocking Queue. Implemented from scratch with intrinsic
 * lock + wait/notifyAll (not java.util.concurrent.ArrayBlockingQueue) to demonstrate
 * the underlying mechanism: enqueue blocks while full, dequeue blocks while empty. */
public class BoundedBlockingQueue {
    private final Deque<Integer> queue = new ArrayDeque<>();
    private final int capacity;
    private final Object lock = new Object();

    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public void enqueue(int element) throws InterruptedException {
        synchronized (lock) {
            while (queue.size() == capacity) {
                lock.wait();
            }
            queue.addLast(element);
            lock.notifyAll();
        }
    }

    public int dequeue() throws InterruptedException {
        synchronized (lock) {
            while (queue.isEmpty()) {
                lock.wait();
            }
            int value = queue.removeFirst();
            lock.notifyAll();
            return value;
        }
    }

    public int size() {
        synchronized (lock) {
            return queue.size();
        }
    }
}
