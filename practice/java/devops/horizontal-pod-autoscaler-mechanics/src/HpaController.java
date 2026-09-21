import java.util.ArrayList;
import java.util.List;

/**
 * Applies the real scale-up-immediate / scale-down-stabilized asymmetry the
 * actual HPA controller uses: a scale-UP recommendation is applied right
 * away (default scale-up stabilization window is 0s), but a scale-DOWN
 * recommendation is only actually applied once the HIGHEST recommendation
 * seen across the whole stabilization window (default 300s in real
 * Kubernetes; compressed here for demo speed) has itself dropped below the
 * current replica count. This is exactly what stops a single brief dip in
 * load from immediately shrinking the fleet, only for load to spike again
 * moments later.
 */
public class HpaController {

    private record Recommendation(long timestampMillis, int value) {
    }

    private final double targetMetricValue;
    private final int minReplicas;
    private final int maxReplicas;
    private final double tolerance;
    private final long scaleDownStabilizationWindowMillis;

    private int currentReplicas;
    private final List<Recommendation> recentRecommendations = new ArrayList<>();

    public HpaController(int initialReplicas, double targetMetricValue, int minReplicas, int maxReplicas,
                          double tolerance, long scaleDownStabilizationWindowMillis) {
        this.currentReplicas = initialReplicas;
        this.targetMetricValue = targetMetricValue;
        this.minReplicas = minReplicas;
        this.maxReplicas = maxReplicas;
        this.tolerance = tolerance;
        this.scaleDownStabilizationWindowMillis = scaleDownStabilizationWindowMillis;
    }

    public int onMetricSample(long nowMillis, double currentMetricValue) {
        int instantRecommendation = HpaCalculator.recommend(
                currentReplicas, currentMetricValue, targetMetricValue, minReplicas, maxReplicas, tolerance);

        recentRecommendations.add(new Recommendation(nowMillis, instantRecommendation));
        recentRecommendations.removeIf(r -> nowMillis - r.timestampMillis() > scaleDownStabilizationWindowMillis);

        if (instantRecommendation >= currentReplicas) {
            // Scale up (or no change): applied immediately, no stabilization delay.
            currentReplicas = instantRecommendation;
            return currentReplicas;
        }

        // Scale down: apply only the HIGHEST recommendation within the
        // stabilization window, not the latest one.
        int highestRecentRecommendation = recentRecommendations.stream()
                .mapToInt(Recommendation::value)
                .max()
                .orElse(currentReplicas);

        if (highestRecentRecommendation < currentReplicas) {
            currentReplicas = highestRecentRecommendation;
        }
        return currentReplicas;
    }

    public int currentReplicas() {
        return currentReplicas;
    }
}
