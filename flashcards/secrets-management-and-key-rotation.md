---
title: "Flashcards: Secrets Management and Key Rotation"
slug: secrets-management-and-key-rotation
document_type: flashcard-deck
domain: security
topic_id: T-1304
canonical: ../handbook/security/secrets-management-and-key-rotation.md
last_updated: 2026-08-06
---

# Flashcards: Secrets Management and Key Rotation

**Canonical chapter:** [`handbook/security/secrets-management-and-key-rotation.md`](../handbook/security/secrets-management-and-key-rotation.md)

## Card: Why naive key rotation breaks existing encrypted data

**Prompt:**
Why does naive key rotation (just replace the key) break existing encrypted data?

**Answer:**
There's no way to know which key protected which record without an explicit tag — envelope encryption's per-record key-version tag solves this.

**Why it matters:**
The precise mechanism that makes key-version tagging a prerequisite for safe rotation, not an optional refinement.

**Common trap:**
Rotating the active key without first ensuring every record is tagged with the key version that encrypted it.

**Related:**
[handbook/security/secrets-management-and-key-rotation.md](../handbook/security/secrets-management-and-key-rotation.md)

## Card: What must happen before deleting an old key

**Prompt:**
What must happen before an old key can be safely deleted after rotation?

**Answer:**
A re-encryption sweep of every record still tagged with that key version must complete and be verified — deleting first is a permanent data-loss event for any remaining dependent records.

**Why it matters:**
The concrete, sequenced safety check that separates a safe rotation from an irreversible mistake.

**Common trap:**
Deleting the old key on a fixed schedule without first verifying the re-encryption sweep actually completed.

**Related:**
[handbook/security/secrets-management-and-key-rotation.md](../handbook/security/secrets-management-and-key-rotation.md)

## Card: Two reasons to rotate keys proactively

**Prompt:**
Name two reasons to rotate keys proactively, absent any known compromise.

**Answer:**
Limiting the volume of data protected by a single key (bounding future blast radius) and satisfying compliance-mandated rotation schedules.

**Why it matters:**
Reframes rotation as a standing risk-reduction practice, not merely an incident-response reaction.

**Common trap:**
Treating "no known compromise" as sufficient justification to skip rotation entirely.

**Related:**
[handbook/security/secrets-management-and-key-rotation.md](../handbook/security/secrets-management-and-key-rotation.md)
