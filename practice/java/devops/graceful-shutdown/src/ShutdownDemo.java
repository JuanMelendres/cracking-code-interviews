import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Two servers that differ only in how they react to SIGTERM, so the cost of
 * getting it wrong is observable rather than asserted.
 *
 *   java -cp out ShutdownDemo abrupt   <- no shutdown hook at all
 *   java -cp out ShutdownDemo graceful <- readiness flip, drain, bounded wait
 *
 * Both serve /work, which takes ~3 seconds, and /readyz, which reports whether
 * the process still wants traffic. run-demo.sh drives both and captures what a
 * real client sees when SIGTERM arrives mid-request.
 *
 * Java 21. Pure JDK, no dependencies.
 */
public class ShutdownDemo {

    private static final AtomicBoolean ACCEPTING = new AtomicBoolean(true);
    private static final AtomicInteger IN_FLIGHT = new AtomicInteger();
    private static final AtomicInteger COMPLETED = new AtomicInteger();
    private static final AtomicInteger REJECTED = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        boolean graceful = args.length > 0 && args[0].equals("graceful");
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8111;

        ExecutorService workers = Executors.newFixedThreadPool(8);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(workers);
        server.createContext("/work", ShutdownDemo::work);
        server.createContext("/readyz", ShutdownDemo::readyz);
        server.start();

        log((graceful ? "graceful" : "abrupt") + " server listening on port " + port
                + " (pid " + ProcessHandle.current().pid() + ")");

        if (graceful) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                Instant start = Instant.now();
                log("SIGTERM received");

                // 1. Stop declaring readiness FIRST. The load balancer or
                //    kubelet needs time to notice before the socket closes.
                ACCEPTING.set(false);
                log("readiness flipped to NOT ready; in-flight requests: " + IN_FLIGHT.get());
                sleep(500); // stand-in for the readiness probe period

                // 2. Stop accepting new connections, then give in-flight work
                //    a bounded window to finish. HttpServer.stop(delay) blocks
                //    up to `delay` seconds waiting for active exchanges.
                log("no longer accepting new connections; draining up to 10s");
                server.stop(10);

                // 3. Shut the worker pool down and wait, bounded.
                workers.shutdown();
                try {
                    boolean clean = workers.awaitTermination(10, TimeUnit.SECONDS);
                    log("worker pool drained cleanly: " + clean);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                log("shutdown complete in " + Duration.between(start, Instant.now()).toMillis()
                        + "ms; completed=" + COMPLETED.get() + " rejected=" + REJECTED.get());
            }, "graceful-shutdown"));
        }

        Thread.currentThread().join();
    }

    /** A request that takes real time, so SIGTERM can land in the middle of one. */
    private static void work(HttpExchange exchange) throws IOException {
        IN_FLIGHT.incrementAndGet();
        try {
            if (!ACCEPTING.get()) {
                // Already draining: refuse fast rather than accept work we
                // cannot finish. 503 is retryable; a dropped connection is not.
                REJECTED.incrementAndGet();
                respond(exchange, 503, "draining\n");
                return;
            }
            log("request start");
            sleep(3000);
            COMPLETED.incrementAndGet();
            log("request finished");
            respond(exchange, 200, "done\n");
        } finally {
            IN_FLIGHT.decrementAndGet();
        }
    }

    private static void readyz(HttpExchange exchange) throws IOException {
        boolean ready = ACCEPTING.get();
        respond(exchange, ready ? 200 : 503, (ready ? "ready" : "not ready") + "\n");
    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void log(String message) {
        System.out.println("[server] " + message);
        System.out.flush();
    }
}
