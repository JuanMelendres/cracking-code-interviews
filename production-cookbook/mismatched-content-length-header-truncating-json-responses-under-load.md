---
title: "Mismatched Content-Length Header Truncating JSON Responses Under Load"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md
source: syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md#production-scenarios
---

# Mismatched Content-Length Header Truncating JSON Responses Under Load

## Context

A frontend team's `fetch` calls to a backend service start intermittently returning incomplete JSON under high server load.

## Symptoms

`JSON.parse` throws intermittently, correlated with high backend load.

## Impact

Intermittent client-side failures under exactly the load conditions where reliability matters most.

## Initial Hypotheses

- A bug in the frontend's parsing code — initial focus, but ruled out once the same failure reproduces against a raw header inspection independent of any frontend parsing logic.
- A network-layer truncation issue — checked via a `curl -v` transcript, ruling out a generic network problem.
- The backend, under load, occasionally emits a `Content-Length` header that doesn't match the body it actually sent — correct.

## Evidence

A `curl -v` transcript captured during a load spike shows the response's declared `Content-Length` header disagreeing with the actual body length delivered.

## Investigation Timeline

1. Intermittent `JSON.parse` failures reported, correlated with high server load.
2. Frontend-parsing-bug hypothesis considered first, then ruled out.
3. A `curl -v` header inspection captured during load reproduces the mismatch directly, isolating it to the backend's response framing.

## Root Cause

A body-generation race under concurrent load causes the backend to occasionally emit a `Content-Length` header that doesn't match the body it actually sends, causing the client to read exactly `Content-Length` bytes and stop mid-JSON-object.

## Immediate Mitigation

Add client-side retry-on-parse-failure for this specific endpoint while the backend race is fixed.

## Permanent Fix

Fix the backend's body-generation race so `Content-Length` always reflects the actual body sent, restoring the header's core purpose: telling a byte-stream-based client exactly where the message ends.

## Alternatives Considered

Switching the client to chunked-transfer-tolerant parsing regardless of `Content-Length` — rejected as treating the symptom; a correct `Content-Length` is a basic HTTP correctness requirement the backend should meet regardless of client-side workarounds.

## Trade-offs

None meaningful — the backend fix restores correct protocol behavior with no functional trade-off.

## Prevention

Load-test response-generation code paths specifically for header/body consistency under concurrency, not just functional correctness at low load.

## Monitoring and Alerts

- Client-side `JSON.parse` failure-rate monitoring correlated against backend load metrics, surfacing this exact class of intermittent, load-dependent failure.
- A backend-side integration test exercising concurrent request generation specifically to catch header/body mismatches before they reach production.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent investigation.

- **Situation:** a frontend's JSON parsing intermittently failed, correlated with backend load.
- **Task:** find the root cause without assuming it was a frontend bug.
- **Action:** captured a `curl -v` transcript during a load spike, isolating the failure to a mismatched `Content-Length` header rather than any frontend parsing logic.
- **Result:** identified and fixed a backend body-generation race, restoring correct response framing under load.

## Staff-Level Discussion

Understanding what `Content-Length` is actually for — telling a byte-stream-based client exactly where the message ends — is the difference between debugging the real cause and debugging the wrong layer entirely. The organizational lesson is that a symptom visible on the frontend (a parse failure) doesn't imply a frontend root cause; a raw protocol-level inspection (`curl -v`) can rule out an entire layer of hypotheses quickly and cheaply before deeper investigation begins.

## Related Handbook Chapters

- [How the Web Works: HTML, CSS, DOM, and HTTP](../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md) — the canonical `Content-Length` mechanics behind this incident.
