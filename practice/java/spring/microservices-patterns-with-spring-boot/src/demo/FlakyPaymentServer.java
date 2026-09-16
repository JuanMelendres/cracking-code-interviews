package demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** A real downstream HTTP server (JDK's own HttpServer, no framework)
 * standing in for a separate "payment-service" microservice. Its
 * behavior is toggled at runtime so the same server can play healthy,
 * failing, and slow-but-successful without restarting anything. */
public class FlakyPaymentServer {

    public enum Mode { HEALTHY, FAILING, SLOW }

    private final HttpServer server;
    private final AtomicReference<Mode> mode = new AtomicReference<>(Mode.HEALTHY);
    private final AtomicInteger requestCount = new AtomicInteger();
    private final long slowResponseMillis;

    public FlakyPaymentServer(long slowResponseMillis) throws IOException {
        this.slowResponseMillis = slowResponseMillis;
        this.server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/charge", this::handle);
        server.setExecutor(Executors.newCachedThreadPool());
    }

    public void start() { server.start(); }
    public void stop() { server.stop(0); }
    public int port() { return server.getAddress().getPort(); }
    public void setMode(Mode m) { mode.set(m); }
    public int requestCount() { return requestCount.get(); }

    private void handle(HttpExchange exchange) throws IOException {
        requestCount.incrementAndGet();
        Mode current = mode.get();
        if (current == Mode.SLOW) {
            try {
                Thread.sleep(slowResponseMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        byte[] body;
        int status;
        if (current == Mode.FAILING) {
            status = 500;
            body = "{\"error\":\"payment processor unavailable\"}".getBytes(StandardCharsets.UTF_8);
        } else {
            status = 200;
            body = "{\"status\":\"CHARGED\",\"amount\":42.00}".getBytes(StandardCharsets.UTF_8);
        }
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
    }
}
