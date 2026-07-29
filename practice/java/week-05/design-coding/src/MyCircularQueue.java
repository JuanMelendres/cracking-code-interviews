// LC 622 -- Design Circular Queue.
// ⛔ ERRATA CONTEXT: the Phase 1 audit found the source Notion guide's
// circular-queue implementation declared a `size` field that was never read
// or written, and was MISSING Front(), Rear(), isEmpty(), and isFull() --
// all four are required by this exact LeetCode problem. This is the
// corrected, complete implementation.
final class MyCircularQueue {
    private final int[] data;
    private int front;
    private int count; // the field the audited version declared but never used

    MyCircularQueue(int k) {
        data = new int[k];
        front = 0;
        count = 0;
    }

    boolean enQueue(int value) {
        if (isFull()) return false;
        int rearIndex = (front + count) % data.length;
        data[rearIndex] = value;
        count++;
        return true;
    }

    boolean deQueue() {
        if (isEmpty()) return false;
        front = (front + 1) % data.length;
        count--;
        return true;
    }

    int Front() {
        return isEmpty() ? -1 : data[front];
    }

    int Rear() {
        if (isEmpty()) return -1;
        int rearIndex = (front + count - 1) % data.length;
        return data[rearIndex];
    }

    boolean isEmpty() {
        return count == 0;
    }

    boolean isFull() {
        return count == data.length;
    }
}
