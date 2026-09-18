import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/** A real closed-loop load generator: a fixed number of worker threads,
 * each sending the next request only after the previous one's response
 * arrives. This is the classic, easy-to-write load-test shape -- and the
 * shape coordinated omission critiques, per
 * syllabus/13-observability/percentiles-tail-latency-and-coordinated-omission.md.
 * Real percentiles computed from real, measured per-request latencies. */
public class ClosedLoopLoadGenerator {

    public static void main(String[] args) throws Exception {
        String url = args[0];
        int concurrency = Integer.parseInt(args[1]);
        int durationSeconds = Integer.parseInt(args[2]);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();

        List<Long> latenciesMs = new CopyOnWriteArrayList<>();
        long endAtNanos = System.nanoTime() + durationSeconds * 1_000_000_000L;
        CountDownLatch done = new CountDownLatch(concurrency);

        for (int i = 0; i < concurrency; i++) {
            Thread worker = new Thread(() -> {
                while (System.nanoTime() < endAtNanos) {
                    long start = System.nanoTime();
                    try {
                        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
                        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
                        if (response.statusCode() == 200) {
                            latenciesMs.add(elapsedMs);
                        }
                    } catch (Exception ignored) {
                        // connection error under load -- not counted as a latency sample
                    }
                }
                done.countDown();
            });
            worker.start();
        }
        done.await();

        List<Long> sorted = new java.util.ArrayList<>(latenciesMs);
        sorted.sort(Long::compareTo);
        int n = sorted.size();
        System.out.println("Closed-loop generator: concurrency=" + concurrency + ", duration=" + durationSeconds + "s");
        System.out.println("  total requests: " + n);
        System.out.println("  p50: " + percentile(sorted, 50) + "ms");
        System.out.println("  p95: " + percentile(sorted, 95) + "ms");
        System.out.println("  p99: " + percentile(sorted, 99) + "ms");
        System.out.println("  max: " + sorted.get(n - 1) + "ms");
    }

    private static long percentile(List<Long> sorted, int p) {
        int index = (int) Math.ceil(p / 100.0 * sorted.size()) - 1;
        return sorted.get(Math.max(0, Math.min(index, sorted.size() - 1)));
    }
}
