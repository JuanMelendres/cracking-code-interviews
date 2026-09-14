import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

// Two hand-picked example tests, both written (as real engineers often do,
// unconsciously) with `a` as the longer-or-equal array. Both pass, despite
// the real bug in MergeSorted -- the examples never happen to exercise the
// exact code path the bug lives in.
public class MergeSortedExampleTest {
    @Test
    void mergesWhenFirstArrayIsLonger() {
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, MergeSorted.merge(new int[]{1, 3, 5}, new int[]{2, 4}));
    }

    @Test
    void mergesWhenSecondArrayIsEmpty() {
        assertArrayEquals(new int[]{1, 2, 3}, MergeSorted.merge(new int[]{1, 2, 3}, new int[]{}));
    }
}
