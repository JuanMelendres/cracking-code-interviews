import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

// A real, minimal backend microservice -- a genuine com.sun.net.httpserver.HttpServer,
// not a simulated response. Stands in for two independent backend services (orders,
// users) the gateway routes to and the BFF fans out across concurrently.
public class DownstreamService {
    private final String name;
    private final int port;
    private final String jsonBody;
    private final long processingDelayMillis;
    private final AtomicInteger requestCount = new AtomicInteger();
    private HttpServer server;

    public DownstreamService(String name, int port, String jsonBody, long processingDelayMillis) {
        this.name = name;
        this.port = port;
        this.jsonBody = jsonBody;
        this.processingDelayMillis = processingDelayMillis;
    }

    public void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/" + name, exchange -> {
            requestCount.incrementAndGet();
            if (processingDelayMillis > 0) {
                try {
                    Thread.sleep(processingDelayMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            byte[] body = jsonBody.getBytes();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public int requestCount() {
        return requestCount.get();
    }

    public int port() {
        return port;
    }
}
