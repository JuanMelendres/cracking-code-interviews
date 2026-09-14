/**
 * Real, concrete answer to the register's own follow-up question: "how do you roll
 * back mid-migration?" Two scenarios run against fresh, independent stores:
 *
 * <ul>
 *   <li><b>Unsafe</b>: the team declares the migration done and disables dual-write
 *       to legacy immediately at cutover. New orders after that point exist only in
 *       the new system. When a bug is found and reads are rolled back to legacy, those
 *       orders are really, verifiably gone from the system reads are now pointed at.</li>
 *   <li><b>Safe</b>: dual-write to legacy is deliberately kept running through a
 *       rollback-safety window after cutover. The identical bug, discovered at the
 *       identical point, triggers an identical rollback -- but every order is really
 *       still present in legacy, because it was never stopped from receiving them.</li>
 * </ul>
 */
public final class RollbackSafetyDemo {

    public static void main(String[] args) {
        System.out.println("=== Scenario A: UNSAFE -- dual-write disabled immediately at cutover ===");
        runScenario(true);

        System.out.println();
        System.out.println("=== Scenario B: SAFE -- dual-write kept running through the rollback window ===");
        runScenario(false);
    }

    private static void runScenario(boolean disableDualWriteAtCutover) {
        OrderStore legacy = new OrderStore("legacy");
        OrderStore newSystem = new OrderStore("new");
        MigrationRouter router = new MigrationRouter(legacy, newSystem);

        System.out.println("Pre-migration: writing orders 1-3 (dual-write active, as it is from the start)");
        router.write(new Order("order-1", "Alice"));
        router.write(new Order("order-2", "Bob"));
        router.write(new Order("order-3", "Carol"));

        router.cutoverReadsTo(MigrationRouter.ReadTarget.NEW);

        if (disableDualWriteAtCutover) {
            router.disableDualWriteToLegacy();
        } else {
            System.out.println("[Router] Dual-write to legacy deliberately left ON through the rollback window");
        }

        System.out.println("Post-cutover: writing orders 4-6");
        router.write(new Order("order-4", "Dave"));
        router.write(new Order("order-5", "Erin"));
        router.write(new Order("order-6", "Frank"));

        System.out.println("A real bug is discovered in the new system.");
        router.rollbackReadsToLegacy();

        System.out.println("Reading all 6 orders now that reads point back at legacy:");
        String[] allOrderIds = {"order-1", "order-2", "order-3", "order-4", "order-5", "order-6"};
        int missing = 0;
        for (String orderId : allOrderIds) {
            Order order = router.read(orderId);
            if (order == null) {
                System.out.println("  " + orderId + ": MISSING FROM LEGACY -- real data loss on rollback");
                missing++;
            } else {
                System.out.println("  " + orderId + ": " + order);
            }
        }

        System.out.println("Result: " + missing + " of " + allOrderIds.length
                + " orders unrecoverable after rollback"
                + (missing > 0 ? "  <-- UNSAFE rollback, real data loss" : "  <-- SAFE rollback, zero data loss"));
    }
}
