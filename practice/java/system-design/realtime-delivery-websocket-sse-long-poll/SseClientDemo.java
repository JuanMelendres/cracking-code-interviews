import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

public class SseClientDemo {

    public static void main(String[] args) throws Exception {
        SseServer server = new SseServer();
        server.start(9210, 5, 400);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:9210/stream")).GET().build();

            Instant start = Instant.now();
            HttpResponse<Stream<String>> response = client.send(request, HttpResponse.BodyHandlers.ofLines());

            System.out.println("=== Real SSE events, timestamped as they actually arrive ===");
            response.body()
                    .filter(line -> !line.isBlank())
                    .forEach(line -> {
                        long elapsed = Duration.between(start, Instant.now()).toMillis();
                        System.out.println("+" + elapsed + "ms  " + line);
                    });
        } finally {
            server.stop();
        }
    }
}
