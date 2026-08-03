import java.util.*;

final class Main {
    public static void main(String[] args) {
        // LC 211
        Problems.WordDictionary wd = new Problems.WordDictionary();
        wd.addWord("bad");
        wd.addWord("dad");
        wd.addWord("mad");
        Check.isTrue(!wd.search("pad"), "LC211 search(pad) not added -> false");
        Check.isTrue(wd.search("bad"), "LC211 search(bad) exact match -> true");
        Check.isTrue(wd.search(".ad"), "LC211 search(.ad) wildcard -> true");
        Check.isTrue(wd.search("b.."), "LC211 search(b..) wildcard -> true");
        Check.isTrue(!wd.search("b.a"), "LC211 search(b.a) no match -> false");

        // LC 212
        char[][] board = {
            {'o','a','a','n'},
            {'e','t','a','e'},
            {'i','h','k','r'},
            {'i','f','l','v'}
        };
        String[] words = {"oath", "pea", "eat", "rain"};
        List<String> found = Problems.findWords(board, words);
        Set<String> foundSet = new HashSet<>(found);
        Check.eq(2, found.size(), "LC212 findWords count = 2");
        Check.isTrue(foundSet.contains("oath") && foundSet.contains("eat"), "LC212 findWords -> {oath, eat}");

        // LC 421
        Check.eq(28, Problems.findMaximumXOR(new int[]{3,10,5,25,2,8}), "LC421 findMaximumXOR([3,10,5,25,2,8]) = 28");
        Check.eq(127, Problems.findMaximumXOR(new int[]{14,70,53,83,49,91,36,80,92,51,66,70}), "LC421 findMaximumXOR(12 nums) = 127");

        // LC 648
        Check.eq("the cat was rat by the bat",
            Problems.replaceWords(List.of("cat","bat","rat"), "the cattle was rattled by the battery"),
            "LC648 replaceWords standard case");
        Check.eq("a a b c", Problems.replaceWords(List.of("a","b","c"), "a aa b c"), "LC648 replaceWords single-letter roots");

        // LC 677
        Problems.MapSum mapSum = new Problems.MapSum();
        mapSum.insert("apple", 3);
        Check.eq(3, mapSum.sum("ap"), "LC677 sum(ap) after insert(apple,3) = 3");
        mapSum.insert("app", 2);
        Check.eq(5, mapSum.sum("ap"), "LC677 sum(ap) after insert(app,2) = 5");
        mapSum.insert("apple", 4);
        Check.eq(6, mapSum.sum("ap"), "LC677 sum(ap) after overwriting apple to 4 = 6 (2+4)");

        Check.summary("Week 21 — Tries (LC 211, 212, 421, 648, 677)");
    }
}
