import java.util.HashMap;
import java.util.Map;

/**
 * ⛔ ERRATA — this is the defective implementation found in the Notion
 * DSA Patterns guide (audit finding, module 23). Do NOT use this as a
 * reference. See LRUCacheFixed.java for the corrected version and
 * study-packs/week-01/07-java-coding-practice.md for the full writeup.
 *
 * The bug: on an update to an EXISTING key, the node is unlinked from
 * the doubly linked list but the map entry for that key is not removed
 * before the capacity check runs. If the cache is already full, the
 * capacity check reads map.size() == capacity as true (the stale
 * mapping is still counted) and evicts the current tail — a different,
 * still-valid key — even though this operation is a pure update and
 * should not evict anything at all.
 */
final class LRUCacheBuggy {
    private static final class Node {
        int key, val;
        Node prev, next;
        Node(int key, int val) { this.key = key; this.val = val; }
    }

    private final int capacity;
    private final Map<Integer, Node> map = new HashMap<>();
    private final Node head = new Node(-1, -1);
    private final Node tail = new Node(-1, -1);

    LRUCacheBuggy(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    private void unlink(Node n) {
        n.prev.next = n.next;
        n.next.prev = n.prev;
    }

    private void insertFront(Node n) {
        n.next = head.next;
        n.prev = head;
        head.next.prev = n;
        head.next = n;
    }

    int get(int key) {
        Node n = map.get(key);
        if (n == null) return -1;
        unlink(n);
        insertFront(n);
        return n.val;
    }

    void put(int key, int value) {
        if (map.containsKey(key)) {
            // BUG: unlinks from the list but never removes the stale
            // entry from the map before the capacity check below.
            unlink(map.get(key));
        }
        if (map.size() == capacity) {
            Node lru = tail.prev;
            unlink(lru);
            map.remove(lru.key);
        }
        Node fresh = new Node(key, value);
        map.put(key, fresh);
        insertFront(fresh);
    }
}
