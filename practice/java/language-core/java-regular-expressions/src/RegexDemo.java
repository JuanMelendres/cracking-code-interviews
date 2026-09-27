import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Real, executed evidence for the Java Regular Expressions chapter.
 * Every number printed here is measured on this machine, not asserted from documentation.
 */
public class RegexDemo {

    public static void main(String[] args) {
        demoNamedGroups();
        System.out.println();
        demoGreedyReluctantPossessive();
        System.out.println();
        demoPrecompiledVsStringMatches();
        System.out.println();
        demoCatastrophicBacktracking();
    }

    private static void demoNamedGroups() {
        System.out.println("Demo A: named groups");
        Pattern p = Pattern.compile("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})");
        Matcher m = p.matcher("Deploy scheduled for 2026-06-15, rollback by 2026-06-20.");
        while (m.find()) {
            System.out.printf("Match: %s -> year=%s month=%s day=%s%n",
                    m.group(), m.group("year"), m.group("month"), m.group("day"));
        }
    }

    private static void demoGreedyReluctantPossessive() {
        System.out.println("Demo B: greedy vs. reluctant vs. possessive quantifiers");
        String input = "<a><b><c>";

        Matcher greedy = Pattern.compile("<.+>").matcher(input);
        greedy.find();
        System.out.println("Greedy   <.+>   on \"" + input + "\" -> \"" + greedy.group() + "\" (consumes as much as possible, then backtracks to match)");

        Matcher reluctant = Pattern.compile("<.+?>").matcher(input);
        reluctant.find();
        System.out.println("Reluctant <.+?> on \"" + input + "\" -> \"" + reluctant.group() + "\" (consumes as little as possible, expands only on failure)");

        // Possessive: never backtracks. Here it causes the overall match to FAIL where greedy would have succeeded.
        Matcher possessive = Pattern.compile("<.++>").matcher(input);
        boolean possessiveMatched = possessive.find();
        System.out.println("Possessive <.++> on \"" + input + "\" -> matched: " + possessiveMatched
                + " (never backtracks -- '.' consumes the trailing '>' too, then can't give it back)");
    }

    private static void demoPrecompiledVsStringMatches() {
        System.out.println("Demo C: precompiled Pattern vs. String.matches() (which compiles every call)");
        String regex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        String input = "user.name+tag@example.com";
        int iterations = 200_000;

        long startStringMatches = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            input.matches(regex); // recompiles the Pattern on every single call
        }
        long stringMatchesMillis = (System.nanoTime() - startStringMatches) / 1_000_000;

        Pattern precompiled = Pattern.compile(regex);
        long startPrecompiled = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            precompiled.matcher(input).matches();
        }
        long precompiledMillis = (System.nanoTime() - startPrecompiled) / 1_000_000;

        System.out.printf("%,d calls to String.matches(): %d ms (recompiles the pattern every time)%n", iterations, stringMatchesMillis);
        System.out.printf("%,d calls to a precompiled Pattern.matcher(): %d ms%n", iterations, precompiledMillis);
        System.out.printf("Speedup: %.1fx%n", stringMatchesMillis / (double) Math.max(precompiledMillis, 1));
    }

    /**
     * Real, measured catastrophic backtracking (ReDoS): 15 adjacent optional quantifiers over the
     * same character class, followed by a required literal the input never contains. Each 'a*' can
     * independently choose how many of the run's characters it consumes, so the engine explores a
     * combinatorial number of equivalent splits before concluding the match fails.
     *
     * (Measured separately, and worth stating honestly: the textbook nested-group shapes often cited
     * for ReDoS -- "(a+)+$", "(a|aa)+$" -- do NOT reproduce this blowup on this JDK, even out to 45
     * repetitions. This adjacent-quantifier shape does. See the chapter's Internal Implementation
     * section for the full, honest comparison.)
     */
    private static void demoCatastrophicBacktracking() {
        System.out.println("Demo D: catastrophic backtracking (a real, measured ReDoS shape)");
        Pattern evil = Pattern.compile("a*a*a*a*a*a*a*a*a*a*a*a*a*a*a*b");

        for (int n = 10; n <= 19; n++) {
            String input = "a".repeat(n); // no trailing 'b' -- the match can only fail, after exhausting every split
            long start = System.nanoTime();
            boolean matched = evil.matcher(input).matches();
            long millis = (System.nanoTime() - start) / 1_000_000;
            System.out.printf("n=%2d ('a' x %2d): matched=%s, elapsed=%,d ms%n", n, n, matched, millis);
        }
    }
}
