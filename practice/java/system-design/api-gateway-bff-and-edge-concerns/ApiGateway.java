import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

// A real API gateway: a genuine HTTP server accepting client requests, real
// path-based routing to real backend services, a real cross-cutting edge
// concern (an API-key check) enforced once, here, before any backend is ever
// reached -- and a real BFF-style aggregation endpoint fanning out to two
// backends concurrently and combining their real responses into one.
public class ApiGateway {
    private static final String REQUIRED_API_KEY = "real-secret-key";

    private final HttpClient client = HttpClient.newHttpClient();
    private final Map<String, Integer> routeTable; // path prefix -> backend port
    private HttpServer server;

    public ApiGateway(Map<String, Integer> routeTable) {
        this.routeTable = routeTable;
    }

    public void start(int port) throws Exception {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this::handle);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private void handle(HttpExchange exchange) throws IOException {
        String apiKey = exchange.getRequestHeaders().getFirst("X-Api-Key");
        if (!REQUIRED_API_KEY.equals(apiKey)) {
            respond(exchange, 401, "{\"error\":\"missing or invalid API key\"}");
            return;
        }

        String path = exchange.getRequestURI().getPath();

        if (path.equals("/bff/dashboard")) {
            handleBffAggregation(exchange);
            return;
        }

        Integer targetPort = null;
        for (Map.Entry<String, Integer> route : routeTable.entrySet()) {
            if (path.startsWith(route.getKey())) {
                targetPort = route.getValue();
                break;
            }
        }

        if (targetPort == null) {
            respond(exchange, 404, "{\"error\":\"no route\"}");
            return;
        }

        respond(exchange, 200, forward(targetPort, path));
    }

    private void handleBffAggregation(HttpExchange exchange) throws IOException {
        CompletableFuture<String> orders = CompletableFuture.supplyAsync(
                () -> forward(routeTable.get("/orders"), "/orders"));
        CompletableFuture<String> users = CompletableFuture.supplyAsync(
                () -> forward(routeTable.get("/users"), "/users"));

        String combined = "{\"orders\":" + orders.join() + ",\"users\":" + users.join() + "}";
        respond(exchange, 200, combined);
    }

    private String forward(int targetPort, String path) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + targetPort + path))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            return "{\"error\":\"backend unreachable\"}";
        }
    }

    private void respond(HttpExchange exchange, int status, String jsonBody) throws IOException {
        byte[] body = jsonBody.getBytes();
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
    }
}
