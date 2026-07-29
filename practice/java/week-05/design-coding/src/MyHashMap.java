// LC 706 -- Design HashMap, from scratch. Separate chaining with a fixed
// bucket array -- the same structural idea java.util.HashMap uses
// internally (bucket array + chaining, treeified past a threshold in the
// real implementation; this version keeps plain linked chaining for clarity).
final class MyHashMap {
    private static final int BUCKETS = 1024;
    private final Node[] table = new Node[BUCKETS];

    private static final class Node {
        int key, value;
        Node next;
        Node(int key, int value) { this.key = key; this.value = value; }
    }

    private int bucketIndex(int key) {
        // Spreads the hash across the bucket count -- java.util.HashMap does
        // something similar (h ^ (h >>> 16)) specifically to use the high bits
        // of hashCode() too, since a plain modulo only ever looks at the low bits.
        int h = Integer.hashCode(key);
        h ^= (h >>> 16);
        return Math.floorMod(h, BUCKETS);
    }

    void put(int key, int value) {
        int idx = bucketIndex(key);
        Node cur = table[idx];
        while (cur != null) {
            if (cur.key == key) { cur.value = value; return; }
            cur = cur.next;
        }
        Node newNode = new Node(key, value);
        newNode.next = table[idx];
        table[idx] = newNode;
    }

    int get(int key) {
        int idx = bucketIndex(key);
        Node cur = table[idx];
        while (cur != null) {
            if (cur.key == key) return cur.value;
            cur = cur.next;
        }
        return -1;
    }

    void remove(int key) {
        int idx = bucketIndex(key);
        Node cur = table[idx], prev = null;
        while (cur != null) {
            if (cur.key == key) {
                if (prev == null) table[idx] = cur.next; else prev.next = cur.next;
                return;
            }
            prev = cur;
            cur = cur.next;
        }
    }
}
