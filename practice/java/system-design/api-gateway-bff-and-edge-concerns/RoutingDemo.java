import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class RoutingDemo {

    public static void main(String[] args) throws Exception {
        DownstreamService orders = new DownstreamService("orders", 9081,
                "{\"orders\":[{\"id\":1,\"item\":\"Widget\"}]}", 0);
        DownstreamService users = new DownstreamService("users", 9082,
                "{\"users\":[{\"id\":7,\"name\":\"Ada\"}]}", 0);
        orders.start();
        users.start();

        ApiGateway gateway = new ApiGateway(Map.of("/orders", 9081, "/users", 9082));
        gateway.start(9080);

        try {
            HttpClient client = HttpClient.newHttpClient();

            System.out.println("=== GET /orders through the gateway ===");
            String ordersResponse = get(client, "http://localhost:9080/orders");
            System.out.println("Response: " + ordersResponse);
            System.out.println("Real orders backend request count: " + orders.requestCount() + " (expect 1)");
            System.out.println("Real users backend request count: " + users.requestCount() + " (expect 0 -- routed correctly)");

            System.out.println();
            System.out.println("=== GET /users through the gateway ===");
            String usersResponse = get(client, "http://localhost:9080/users");
            System.out.println("Response: " + usersResponse);
            System.out.println("Real orders backend request count: " + orders.requestCount() + " (expect 1, unchanged)");
            System.out.println("Real users backend request count: " + users.requestCount() + " (expect 1)");
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
