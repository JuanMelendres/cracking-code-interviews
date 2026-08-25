import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A real reverse proxy: it actually forwards each request over a real HTTP
 * connection to a chosen backend and returns that backend's real response. Two real
 * selection strategies -- round-robin and least-connections -- and a shared
 * healthy-set that HealthChecker mutates concurrently from its own thread.
 */
public class LoadBalancer {
    public enum Strategy { ROUND_ROBIN, LEAST_CONNECTIONS }

    private final List<Backend> backends;
    private final Map<String, Boolean> healthy = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> inFlight = new ConcurrentHashMap<>();
    private final AtomicInteger roundRobinCursor = new AtomicInteger();
    private final HttpClient client = HttpClient.newHttpClient();

    public LoadBalancer(List<Backend> backends) {
        this.backends = backends;
        for (Backend b : backends) {
            healthy.put(b.id(), true);
            inFlight.put(b.id(), new AtomicInteger());
        }
    }

    public void markHealthy(String backendId, boolean value) {
        healthy.put(backendId, value);
    }

    public boolean isHealthy(String backendId) {
        return healthy.getOrDefault(backendId, false);
    }

    public String forward(Strategy strategy) throws Exception {
        Backend target = select(strategy);
        if (target == null) {
            throw new IllegalStateException("no healthy backend available");
        }
        AtomicInteger counter = inFlight.get(target.id());
        counter.incrementAndGet();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + target.port() + "/"))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } finally {
            counter.decrementAndGet();
        }
    }

    private Backend select(Strategy strategy) {
        List<Backend> candidates = backends.stream().filter(b -> isHealthy(b.id())).toList();
        if (candidates.isEmpty()) {
            return null;
        }
        if (strategy == Strategy.ROUND_ROBIN) {
            int idx = Math.floorMod(roundRobinCursor.getAndIncrement(), candidates.size());
            return candidates.get(idx);
        }
        // LEAST_CONNECTIONS
        Backend best = null;
        int bestCount = Integer.MAX_VALUE;
        for (Backend b : candidates) {
            int count = inFlight.get(b.id()).get();
            if (count < bestCount) {
                bestCount = count;
                best = b;
            }
        }
        return best;
    }
}
