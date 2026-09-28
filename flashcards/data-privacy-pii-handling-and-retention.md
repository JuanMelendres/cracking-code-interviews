---
title: "Flashcards: Data Privacy — PII Handling, Retention, and Erasure"
slug: data-privacy-pii-handling-and-retention
document_type: flashcard-deck
domain: 12-security
topic_id: T-2437
canonical: ../syllabus/12-security/data-privacy-pii-handling-and-retention.md
last_updated: 2026-09-28
---

# Flashcards: Data Privacy — PII Handling, Retention, and Erasure

**Canonical chapter:** [`syllabus/12-security/data-privacy-pii-handling-and-retention.md`](../syllabus/12-security/data-privacy-pii-handling-and-retention.md)

## Card: Pseudonymisation vs. anonymisation

**Prompt:**
What is the difference, and which one takes data out of regulatory scope?

**Answer:**
Pseudonymisation replaces an identifier with a stable reference while a mapping back to the identity still exists somewhere — it reduces blast radius and keeps joins working, and the data is **still personal data**. Anonymisation means no one can re-identify the subject, which normally requires losing row-level granularity (aggregation, k-anonymity, calibrated noise). Only anonymisation leaves scope.

**Why it matters:**
Teams routinely believe a transformation moved data out of scope when it only reduced risk.

**Common trap:**
Treating encryption at rest as anonymisation — it protects against stolen media, not against a compromised application.

**Related:**
[Data Privacy: PII Handling, Retention, and Erasure](../syllabus/12-security/data-privacy-pii-handling-and-retention.md)

## Card: Why a hashed email is not anonymous

**Prompt:**
Your analytics export replaces emails with SHA-256 hashes. Is it anonymous?

**Answer:**
No — demonstrated by recovery: guessing candidate addresses and hashing them produced `MATCH -- identity recovered`. The value space of email addresses is small enough to enumerate or to test against a customer list you already hold. A keyed hash (HMAC) stops an attacker without the key, and is still pseudonymisation because the key holder can re-identify.

**Why it matters:**
It is the single most common wrong answer on this topic, and it has a one-sentence refutation.

**Common trap:**
Ignoring quasi-identifiers — timestamps plus coarse location are frequently unique to one person even with a perfect pseudonym.

**Related:**
[Applied Cryptography](../syllabus/12-security/applied-cryptography-hashing-signing-tls.md)

## Card: Erasure where deletion is impossible

**Prompt:**
A user requests erasure. The data is in immutable backups and an append-only event log. Now what?

**Answer:**
Crypto-shredding: encrypt each subject's data with a per-subject key and erase by **destroying the key**. Demonstrated directly — two independently-stored ciphertexts became permanently unreadable the moment one key was removed, with neither storage system touched. The ciphertext remains physically present and is inert.

**Why it matters:**
It is the only design that reconciles "delete my data" with "we keep an immutable audit log."

**Common trap:**
Forgetting the cost: key management becomes availability-critical, because losing a key is indistinguishable from erasing a subject.

**Related:**
[Secrets Management and Key Rotation](../syllabus/12-security/secrets-management-and-key-rotation.md)

## Card: The copies an erasure request must reach

**Prompt:**
Beyond the primary database, where does a user's personal data live?

**Answer:**
Read replicas, backups, the event log or event store, search indexes, caches, the data warehouse, log files, and third-party processors. Backups and append-only event logs **cannot delete a single record**, which is exactly what crypto-shredding is for; log files are rarely selectively deletable, which is why the real answer there is not to log it at all.

**Why it matters:**
`DELETE FROM users` is the naive answer, and the gap between it and this list is the whole interview signal.

**Common trap:**
Forgetting third parties — their copies are still your obligation.

**Related:**
[Structured Logging, Correlation IDs, and Log Hygiene](../syllabus/13-observability/structured-logging-correlation-ids-and-log-hygiene.md)

## Card: Making retention actually hold

**Prompt:**
Why do retention policies implemented as a cleanup script fail, and what replaces them?

**Answer:**
A script gets disabled during an incident, misses tables added after it was written, and is silent when it does not run — nothing fails. The durable version puts retention in the data model: an explicit period per table holding personal data, a column the policy acts on, continuous deletion, an alert on the **absence** of deletions, and a stated period required for every new table at review time.

**Why it matters:**
Retention bounds breach blast radius, not just compliance exposure.

**Common trap:**
Applying retention only to the primary database while the warehouse and event log keep everything for years.

**Related:**
[Data Privacy: PII Handling, Retention, and Erasure](../syllabus/12-security/data-privacy-pii-handling-and-retention.md)
