import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

// A tiny, real HTTP server (JDK's own HttpServer, zero dependencies) whose
// only job is to prove two things about Docker Compose networking: that
// "db" resolves to the database container by service name alone (no IP,
// no localhost), and that `depends_on` without a healthcheck condition
// only waits for the database CONTAINER to start, not for Postgres itself
// to finish initializing and accept connections -- a real, reproducible
// race, not a description of one.
public class ApiServer {
    private static final String DB_HOST = "db";
    private static final int DB_PORT = 5432;

    // A raw TCP connect attempt -- no JDBC driver, no SQL. Success here
    // proves only that something is listening on db:5432 and accepting
    // TCP connections; that's exactly the granularity needed to observe
    // the depends_on race, without adding a driver dependency this image
    // doesn't otherwise need.
    private static String checkDb() {
        long start = System.nanoTime();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(DB_HOST, DB_PORT), 2000);
            long ms = (System.nanoTime() - start) / 1_000_000;
            return "CONNECTED to " + DB_HOST + ":" + DB_PORT + " in " + ms + "ms";
        } catch (IOException e) {
            long ms = (System.nanoTime() - start) / 1_000_000;
            return "FAILED to connect to " + DB_HOST + ":" + DB_PORT + " after " + ms + "ms -- " + e.getMessage();
        }
    }

    public static void main(String[] args) throws IOException {
        // The startup-time check: this is the one that races Postgres's
        // own real initialization time on a cold volume.
        System.out.println("Startup DB check: " + checkDb());

        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/health", exchange -> {
            String response = "ok\n";
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
        server.createContext("/db-check", exchange -> {
            String response = checkDb() + "\n";
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
        server.setExecutor(null);
        server.start();
        System.out.println("ApiServer started on port " + port);
    }
}
