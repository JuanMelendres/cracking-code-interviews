---
title: "Cheat Sheet: Java File I/O and NIO.2"
slug: java-file-io-and-nio2
document_type: cheat-sheet
domain: 02-java/language-core
topic_id: T-2429
canonical: ../syllabus/02-java/language-core/java-file-io-and-nio2.md
last_updated: 2026-09-26
---

# Java File I/O and NIO.2

**Canonical chapter:** [`syllabus/02-java/language-core/java-file-io-and-nio2.md`](../syllabus/02-java/language-core/java-file-io-and-nio2.md)

## Core Mental Model

Ask two questions for any I/O code: is this actually text, and what charset — stated explicitly, never assumed? And if something throws mid-operation, does every resource still close, in the right order, without silently losing the original exception?

## Essential Definitions

- **`InputStream`/`OutputStream`** — byte-oriented, for binary data.
- **`Reader`/`Writer`** — character-oriented, for text; requires a `Charset` to translate to/from bytes.
- **`BufferedReader`/`BufferedWriter`** — wraps a stream to batch real system calls into large chunks.
- **`Path`/`Files` (NIO.2, Java 7+)** — the modern replacement for `java.io.File`'s filesystem-metadata operations; throws real, specific exceptions instead of returning `false`/`-1`.

## Decision Table

| Symptom | Likely cause | Fix |
|---|---|---|
| Non-ASCII text reads back garbled, no exception | Text stream built without an explicit `Charset` | Pass `StandardCharsets.UTF_8` explicitly, every call |
| I/O slower than expected on a moderate file | Raw, unbuffered `FileReader`/`FileWriter` | Wrap in `BufferedReader`/`BufferedWriter`, or use `Files.newBuffered...` |
| A real bug seems to have "disappeared" | Manual `finally` block's `close()` exception silently replaced the original | Use `try`-with-resources; check `getSuppressed()` |
| `File.delete()` returns `false`, no detail | `java.io.File`'s boolean-only failure signal | Use NIO.2's `Files.delete()`, which throws a specific exception |

## Common Pitfalls

- Constructing a text stream with no explicit `Charset` — real, measured silent corruption, not a style nit.
- Skipping buffering for anything beyond a handful of reads/writes — a real, measured 4.6x cost in this chapter's own demo.
- A manual `finally` block instead of `try`-with-resources — silently discards the original exception if `close()` also throws.

## Interview Answer Skeleton

**30-sec:** A "text file" is bytes plus a charset decision — get the charset wrong and you get silent, wrong output, not an exception. Buffering exists to amortize real syscall overhead. `try`-with-resources preserves a `close()` failure as a suppressed exception; a manual `finally` block can silently discard it instead.

**2-min:** Add: measured 4.6x buffered-vs-unbuffered speedup; a real UTF-8-vs-ISO-8859-1 mismatch producing genuine mojibake with zero exception thrown; NIO.2's `Path`/`Files` as the modern default over `File`.

**Staff-level framing:** treat any charset-less text-stream constructor as a standing code-review flag — implicit, environment-dependent defaults (charset, locale, time zone) are a real, recurring production-risk category, not a one-off bug.

## Related

- syllabus/02-java/language-core/java-time-api.md
- syllabus/16-performance-jvm/memory-mapped-files-and-zero-copy-io.md
