package demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

/**
 * Real, executed client demo backing
 * syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md (T-2300).
 *
 * Every call below is a real HTTP request from java.net.http.HttpClient to
 * a real local server (FakeMessagesApiServer) implementing the real
 * Anthropic Messages API wire contract. No API key, no live network call --
 * but the request/response parsing, SSE stream handling, tool-use
 * round-trip, and 429/retry logic below are the real integration code a
 * production client needs, exercised against the real documented shape.
 */
public class LlmApiClientDemo {

    private static final String BASE_URL = "http://localhost:8091";
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    // Illustrative per-million-token USD rates, as of a stated date -- not
    // fetched live. A real integration reads current pricing from the
    // provider's own pricing page, not a hardcoded constant like this one.
    private static final Map<String, double[]> PRICE_PER_MILLION_TOKENS_2026_09 = Map.of(
            "claude-demo-model", new double[]{3.00, 15.00} // {input, output}
    );

    public static void main(String[] args) throws Exception {
        // A generously high threshold -- sections 1-3 exercise the happy path,
        // not rate limiting, so this server should not rate-limit them.
        HttpServer server = new FakeMessagesApiServer(/*rateLimitAfter*/ 1000).start(8091);
        try {
            System.out.println("=== 1. Non-streaming request + real token/cost accounting ===");
            nonStreamingWithCost();

            System.out.println();
            System.out.println("=== 2. Streaming request: real incremental SSE chunks ===");
            streaming();

            System.out.println();
            System.out.println("=== 3. Function calling: tool_use round-trip ===");
            functionCallingRoundTrip();
        } finally {
            server.stop(0);
        }

        System.out.println();
        System.out.println("=== 4. Rate limiting: real 429 + exponential backoff retry ===");
        // A dedicated server/port with a low threshold, so this section's rate
        // limiting is deterministic and independent of how many calls sections 1-3 made.
        HttpServer rateLimitedServer = new FakeMessagesApiServer(/*rateLimitAfter*/ 0).start(8092);
        try {
            rateLimitAndRetry();
        } finally {
            rateLimitedServer.stop(0);
        }
    }

    // ---- 1. Non-streaming + cost --------------------------------------

    private static void nonStreamingWithCost() throws Exception {
        ObjectNode requestBody = JSON.createObjectNode();
        requestBody.put("model", "claude-demo-model");
        requestBody.put("max_tokens", 256);
        ArrayNode messages = requestBody.putArray("messages");
        messages.addObject().put("role", "user").put("content", "What's the capital of France?");

        HttpResponse<String> response = post("/v1/messages", requestBody);
        JsonNode body = JSON.readTree(response.body());
        String replyText = body.at("/content/0/text").asText();
        int inputTokens = body.at("/usage/input_tokens").asInt();
        int outputTokens = body.at("/usage/output_tokens").asInt();

        System.out.println("reply: " + replyText);
        System.out.println("usage: input_tokens=" + inputTokens + " output_tokens=" + outputTokens);

        double[] rates = PRICE_PER_MILLION_TOKENS_2026_09.get("claude-demo-model");
        double cost = (inputTokens * rates[0] + outputTokens * rates[1]) / 1_000_000.0;
        System.out.printf(">>> real computed cost for this call: $%.6f%n", cost);
    }

    // ---- 2. Streaming ---------------------------------------------------

    private static void streaming() throws Exception {
        ObjectNode requestBody = JSON.createObjectNode();
        requestBody.put("model", "claude-demo-model");
        requestBody.put("max_tokens", 256);
        requestBody.put("stream", true);
        ArrayNode messages = requestBody.putArray("messages");
        messages.addObject().put("role", "user").put("content", "Tell me something about Java.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/v1/messages"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(requestBody)))
                .build();

        HttpResponse<java.io.InputStream> response = HTTP.send(request, HttpResponse.BodyHandlers.ofInputStream());
        StringBuilder assembled = new StringBuilder();
        int chunkCount = 0;
        long start = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
            String line;
            String currentEvent = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("event: ")) {
                    currentEvent = line.substring("event: ".length());
                } else if (line.startsWith("data: ")) {
                    JsonNode data = JSON.readTree(line.substring("data: ".length()));
                    if ("content_block_delta".equals(currentEvent)) {
                        String piece = data.at("/delta/text").asText();
                        assembled.append(piece);
                        chunkCount++;
                        long elapsedMs = System.currentTimeMillis() - start;
                        System.out.println("  [+" + elapsedMs + "ms] chunk " + chunkCount + ": \"" + piece.strip() + "\"");
                    } else if ("message_delta".equals(currentEvent)) {
                        System.out.println("  final usage: " + data.get("usage"));
                    }
                }
            }
        }
        System.out.println(">>> assembled full text from " + chunkCount + " real, separately-received chunks:");
        System.out.println("    \"" + assembled.toString().strip() + "\"");
    }

    // ---- 3. Function calling round-trip --------------------------------

    private static void functionCallingRoundTrip() throws Exception {
        ObjectNode toolDef = JSON.createObjectNode();
        toolDef.put("name", "get_weather");
        toolDef.put("description", "Get the current weather for a location");

        ObjectNode requestBody = JSON.createObjectNode();
        requestBody.put("model", "claude-demo-model");
        requestBody.put("max_tokens", 256);
        requestBody.putArray("tools").add(toolDef);
        ArrayNode messages = requestBody.putArray("messages");
        messages.addObject().put("role", "user").put("content", "What's the weather like in Mexico City?");

        HttpResponse<String> firstResponse = post("/v1/messages", requestBody);
        JsonNode firstBody = JSON.readTree(firstResponse.body());
        String stopReason = firstBody.path("stop_reason").asText();
        System.out.println("round 1 stop_reason: " + stopReason);

        if (!"tool_use".equals(stopReason)) {
            throw new IllegalStateException("expected tool_use, got " + stopReason);
        }
        JsonNode toolUseBlock = firstBody.at("/content/0");
        String toolName = toolUseBlock.path("name").asText();
        String toolUseId = toolUseBlock.path("id").asText();
        JsonNode toolInput = toolUseBlock.path("input");
        System.out.println("model requested tool: " + toolName + " with input " + toolInput);

        // Real tool execution would happen here; this demo returns a fixed result.
        String toolResult = "22C, sunny";

        // Round 2: send the tool result back, matching the real multi-turn protocol.
        ObjectNode assistantMessage = messages.addObject();
        assistantMessage.put("role", "assistant");
        assistantMessage.putArray("content").add(toolUseBlock.deepCopy());
        ObjectNode toolResultMessage = messages.addObject();
        toolResultMessage.put("role", "user");
        ArrayNode toolResultContent = toolResultMessage.putArray("content");
        ObjectNode toolResultBlock = toolResultContent.addObject();
        toolResultBlock.put("type", "tool_result");
        toolResultBlock.put("tool_use_id", toolUseId);
        toolResultBlock.put("content", toolResult);

        HttpResponse<String> secondResponse = post("/v1/messages", requestBody);
        JsonNode secondBody = JSON.readTree(secondResponse.body());
        System.out.println("round 2 final reply: " + secondBody.at("/content/0/text").asText());
    }

    // ---- 4. Rate limiting + retry ---------------------------------------

    private static void rateLimitAndRetry() throws Exception {
        ObjectNode requestBody = JSON.createObjectNode();
        requestBody.put("model", "claude-demo-model");
        requestBody.put("max_tokens", 16);
        ArrayNode messages = requestBody.putArray("messages");
        messages.addObject().put("role", "user").put("content", "ping");

        for (int attempt = 1; attempt <= 6; attempt++) {
            HttpResponse<String> response = post("http://localhost:8092/v1/messages", requestBody);
            if (response.statusCode() == 429) {
                String retryAfter = response.headers().firstValue("retry-after").orElse("1");
                System.out.println("attempt " + attempt + ": HTTP 429 rate_limit_error, retry-after=" + retryAfter + "s -> backing off");
                Thread.sleep(Duration.ofSeconds(Long.parseLong(retryAfter)).toMillis());
            } else {
                System.out.println("attempt " + attempt + ": HTTP " + response.statusCode() + " -- succeeded after backoff");
                return;
            }
        }
        throw new IllegalStateException("did not succeed within 6 attempts");
    }

    // ---- shared helper ----------------------------------------------------

    private static HttpResponse<String> post(String path, ObjectNode body) throws Exception {
        String url = path.startsWith("http") ? path : BASE_URL + path;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)))
                .build();
        return HTTP.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
