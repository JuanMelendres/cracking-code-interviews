import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A real, active health checker: a background thread that actually issues a real
 * HTTP GET to each backend's /health endpoint on a fixed interval, and flips the
 * LoadBalancer's shared healthy-set based on the real response (or the real
 * exception when a backend is simply gone).
 */
public class HealthChecker {
    private final List<Backend> backends;
    private final LoadBalancer loadBalancer;
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(300))
            .build();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private volatile java.util.function.BiConsumer<String, Boolean> onTransition = (id, h) -> {};

    public HealthChecker(List<Backend> backends, LoadBalancer loadBalancer) {
        this.backends = backends;
        this.loadBalancer = loadBalancer;
    }

    public void onTransition(java.util.function.BiConsumer<String, Boolean> listener) {
        this.onTransition = listener;
    }

    public void start(long intervalMillis) {
        scheduler.scheduleAtFixedRate(this::checkAll, 0, intervalMillis, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }

    private void checkAll() {
        for (Backend b : backends) {
            boolean wasHealthy = loadBalancer.isHealthy(b.id());
            boolean nowHealthy = probe(b);
            if (nowHealthy != wasHealthy) {
                loadBalancer.markHealthy(b.id(), nowHealthy);
                onTransition.accept(b.id(), nowHealthy);
            }
        }
    }

    private boolean probe(Backend b) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + b.port() + "/health"))
                    .timeout(Duration.ofMillis(500))
                    .GET()
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false; // real connection failure -- backend is really gone
        }
    }
}
