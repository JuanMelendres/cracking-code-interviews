import java.util.concurrent.atomic.AtomicInteger;

/**
 * T-515 -- a real circuit breaker (CLOSED -> OPEN -> HALF_OPEN -> CLOSED),
 * measuring what it actually saves: without it, every call to a downstream
 * that's down still pays the full simulated 200ms timeout; once OPEN, the
 * breaker fails calls in ~0ms instead.
 */
public class CircuitBreakerDemo {
    enum State { CLOSED, OPEN, HALF_OPEN }

    static class CircuitBreaker {
        State state = State.CLOSED;
        int consecutiveFailures = 0;
        final int failureThreshold;
        final long openDurationMs;
        long openedAt;

        CircuitBreaker(int failureThreshold, long openDurationMs) {
            this.failureThreshold = failureThreshold;
            this.openDurationMs = openDurationMs;
        }

        boolean allowCall() {
            if (state == State.OPEN) {
                if (System.currentTimeMillis() - openedAt >= openDurationMs) {
                    state = State.HALF_OPEN;
                    System.out.println("  [breaker] OPEN -> HALF_OPEN (cool-down elapsed, allowing one trial call)");
                    return true;
                }
                return false; // fail fast, no call attempted
            }
            return true; // CLOSED or HALF_OPEN: allow the call
        }

        void recordSuccess() {
            if (state == State.HALF_OPEN) {
                System.out.println("  [breaker] HALF_OPEN -> CLOSED (trial call succeeded)");
            }
            state = State.CLOSED;
            consecutiveFailures = 0;
        }

        void recordFailure() {
            if (state == State.HALF_OPEN) {
                System.out.println("  [breaker] HALF_OPEN -> OPEN (trial call failed, back to fail-fast)");
                state = State.OPEN;
                openedAt = System.currentTimeMillis();
                return;
            }
            consecutiveFailures++;
            if (consecutiveFailures >= failureThreshold && state == State.CLOSED) {
                System.out.println("  [breaker] CLOSED -> OPEN (" + consecutiveFailures + " consecutive failures)");
                state = State.OPEN;
                openedAt = System.currentTimeMillis();
            }
        }
    }

    /** A downstream that's down for its first N calls, then recovers. */
    static class FlakyDownstream {
        final int failuresBeforeRecovery;
        final AtomicInteger callCount = new AtomicInteger();

        FlakyDownstream(int failuresBeforeRecovery) {
            this.failuresBeforeRecovery = failuresBeforeRecovery;
        }

        boolean call() throws InterruptedException {
            Thread.sleep(200); // simulated network timeout cost -- paid on EVERY real call attempt
            return callCount.incrementAndGet() > failuresBeforeRecovery;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("== WITHOUT a circuit breaker: every call pays the full 200ms, even while the downstream is down ==");
        FlakyDownstream plainDownstream = new FlakyDownstream(6);
        long start = System.nanoTime();
        int attempted = 0, succeeded = 0;
        for (int i = 0; i < 10; i++) {
            attempted++;
            if (plainDownstream.call()) succeeded++;
        }
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        System.out.printf("10 calls, %d attempted (all of them), %d succeeded, %dms total (== 10 x 200ms, every call pays full cost)%n",
                attempted, succeeded, elapsedMs);

        System.out.println();
        System.out.println("== WITH a circuit breaker (threshold=3, open for 500ms): fails fast once open ==");
        CircuitBreaker breaker = new CircuitBreaker(3, 500);
        FlakyDownstream downstream = new FlakyDownstream(3); // recovers after its 3rd call, so the HALF_OPEN trial should succeed
        start = System.nanoTime();
        attempted = 0; succeeded = 0;
        int rejectedFast = 0;
        for (int i = 0; i < 20; i++) {
            if (i == 8) Thread.sleep(500); // let the cool-down window elapse partway through, BEFORE this call is attempted
            if (!breaker.allowCall()) {
                rejectedFast++;
                continue;
            }
            attempted++;
            try {
                if (downstream.call()) {
                    breaker.recordSuccess();
                    succeeded++;
                } else {
                    breaker.recordFailure();
                }
            } catch (Exception e) {
                breaker.recordFailure();
            }
        }
        elapsedMs = (System.nanoTime() - start) / 1_000_000;
        System.out.printf("20 call attempts: %d actually reached the downstream (200ms each), %d rejected fast (~0ms), "
                        + "%d succeeded, %dms total%n",
                attempted, rejectedFast, succeeded, elapsedMs);
    }
}
