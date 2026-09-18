# Webhook Design and Delivery Guarantees — Real, Executed Demos

Backs [Webhook Design and Delivery Guarantees](../../../syllabus/07-api-design/webhook-design-and-delivery-guarantees.md)
(T-2415). Pure JDK 21 -- a real `com.sun.net.httpserver.HttpServer` receiver
and a real `java.net.http.HttpClient` sender talking over a real loopback
socket, no framework and no Maven/Gradle. Only dependency: the JUnit 5
console launcher.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -parameters -cp "lib/*" -d out src/demo/*.java
```

## Run

```bash
java -cp "out:lib/*" org.junit.platform.console.ConsoleLauncher --select-class demo.WebhookDeliveryGuaranteesTest
```

Real output ([full capture](test-run-output.txt)); all 4 tests pass.

## What each test proves

1. **`validSignature_realHmacVerification_accepted`** — a real HMAC-SHA256
   signature (`HmacSigner`, `javax.crypto.Mac`) computed over the real
   payload bytes, sent as an `X-Signature` header, is verified by the
   receiver via `MessageDigest.isEqual` (constant-time comparison, avoiding
   a timing side-channel) and accepted with a real `200`.

2. **`tamperedPayload_realSignatureMismatch_rejected`** — a signature is
   computed over one payload, then a *different* (tampered) payload is sent
   with that same signature header. Real result: a real `401`, and the
   receiver's `receivedCount` stays at `0` — the exact scenario the
   signature exists to catch: a payload modified in transit, or a forged
   request from someone who doesn't know the shared secret.

3. **`flakyReceiver_realExponentialBackoffRecovers`** — the receiver is
   configured to return a real `503` on its first 2 real requests, then
   `200`. The sender's real retry loop (50ms, then 100ms backoff) recovers
   on the 3rd real attempt — verified both by the real per-attempt status
   codes and by real measured elapsed time (≥150ms, proving the backoff
   delays actually happened, not just that a retry eventually succeeded).

4. **`duplicateDelivery_sameDeliveryId_realIdempotentDedup`** — the exact
   same delivery ID is sent twice (simulating a real webhook redelivery,
   which every major provider's at-least-once delivery guarantee makes a
   real possibility, not an edge case). The receiver's real
   `receivedCount` stays at `1` and `duplicateDeliveriesSkipped` becomes
   `1` — proving the receiver, not the sender, is what makes delivery
   effectively exactly-once: a client must track delivery IDs it has already
   processed and treat a repeat as a no-op rather than reprocessing it.
