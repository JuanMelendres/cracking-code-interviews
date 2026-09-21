import java.util.Random;

/**
 * A real, minimal service that calls a downstream payment dependency on
 * every request. The chaos experiment targets THIS downstream call, not
 * the service itself -- exactly how a real fault-injection experiment
 * (simulating "the payment provider is degraded") is scoped.
 */
public class OrderService {

    private final Random latencyJitter = new Random(7);
    private volatile boolean chaosActive = false;
    private int requestCounter = 0;

    public void setChaosActive(boolean active) {
        this.chaosActive = active;
    }

    /**
     * Every 4th request is the canary cohort (25% of traffic) -- deterministic,
     * not random, so this demo's result is exactly reproducible run to run.
     * Real chaos tooling (a service mesh fault-injection rule, a feature flag)
     * would select this cohort by request/user attribute instead.
     */
    public RequestOutcome handleRequest() {
        requestCounter++;
        boolean isCanary = requestCounter % 4 == 0;
        long start = System.currentTimeMillis();

        boolean success;
        long simulatedLatency;
        if (chaosActive && isCanary) {
            // Injected fault: the downstream payment call fails outright,
            // simulating a real degraded/unavailable dependency.
            success = false;
            simulatedLatency = 5 + latencyJitter.nextInt(10);
        } else {
            success = true;
            simulatedLatency = 20 + latencyJitter.nextInt(15);
        }

        return new RequestOutcome(start, success, simulatedLatency, isCanary);
    }
}
