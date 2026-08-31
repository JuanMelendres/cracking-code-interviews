import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

public class PollingComparisonDemo {

    private static final long EVENT_DELAY_MS = 1300;

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        System.out.println("=== Short polling: client polls every 200ms until it observes the real event ===");
        AtomicReference<String> eventA = new AtomicReference<>();
        ShortPollServer shortServer = new ShortPollServer(eventA);
        shortServer.start(9201);
        scheduleEventAfterDelay(eventA, null, EVENT_DELAY_MS);

        Instant start1 = Instant.now();
        String observed = "no-new-data";
        while (observed.equals("no-new-data")) {
            HttpRequest req = HttpRequest.newBuilder(URI.create("http://localhost:9201/poll-short")).GET().build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            observed = resp.body();
            if (observed.equals("no-new-data")) {
                Thread.sleep(200);
            }
        }
        long shortElapsed = Duration.between(start1, Instant.now()).toMillis();
        System.out.println("Observed: " + observed);
        System.out.println("Real requests made: " + shortServer.requestsServed() + ", elapsed: " + shortElapsed + "ms");
        shortServer.stop();

        System.out.println();
        System.out.println("=== Long polling: client makes ONE request that blocks until the real event arrives ===");
        AtomicReference<String> eventB = new AtomicReference<>();
        Object lock = new Object();
        LongPollServer longServer = new LongPollServer(eventB, lock, 5000);
        longServer.start(9202);
        scheduleEventAfterDelay(eventB, lock, EVENT_DELAY_MS);

        Instant start2 = Instant.now();
        HttpRequest req2 = HttpRequest.newBuilder(URI.create("http://localhost:9202/poll-long")).GET().build();
        HttpResponse<String> resp2 = client.send(req2, HttpResponse.BodyHandlers.ofString());
        long longElapsed = Duration.between(start2, Instant.now()).toMillis();
        System.out.println("Observed: " + resp2.body());
        System.out.println("Real requests made: " + longServer.requestsServed() + ", elapsed: " + longElapsed + "ms");
        longServer.stop();

        System.out.println();
        System.out.println("Real comparison for the SAME event, arriving at the same real ~" + EVENT_DELAY_MS
                + "ms: short polling took " + shortServer.requestsServed()
                + " real requests; long polling took " + longServer.requestsServed() + " real request.");
    }

    private static void scheduleEventAfterDelay(AtomicReference<String> eventRef, Object lock, long delayMillis) {
        Thread publisher = new Thread(() -> {
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            eventRef.set("real-event-fired");
            if (lock != null) {
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        });
        publisher.setDaemon(true);
        publisher.start();
    }
}
