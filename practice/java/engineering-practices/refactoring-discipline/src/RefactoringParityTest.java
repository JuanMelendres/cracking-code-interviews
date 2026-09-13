import java.util.List;

/**
 * The actual proof a refactor is behavior-preserving: run every real test case
 * through BOTH the before and after versions and confirm identical output.
 * This is the test suite that must exist and pass BEFORE a refactor starts,
 * and must still pass, unmodified, after -- if it needs editing to pass, the
 * change wasn't a pure refactor.
 */
public class RefactoringParityTest {

    record Case(double weightKg, String region, boolean express) {}

    public static void main(String[] args) {
        List<Case> cases = List.of(
            new Case(0.5, "domestic", false),
            new Case(0.5, "domestic", true),
            new Case(3.0, "continental", false),
            new Case(3.0, "continental", true),
            new Case(20.0, "international", false),
            new Case(20.0, "international", true),
            new Case(45.5, "domestic", true),
            new Case(1.0, "domestic", false),
            new Case(5.0, "continental", true),
            new Case(100.0, "international", false)
        );

        int failures = 0;
        for (Case c : cases) {
            double before = ShippingCostBefore.cost(c.weightKg(), c.region(), c.express());
            double after = ShippingCostAfter.cost(c.weightKg(), c.region(), c.express());
            boolean match = Double.compare(before, after) == 0;
            System.out.printf("  %s  weight=%.1f region=%-13s express=%-5s before=%.2f after=%.2f%n",
                match ? "PASS" : "FAIL", c.weightKg(), c.region(), c.express(), before, after);
            if (!match) failures++;
        }

        if (failures == 0) {
            System.out.println("All " + cases.size() + " cases: before and after produce identical output.");
        } else {
            System.out.println(failures + " case(s) diverged -- this was NOT a pure refactor.");
            System.exit(1);
        }
    }
}
