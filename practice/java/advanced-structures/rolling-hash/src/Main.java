import java.util.*;

final class Main {
    public static void main(String[] args) {
        // LC 187: Repeated DNA Sequences
        List<String> found1 = Problems.findRepeatedDnaSequences("AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT");
        Check.eq(new HashSet<>(List.of("AAAAACCCCC", "CCCCCAAAAA")), new HashSet<>(found1),
            "LC187 findRepeatedDnaSequences classic example -> {AAAAACCCCC, CCCCCAAAAA}");
        Check.eq(2, found1.size(), "LC187 classic example returns exactly 2 sequences, no duplicate entries");

        List<String> found2 = Problems.findRepeatedDnaSequences("AAAAAAAAAAAAA");
        Check.eq(List.of("AAAAAAAAAA"), found2, "LC187 findRepeatedDnaSequences(\"AAAAAAAAAAAAA\") = [AAAAAAAAAA] (one entry, not one per overlap)");

        Check.eq(List.of(), Problems.findRepeatedDnaSequences("ACGTACGTAC"),
            "LC187 findRepeatedDnaSequences with no repeats (exactly 10 chars, single window) = []");

        // LC 1044: Longest Duplicate Substring
        Check.eq("ana", Problems.longestDupSubstring("banana"), "LC1044 longestDupSubstring(\"banana\") = \"ana\"");
        Check.eq("", Problems.longestDupSubstring("abcd"), "LC1044 longestDupSubstring(\"abcd\") = \"\" (no duplicate substring)");
        Check.eq("aaaa", Problems.longestDupSubstring("aaaaa"), "LC1044 longestDupSubstring(\"aaaaa\") = \"aaaa\" (overlapping occurrences allowed)");
        Check.eq("abcabc", Problems.longestDupSubstring("abcabcabc"), "LC1044 longestDupSubstring(\"abcabcabc\") = \"abcabc\"");

        Check.summary("Advanced Structures — Rolling Hash (LC 187, 1044)");
    }
}
