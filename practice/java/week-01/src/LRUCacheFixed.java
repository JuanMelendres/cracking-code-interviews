import java.util.HashMap;
import java.util.Map;

/**
 * LC 146 — LRU Cache. HashMap + intrusive doubly linked list, O(1) get/put.
 * Sentinel head/tail nodes remove all null-checks at the list boundaries.
 *
 * Fix relative to LRUCacheBuggy: an update to an existing key removes the
 * map entry (map.remove(key)) at the same time it unlinks the node, so the
 * capacity check that follows only ever counts genuinely distinct keys.
 */
final class LRUCacheFixed {
    private static final class Node {
        int key, val;
        Node prev, next;
        Node(int key, int val) { this.key = key; this.val = val; }
    }

    private final int capacity;
    private final Map<Integer, Node> map = new HashMap<>();
    private final Node head = new Node(-1, -1);
    private final Node tail = new Node(-1, -1);

    LRUCacheFixed(int capacity) {
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
            Node existing = map.get(key);
            unlink(existing);
            map.remove(key); // <- the missing line in the buggy version
        } else if (map.size() == capacity) {
            Node lru = tail.prev;
            unlink(lru);
            map.remove(lru.key);
        }
        Node fresh = new Node(key, value);
        map.put(key, fresh);
        insertFront(fresh);
    }
}
