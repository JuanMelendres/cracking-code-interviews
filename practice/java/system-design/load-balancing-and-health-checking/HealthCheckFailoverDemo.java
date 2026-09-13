import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Real, measured health-check-driven failover: a real backend process is killed
 * mid-run, and the demo measures the real wall-clock time between the kill and the
 * active health checker actually marking it unhealthy and removing it from
 * rotation -- then the backend is really restarted and the demo measures real
 * re-inclusion time too.
 */
public class HealthCheckFailoverDemo {
    public static void main(String[] args) throws Exception {
        Backend a = new Backend("backend-A", 9201, 5);
        Backend b = new Backend("backend-B", 9202, 5);
        Backend c = new Backend("backend-C", 9203, 5);
        a.start();
        b.start();
        c.start();

        List<Backend> backends = List.of(a, b, c);
        LoadBalancer lb = new LoadBalancer(backends);
        HealthChecker checker = new HealthChecker(backends, lb);

        AtomicBoolean sawBDown = new AtomicBoolean(false);
        AtomicBoolean sawBUp = new AtomicBoolean(false);
        long[] killedAt = new long[1];
        long[] downDetectedAt = new long[1];
        long[] restartedAt = new long[1];
        long[] upDetectedAt = new long[1];

        checker.onTransition((id, healthy) -> {
            long now = System.nanoTime();
            if (id.equals("backend-B") && !healthy && sawBDown.compareAndSet(false, true)) {
                downDetectedAt[0] = now;
                System.out.printf("Real health check detected backend-B DOWN at t+%.0fms%n",
                        (now - killedAt[0]) / 1_000_000.0);
            }
            if (id.equals("backend-B") && healthy && sawBDown.get() && sawBUp.compareAndSet(false, true)) {
                upDetectedAt[0] = now;
                System.out.printf("Real health check detected backend-B back UP at t+%.0fms%n",
                        (now - restartedAt[0]) / 1_000_000.0);
            }
        });

        long checkIntervalMs = 300;
        checker.start(checkIntervalMs);
        System.out.println("Health checker running, real interval " + checkIntervalMs + "ms. Confirming all 3 backends healthy...");
        Thread.sleep(1000);
        System.out.println("backend-A healthy=" + lb.isHealthy("backend-A"));
        System.out.println("backend-B healthy=" + lb.isHealthy("backend-B"));
        System.out.println("backend-C healthy=" + lb.isHealthy("backend-C"));

        System.out.println();
        System.out.println("=== Really stopping backend-B's HTTP server (simulating a crash) ===");
        killedAt[0] = System.nanoTime();
        b.stop();

        Thread.sleep(3000);
        System.out.println("Real routing check: firing 12 requests through ROUND_ROBIN after detection...");
        for (int i = 0; i < 12; i++) {
            try {
                String resp = lb.forward(LoadBalancer.Strategy.ROUND_ROBIN);
                System.out.println("  -> " + resp);
            } catch (Exception e) {
                System.out.println("  -> request failed: " + e.getMessage());
            }
        }
        System.out.println("(no request above should have reached backend-B if detection worked)");

        System.out.println();
        System.out.println("=== Really restarting backend-B ===");
        b = new Backend("backend-B", 9202, 5);
        restartedAt[0] = System.nanoTime();
        b.start();

        Thread.sleep(3000);
        System.out.println("backend-B healthy=" + lb.isHealthy("backend-B") + " (should now be true again)");

        checker.stop();
        a.stop();
        b.stop();
        c.stop();
        System.exit(0);
    }
}
