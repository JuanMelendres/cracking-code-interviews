/**
 * The migration's real routing and dual-write logic, as an explicit, inspectable
 * piece of state -- exactly what a Strangler Fig facade is: a single seam that
 * decides, per call, which system actually handles a request, and that seam is what
 * makes incremental cutover (and rollback) possible without touching every caller.
 */
final class MigrationRouter {

    enum ReadTarget { LEGACY, NEW }

    private final OrderStore legacy;
    private final OrderStore newSystem;
    private ReadTarget readTarget = ReadTarget.LEGACY;
    private boolean dualWriteToLegacyEnabled = true;

    MigrationRouter(OrderStore legacy, OrderStore newSystem) {
        this.legacy = legacy;
        this.newSystem = newSystem;
    }

    void write(Order order) {
        newSystem.save(order);
        if (dualWriteToLegacyEnabled) {
            legacy.save(order);
        }
    }

    Order read(String orderId) {
        OrderStore active = (readTarget == ReadTarget.NEW) ? newSystem : legacy;
        return active.find(orderId);
    }

    void cutoverReadsTo(ReadTarget target) {
        System.out.println("[Router] Cutting over reads to " + target);
        this.readTarget = target;
    }

    void disableDualWriteToLegacy() {
        System.out.println("[Router] Disabling dual-write to legacy (declaring migration complete)");
        this.dualWriteToLegacyEnabled = false;
    }

    void rollbackReadsToLegacy() {
        System.out.println("[Router] ROLLBACK: cutting reads back to legacy");
        this.readTarget = ReadTarget.LEGACY;
    }
}
