import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

// Real short polling: the server ALWAYS responds immediately, whether or not
// there's anything new -- the client bears the cost of repeatedly asking.
public class ShortPollServer {
    private final AtomicReference<String> latestEvent;
    private final AtomicInteger requestsServed = new AtomicInteger();
    private HttpServer server;

    public ShortPollServer(AtomicReference<String> latestEvent) {
        this.latestEvent = latestEvent;
    }

    public void start(int port) throws Exception {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/poll-short", exchange -> {
            requestsServed.incrementAndGet();
            String event = latestEvent.get();
            String body = event != null ? event : "no-new-data";
            byte[] bytes = body.getBytes();
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
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

    public int requestsServed() {
        return requestsServed.get();
    }
}
