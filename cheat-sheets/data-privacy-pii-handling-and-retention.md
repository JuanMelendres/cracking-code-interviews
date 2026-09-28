---
title: "Cheat Sheet: Data Privacy — PII Handling, Retention, and Erasure"
slug: data-privacy-pii-handling-and-retention
document_type: cheat-sheet
domain: 12-security
topic_id: T-2437
canonical: ../syllabus/12-security/data-privacy-pii-handling-and-retention.md
last_updated: 2026-09-28
---

# Data Privacy: PII Handling, Retention, and Erasure

**Canonical chapter:** [`syllabus/12-security/data-privacy-pii-handling-and-retention.md`](../syllabus/12-security/data-privacy-pii-handling-and-retention.md)

## Core Mental Model

Personal data is hazardous material with a licence and an expiry date. The cheapest data to protect, retain, and erase is the data you never collected.

## What Each Technique Buys

| Technique | Gives you | Still personal data? |
|---|---|---|
| Pseudonymisation (stable ref) | Smaller blast radius; joins work | **Yes** |
| Unsalted hash | Very little — enumerable | **Yes** |
| Keyed hash (HMAC) | Attacker cannot reverse it | **Yes** |
| Encryption at rest | Protects against stolen media | **Yes** |
| Crypto-shredding | Erasure where deletion is impossible | Until the key dies |
| Aggregation / k-anonymity | Genuine anonymisation | **No** |

Only the last row leaves scope, and it costs row-level detail.

## Erasure Is Distributed

| Location | Can delete a row? | Approach |
|---|---|---|
| Primary DB | Yes | Delete or shred |
| Backups | **No** | Crypto-shred, or expire |
| Event log | **No** (append-only) | Crypto-shred, or keep PII out of events |
| Search index | Yes | Delete + verify |
| Cache | Yes / TTL | Invalidate |
| Warehouse | Expensively | Propagate |
| Log files | Rarely | Do not log it; bound retention |
| Processors | Their API | Contract + actual call |

## Crypto-Shredding

Per-subject DEK → encrypt that subject's fields → erase by **destroying the key**. Ciphertext stays, becomes inert.

Three real costs: key management becomes availability-critical (losing a key = erasing a subject); key cardinality grows with subjects; encrypted fields are **not queryable**, which pushes queryable data toward references.

## Retention

Belongs in the **schema**, not a cron job: explicit period per table, a column the policy acts on, continuous deletion, and an alert on the **absence** of deletions. New tables state a period at review time.

## Common Pitfalls

- "We hashed it, so it's anonymous" — the demo recovers the identity by guessing.
- `DELETE FROM users` as the whole erasure design.
- Confusing encryption at rest with access control.
- Forgetting third-party processors.
- Assuming pseudonymised data is out of scope. It is explicitly not.

## Interview Answer Skeleton

**30-sec:** Minimise, bound retention, protect, and be able to remove. Removal is the hard part — backups and append-only logs cannot delete a row — which is what crypto-shredding solves: destroy the per-subject key and every stored ciphertext becomes permanently unreadable.

## Related

- [Applied Cryptography](../syllabus/12-security/applied-cryptography-hashing-signing-tls.md)
- [Secrets Management and Key Rotation](../syllabus/12-security/secrets-management-and-key-rotation.md)
- [Structured Logging, Correlation IDs, and Log Hygiene](../syllabus/13-observability/structured-logging-correlation-ids-and-log-hygiene.md)
