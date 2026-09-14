import java.util.Objects;

final class Check {
    static int pass = 0;
    static int fail = 0;

    static void eq(Object expected, Object actual, String label) {
        if (Objects.equals(expected, actual)) {
            pass++;
            System.out.println("  PASS  " + label);
        } else {
            fail++;
            System.out.println("  FAIL  " + label + "  expected=" + expected + " actual=" + actual);
        }
    }

    static void isTrue(boolean cond, String label) {
        eq(true, cond, label);
    }

    static void summary(String suite) {
        System.out.println(suite + ": " + pass + "/" + (pass + fail) + " assertions passed");
    }
}
