import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A real HTTP server and client demonstrating conditional requests: strong
 * ETags, If-None-Match, 304 Not Modified, and what Cache-Control actually
 * changes about a client's behaviour.
 *
 * Measures the two things that matter in an interview answer: the byte savings
 * of a 304 versus a 200, and the fact that a 304 still costs a round trip --
 * which is exactly what `max-age` removes and revalidation does not.
 *
 * Java 21. Pure JDK, no dependencies.
 *
 * Compile and run:
 *   javac -d out src/HttpCachingDemo.java
 *   java -cp out HttpCachingDemo
 */
public class HttpCachingDemo {

    private static final AtomicInteger RENDERS = new AtomicInteger();
    private static String payload = """
            {"id":42,"name":"Widget","price":1999,"version":1}""";

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8113), 0);
        server.createContext("/product/42", HttpCachingDemo::product);
        server.start();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8113/product/42");

        header("1. First request: no validator held by the client");
        HttpResponse<String> first = send(client, uri, null);
        report(first);
        String etag = first.headers().firstValue("ETag").orElseThrow();

        header("2. Same resource, unchanged, WITH If-None-Match");
        HttpResponse<String> revalidated = send(client, uri, etag);
        report(revalidated);

        header("3. Resource changes on the server");
        payload = """
                {"id":42,"name":"Widget","price":2499,"version":2}""";
        System.out.println("(price updated; the ETag is derived from the body, so it changes too)");
        HttpResponse<String> changed = send(client, uri, etag);
        report(changed);

        header("4. Revalidating with the NEW ETag");
        String newEtag = changed.headers().firstValue("ETag").orElseThrow();
        report(send(client, uri, newEtag));

        header("Summary");
        int fullBody = first.body().getBytes(StandardCharsets.UTF_8).length;
        System.out.println("200 response body: " + fullBody + " bytes");
        System.out.println("304 response body: " + revalidated.body().getBytes(StandardCharsets.UTF_8).length
                + " bytes  (" + fullBody + " bytes saved on the wire per revalidation)");
        System.out.println("Server-side renders performed: " + RENDERS.get()
                + " for 4 requests -- the 304s skipped serialization entirely.");
        System.out.println();
        System.out.println("Both 304s still cost a full round trip. Cache-Control: max-age is what");
        System.out.println("removes the round trip; a validator only removes the body.");

        server.stop(0);
    }

    private static HttpResponse<String> send(HttpClient client, URI uri, String ifNoneMatch)
            throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(5)).GET();
        if (ifNoneMatch != null) {
            b.header("If-None-Match", ifNoneMatch);
            System.out.println("--> GET /product/42   If-None-Match: " + ifNoneMatch);
        } else {
            System.out.println("--> GET /product/42   (no If-None-Match)");
        }
        return client.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static void report(HttpResponse<String> response) {
        System.out.println("<-- " + response.statusCode() + " "
                + (response.statusCode() == 304 ? "Not Modified" : "OK"));
        response.headers().firstValue("ETag")
                .ifPresent(v -> System.out.println("    ETag:          " + v));
        response.headers().firstValue("Cache-Control")
                .ifPresent(v -> System.out.println("    Cache-Control: " + v));
        response.headers().firstValue("Vary")
                .ifPresent(v -> System.out.println("    Vary:          " + v));
        System.out.println("    body:          "
                + (response.body().isEmpty() ? "(empty)" : response.body())
                + "  [" + response.body().getBytes(StandardCharsets.UTF_8).length + " bytes]");
    }

    private static void product(HttpExchange exchange) throws IOException {
        String body = payload;
        String etag = "\"" + sha256(body).substring(0, 16) + "\"";

        // Directives are set on BOTH 200 and 304 -- a 304 that omits them lets
        // the client fall back to heuristic freshness, which is a real bug.
        exchange.getResponseHeaders().add("ETag", etag);
        exchange.getResponseHeaders().add("Cache-Control", "private, max-age=30, must-revalidate");
        exchange.getResponseHeaders().add("Vary", "Accept, Accept-Encoding");

        String ifNoneMatch = exchange.getRequestHeaders().getFirst("If-None-Match");
        if (etag.equals(ifNoneMatch)) {
            exchange.sendResponseHeaders(304, -1); // -1 == no body, per the spec
            exchange.close();
            return;
        }

        RENDERS.incrementAndGet();
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(s.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static void header(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
