import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Demonstrates List, Map, and Set at a usage level -- what each one is FOR,
 * not how it works internally (see hashmap-internals.md,
 * arraylist-and-linkedlist-internals.md, and this repo's other Collections
 * chapters for the "how" once this "what and when" is solid). One small,
 * real task -- counting word frequency in a sentence -- uses all three.
 */
public class WordFrequencyDemo {

    private static int assertions = 0;
    private static final List<String> failures = new ArrayList<>();

    private static void check(boolean condition, String description) {
        assertions++;
        if (!condition) {
            failures.add(description);
        }
    }

    public static void main(String[] args) {
        String sentence = "the quick brown fox jumps over the lazy dog the fox runs";
        String[] wordArray = sentence.split(" ");

        // List: an ORDERED collection that ALLOWS DUPLICATES -- exactly
        // matching the words as they actually appeared, in order, "the"
        // appearing three separate times.
        List<String> words = new ArrayList<>();
        for (String w : wordArray) {
            words.add(w);
        }
        check(words.size() == 12, "List keeps every word, including repeats -- 12 words total");
        check(words.get(0).equals("the"), "List preserves insertion order -- get(0) is the first word");
        long theCountInList = words.stream().filter(w -> w.equals("the")).count();
        check(theCountInList == 3, "the same word can appear multiple times in a List");

        // Set: an UNORDERED collection with NO DUPLICATES -- adding "the"
        // three times still leaves exactly one "the" in the Set.
        Set<String> uniqueWords = new HashSet<>(words);
        check(uniqueWords.size() == 9, "Set automatically collapses the 3 occurrences of \"the\" and 2 of \"fox\" down to unique words only");
        check(uniqueWords.contains("fox"), "Set supports fast membership checks with contains()");
        check(!uniqueWords.contains("cat"), "contains() correctly returns false for a word never in the sentence");

        // Map: an ASSOCIATION between a key and a value -- here, each
        // unique word mapped to how many times it appeared.
        Map<String, Integer> wordCounts = new HashMap<>();
        for (String w : words) {
            // getOrDefault: read the current count, or 0 if this is the
            // word's first appearance -- avoids a null check.
            int currentCount = wordCounts.getOrDefault(w, 0);
            wordCounts.put(w, currentCount + 1);
        }
        check(wordCounts.get("the") == 3, "Map correctly counts 3 occurrences of \"the\"");
        check(wordCounts.get("fox") == 2, "Map correctly counts 2 occurrences of \"fox\"");
        check(wordCounts.get("quick") == 1, "Map correctly counts a word that appears only once");
        check(wordCounts.size() == uniqueWords.size(), "Map has exactly one entry per unique word -- same count as the Set");

        // Removing from each: List removes one specific occurrence by
        // value; Map removes a whole key-value entry.
        words.remove("dog");
        check(words.size() == 11, "List.remove(value) removes exactly one occurrence");
        check(words.contains("the"), "removing \"dog\" doesn't affect other elements still in the List");

        wordCounts.remove("lazy");
        check(!wordCounts.containsKey("lazy"), "Map.remove(key) removes the entire key-value entry");
        check(wordCounts.size() == 8, "Map size decreases by exactly one after removing one key");

        System.out.println((failures.isEmpty() ? "PASS" : "FAIL") + " " + (assertions - failures.size()) + "/" + assertions + " assertions");
        for (String f : failures) {
            System.out.println("  FAILED: " + f);
        }
        if (!failures.isEmpty()) {
            System.exit(1);
        }
    }
}
