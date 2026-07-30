import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("== Loop 1 -- LC 3: Longest Substring Without Repeating Characters ==");
        Check.eq(3, FinalLoopProblems.lengthOfLongestSubstring("abcabcbb"), "\"abcabcbb\" -> 3 (\"abc\")");
        Check.eq(1, FinalLoopProblems.lengthOfLongestSubstring("bbbbb"), "\"bbbbb\" -> 1");
        Check.eq(3, FinalLoopProblems.lengthOfLongestSubstring("pwwkew"), "\"pwwkew\" -> 3 (\"wke\")");

        System.out.println("\n== Loop 1 -- LC 207: Course Schedule ==");
        Check.isTrue(FinalLoopProblems.canFinish(2, new int[][]{{1, 0}}), "2 courses, 1->0: finishable");
        Check.isTrue(!FinalLoopProblems.canFinish(2, new int[][]{{1, 0}, {0, 1}}), "2 courses, cycle: NOT finishable");

        System.out.println("\n== Loop 2 -- LC 56: Merge Intervals ==");
        int[][] merged = FinalLoopProblems.mergeIntervals(new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}});
        Check.eq("[[1, 6], [8, 10], [15, 18]]".replace(" ", ""),
                Arrays.deepToString(merged).replace(" ", ""), "merge([[1,3],[2,6],[8,10],[15,18]]) = [[1,6],[8,10],[15,18]]");

        System.out.println("\n== Loop 2 -- LC 139: Word Break ==");
        Check.isTrue(FinalLoopProblems.wordBreak("leetcode", List.of("leet", "code")), "\"leetcode\" breakable with [leet,code]");
        Check.isTrue(!FinalLoopProblems.wordBreak("catsandog", List.of("cats", "dog", "sand", "and", "cat")), "\"catsandog\" NOT breakable");

        System.out.println("\n== Loop 3 -- LC 128: Longest Consecutive Sequence ==");
        Check.eq(4, FinalLoopProblems.longestConsecutive(new int[]{100, 4, 200, 1, 3, 2}), "longestConsecutive = 4 ([1,2,3,4])");

        System.out.println("\n== Loop 3 -- LC 973: K Closest Points to Origin ==");
        int[][] closest = FinalLoopProblems.kClosest(new int[][]{{1, 3}, {-2, 2}}, 1);
        Check.eq(1, closest.length, "kClosest returns exactly k=1 point");
        Check.eq("[-2, 2]", Arrays.toString(closest[0]), "kClosest([[1,3],[-2,2]], k=1) = [[-2,2]] (closer to origin)");

        System.out.println("\n== Loop 4 -- LC 55: Jump Game ==");
        Check.isTrue(FinalLoopProblems.canJump(new int[]{2, 3, 1, 1, 4}), "[2,3,1,1,4] -> true, reaches the end");
        Check.isTrue(!FinalLoopProblems.canJump(new int[]{3, 2, 1, 0, 4}), "[3,2,1,0,4] -> false, stuck at the 0");

        System.out.println("\n== Loop 4 -- LC 127: Word Ladder ==");
        Check.eq(5, FinalLoopProblems.ladderLength("hit", "cog", List.of("hot", "dot", "dog", "lot", "log", "cog")),
                "ladderLength(hit->cog) = 5 (hit->hot->dot->dog->cog)");
        Check.eq(0, FinalLoopProblems.ladderLength("hit", "cog", List.of("hot", "dot", "dog", "lot", "log")),
                "ladderLength = 0 when endWord not in the dictionary");

        Check.summary("Week 12 final-loop coding suite (8 problems)");
        if (Check.fail > 0) System.exit(1);
    }
}
