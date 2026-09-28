import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Structured logging, correlation-ID propagation, and log hygiene, shown as
 * real output rather than described.
 *
 * Deliberately dependency-free: the point is the shape of the record and the
 * propagation rules, not a particular logging library. A real service would use
 * SLF4J + Logback with a JSON encoder and MDC; the MDC here is a ThreadLocal,
 * which is exactly what MDC is.
 *
 * Java 21. Pure JDK, no dependencies.
 *
 * Compile and run:
 *   javac -d out src/StructuredLoggingDemo.java
 *   java -cp out StructuredLoggingDemo
 */
public class StructuredLoggingDemo {

    public static void main(String[] args) throws Exception {
        section("1. The same event, unstructured then structured");
        unstructuredVersusStructured();

        section("2. Correlation ID tying one request's records together");
        correlatedRequest();

        section("3. What happens to context when work crosses a thread boundary");
        threadBoundary();

        section("4. Redaction: what must never reach the log in the first place");
        redaction();

        section("5. Level discipline: what each level is actually for");
        levelDiscipline();
    }

    // ------------------------------------------------ MDC (a ThreadLocal)

    private static final ThreadLocal<Map<String, String>> CONTEXT =
            ThreadLocal.withInitial(LinkedHashMap::new);

    static void put(String key, String value) {
        CONTEXT.get().put(key, value);
    }

    static Map<String, String> snapshot() {
        return new LinkedHashMap<>(CONTEXT.get());
    }

    static void adopt(Map<String, String> context) {
        CONTEXT.get().putAll(context);
    }

    static void clear() {
        CONTEXT.remove();
    }

    // ------------------------------------------------ the log record

    static void log(String level, String event, Object... keyValues) {
        StringBuilder sb = new StringBuilder("{");
        field(sb, "ts", Instant.parse("2026-09-28T10:15:30.123Z").toString()); // fixed for reproducibility
        field(sb, "level", level);
        field(sb, "logger", "OrderService");
        field(sb, "thread", Thread.currentThread().getName());
        field(sb, "event", event);
        for (Map.Entry<String, String> e : CONTEXT.get().entrySet()) {
            field(sb, e.getKey(), e.getValue());
        }
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            field(sb, String.valueOf(keyValues[i]), String.valueOf(keyValues[i + 1]));
        }
        sb.append('}');
        System.out.println(sb);
    }

    private static void field(StringBuilder sb, String k, String v) {
        if (sb.length() > 1) {
            sb.append(',');
        }
        sb.append('"').append(k).append("\":\"").append(v.replace("\"", "\\\"")).append('"');
    }

    // ------------------------------------------------ 1

    private static void unstructuredVersusStructured() {
        String orderId = "ORD-1001";
        long millis = 412;
        System.out.println("Unstructured (what most services actually emit):");
        System.out.println("2026-09-28 10:15:30.123 INFO  OrderService - Order " + orderId
                + " placed successfully in " + millis + "ms");
        System.out.println();
        System.out.println("Structured, same event:");
        log("INFO", "order_placed", "order_id", orderId, "duration_ms", String.valueOf(millis));
        System.out.println();
        System.out.println("The first can only be searched with a regex that breaks the day someone");
        System.out.println("rewords the sentence. The second supports `duration_ms > 400` as a query,");
        System.out.println("and the message text is a stable event NAME, not prose.");
        clear();
    }

    // ------------------------------------------------ 2

    private static void correlatedRequest() {
        put("correlation_id", "c-7f3a91");
        put("user_id", "u-2048");
        log("INFO", "request_received", "path", "/orders", "method", "POST");
        log("DEBUG", "inventory_checked", "sku", "SKU-1", "available", "true");
        log("WARN", "payment_retry", "attempt", "2", "reason", "gateway_timeout");
        log("INFO", "order_placed", "order_id", "ORD-1002", "duration_ms", "980");
        System.out.println();
        System.out.println("Every record carries correlation_id without any call site passing it.");
        System.out.println("One query -- correlation_id=\"c-7f3a91\" -- returns the whole request story.");
        clear();
    }

    // ------------------------------------------------ 3

    private static void threadBoundary() throws Exception {
        put("correlation_id", "c-aa0102");
        log("INFO", "before_async_dispatch");

        ExecutorService pool = Executors.newFixedThreadPool(2);

        pool.submit(() -> log("INFO", "inside_pool_naive")).get();

        Map<String, String> captured = snapshot();
        pool.submit(() -> {
            adopt(captured);
            try {
                log("INFO", "inside_pool_propagated");
            } finally {
                clear();
            }
        }).get();

        pool.shutdown();
        pool.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println();
        System.out.println("The naive record lost correlation_id entirely: the pool thread has its own");
        System.out.println("ThreadLocal, and nothing copied the context across. That is the single most");
        System.out.println("common reason a trace goes silent halfway through a request. The fix is to");
        System.out.println("capture a snapshot on the submitting thread and adopt it inside the task --");
        System.out.println("and to clear it afterwards, because pooled threads are reused and stale");
        System.out.println("context is worse than none: it attributes work to the wrong request.");
        clear();
    }

    // ------------------------------------------------ 4

    private static final Pattern EMAIL = Pattern.compile("[\\w.+-]+@[\\w-]+\\.[\\w.]+");
    private static final Pattern PAN = Pattern.compile("\\b(?:\\d[ -]*?){13,16}\\b");

    private static String redact(String s) {
        String out = EMAIL.matcher(s).replaceAll("<email redacted>");
        Matcher m = PAN.matcher(out);
        return m.replaceAll(r -> "<card redacted, last4=" + r.group().replaceAll("\\D", "")
                .substring(r.group().replaceAll("\\D", "").length() - 4) + ">");
    }

    private static void redaction() {
        String raw = "payment failed for alice@example.com using card 4111 1111 1111 1111";
        System.out.println("Raw message (must never be logged):");
        System.out.println("  " + raw);
        System.out.println("Redacted:");
        System.out.println("  " + redact(raw));
        System.out.println();
        put("correlation_id", "c-bb0304");
        log("ERROR", "payment_failed", "user_ref", "u-2048", "card_last4", "1111",
                "message", redact(raw));
        System.out.println();
        System.out.println("Redaction at the sink is a safety net, not the design. The design is to log");
        System.out.println("STABLE REFERENCES (user_ref, card_last4) instead of the values themselves --");
        System.out.println("a log that never received the PAN cannot leak it, and logs are replicated to");
        System.out.println("places your access-control review never looks.");
        clear();
    }

    // ------------------------------------------------ 5

    private static void levelDiscipline() {
        put("correlation_id", "c-cc0506");
        log("ERROR", "order_rejected", "reason", "downstream_unavailable", "actionable", "true");
        log("WARN", "retry_budget_low", "remaining", "1", "actionable", "soon");
        log("INFO", "order_placed", "order_id", "ORD-1003");
        log("DEBUG", "cache_lookup", "key", "product:42", "hit", "false");
        System.out.println();
        System.out.println("ERROR means a human must do something. WARN means it is heading that way.");
        System.out.println("INFO is the business event trail. DEBUG is for reproducing a specific bug");
        System.out.println("and is off in production by default. An ERROR that nobody acts on trains");
        System.out.println("everyone to ignore ERROR, which is how real incidents get missed.");
        clear();
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
