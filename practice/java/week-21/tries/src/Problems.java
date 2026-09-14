import java.util.*;

final class Problems {

    // ---- LC 211: Design Add and Search Words Data Structure ----
    static class WordDictionary {
        static class Node {
            Node[] children = new Node[26];
            boolean end;
        }
        private final Node root = new Node();

        void addWord(String word) {
            Node cur = root;
            for (char c : word.toCharArray()) {
                int i = c - 'a';
                if (cur.children[i] == null) cur.children[i] = new Node();
                cur = cur.children[i];
            }
            cur.end = true;
        }

        boolean search(String word) {
            return dfs(word, 0, root);
        }

        private boolean dfs(String word, int idx, Node node) {
            if (node == null) return false;
            if (idx == word.length()) return node.end;
            char c = word.charAt(idx);
            if (c == '.') {
                for (Node child : node.children) {
                    if (dfs(word, idx + 1, child)) return true;
                }
                return false;
            }
            return dfs(word, idx + 1, node.children[c - 'a']);
        }
    }

    // ---- LC 212: Word Search II ----
    static class TrieNode212 {
        TrieNode212[] children = new TrieNode212[26];
        String word;
    }

    static List<String> findWords(char[][] board, String[] words) {
        TrieNode212 root = new TrieNode212();
        for (String w : words) {
            TrieNode212 cur = root;
            for (char c : w.toCharArray()) {
                int i = c - 'a';
                if (cur.children[i] == null) cur.children[i] = new TrieNode212();
                cur = cur.children[i];
            }
            cur.word = w;
        }
        List<String> result = new ArrayList<>();
        int rows = board.length, cols = board[0].length;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                dfs212(board, r, c, root, result);
            }
        }
        return result;
    }

    private static void dfs212(char[][] board, int r, int c, TrieNode212 node, List<String> result) {
        int rows = board.length, cols = board[0].length;
        if (r < 0 || r >= rows || c < 0 || c >= cols || board[r][c] == '#') return;
        char ch = board[r][c];
        TrieNode212 next = node.children[ch - 'a'];
        if (next == null) return;
        if (next.word != null) {
            result.add(next.word);
            next.word = null; // avoid duplicate additions
        }
        board[r][c] = '#';
        dfs212(board, r + 1, c, next, result);
        dfs212(board, r - 1, c, next, result);
        dfs212(board, r, c + 1, next, result);
        dfs212(board, r, c - 1, next, result);
        board[r][c] = ch;
    }

    // ---- LC 421: Maximum XOR of Two Numbers in an Array ----
    static class BitTrieNode {
        BitTrieNode[] children = new BitTrieNode[2];
    }

    static int findMaximumXOR(int[] nums) {
        BitTrieNode root = new BitTrieNode();
        int maxXor = 0;
        for (int num : nums) {
            BitTrieNode insertCur = root;
            BitTrieNode queryCur = root;
            int currentXor = 0;
            for (int bit = 31; bit >= 0; bit--) {
                int b = (num >> bit) & 1;
                if (insertCur.children[b] == null) insertCur.children[b] = new BitTrieNode();
                insertCur = insertCur.children[b];

                int wanted = 1 - b;
                if (queryCur.children[wanted] != null) {
                    currentXor |= (1 << bit);
                    queryCur = queryCur.children[wanted];
                } else if (queryCur.children[b] != null) {
                    queryCur = queryCur.children[b];
                }
            }
            maxXor = Math.max(maxXor, currentXor);
        }
        return maxXor;
    }

    // ---- LC 648: Replace Words ----
    static class TrieNode648 {
        TrieNode648[] children = new TrieNode648[26];
        boolean end;
    }

    static String replaceWords(List<String> dictionary, String sentence) {
        TrieNode648 root = new TrieNode648();
        for (String root_ : dictionary) {
            TrieNode648 cur = root;
            for (char c : root_.toCharArray()) {
                int i = c - 'a';
                if (cur.children[i] == null) cur.children[i] = new TrieNode648();
                cur = cur.children[i];
            }
            cur.end = true;
        }
        StringBuilder result = new StringBuilder();
        for (String word : sentence.split(" ")) {
            if (result.length() > 0) result.append(" ");
            result.append(shortestRoot(root, word));
        }
        return result.toString();
    }

    private static String shortestRoot(TrieNode648 root, String word) {
        TrieNode648 cur = root;
        StringBuilder prefix = new StringBuilder();
        for (char c : word.toCharArray()) {
            int i = c - 'a';
            if (cur.children[i] == null) return word;
            cur = cur.children[i];
            prefix.append(c);
            if (cur.end) return prefix.toString();
        }
        return word;
    }

    // ---- LC 677: Map Sum Pairs ----
    static class MapSum {
        static class Node {
            Node[] children = new Node[26];
            int score;
        }
        private final Node root = new Node();
        private final Map<String, Integer> stored = new HashMap<>();

        void insert(String key, int val) {
            int delta = val - stored.getOrDefault(key, 0);
            stored.put(key, val);
            Node cur = root;
            cur.score += delta;
            for (char c : key.toCharArray()) {
                int i = c - 'a';
                if (cur.children[i] == null) cur.children[i] = new Node();
                cur = cur.children[i];
                cur.score += delta;
            }
        }

        int sum(String prefix) {
            Node cur = root;
            for (char c : prefix.toCharArray()) {
                int i = c - 'a';
                if (cur.children[i] == null) return 0;
                cur = cur.children[i];
            }
            return cur.score;
        }
    }
}
