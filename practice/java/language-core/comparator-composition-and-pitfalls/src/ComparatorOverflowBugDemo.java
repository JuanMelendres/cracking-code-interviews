import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Real, reproduced overflow bug: a subtraction-based int Comparator is
 * genuinely wrong (not just "old style") because int subtraction can wrap
 * around silently. Comparator.comparingInt (backed by Integer.compare) does
 * not have this bug, verified against the identical data.
 */
public class ComparatorOverflowBugDemo {

    record Score(String player, int value) {}

    public static void main(String[] args) {
        List<Score> scores = new ArrayList<>(List.of(
                new Score("Low", Integer.MIN_VALUE),
                new Score("Mid", 0),
                new Score("High", 1)
        ));

        System.out.println("== Real values, for reference ==");
        System.out.println("Integer.MIN_VALUE = " + Integer.MIN_VALUE);
        System.out.println("Integer.MIN_VALUE - 1 (raw int subtraction, wraps) = " + (Integer.MIN_VALUE - 1));

        Comparator<Score> broken = (a, b) -> a.value() - b.value();

        List<Score> brokenSorted = new ArrayList<>(scores);
        brokenSorted.sort(broken);

        System.out.println();
        System.out.println("== Sorted with BROKEN subtraction comparator: (a, b) -> a.value() - b.value() ==");
        brokenSorted.forEach(s -> System.out.println(s.player() + " = " + s.value()));

        boolean brokenIsCorrectlyOrdered = isAscending(brokenSorted);
        System.out.println("Actually ascending by value? " + brokenIsCorrectlyOrdered);

        Comparator<Score> fixed = Comparator.comparingInt(Score::value);
        List<Score> fixedSorted = new ArrayList<>(scores);
        fixedSorted.sort(fixed);

        System.out.println();
        System.out.println("== Sorted with FIXED Comparator.comparingInt(Score::value) (uses Integer.compare) ==");
        fixedSorted.forEach(s -> System.out.println(s.player() + " = " + s.value()));

        boolean fixedIsCorrectlyOrdered = isAscending(fixedSorted);
        System.out.println("Actually ascending by value? " + fixedIsCorrectlyOrdered);

        System.out.println();
        System.out.println("Direct comparator call, broken.compare(MIN_VALUE, 1) = "
                + broken.compare(new Score("Low", Integer.MIN_VALUE), new Score("High", 1))
                + "  <-- POSITIVE means \"MIN_VALUE is greater than 1\" to Collections.sort -- WRONG");
        System.out.println("Direct comparator call, fixed.compare(MIN_VALUE, 1)  = "
                + fixed.compare(new Score("Low", Integer.MIN_VALUE), new Score("High", 1))
                + "  <-- NEGATIVE, correctly \"MIN_VALUE is less than 1\"");
    }

    private static boolean isAscending(List<Score> scores) {
        for (int i = 1; i < scores.size(); i++) {
            if (scores.get(i - 1).value() > scores.get(i).value()) {
                return false;
            }
        }
        return true;
    }
}
