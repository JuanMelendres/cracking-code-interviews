package demo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/** Real delivery with real exponential backoff on a 5xx -- a receiver's
 * transient outage shouldn't be treated the same as a real rejection
 * (a 4xx, e.g. a bad signature) which retrying can never fix. */
public class WebhookSender {

    public record Attempt(int attemptNumber, int statusCode) { }

    private final HttpClient client = HttpClient.newHttpClient();
    private final String secret;

    public WebhookSender(String secret) {
        this.secret = secret;
    }

    public List<Attempt> deliverWithRetry(URI endpoint, byte[] payload, String deliveryId, int maxAttempts) throws Exception {
        List<Attempt> attempts = new ArrayList<>();
        String signature = HmacSigner.sign(payload, secret);
        long backoffMillis = 50;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            HttpRequest request = HttpRequest.newBuilder(endpoint)
                    .header("X-Signature", signature)
                    .header("X-Delivery-Id", deliveryId)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(payload))
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            attempts.add(new Attempt(attempt, response.statusCode()));

            if (response.statusCode() < 500) {
                return attempts; // 2xx succeeded, or a 4xx that retrying can never fix -- stop either way
            }

            Thread.sleep(backoffMillis);
            backoffMillis *= 2;
        }
        return attempts;
    }
}
