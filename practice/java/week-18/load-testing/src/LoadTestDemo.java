import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// Real demo: a local HTTP server with a realistic latency profile (fast most
// of the time, occasionally slow -- simulating a cache-miss or GC-pause-like
// tail), load-tested by a concurrent client pool measuring per-request
// latency directly, then reporting mean AND percentiles side by side.
public class LoadTestDemo {

    static HttpServer startServer(int port) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        AtomicInteger counter = new AtomicInteger(0);
        server.createContext("/price", exchange -> {
            int n = counter.incrementAndGet();
            try {
                // 1-in-20 requests simulate a slow path (cache miss / GC pause style tail).
                if (n % 20 == 0) {
                    Thread.sleep(150);
                } else {
                    Thread.sleep(3);
                }
            } catch (InterruptedException ignored) {}
            byte[] body = "{\"price\":42.00}".getBytes();
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.setExecutor(Executors.newFixedThreadPool(50));
        server.start();
        return server;
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = startServer(15800);
        try {
            int totalRequests = 2000;
            int concurrency = 20;
            HttpClient client = HttpClient.newHttpClient();
            ExecutorService pool = Executors.newFixedThreadPool(concurrency);
            long[] latenciesMicros = new long[totalRequests];

            CountDownLatch latch = new CountDownLatch(totalRequests);
            for (int i = 0; i < totalRequests; i++) {
                final int idx = i;
                pool.submit(() -> {
                    try {
                        long start = System.nanoTime();
                        HttpRequest req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:15800/price")).GET().build();
                        client.send(req, HttpResponse.BodyHandlers.discarding());
                        latenciesMicros[idx] = (System.nanoTime() - start) / 1000;
                    } catch (Exception e) {
                        latenciesMicros[idx] = -1;
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            pool.shutdown();

            long[] sorted = Arrays.stream(latenciesMicros).filter(v -> v >= 0).sorted().toArray();
            double meanMs = Arrays.stream(sorted).average().orElse(0) / 1000.0;
            long p50 = sorted[(int) (sorted.length * 0.50)];
            long p95 = sorted[(int) (sorted.length * 0.95)];
            long p99 = sorted[(int) (sorted.length * 0.99)];
            long max = sorted[sorted.length - 1];

            System.out.printf("requests=%d concurrency=%d%n", totalRequests, concurrency);
            System.out.printf("mean=%.2fms  p50=%.2fms  p95=%.2fms  p99=%.2fms  max=%.2fms%n",
                    meanMs, p50 / 1000.0, p95 / 1000.0, p99 / 1000.0, max / 1000.0);
        } finally {
            server.stop(0);
        }
    }
}
