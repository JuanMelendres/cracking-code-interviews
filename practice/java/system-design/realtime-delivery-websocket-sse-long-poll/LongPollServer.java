import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

// Real long polling: the server holds the request open (blocked on a real
// monitor wait, not a busy loop) and only responds once real new data exists
// or a real timeout elapses -- the server bears the cost of holding the
// connection, not the client repeatedly asking.
public class LongPollServer {
    private final AtomicReference<String> latestEvent;
    private final Object lock;
    private final long maxWaitMillis;
    private final AtomicInteger requestsServed = new AtomicInteger();
    private HttpServer server;

    public LongPollServer(AtomicReference<String> latestEvent, Object lock, long maxWaitMillis) {
        this.latestEvent = latestEvent;
        this.lock = lock;
        this.maxWaitMillis = maxWaitMillis;
    }

    public void start(int port) throws Exception {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/poll-long", exchange -> {
            requestsServed.incrementAndGet();
            String body;
            synchronized (lock) {
                long deadline = System.currentTimeMillis() + maxWaitMillis;
                while (latestEvent.get() == null) {
                    long remaining = deadline - System.currentTimeMillis();
                    if (remaining <= 0) {
                        break;
                    }
                    try {
                        lock.wait(remaining);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                String event = latestEvent.get();
                body = event != null ? event : "timeout-no-data";
            }
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
