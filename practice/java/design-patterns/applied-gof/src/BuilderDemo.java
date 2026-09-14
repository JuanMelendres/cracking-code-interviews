// Java 21. Builder: separate the construction of a complex, immutable object
// from its representation, giving named/optional parameters and validation a
// single, fluent entry point instead of a "telescoping constructor" (many
// overloaded constructors covering every combination of optional fields).

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

final class HttpRequest {
    private final String url;
    private final String method;
    private final Map<String, String> headers;
    private final String body;
    private final int timeoutMs;

    private HttpRequest(Builder b) {
        this.url = b.url;
        this.method = b.method;
        this.headers = Map.copyOf(b.headers); // defensively copied AND made unmodifiable
        this.body = b.body;
        this.timeoutMs = b.timeoutMs;
    }

    Map<String, String> headers() { return headers; }

    @Override
    public String toString() {
        return method + " " + url + " headers=" + headers + " body=" + body + " timeoutMs=" + timeoutMs;
    }

    static Builder builder(String url) { return new Builder(url); }

    static final class Builder {
        private final String url;
        private String method = "GET";
        private final Map<String, String> headers = new LinkedHashMap<>();
        private String body;
        private int timeoutMs = 5000;

        private Builder(String url) { this.url = Objects.requireNonNull(url); }

        Builder method(String method) { this.method = method; return this; }
        Builder header(String key, String value) { this.headers.put(key, value); return this; }
        Builder body(String body) { this.body = body; return this; }
        Builder timeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; return this; }

        HttpRequest build() { return new HttpRequest(this); }
    }
}

class BuilderDemo {
    public static void main(String[] args) {
        HttpRequest get = HttpRequest.builder("https://api.example.com/orders/42").build();
        System.out.println("== Only the required field set, everything else defaults ==");
        System.out.println(get);

        System.out.println();
        System.out.println("== Fluent construction, only the optional fields this call actually needs ==");
        HttpRequest post = HttpRequest.builder("https://api.example.com/orders")
            .method("POST")
            .header("Content-Type", "application/json")
            .header("Idempotency-Key", "abc-123")
            .body("{\"item\":\"widget\"}")
            .timeoutMs(2000)
            .build();
        System.out.println(post);

        System.out.println();
        System.out.println("== Proving the built object is genuinely immutable, not just conventionally treated that way ==");
        try {
            post.headers().put("X-Injected", "should-not-be-allowed");
            System.out.println("Mutation SUCCEEDED -- this would be a bug");
        } catch (UnsupportedOperationException e) {
            System.out.println("Mutation attempt threw: " + e.getClass().getSimpleName() + "  (headers() returns Map.copyOf(...) -- genuinely unmodifiable, not just an unenforced convention)");
        }
    }
}
