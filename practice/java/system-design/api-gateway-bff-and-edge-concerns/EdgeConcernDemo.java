import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

// Proves the gateway's API-key check is a real, centralized edge concern --
// enforced once at the gateway, never reaching a backend at all when it fails.
public class EdgeConcernDemo {

    public static void main(String[] args) throws Exception {
        DownstreamService orders = new DownstreamService("orders", 9091,
                "{\"orders\":[]}", 0);
        DownstreamService users = new DownstreamService("users", 9092,
                "{\"users\":[]}", 0);
        orders.start();
        users.start();

        ApiGateway gateway = new ApiGateway(Map.of("/orders", 9091, "/users", 9092));
        gateway.start(9090);

        try {
            HttpClient client = HttpClient.newHttpClient();

            System.out.println("=== Request with NO API key ===");
            HttpRequest noKey = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:9090/orders"))
                    .GET()
                    .build();
            HttpResponse<String> rejected = client.send(noKey, HttpResponse.BodyHandlers.ofString());
            System.out.println("Real gateway status: " + rejected.statusCode() + " (expect 401)");
            System.out.println("Real body: " + rejected.body());
            System.out.println("Real orders backend request count: " + orders.requestCount()
                    + " (expect 0 -- the backend was NEVER reached)");

            System.out.println();
            System.out.println("=== Request WITH the correct API key ===");
            HttpRequest withKey = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:9090/orders"))
                    .header("X-Api-Key", "real-secret-key")
                    .GET()
                    .build();
            HttpResponse<String> accepted = client.send(withKey, HttpResponse.BodyHandlers.ofString());
            System.out.println("Real gateway status: " + accepted.statusCode() + " (expect 200)");
            System.out.println("Real orders backend request count: " + orders.requestCount()
                    + " (expect 1 -- now really forwarded)");
        } finally {
            gateway.stop();
            orders.stop();
            users.stop();
        }
    }
}
