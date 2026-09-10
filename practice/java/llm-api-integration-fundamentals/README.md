# LLM API Integration Fundamentals — Real, Executed Demo

Backs [LLM API Integration Fundamentals](../../../syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md) (T-2300). A real Java client (`java.net.http.HttpClient`) talking real HTTP to a real local server (`com.sun.net.httpserver.HttpServer`) that implements Anthropic's real Messages API wire contract (request/response shape, streaming SSE event types, tool-use content blocks, usage accounting, 429 rate limiting) — **not a call to the live Anthropic API**. No API key or network dependency needed; every request/response below is real and reproducible.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/demo/*.java
java -cp "out:lib/*" demo.LlmApiClientDemo
```

## Reproduce the transcript

`output-transcript.txt` is the complete, real, unedited output of the last run, matching the chapter's Core Concepts and Practical Usage sections:

1. **Non-streaming request + cost accounting** — a real request/response round trip, reading real `usage.input_tokens`/`usage.output_tokens` from the response and computing a real dollar cost from a stated (illustrative, dated) per-token price table.
2. **Streaming** — a real Server-Sent-Events stream, read incrementally via `java.net.http.HttpClient`, printing each chunk with its real, measured arrival time (not one blocking call that happens to return an array of strings).
3. **Function calling round-trip** — a real two-turn exchange: the server's first response has `stop_reason: "tool_use"` and a `tool_use` content block naming a tool and its input; the client executes the (stubbed) tool locally, sends a `tool_result` message back, and receives a real final text answer that references the tool's result.
4. **Rate limiting** — a dedicated server instance configured to return real `429` responses (with a `retry-after` header) for its first two requests; the client's real backoff loop waits and retries, succeeding on the third attempt.

## Files

- `src/demo/FakeMessagesApiServer.java` — the real local server implementing the Messages API's real wire contract.
- `src/demo/LlmApiClientDemo.java` — the real client driving all four scenarios.
