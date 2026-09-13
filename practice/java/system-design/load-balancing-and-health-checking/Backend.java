import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A real, minimal HTTP backend instance -- a genuine java.net.HttpServer, not a
 * simulated response generator. Serves /health (toggle-able healthy/unhealthy) and /
 * (does real, configurable, blocking work before responding, so load-balancing
 * algorithms have something real to react to).
 */
public class Backend {
    private final String id;
    private final int port;
    private final long processingDelayMillis;
    private final AtomicBoolean healthy = new AtomicBoolean(true);
    private final AtomicInteger requestCount = new AtomicInteger();
    private HttpServer server;

    public Backend(String id, int port, long processingDelayMillis) {
        this.id = id;
        this.port = port;
        this.processingDelayMillis = processingDelayMillis;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/health", healthHandler());
        server.createContext("/", requestHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public void setHealthy(boolean value) {
        healthy.set(value);
    }

    public int requestCount() {
        return requestCount.get();
    }

    private HttpHandler healthHandler() {
        return exchange -> {
            int status = healthy.get() ? 200 : 503;
            byte[] body = (healthy.get() ? "OK" : "UNHEALTHY").getBytes();
            exchange.sendResponseHeaders(status, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        };
    }

    private HttpHandler requestHandler() {
        return exchange -> {
            int n = requestCount.incrementAndGet();
            try {
                Thread.sleep(processingDelayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            byte[] body = (id + " handled request " + n).getBytes();
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        };
    }

    public String id() {
        return id;
    }

    public int port() {
        return port;
    }
}
