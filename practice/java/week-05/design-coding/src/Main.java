public class Main {
    public static void main(String[] args) {
        System.out.println("== LC 380: Insert Delete GetRandom O(1) ==");
        RandomizedSet set = new RandomizedSet();
        Check.isTrue(set.insert(1), "insert 1 succeeds (new value)");
        Check.isTrue(!set.insert(1), "insert 1 again fails (already present)");
        Check.isTrue(set.insert(2), "insert 2 succeeds");
        Check.isTrue(set.remove(1), "remove 1 succeeds");
        Check.isTrue(!set.remove(1), "remove 1 again fails (already gone)");
        set.insert(1);
        int r = set.getRandom();
        Check.isTrue(r == 1 || r == 2, "getRandom returns a value currently in the set");

        System.out.println("\n== LC 706: Design HashMap ==");
        MyHashMap map = new MyHashMap();
        map.put(1, 100);
        map.put(2, 200);
        Check.eq(100, map.get(1), "get key 1");
        Check.eq(-1, map.get(3), "get missing key returns -1");
        map.put(2, 250);
        Check.eq(250, map.get(2), "put on existing key updates value");
        map.remove(2);
        Check.eq(-1, map.get(2), "get after remove returns -1");
        // Force a real collision: 1 and 1 + BUCKETS(1024) hash to the same bucket
        // after the spread function, since floorMod wraps at 1024.
        map.put(1, 111);
        map.put(1025, 999);
        Check.eq(111, map.get(1), "collision: key 1 still correct after inserting key 1025");
        Check.eq(999, map.get(1025), "collision: key 1025 correct, chained in the same bucket as key 1");

        System.out.println("\n== LC 622: Design Circular Queue (errata fix) ==");
        MyCircularQueue q = new MyCircularQueue(3);
        Check.isTrue(q.enQueue(1), "enqueue 1");
        Check.isTrue(q.enQueue(2), "enqueue 2");
        Check.isTrue(q.enQueue(3), "enqueue 3");
        Check.isTrue(!q.enQueue(4), "enqueue 4 fails, queue is full (isFull correctly implemented)");
        Check.eq(3, q.Rear(), "Rear() returns 3");
        Check.isTrue(q.deQueue(), "dequeue succeeds");
        Check.isTrue(q.enQueue(4), "enqueue 4 now succeeds after a dequeue freed a slot");
        Check.eq(2, q.Front(), "Front() returns 2 after the first dequeue");
        Check.eq(4, q.Rear(), "Rear() returns 4 (wrapped around the circular buffer)");
        q.deQueue(); q.deQueue(); q.deQueue();
        Check.isTrue(q.isEmpty(), "isEmpty() true after dequeuing everything");
        Check.eq(-1, q.Front(), "Front() on empty queue returns -1");

        Check.summary("Week 5 design-coding suite");
        if (Check.fail > 0) System.exit(1);
    }
}
