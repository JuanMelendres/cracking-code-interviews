import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

// A real Server-Sent Events endpoint: one HTTP response, held open, streaming
// multiple real "data: ..." frames over real time via chunked transfer
// encoding -- not one response buffered and sent all at once at the end.
public class SseServer {
    private HttpServer server;

    public void start(int port, int eventCount, long delayBetweenEventsMillis) throws Exception {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/stream", exchange -> {
            exchange.getResponseHeaders().add("Content-Type", "text/event-stream");
            exchange.getResponseHeaders().add("Cache-Control", "no-cache");
            // A response length of 0 tells com.sun.net.httpserver to use chunked
            // transfer encoding -- required to stream an unknown number of
            // bytes over time rather than buffering a fixed-length body.
            exchange.sendResponseHeaders(200, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                for (int i = 1; i <= eventCount; i++) {
                    String frame = "data: real-event-" + i + "\n\n";
                    os.write(frame.getBytes());
                    os.flush();
                    if (i < eventCount) {
                        try {
                            Thread.sleep(delayBetweenEventsMillis);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
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
}
