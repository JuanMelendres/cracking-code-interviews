/**
 * Real, executed proof that relying on Enum.ordinal() for any externally-
 * persisted representation (a database column, a serialized file, a wire
 * protocol) is genuinely dangerous: inserting a new constant in the middle
 * of the declaration list silently shifts every later constant's real
 * ordinal value, with zero compiler warning and zero runtime error --
 * only silently wrong data.
 */
public class OrdinalDangerDemo {

    // "V1" of the enum, as it might have been originally deployed and
    // persisted to a database using ordinal() as the stored value.
    enum StatusV1 {
        PENDING, APPROVED, REJECTED
    }

    // "V2" -- a real, innocent-looking change: a new status inserted in
    // the middle, in an order that made sense to the developer reading
    // the code top to bottom.
    enum StatusV2 {
        PENDING, IN_REVIEW, APPROVED, REJECTED
    }

    public static void main(String[] args) {
        System.out.println("== Real ordinal values, V1 (as originally persisted to a database) ==");
        for (StatusV1 s : StatusV1.values()) {
            System.out.println("  " + s + ".ordinal() = " + s.ordinal());
        }

        System.out.println("\n== Real ordinal values, V2 (after inserting IN_REVIEW in the middle) ==");
        for (StatusV2 s : StatusV2.values()) {
            System.out.println("  " + s + ".ordinal() = " + s.ordinal());
        }

        System.out.println("\n== The real, silent corruption ==");
        System.out.println("A database row stored ordinal=2 back when it meant StatusV1.REJECTED.");
        System.out.println("Reading that SAME stored value (2) back through StatusV2.values()[2] now returns: "
                + StatusV2.values()[2]
                + "  <-- REAL: silently, incorrectly APPROVED instead of REJECTED. No exception. No warning. Just wrong data.");

        System.out.println("\n== The real, safe alternative: name() is stable across reordering ==");
        System.out.println("StatusV1.REJECTED.name() = \"" + StatusV1.REJECTED.name() + "\"");
        System.out.println("StatusV2.valueOf(\"REJECTED\") = " + StatusV2.valueOf("REJECTED")
                + "  <-- REAL: correctly resolves to REJECTED regardless of declaration order, because name() never changes");
    }
}
