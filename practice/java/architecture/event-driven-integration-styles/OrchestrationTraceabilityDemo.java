/**
 * The orchestration counterpart to {@link ChoreographyTraceabilityDemo}, run against
 * the identical three steps (reserve, charge, ship) for the identical order. A single
 * {@code OrderOrchestrator.placeOrder} method calls each step directly and
 * sequentially -- no event bus, no async dispatch. This demo captures the REAL call
 * stack at the same logical point (inside the ship step) and prints it verbatim,
 * proving the opposite property: every frame from {@code main} down to
 * {@code shipOrder} is present in one real stack trace, because orchestration's
 * explicit calls never sever the call stack the way an event bus's dispatch does.
 */
public final class OrchestrationTraceabilityDemo {

    static final class OrderOrchestrator {

        void placeOrder(String orderId) {
            reserveInventory(orderId);
            chargePayment(orderId);
            shipOrder(orderId);
        }

        private void reserveInventory(String orderId) {
            System.out.println("[Orchestrator] reserving stock for order=" + orderId);
        }

        private void chargePayment(String orderId) {
            System.out.println("[Orchestrator] charging card for order=" + orderId);
        }

        private void shipOrder(String orderId) {
            System.out.println("[Orchestrator] shipping order=" + orderId);

            StackTraceElement[] realStack = Thread.currentThread().getStackTrace();
            System.out.println("[Orchestrator] REAL call stack at ship-time (" + realStack.length + " frames):");
            for (StackTraceElement frame : realStack) {
                System.out.println("    at " + frame);
            }

            boolean stackMentionsPlaceOrder = false;
            for (StackTraceElement frame : realStack) {
                if (frame.getMethodName().equals("placeOrder")) {
                    stackMentionsPlaceOrder = true;
                }
            }
            System.out.println("[Orchestrator] Does the real call stack reference the original placeOrder call? "
                    + stackMentionsPlaceOrder);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Orchestration: OrderOrchestrator calls each step directly, in sequence ===");
        new OrderOrchestrator().placeOrder("order-42");
    }
}
