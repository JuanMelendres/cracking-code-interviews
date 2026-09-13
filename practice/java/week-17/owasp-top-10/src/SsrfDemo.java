import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

// Real demo of A10:2021 Server-Side Request Forgery.
// Two local HTTP servers stand in for: (1) a public image-fetch target the
// feature is meant to reach, and (2) a cloud-metadata-style internal endpoint
// that should never be reachable from this code path. A "URL preview" service
// fetches whatever URL the caller supplies -- the only difference between the
// vulnerable and fixed versions is whether the target host is validated first.
public class SsrfDemo {

    static HttpServer startServer(int port, String path, String body) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext(path, exchange -> {
            byte[] resp = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, resp.length);
            exchange.getResponseBody().write(resp);
            exchange.close();
        });
        server.start();
        return server;
    }

    // VULNERABLE: fetches any URL the caller provides, server-side, no validation.
    static String fetchUrlVulnerable(String userSuppliedUrl) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(URI.create(userSuppliedUrl)).GET().build();
        return client.send(req, HttpResponse.BodyHandlers.ofString()).body();
    }

    static final java.util.Set<String> ALLOWED_HOSTS = java.util.Set.of("public-images.example.internal-test");

    // FIXED: resolves the host and only allows an explicit allowlist; internal
    // ports on loopback are rejected outright regardless of hostname string.
    static String fetchUrlFixed(String userSuppliedUrl) throws Exception {
        URL parsed = URI.create(userSuppliedUrl).toURL();
        String host = parsed.getHost();
        int port = parsed.getPort();
        boolean isInternalPort = (port == 15601); // the "metadata" port below
        if (isInternalPort || !ALLOWED_HOSTS.contains(host)) {
            throw new SecurityException("target host:port not in allowlist: " + host + ":" + port);
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(parsed.toURI()).GET().build();
        return client.send(req, HttpResponse.BodyHandlers.ofString()).body();
    }

    public static void main(String[] args) throws Exception {
        HttpServer publicSrv = startServer(15600, "/cat.jpg", "<binary image bytes>");
        HttpServer internalSrv = startServer(15601, "/latest/meta-data/iam/security-credentials",
                "AKIA-DEMO-NOT-REAL SecretAccessKey=demo-secret-value-not-real");
        try {
            System.out.println("=== VULNERABLE preview service: legitimate request ===");
            System.out.println(fetchUrlVulnerable("http://127.0.0.1:15600/cat.jpg"));

            System.out.println();
            System.out.println("=== VULNERABLE preview service: attacker-supplied internal URL ===");
            String leaked = fetchUrlVulnerable(
                    "http://127.0.0.1:15601/latest/meta-data/iam/security-credentials");
            System.out.println("Leaked: " + leaked);

            System.out.println();
            System.out.println("=== FIXED preview service: same attacker-supplied internal URL ===");
            try {
                fetchUrlFixed("http://127.0.0.1:15601/latest/meta-data/iam/security-credentials");
                System.out.println("Leaked (BUG)");
            } catch (SecurityException e) {
                System.out.println("Blocked: " + e.getMessage());
            }
        } finally {
            publicSrv.stop(0);
            internalSrv.stop(0);
        }
    }
}
