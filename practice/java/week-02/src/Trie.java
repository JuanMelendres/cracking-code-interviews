import java.util.HashMap;
import java.util.Map;

// LC 208 — Implement Trie (Prefix Tree). O(k) insert/search/startsWith, k = word length.
final class Trie {
    private static final class Node {
        Map<Character, Node> children = new HashMap<>();
        boolean isEnd = false;
    }

    private final Node root = new Node();

    void insert(String word) {
        Node cur = root;
        for (char c : word.toCharArray()) {
            cur = cur.children.computeIfAbsent(c, k -> new Node());
        }
        cur.isEnd = true;
    }

    boolean search(String word) {
        Node n = find(word);
        return n != null && n.isEnd;
    }

    boolean startsWith(String prefix) {
        return find(prefix) != null;
    }

    private Node find(String s) {
        Node cur = root;
        for (char c : s.toCharArray()) {
            cur = cur.children.get(c);
            if (cur == null) return null;
        }
        return cur;
    }
}
