import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

// Measures the real difference between a client making two sequential direct
// calls (one per backend) versus one call to the BFF endpoint, which fans out
// to both backends CONCURRENTLY on the gateway side.
public class BffAggregationDemo {

    private static final long BACKEND_DELAY_MS = 150;

    public static void main(String[] args) throws Exception {
        DownstreamService orders = new DownstreamService("orders", 9101,
                "{\"orders\":[{\"id\":1,\"item\":\"Widget\"}]}", BACKEND_DELAY_MS);
        DownstreamService users = new DownstreamService("users", 9102,
                "{\"users\":[{\"id\":7,\"name\":\"Ada\"}]}", BACKEND_DELAY_MS);
        orders.start();
        users.start();

        ApiGateway gateway = new ApiGateway(Map.of("/orders", 9101, "/users", 9102));
        gateway.start(9100);

        try {
            HttpClient client = HttpClient.newHttpClient();

            System.out.println("=== Client calling both backends directly, sequentially (2 round trips) ===");
            Instant start1 = Instant.now();
            String ordersDirect = get(client, "http://localhost:9100/orders");
            String usersDirect = get(client, "http://localhost:9100/users");
            long directTotal = Duration.between(start1, Instant.now()).toMillis();
            System.out.println("orders: " + ordersDirect);
            System.out.println("users: " + usersDirect);
            System.out.println("Real total client time: " + directTotal + "ms (expect ~" + (BACKEND_DELAY_MS * 2) + "ms -- two sequential round trips)");

            System.out.println();
            System.out.println("=== Client calling the BFF endpoint ONCE (gateway fans out concurrently) ===");
            Instant start2 = Instant.now();
            String bffResponse = get(client, "http://localhost:9100/bff/dashboard");
            long bffTotal = Duration.between(start2, Instant.now()).toMillis();
            System.out.println("combined: " + bffResponse);
            System.out.println("Real total client time: " + bffTotal + "ms (expect ~" + BACKEND_DELAY_MS + "ms -- ONE client round trip, backends fanned out in parallel)");
        } finally {
            gateway.stop();
            orders.stop();
            users.stop();
        }
    }

    private static String get(HttpClient client, String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Api-Key", "real-secret-key")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
