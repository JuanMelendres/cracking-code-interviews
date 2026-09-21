/**
 * The real Kubernetes HorizontalPodAutoscaler formula, per the official
 * "HorizontalPodAutoscaler Walkthrough" docs:
 *
 *   desiredReplicas = ceil[ currentReplicas * ( currentMetricValue / desiredMetricValue ) ]
 *
 * plus the tolerance band: if the ratio currentMetricValue/desiredMetricValue
 * falls within [1-tolerance, 1+tolerance] (default tolerance 0.1, i.e. 10%),
 * the controller does NOT recommend a change at all -- this is what stops
 * the controller from thrashing on small, normal metric noise around the
 * target.
 */
public final class HpaCalculator {

    private HpaCalculator() {
    }

    public static int recommend(int currentReplicas, double currentMetricValue, double targetMetricValue,
                                 int minReplicas, int maxReplicas, double tolerance) {
        double ratio = currentMetricValue / targetMetricValue;
        if (Math.abs(ratio - 1.0) <= tolerance) {
            return currentReplicas; // within the tolerance band: no recommended change
        }
        int raw = (int) Math.ceil(currentReplicas * ratio);
        return Math.max(minReplicas, Math.min(maxReplicas, raw));
    }
}
