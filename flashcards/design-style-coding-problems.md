---
title: "Flashcards: Design-Style Coding Problems (LRU, LFU, Iterators)"
slug: design-style-coding-problems
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: T-2115
canonical: ../syllabus/03-data-structures-algorithms/design-style-coding-problems.md
last_updated: 2026-09-07
---

# Flashcards: Design-Style Coding Problems (LRU, LFU, Iterators)

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/design-style-coding-problems.md`](../syllabus/03-data-structures-algorithms/design-style-coding-problems.md)

## Card: The core cache-design composition pattern

**Prompt:**
What is the single most common composition pattern behind design-style problems like LRU and LFU caches?

**Answer:**
A hash map from key to node, plus a linked structure maintaining some order among those same nodes — the hash map gives O(1) lookup by key; the linked structure gives O(1) reordering or removal once a node is found, without searching for it again.

**Why it matters:**
A design that achieves O(1) `get` but leaves `put` at O(n) has usually missed the actual point of the exercise — every required operation must hit its target complexity simultaneously.

**Common trap:**
Optimizing one required operation while overlooking another's stated complexity requirement.

**Related:**
[syllabus/03-data-structures-algorithms/design-style-coding-problems.md](../syllabus/03-data-structures-algorithms/design-style-coding-problems.md)

## Card: LFU Cache's minFreq invariant

**Prompt:**
Why does LFU Cache's `minFreq` pointer only ever need incrementing or resetting to 1, never a full rescan?

**Answer:**
An entry's frequency only ever increases on access, never decreases. So `minFreq` only needs incrementing (when the bucket at the current `minFreq` becomes empty) or resetting to 1 (a brand-new key always starts at frequency 1, which is always ≤ the current `minFreq`).

**Why it matters:**
Without this invariant, finding the globally least-frequent bucket on every eviction would require scanning all frequency buckets, breaking the O(1) bound for `get` and `put`.

**Common trap:**
Using a plain `HashSet` instead of `LinkedHashSet` for the per-frequency bucket, silently losing the LRU-within-frequency tiebreak since a plain `HashSet` has no defined iteration order.

**Related:**
[syllabus/03-data-structures-algorithms/design-style-coding-problems.md](../syllabus/03-data-structures-algorithms/design-style-coding-problems.md)

## Card: Design Twitter's bounded per-source heap input

**Prompt:**
Why is it safe for Design Twitter's `getNewsFeed` to feed only each followee's last 10 tweets into the merge heap, rather than their entire history?

**Answer:**
The final result can contain at most 10 tweets total, drawn from however many followees exist. None of a followee's older tweets could ever displace a different followee's more recent tweet in a top-10 result, since at most 10 slots exist regardless of followee count.

**Why it matters:**
This keeps the heap's total size at O(10 · f) instead of O(total tweet history across all followees) — a significant difference for a user following many prolific accounts.

**Common trap:**
Feeding a followee's entire tweet history into the heap "to be safe," which is provably wasteful.

**Related:**
[syllabus/03-data-structures-algorithms/design-style-coding-problems.md](../syllabus/03-data-structures-algorithms/design-style-coding-problems.md)

## Card: Browser History's structure-collapsing insight

**Prompt:**
Why can Design Browser History replace a two-stack design with a single list plus a movable pointer?

**Answer:**
"Forward history" is definitionally just the suffix of the list past the current pointer — discarding it on a new `visit()` is a single `clear()` operation, rather than manually popping from one stack and pushing to another on every navigation call.

**Why it matters:**
Recognizing when a problem's apparent two-structure shape collapses into one simpler structure is a real, transferable design instinct.

**Common trap:**
Defaulting to the two-stack design reflexively for any "history/navigation" problem, purely because it's the more commonly taught textbook answer.

**Related:**
[syllabus/03-data-structures-algorithms/design-style-coding-problems.md](../syllabus/03-data-structures-algorithms/design-style-coding-problems.md)

## Card: A "rate limiter" name doesn't imply thread-safety

**Prompt:**
Why does Logger Rate Limiter not require thread-safety primitives, despite sounding like it might?

**Answer:**
It is LeetCode-tagged plain "Design," not "Concurrency" — its actual difficulty is entirely in the data model (one last-seen timestamp per distinct message), not synchronization.

**Why it matters:**
Correctly judging that a "rate limiter"- or "queue"-sounding problem doesn't automatically imply concurrency concerns is itself a signal of judgment, distinct from this domain's genuinely thread-safe sibling pattern.

**Common trap:**
Assuming any problem with "rate limiter," "queue," or "cache" in its name requires thread-safety primitives, and adding unnecessary synchronization as a design misstep.

**Related:**
[syllabus/03-data-structures-algorithms/design-style-coding-problems.md](../syllabus/03-data-structures-algorithms/design-style-coding-problems.md)
