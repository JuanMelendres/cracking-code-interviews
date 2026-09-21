/**
 * Real, timed simulation of the actual Kubernetes HPA algorithm (see
 * HpaCalculator/HpaController) driving a CPU-utilization-based scaling
 * decision for a Deployment with target=70%, min=3, max=12 replicas --
 * the exact manifest shape shown (but never explained) in
 * kubernetes-objects-scheduling-and-networking.md.
 *
 * No mocked clock -- real System.currentTimeMillis(), real Thread.sleep()
 * between samples at ~300ms.
 */
public class HpaDemo {

    public static void main(String[] args) throws InterruptedException {
        double target = 70.0;
        int min = 3, max = 12;
        double tolerance = 0.10; // default 10%
        long stabilizationWindowMillis = 2000; // compressed from real default 300s

        HpaController controller = new HpaController(3, target, min, max, tolerance, stabilizationWindowMillis);
        long start = System.currentTimeMillis();
        int previousReplicas = controller.currentReplicas();

        System.out.println("=== Phase 1: near-target noise (68%, 72%) -- tolerance band should suppress both ===");
        sample(controller, start, 68.0, previousReplicas);
        Thread.sleep(300);
        previousReplicas = sample(controller, start, 72.0, previousReplicas);
        Thread.sleep(300);

        System.out.println();
        System.out.println("=== Phase 2: real load spike -- scale-up applies immediately, no stabilization delay ===");
        previousReplicas = sample(controller, start, 140.0, previousReplicas);
        Thread.sleep(300);
        previousReplicas = sample(controller, start, 200.0, previousReplicas);

        System.out.println();
        System.out.println("=== Phase 3: load drops to 20% -- naive formula says scale down NOW, real HPA holds for the stabilization window ===");
        long scaleDownRecommendedAt = -1;
        long actualScaleDownAt = -1;
        for (int i = 0; i < 13; i++) {
            Thread.sleep(300);
            long now = System.currentTimeMillis();
            int beforeReplicas = controller.currentReplicas();
            int instant = HpaCalculator.recommend(beforeReplicas, 20.0, target, min, max, tolerance);
            if (scaleDownRecommendedAt == -1 && instant < previousReplicas) {
                scaleDownRecommendedAt = now - start;
            }
            int actual = controller.onMetricSample(now, 20.0);
            System.out.printf("  t=%4dms  metric=20.0%%  instant-formula-says=%d  actual-replicas=%d%s%n",
                    now - start, instant, actual, actual != previousReplicas ? "  <-- CHANGED" : "");
            if (actual != previousReplicas && actualScaleDownAt == -1) {
                actualScaleDownAt = now - start;
            }
            previousReplicas = actual;
        }

        System.out.println();
        System.out.println("=== Result ===");
        System.out.printf("  Naive per-sample formula recommended scaling down at t=%dms.%n", scaleDownRecommendedAt);
        System.out.printf("  Real HPA controller actually scaled down at t=%dms -- a real, measured %dms stabilization delay,%n",
                actualScaleDownAt, actualScaleDownAt - scaleDownRecommendedAt);
        System.out.println("  by design: a single low sample must not immediately shrink the fleet.");
        System.out.println("  Final replica count settled at: " + controller.currentReplicas());
    }

    static int sample(HpaController controller, long start, double metric, int previousReplicas) {
        long now = System.currentTimeMillis();
        int actual = controller.onMetricSample(now, metric);
        System.out.printf("  t=%4dms  metric=%.1f%%  actual-replicas=%d%s%n",
                now - start, metric, actual, actual != previousReplicas ? "  <-- CHANGED" : "");
        return actual;
    }
}
