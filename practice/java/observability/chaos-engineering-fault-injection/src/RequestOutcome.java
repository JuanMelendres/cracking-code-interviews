public record RequestOutcome(long timestampMillis, boolean success, long latencyMillis, boolean wasCanary) {
}
