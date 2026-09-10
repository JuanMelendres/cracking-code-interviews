import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Real demo of three related web-app risks this chapter covers: CSRF (synchronizer
// token pattern), CORS (origin-allowlist header logic), and session fixation /
// cookie-flag hygiene. All three run against a real com.sun.net.httpserver
// HttpServer, driven by real java.net.http.HttpClient requests -- no browser is
// involved, so anywhere "the browser" would enforce something (CORS actually
// blocking a cross-origin script from reading a response; a browser auto-attaching
// cookies to a cross-site form POST) is called out explicitly as simulated by this
// demo's client code rather than actually observed from a browser.
public class CsrfCorsSessionDemo {

    static final SecureRandom RNG = new SecureRandom();

    static String randomToken() {
        byte[] b = new byte[16];
        RNG.nextBytes(b);
        return HexFormat.of().formatHex(b);
    }

    // ---- Session store shared by the CSRF and session-fixation sections ----
    record Session(String csrfToken, int balanceUsd, boolean authenticated) {}
    static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    static String cookieValue(HttpRequest.Builder ignored) { return null; } // unused, kept for clarity

    static String extractCookie(com.sun.net.httpserver.HttpExchange ex, String name) {
        String header = ex.getRequestHeaders().getFirst("Cookie");
        if (header == null) return null;
        for (String part : header.split(";\\s*")) {
            int eq = part.indexOf('=');
            if (eq > 0 && part.substring(0, eq).equals(name)) return part.substring(eq + 1);
        }
        return null;
    }

    static Map<String, String> parseForm(String body) {
        Map<String, String> out = new ConcurrentHashMap<>();
        if (body == null || body.isEmpty()) return out;
        for (String pair : body.split("&")) {
            int eq = pair.indexOf('=');
            if (eq > 0) out.put(pair.substring(0, eq), pair.substring(eq + 1));
        }
        return out;
    }

    static void respond(com.sun.net.httpserver.HttpExchange ex, int status, String body) throws IOException {
        byte[] b = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(status, b.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(b); }
    }

    // ================= CSRF: synchronizer token pattern =================

    static HttpServer startCsrfServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);

        server.createContext("/login", ex -> {
            String sessionId = randomToken();
            String csrfToken = randomToken();
            SESSIONS.put(sessionId, new Session(csrfToken, 1000, true));
            ex.getResponseHeaders().add("Set-Cookie", "SESSIONID=" + sessionId + "; HttpOnly");
            respond(ex, 200, "sessionId=" + sessionId + " csrfToken=" + csrfToken);
        });

        // VULNERABLE: checks the session cookie only. Never looks at any CSRF token.
        server.createContext("/transfer-vulnerable", ex -> {
            String sessionId = extractCookie(ex, "SESSIONID");
            Session s = sessionId == null ? null : SESSIONS.get(sessionId);
            if (s == null) { respond(ex, 401, "no session"); return; }
            String newBalance = "balance now " + (s.balanceUsd() - 500);
            respond(ex, 200, "TRANSFERRED $500. " + newBalance);
        });

        // FIXED: also requires the request body's csrfToken to match the token
        // this exact session was issued at /login -- a value the session cookie
        // alone never carries and an off-site attacker has no way to read.
        server.createContext("/transfer-fixed", ex -> {
            String sessionId = extractCookie(ex, "SESSIONID");
            Session s = sessionId == null ? null : SESSIONS.get(sessionId);
            if (s == null) { respond(ex, 401, "no session"); return; }
            String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String suppliedToken = parseForm(body).get("csrfToken");
            if (suppliedToken == null || !suppliedToken.equals(s.csrfToken())) {
                respond(ex, 403, "Blocked: missing or invalid csrfToken");
                return;
            }
            respond(ex, 200, "TRANSFERRED $500. balance now " + (s.balanceUsd() - 500));
        });

        server.start();
        return server;
    }

    static void runCsrfDemo(int port) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        System.out.println("=== Victim logs in for real (GET /login) ===");
        HttpResponse<String> loginResp = client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/login")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        System.out.println(loginResp.body());
        String sessionCookie = loginResp.headers().firstValue("Set-Cookie").orElseThrow()
                .split(";")[0]; // "SESSIONID=..."
        String realCsrfToken = loginResp.body().split("csrfToken=")[1];

        System.out.println();
        System.out.println("=== Legitimate request: victim's own page submits the real form,");
        System.out.println("    Cookie AND matching csrfToken both present (VULNERABLE endpoint) ===");
        var legit = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/transfer-vulnerable"))
                .header("Cookie", sessionCookie)
                .POST(HttpRequest.BodyPublishers.ofString("csrfToken=" + realCsrfToken))
                .build(), HttpResponse.BodyHandlers.ofString());
        System.out.println("status=" + legit.statusCode() + " body=" + legit.body());

        System.out.println();
        System.out.println("=== Forged cross-site request (VULNERABLE endpoint): attacker's page");
        System.out.println("    can't read the victim's csrfToken, but the browser still auto-attaches");
        System.out.println("    the victim's session cookie to any request to this domain ===");
        var forgedVuln = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/transfer-vulnerable"))
                .header("Cookie", sessionCookie) // simulates the browser's automatic cross-site cookie attach
                .POST(HttpRequest.BodyPublishers.ofString("")) // attacker never had a csrfToken to send
                .build(), HttpResponse.BodyHandlers.ofString());
        System.out.println("status=" + forgedVuln.statusCode() + " body=" + forgedVuln.body()
                + "  <-- forged transfer SUCCEEDED, endpoint never checked for a token");

        System.out.println();
        System.out.println("=== Identical forged request against the FIXED endpoint ===");
        var forgedFixed = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/transfer-fixed"))
                .header("Cookie", sessionCookie)
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build(), HttpResponse.BodyHandlers.ofString());
        System.out.println("status=" + forgedFixed.statusCode() + " body=" + forgedFixed.body());
    }

    // ================= CORS: origin-allowlist header logic =================

    static final java.util.Set<String> CORS_ALLOWED_ORIGINS = java.util.Set.of("https://app.example.com");

    static HttpServer startCorsServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/api/data", ex -> {
            String origin = ex.getRequestHeaders().getFirst("Origin");
            if (origin != null && CORS_ALLOWED_ORIGINS.contains(origin)) {
                ex.getResponseHeaders().add("Access-Control-Allow-Origin", origin);
                ex.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
            }
            // No ACAO header at all when the origin isn't allowlisted -- this is
            // the real signal a browser's fetch() uses to block the response from
            // being readable by the calling page's JavaScript.
            respond(ex, 200, "{\"balanceUsd\":1000}");
        });
        server.start();
        return server;
    }

    static void runCorsDemo(int port) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        System.out.println("=== Request with Origin: https://app.example.com (allowlisted) ===");
        var allowed = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/data"))
                .header("Origin", "https://app.example.com").GET().build(), HttpResponse.BodyHandlers.ofString());
        System.out.println("Access-Control-Allow-Origin: " + allowed.headers().firstValue("Access-Control-Allow-Origin").orElse("<absent>"));

        System.out.println();
        System.out.println("=== Identical request with Origin: https://evil.example (NOT allowlisted) ===");
        var blocked = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/data"))
                .header("Origin", "https://evil.example").GET().build(), HttpResponse.BodyHandlers.ofString());
        System.out.println("Access-Control-Allow-Origin: " + blocked.headers().firstValue("Access-Control-Allow-Origin").orElse("<absent>"));
        System.out.println("HTTP body was still returned to this Java client either way (body=" + blocked.body() + ")");
        System.out.println("-- CORS is enforced by the BROWSER reading the missing header, not by the server");
        System.out.println("   refusing to answer; a non-browser client like this one always sees the body.");
    }

    // ================= Session fixation and cookie flags =================

    static final Map<String, Boolean> AUTHENTICATED_SESSIONS = new ConcurrentHashMap<>();

    static HttpServer startSessionServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);

        // VULNERABLE: if the client already presents a SESSIONID cookie, reuse it
        // as-is after "authenticating" -- an attacker who chose that ID beforehand
        // now knows a valid, authenticated session ID.
        server.createContext("/login-vulnerable", ex -> {
            String presented = extractCookie(ex, "SESSIONID");
            String sessionId = presented != null ? presented : randomToken();
            AUTHENTICATED_SESSIONS.put(sessionId, true);
            ex.getResponseHeaders().add("Set-Cookie", "SESSIONID=" + sessionId);
            respond(ex, 200, "authenticated as session " + sessionId);
        });

        // FIXED: always issues a brand-new session ID on successful login,
        // regardless of what the client presented -- the old, attacker-known ID
        // (if any) is never the one that ends up authenticated.
        server.createContext("/login-fixed", ex -> {
            String freshId = randomToken();
            AUTHENTICATED_SESSIONS.put(freshId, true);
            ex.getResponseHeaders().add("Set-Cookie",
                    "SESSIONID=" + freshId + "; HttpOnly; Secure; SameSite=Strict");
            respond(ex, 200, "authenticated as session " + freshId);
        });

        server.start();
        return server;
    }

    static void runSessionDemo(int port) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String attackerChosenIdA = "attacker-pre-set-session-id-0001";
        String attackerChosenIdB = "attacker-pre-set-session-id-0002";

        System.out.println("=== Session fixation: attacker pre-set SESSIONID=" + attackerChosenIdA + " ===");
        System.out.println();
        System.out.println("--- Victim's browser then logs in against the VULNERABLE endpoint,");
        System.out.println("    already carrying the attacker's pre-set cookie ---");
        var vuln = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/login-vulnerable"))
                .header("Cookie", "SESSIONID=" + attackerChosenIdA)
                .GET().build(), HttpResponse.BodyHandlers.ofString());
        System.out.println(vuln.body());
        System.out.println("Set-Cookie: " + vuln.headers().firstValue("Set-Cookie").orElse("<none>"));
        System.out.println("attacker's pre-chosen ID is now a REAL authenticated session: "
                + AUTHENTICATED_SESSIONS.getOrDefault(attackerChosenIdA, false));

        System.out.println();
        System.out.println("--- Same attack, DIFFERENT attacker-chosen ID, against the FIXED endpoint ---");
        var fixed = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/login-fixed"))
                .header("Cookie", "SESSIONID=" + attackerChosenIdB)
                .GET().build(), HttpResponse.BodyHandlers.ofString());
        System.out.println(fixed.body());
        System.out.println("Set-Cookie: " + fixed.headers().firstValue("Set-Cookie").orElse("<none>"));
        System.out.println("attacker's pre-chosen ID (" + attackerChosenIdB + ") is now a REAL authenticated session: "
                + AUTHENTICATED_SESSIONS.getOrDefault(attackerChosenIdB, false)
                + "  (server issued a brand-new ID instead, ignoring the presented one)");
        System.out.println();
        System.out.println("Note the fixed endpoint's own Set-Cookie also carries HttpOnly; Secure; SameSite=Strict --");
        System.out.println("real response-header evidence of the cookie-flag hygiene this section also covers.");
    }

    public static void main(String[] args) throws Exception {
        HttpServer csrf = startCsrfServer(15710);
        HttpServer cors = startCorsServer(15711);
        HttpServer session = startSessionServer(15712);
        try {
            System.out.println("############ CSRF: synchronizer token pattern ############");
            runCsrfDemo(15710);

            System.out.println();
            System.out.println("############ CORS: origin-allowlist header logic ############");
            runCorsDemo(15711);

            System.out.println();
            System.out.println("############ Session fixation and cookie flags ############");
            runSessionDemo(15712);
        } finally {
            csrf.stop(0);
            cors.stop(0);
            session.stop(0);
        }
    }
}
