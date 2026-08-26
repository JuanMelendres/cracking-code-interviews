/**
 * Real token-bucket limiter. Tokens refill continuously based on elapsed wall-clock
 * time (lazy refill computed on each call, no background thread), capped at capacity.
 * `synchronized` is used deliberately: correctness of the refill-then-consume sequence
 * matters more here than lock-free throughput, and the chapter's point is the
 * algorithm's shape, not a lock-free micro-optimization.
 */
final class TokenBucket {

    private final double capacity;
    private final double refillPerSecond;
    private double tokens;
    private long lastRefillNanos;

    TokenBucket(double capacity, double refillPerSecond) {
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
        this.tokens = capacity;
        this.lastRefillNanos = System.nanoTime();
    }

    synchronized boolean tryAcquire() {
        long now = System.nanoTime();
        double elapsedSeconds = (now - lastRefillNanos) / 1_000_000_000.0;
        lastRefillNanos = now;
        tokens = Math.min(capacity, tokens + elapsedSeconds * refillPerSecond);
        if (tokens >= 1.0) {
            tokens -= 1.0;
            return true;
        }
        return false;
    }

    synchronized double currentTokens() {
        return tokens;
    }
}
