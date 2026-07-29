import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("== Week 2 problem set — binary search family ==");
        Check.eq(4, BinarySearchProblems.search(new int[]{-1,0,3,5,9,12}, 9), "LC704 search found");
        Check.eq(-1, BinarySearchProblems.search(new int[]{-1,0,3,5,9,12}, 2), "LC704 search not found");

        Check.eq(2, BinarySearchProblems.searchInsert(new int[]{1,3,5,6}, 5), "LC35 exact match");
        Check.eq(1, BinarySearchProblems.searchInsert(new int[]{1,3,5,6}, 2), "LC35 insert between");
        Check.eq(4, BinarySearchProblems.searchInsert(new int[]{1,3,5,6}, 7), "LC35 insert at end");

        Check.eq(4, BinarySearchProblems.searchRotated(new int[]{4,5,6,7,0,1,2}, 0), "LC33 target in right half");
        Check.eq(-1, BinarySearchProblems.searchRotated(new int[]{4,5,6,7,0,1,2}, 3), "LC33 target absent");

        Check.eq(4, BinarySearchProblems.minEatingSpeed(new int[]{3,6,7,11}, 8), "LC875 koko example 1");
        Check.eq(30, BinarySearchProblems.minEatingSpeed(new int[]{30,11,23,4,20}, 5), "LC875 koko example 2");
        Check.eq(23, BinarySearchProblems.minEatingSpeed(new int[]{30,11,23,4,20}, 6), "LC875 koko example 3");

        System.out.println("\n== Stack family ==");
        Check.isTrue(StackProblems.isValid("()[]{}"), "LC20 valid nested");
        Check.isTrue(!StackProblems.isValid("(]"), "LC20 mismatched");
        Check.isTrue(!StackProblems.isValid("(("), "LC20 unclosed");

        MinStack ms = new MinStack();
        ms.push(-2); ms.push(0); ms.push(-3);
        Check.eq(-3, ms.getMin(), "LC155 min after 3 pushes");
        ms.pop();
        Check.eq(0, ms.top(), "LC155 top after pop");
        Check.eq(-2, ms.getMin(), "LC155 min after pop");

        int[] temps = {73,74,75,71,69,72,76,73};
        int[] expected = {1,1,4,2,1,1,0,0};
        Check.eq(Arrays.toString(expected), Arrays.toString(StackProblems.dailyTemperatures(temps)),
                "LC739 daily temperatures, index-based monotonic stack (corrected — see errata)");

        System.out.println("\n== Trie ==");
        Trie trie = new Trie();
        trie.insert("apple");
        Check.isTrue(trie.search("apple"), "LC208 search exact word after insert");
        Check.isTrue(!trie.search("app"), "LC208 prefix alone is not a word");
        Check.isTrue(trie.startsWith("app"), "LC208 startsWith matches prefix");
        trie.insert("app");
        Check.isTrue(trie.search("app"), "LC208 search matches after inserting the prefix as its own word");

        Check.summary("Week 2 suite");
        if (Check.fail > 0) System.exit(1);
    }
}
