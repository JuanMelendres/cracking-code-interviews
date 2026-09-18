package demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** A real HTTP server (JDK's own com.sun.net.httpserver -- no framework)
 * standing in for a webhook consumer. Three real behaviors under test:
 * signature verification, transient failure (to force the sender's real
 * retry logic), and delivery-ID-based idempotent deduplication. */
public class WebhookReceiver {

    private final HttpServer server;
    private final String secret;
    private final Set<String> processedDeliveryIds = ConcurrentHashMap.newKeySet();
    private final AtomicInteger requestsToFailBeforeSucceeding;
    public final AtomicInteger receivedCount = new AtomicInteger();
    public final AtomicInteger duplicateDeliveriesSkipped = new AtomicInteger();

    public WebhookReceiver(int port, String secret, int failFirstNAttempts) throws IOException {
        this.secret = secret;
        this.requestsToFailBeforeSucceeding = new AtomicInteger(failFirstNAttempts);
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/webhook", this::handle);
        server.start();
    }

    private void handle(HttpExchange exchange) throws IOException {
        byte[] body = exchange.getRequestBody().readAllBytes();
        String signatureHeader = exchange.getRequestHeaders().getFirst("X-Signature");
        String deliveryId = exchange.getRequestHeaders().getFirst("X-Delivery-Id");
        String expectedSignature = HmacSigner.sign(body, secret);

        boolean signatureValid = signatureHeader != null && MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                signatureHeader.getBytes(StandardCharsets.UTF_8));

        if (!signatureValid) {
            exchange.sendResponseHeaders(401, -1);
            exchange.close();
            return;
        }

        if (requestsToFailBeforeSucceeding.getAndUpdate(n -> Math.max(0, n - 1)) > 0) {
            exchange.sendResponseHeaders(503, -1);
            exchange.close();
            return;
        }

        if (!processedDeliveryIds.add(deliveryId)) {
            duplicateDeliveriesSkipped.incrementAndGet();
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
            return;
        }

        receivedCount.incrementAndGet();
        exchange.sendResponseHeaders(200, -1);
        exchange.close();
    }

    public void stop() {
        server.stop(0);
    }
}
