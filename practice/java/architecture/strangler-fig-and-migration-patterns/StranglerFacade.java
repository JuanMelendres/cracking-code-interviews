import java.util.function.Function;

/**
 * A real Strangler Fig facade: one seam that routes each incoming call to either the
 * legacy system or the new system, based on a real, deterministic per-key rule (here,
 * a hash-bucket percentage) rather than an all-or-nothing switch. This is the
 * mechanism that makes "incremental extraction" a real, gradual process instead of a
 * single cutover event -- the facade's routing rule can move traffic 1% at a time,
 * observing real behavior at each step, without changing a single caller.
 */
final class StranglerFacade {

    private final Function<String, String> legacyHandler;
    private final Function<String, String> newHandler;
    private volatile int newSystemPercentage;

    StranglerFacade(Function<String, String> legacyHandler, Function<String, String> newHandler) {
        this.legacyHandler = legacyHandler;
        this.newHandler = newHandler;
        this.newSystemPercentage = 0;
    }

    void setNewSystemPercentage(int percentage) {
        this.newSystemPercentage = percentage;
    }

    String handle(String requestKey) {
        int bucket = Math.floorMod(requestKey.hashCode(), 100);
        boolean routeToNew = bucket < newSystemPercentage;
        return routeToNew ? newHandler.apply(requestKey) : legacyHandler.apply(requestKey);
    }
}
