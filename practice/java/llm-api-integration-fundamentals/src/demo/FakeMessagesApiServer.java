package demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A real, local, in-JVM HTTP server implementing the real wire contract of
 * Anthropic's Messages API (POST /v1/messages) -- request/response shape,
 * streaming SSE event types, tool-use content blocks, usage accounting, and
 * a 429 rate-limit response -- WITHOUT calling the live Anthropic API.
 *
 * This backs syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md
 * (T-2300). It exists so this chapter's demo is real, executed, and
 * reproducible without an API key or network dependency, while exercising
 * the actual documented request/response contract a real integration has to
 * handle. It is explicitly NOT a call to the live Anthropic service --
 * every response below is this local server's own logic, chosen to
 * demonstrate one specific real integration concern per request.
 */
public class FakeMessagesApiServer {

    private final ObjectMapper json = new ObjectMapper();
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final int rateLimitAfter;

    public FakeMessagesApiServer(int rateLimitAfter) {
        this.rateLimitAfter = rateLimitAfter;
    }

    public HttpServer start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/v1/messages", new MessagesHandler());
        server.start();
        return server;
    }

    private class MessagesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            int n = requestCount.incrementAndGet();

            if (n > rateLimitAfter && n <= rateLimitAfter + 2) {
                sendRateLimited(exchange);
                return;
            }

            JsonNode request = json.readTree(exchange.getRequestBody());
            String model = request.path("model").asText("unknown-model");
            boolean stream = request.path("stream").asBoolean(false);
            JsonNode messages = request.path("messages");
            String lastUserText = lastUserMessageText(messages);
            boolean hasTools = request.has("tools") && request.get("tools").size() > 0;
            boolean isToolResult = containsToolResult(messages);

            if (isToolResult) {
                respondFinalAfterTool(exchange, model, lastUserText);
                return;
            }

            if (hasTools && lastUserText.toLowerCase().contains("weather")) {
                respondWithToolUse(exchange, model);
                return;
            }

            if (stream) {
                respondStreaming(exchange, model, lastUserText);
            } else {
                respondNonStreaming(exchange, model, lastUserText);
            }
        }
    }

    private String lastUserMessageText(JsonNode messages) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            JsonNode m = messages.get(i);
            if ("user".equals(m.path("role").asText())) {
                JsonNode content = m.path("content");
                if (content.isTextual()) return content.asText();
                if (content.isArray()) {
                    for (JsonNode block : content) {
                        if ("text".equals(block.path("type").asText())) return block.path("text").asText();
                    }
                }
            }
        }
        return "";
    }

    private boolean containsToolResult(JsonNode messages) {
        for (JsonNode m : messages) {
            JsonNode content = m.path("content");
            if (content.isArray()) {
                for (JsonNode block : content) {
                    if ("tool_result".equals(block.path("type").asText())) return true;
                }
            }
        }
        return false;
    }

    // ---- non-streaming: real Messages API response shape -------------

    private void respondNonStreaming(HttpExchange exchange, String model, String userText) throws IOException {
        String replyText = "You said: \"" + userText + "\". This is a real, locally-served response.";
        int inputTokens = estimateTokens(userText);
        int outputTokens = estimateTokens(replyText);

        ObjectNode body = json.createObjectNode();
        body.put("id", "msg_" + System.nanoTime());
        body.put("type", "message");
        body.put("role", "assistant");
        body.put("model", model);
        ArrayNode content = body.putArray("content");
        ObjectNode textBlock = content.addObject();
        textBlock.put("type", "text");
        textBlock.put("text", replyText);
        body.put("stop_reason", "end_turn");
        ObjectNode usage = body.putObject("usage");
        usage.put("input_tokens", inputTokens);
        usage.put("output_tokens", outputTokens);

        sendJson(exchange, 200, body);
    }

    // ---- tool use: real tool_use content block + stop_reason ---------

    private void respondWithToolUse(HttpExchange exchange, String model) throws IOException {
        ObjectNode body = json.createObjectNode();
        body.put("id", "msg_" + System.nanoTime());
        body.put("type", "message");
        body.put("role", "assistant");
        body.put("model", model);
        ArrayNode content = body.putArray("content");
        ObjectNode toolUse = content.addObject();
        toolUse.put("type", "tool_use");
        toolUse.put("id", "toolu_" + System.nanoTime());
        toolUse.put("name", "get_weather");
        ObjectNode input = toolUse.putObject("input");
        input.put("location", "Mexico City");
        body.put("stop_reason", "tool_use");
        ObjectNode usage = body.putObject("usage");
        usage.put("input_tokens", 42);
        usage.put("output_tokens", 18);

        sendJson(exchange, 200, body);
    }

    private void respondFinalAfterTool(HttpExchange exchange, String model, String ignoredUserText) throws IOException {
        String replyText = "Based on the tool result: it's 22C and sunny in Mexico City right now.";
        ObjectNode body = json.createObjectNode();
        body.put("id", "msg_" + System.nanoTime());
        body.put("type", "message");
        body.put("role", "assistant");
        body.put("model", model);
        ArrayNode content = body.putArray("content");
        ObjectNode textBlock = content.addObject();
        textBlock.put("type", "text");
        textBlock.put("text", replyText);
        body.put("stop_reason", "end_turn");
        ObjectNode usage = body.putObject("usage");
        usage.put("input_tokens", 65);
        usage.put("output_tokens", estimateTokens(replyText));

        sendJson(exchange, 200, body);
    }

    // ---- streaming: real SSE event sequence ---------------------------

    private void respondStreaming(HttpExchange exchange, String model, String userText) throws IOException {
        String replyText = "Streaming a real reply, one chunk at a time, for: " + userText;
        String[] words = replyText.split(" ");

        exchange.getResponseHeaders().add("Content-Type", "text/event-stream");
        exchange.sendResponseHeaders(200, 0);
        OutputStream out = exchange.getResponseBody();

        writeEvent(out, "message_start", messageStartEvent(model));
        writeEvent(out, "content_block_start", contentBlockStartEvent());

        for (String word : words) {
            String piece = word + " ";
            writeEvent(out, "content_block_delta", contentBlockDeltaEvent(piece));
            out.flush();
            sleep(30); // real, observable inter-chunk delay
        }

        writeEvent(out, "content_block_stop", json.createObjectNode().put("type", "content_block_stop").put("index", 0));
        writeEvent(out, "message_delta", messageDeltaEvent(estimateTokens(userText), estimateTokens(replyText)));
        writeEvent(out, "message_stop", json.createObjectNode().put("type", "message_stop"));
        out.close();
    }

    private ObjectNode messageStartEvent(String model) {
        ObjectNode event = json.createObjectNode();
        event.put("type", "message_start");
        ObjectNode message = event.putObject("message");
        message.put("id", "msg_" + System.nanoTime());
        message.put("type", "message");
        message.put("role", "assistant");
        message.put("model", model);
        message.putArray("content");
        ObjectNode usage = message.putObject("usage");
        usage.put("input_tokens", 0);
        usage.put("output_tokens", 0);
        return event;
    }

    private ObjectNode contentBlockStartEvent() {
        ObjectNode event = json.createObjectNode();
        event.put("type", "content_block_start");
        event.put("index", 0);
        ObjectNode contentBlock = event.putObject("content_block");
        contentBlock.put("type", "text");
        contentBlock.put("text", "");
        return event;
    }

    private ObjectNode contentBlockDeltaEvent(String textPiece) {
        ObjectNode event = json.createObjectNode();
        event.put("type", "content_block_delta");
        event.put("index", 0);
        ObjectNode delta = event.putObject("delta");
        delta.put("type", "text_delta");
        delta.put("text", textPiece);
        return event;
    }

    private ObjectNode messageDeltaEvent(int inputTokens, int outputTokens) {
        ObjectNode event = json.createObjectNode();
        event.put("type", "message_delta");
        ObjectNode delta = event.putObject("delta");
        delta.put("stop_reason", "end_turn");
        ObjectNode usage = event.putObject("usage");
        usage.put("input_tokens", inputTokens);
        usage.put("output_tokens", outputTokens);
        return event;
    }

    private void writeEvent(OutputStream out, String eventName, ObjectNode data) throws IOException {
        String payload = "event: " + eventName + "\n" + "data: " + json.writeValueAsString(data) + "\n\n";
        out.write(payload.getBytes(StandardCharsets.UTF_8));
    }

    // ---- rate limiting: real 429 + retry-after -------------------------

    private void sendRateLimited(HttpExchange exchange) throws IOException {
        ObjectNode body = json.createObjectNode();
        body.put("type", "error");
        ObjectNode error = body.putObject("error");
        error.put("type", "rate_limit_error");
        error.put("message", "Number of request tokens has exceeded your per-minute rate limit.");
        exchange.getResponseHeaders().add("retry-after", "1");
        sendJson(exchange, 429, body);
    }

    // ---- shared helpers -------------------------------------------------

    private void sendJson(HttpExchange exchange, int status, ObjectNode body) throws IOException {
        byte[] bytes = json.writeValueAsBytes(body);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    /** A real tokenizer isn't needed to demonstrate the integration concern -- this is a stated, deliberate approximation (roughly 1 token per 4 characters), not a claim of exact provider tokenization. */
    private int estimateTokens(String text) {
        return Math.max(1, text.length() / 4);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
