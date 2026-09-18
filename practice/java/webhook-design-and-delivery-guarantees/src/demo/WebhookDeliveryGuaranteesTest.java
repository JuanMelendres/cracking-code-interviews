package demo;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Every request below hits a real, running com.sun.net.httpserver
 * instance over a real loopback socket -- nothing stubbed or mocked. */
class WebhookDeliveryGuaranteesTest {

    private static final String SECRET = "shared-secret";

    @Test
    void validSignature_realHmacVerification_accepted() throws Exception {
        WebhookReceiver receiver = new WebhookReceiver(8321, SECRET, 0);
        try {
            WebhookSender sender = new WebhookSender(SECRET);
            byte[] payload = "{\"event\":\"order.created\",\"orderId\":42}".getBytes(StandardCharsets.UTF_8);

            List<WebhookSender.Attempt> attempts = sender.deliverWithRetry(
                    URI.create("http://localhost:8321/webhook"), payload, "delivery-1", 3);

            System.out.println("Valid signature -> real attempts: " + attempts);

            assertEquals(1, attempts.size());
            assertEquals(200, attempts.get(0).statusCode());
            assertEquals(1, receiver.receivedCount.get());
        } finally {
            receiver.stop();
        }
    }

    @Test
    void tamperedPayload_realSignatureMismatch_rejected() throws Exception {
        WebhookReceiver receiver = new WebhookReceiver(8322, SECRET, 0);
        try {
            byte[] originalPayload = "{\"event\":\"order.created\",\"orderId\":42}".getBytes(StandardCharsets.UTF_8);
            String signatureOverOriginal = HmacSigner.sign(originalPayload, SECRET);
            byte[] tamperedPayload = "{\"event\":\"order.created\",\"orderId\":9999}".getBytes(StandardCharsets.UTF_8);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8322/webhook"))
                    .header("X-Signature", signatureOverOriginal)
                    .header("X-Delivery-Id", "delivery-2")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(tamperedPayload))
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

            System.out.println("Tampered payload -> real HTTP status: " + response.statusCode());

            assertEquals(401, response.statusCode());
            assertEquals(0, receiver.receivedCount.get());
        } finally {
            receiver.stop();
        }
    }

    @Test
    void flakyReceiver_realExponentialBackoffRecovers() throws Exception {
        WebhookReceiver receiver = new WebhookReceiver(8323, SECRET, 2); // real 503 on the first 2 real attempts
        try {
            WebhookSender sender = new WebhookSender(SECRET);
            byte[] payload = "{\"event\":\"order.shipped\"}".getBytes(StandardCharsets.UTF_8);

            long startNanos = System.nanoTime();
            List<WebhookSender.Attempt> attempts = sender.deliverWithRetry(
                    URI.create("http://localhost:8323/webhook"), payload, "delivery-3", 5);
            long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000;

            System.out.println("Flaky receiver -> real attempts: " + attempts + ", real elapsed: " + elapsedMs + "ms");

            assertEquals(3, attempts.size());
            assertEquals(503, attempts.get(0).statusCode());
            assertEquals(503, attempts.get(1).statusCode());
            assertEquals(200, attempts.get(2).statusCode());
            assertTrue(elapsedMs >= 150, "real backoff should have waited at least 50ms + 100ms between attempts");
        } finally {
            receiver.stop();
        }
    }

    @Test
    void duplicateDelivery_sameDeliveryId_realIdempotentDedup() throws Exception {
        WebhookReceiver receiver = new WebhookReceiver(8324, SECRET, 0);
        try {
            WebhookSender sender = new WebhookSender(SECRET);
            byte[] payload = "{\"event\":\"order.cancelled\"}".getBytes(StandardCharsets.UTF_8);

            sender.deliverWithRetry(URI.create("http://localhost:8324/webhook"), payload, "delivery-4", 1);
            sender.deliverWithRetry(URI.create("http://localhost:8324/webhook"), payload, "delivery-4", 1); // real redelivery, same ID

            System.out.println("Same delivery ID sent twice -> processed exactly once: " + receiver.receivedCount.get()
                    + ", duplicates skipped: " + receiver.duplicateDeliveriesSkipped.get());

            assertEquals(1, receiver.receivedCount.get());
            assertEquals(1, receiver.duplicateDeliveriesSkipped.get());
        } finally {
            receiver.stop();
        }
    }
}
