---
title: "Search and Indexing Systems"
slug: search-and-indexing-systems
document_type: handbook-chapter
domain: 11-system-design
status: draft
version: 1.0
last_updated: 2026-09-04
source_history:
  - handbook/system-design/search-and-indexing-systems.md
topic_id: T-810
mastery_levels_covered:
  - L1
  - L2
  - L3
  - L4
difficulty:
  - intermediate
  - advanced
target_levels:
  - senior
  - staff
estimated_reading_minutes: 40
prerequisites:
  - storage-selection-tradeoffs.md
related:
  - storage-selection-tradeoffs.md
  - ../09-messaging-event-driven/messaging-patterns-and-change-data-capture.md
  - ../06-databases/index-structures-btree-composite-covering.md
  - ../17-architecture/cqrs-read-write-separation.md
  - ../../practice/java/system-design/search-and-indexing-systems/README.md
  - ../../practice/sql/search-and-indexing-systems/README.md
official_references:
  - https://www.postgresql.org/docs/current/textsearch.html
  - https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-similarity.html
  - https://en.wikipedia.org/wiki/Okapi_BM25
---

# Search and Indexing Systems

> **Topic register:** T-810 (Search & indexing systems, IWI 5.8) · Advanced tier · Moderate interview frequency
> **Provenance:** every postings list, score, and query-plan line in this
> chapter's Java Examples section is real, executed output — a real,
> from-scratch inverted index and BM25/TF-IDF scorers proving a genuine
> ranking difference between the two, and a real PostgreSQL 16 in Docker
> proving a `tsvector`/GIN index is a structurally different execution
> strategy from `LIKE '%...%'`, not just a faster version of the same plan.
> Reproducible source:
> [`practice/java/system-design/search-and-indexing-systems/`](../../practice/java/system-design/search-and-indexing-systems/README.md)
> and
> [`practice/sql/search-and-indexing-systems/`](../../practice/sql/search-and-indexing-systems/README.md).

## Table of Contents

1. [Learning Objectives](#learning-objectives)
2. [Why This Matters in Interviews](#why-this-matters-in-interviews)
3. [Level 1 — Foundation](#level-1--foundation)
4. [Level 2 — Working Knowledge](#level-2--working-knowledge)
5. [Mental Model](#mental-model)
6. [Definition and Purpose](#definition-and-purpose)
7. [Core Concepts](#core-concepts)
8. [Internal Implementation](#internal-implementation)
9. [Diagrams](#diagrams)
10. [Java Examples](#java-examples)
11. [Production Scenarios](#production-scenarios)
12. [Failure Modes and Debugging](#failure-modes-and-debugging)
13. [Trade-offs](#trade-offs)
14. [Decision Framework](#decision-framework)
15. [Comparisons](#comparisons)
16. [Common Mistakes](#common-mistakes)
17. [Anti-Patterns](#anti-patterns)
18. [Best Practices](#best-practices)
19. [Interview Answer Framework](#interview-answer-framework)
20. [Interview Questions](#interview-questions)
21. [Summary](#summary)
22. [Key Takeaways](#key-takeaways)
23. [Cheat Sheet](#cheat-sheet)
24. [Flashcards](#flashcards)
25. [Practice Exercises](#practice-exercises)
26. [Solutions](#solutions)
27. [Additional Reading](#additional-reading)
28. [Official References](#official-references)

## Learning Objectives

After this chapter you should be able to:

- Explain an inverted index precisely — term → postings list — and why it
  makes "which documents contain this term" a lookup instead of a scan.
- Explain TF-IDF and BM25 well enough to compute a real score by hand, and
  articulate exactly what BM25 corrects that plain TF-IDF doesn't.
- Explain why `LIKE '%term%'` cannot use a standard index and reproduce the
  real query-plan and performance difference against an inverted index.
- Reason about when a database's native full-text search is enough and when
  a dedicated search engine (Elasticsearch/OpenSearch) earns its added
  complexity.
- Explain how a search index is kept in sync with a system of record, and
  the real staleness-window trade-off that introduces.

## Why This Matters in Interviews

Search and indexing questions test whether a candidate understands *why*
full-text search needs different infrastructure than a normal indexed
lookup, not just that "Elasticsearch is for search." The inverted index
itself is a small, teachable data structure — a candidate who can derive it
from first principles ("if I need fast term lookup, what if I flip document
→ terms into term → documents?") demonstrates real algorithmic reasoning, the
same muscle system-design interviews are built to probe. It's also a
genuinely practical production topic: keeping a search index in sync with a
system of record is a real, common architecture (this repository's own CDC
chapter already references "a search index kept in sync via CDC" as a
recurring pattern), and knowing the real trade-offs — staleness window,
dual-write risk, when a database's own full-text search is simply enough —
is exactly the kind of calibrated judgment Staff interviews look for over
reflexively reaching for a dedicated search cluster.

## Level 1 — Foundation

Imagine trying to find every book in a library that mentions "dragons" by walking down every aisle and flipping through every single book — that's slow and gets slower as the library grows. Now imagine instead the library keeps a separate master list: for every word that appears anywhere in any book, there's a card listing exactly which books contain it. Look up "dragons" on that list, and you instantly get the exact set of books — no walking the aisles at all. That master list, word → books, is an **inverted index** (inverted because it flips the natural book → words direction around), and it's the entire reason full-text search can be fast at any scale.

Once you have a list of books that all mention "dragons," you still need to decide which one the searcher probably actually wants. **TF-IDF** ranks by two things: how often the word appears in a given book (mentioning "dragons" fifty times probably means it's more relevant than mentioning it once), and how rare that word is across the whole library (a word like "the" appearing in every book tells you nothing distinctive, so it counts for less). **BM25** is a refined version of that same idea with two real corrections: it stops giving extra credit for repeating a word past a certain point, and it discounts raw word counts in very long books, since a 900-page book naturally mentions any word more times than a 10-page pamphlet, for reasons that have nothing to do with actual relevance.

## Level 2 — Working Knowledge

At this level you should be able to explain precisely why a normal database index can't help with a "contains this word anywhere" search. A standard index is like a library's card catalog sorted alphabetically by title — great for finding an exact title fast, useless for "which books mention dragons somewhere inside," since the word could be anywhere in the text with no fixed starting point to search from. This is exactly why a query like `LIKE '%term%'` forces the database to check every single row — there's no shortcut available without a genuinely different structure.

You should also be comfortable with the practical, working recommendation most systems should follow: start with your existing relational database's own built-in full-text search (PostgreSQL's `tsvector`/GIN index is a real, production-grade inverted index) before reaching for a dedicated search engine like Elasticsearch. Standing up a whole separate search cluster is real, ongoing operational overhead — worth it once you actually hit a specific limitation (advanced relevance tuning, fuzzy matching, very high query volume), but often unnecessary for a system's actual, current needs.

Practically, if a search feature is built on `LIKE '%term%'` against a table that's expected to keep growing, that's a concrete, reviewable risk worth flagging now rather than after it becomes a production incident — the fix isn't a bigger database server, it's a structurally different index. And whenever a search index is kept in sync with a separate system of record (via CDC, a queue, or a batch job), that sync mechanism introduces a real staleness window worth naming explicitly, not an implementation detail to assume away.

## Mental Model

A normal database index answers "give me the row for this exact key" fast.
Full-text search needs a different question answered fast: "give me every
document containing this word," across potentially millions of documents,
ranked by how relevant each one is — a question a B-tree on a text column
can't answer efficiently, because the word could be anywhere inside the
text. An **inverted index** flips the natural document → words mapping
around: instead of scanning every document's words, look up the word once
and get back the list of documents that contain it, already computed. Once
you have that list, **relevance scoring** (TF-IDF, then BM25) is just the
question "given several documents that all technically match, which ones are
probably what the searcher actually wanted?" — answered by weighing how
distinctively and how appropriately-often the query terms appear in each
one, not just whether they appear at all.

## Definition and Purpose

An **inverted index** is a mapping from each term to a *postings list* of the
documents (and typically the term's frequency and position) that contain
it — the reverse of a "forward index" (document → terms). It exists because
full-text search fundamentally needs "find documents containing X" answered
in time proportional to the number of matches, not the number of documents.
**TF-IDF** (term frequency–inverse document frequency) scores a
document-query match by how often a query term appears in that document
(term frequency) weighted by how rare that term is across the whole
corpus (inverse document frequency) — common terms contribute less,
distinctive terms contribute more. **BM25** (Best Matching 25) is a refined
ranking function built on the same TF-IDF intuition, adding two real
corrections: **term-frequency saturation** (repeating a term many times
stops adding much extra score past a point) and **document-length
normalization** (a raw term count is naturally higher in a longer document
for reasons unrelated to relevance, so BM25 discounts for it) — it's the
actual default relevance function behind Elasticsearch and Lucene.

## Core Concepts

- **A postings list turns "which documents contain X" into a map lookup.**
  Proven directly: this chapter's own from-scratch inverted index answers a
  term lookup and a multi-term Boolean AND query via direct map access and
  set intersection, not a document scan.
- **`LIKE '%term%'` cannot use a standard B-tree index.** A leading wildcard
  means the database can't binary-search into the index at all — proven
  directly with a real `Parallel Seq Scan` touching all 200,006 real rows,
  versus a real `Bitmap Index Scan` for the equivalent inverted-index-backed
  query.
- **TF-IDF has no length normalization; BM25 does.** Proven directly: a
  long, keyword-stuffed but mostly off-topic document ranked artificially
  high under plain TF-IDF, and measurably dropped under BM25 once
  document-length normalization and term-frequency saturation were applied.
- **Relational databases have real, built-in inverted-index support.**
  PostgreSQL's `tsvector` + GIN index is a genuine, production-grade
  inverted index with real relevance scoring (`ts_rank`) — not every
  full-text search need requires a separate search engine.
- **A search index synced from a system of record introduces a real
  staleness window.** Whatever mechanism populates the index (CDC, a
  message queue, a batch job) determines how stale search results can be
  relative to the underlying data — a real, unavoidable trade-off, not an
  implementation detail to overlook.

## Internal Implementation

[`InvertedIndex.java`](../../practice/java/system-design/search-and-indexing-systems/InvertedIndex.java)
stores `Map<String, Map<Integer, Integer>>` (term → docId → term frequency)
plus per-document lengths, and answers a Boolean AND query via real
`Set.retainAll` intersection across postings lists.
[`TfIdfScorer.java`](../../practice/java/system-design/search-and-indexing-systems/TfIdfScorer.java)
implements classic log-dampened TF times `log(N/df)` IDF, with no length
term at all. [`Bm25Scorer.java`](../../practice/java/system-design/search-and-indexing-systems/Bm25Scorer.java)
implements the real Okapi BM25 formula (`k1=1.5`, `b=0.75`), including the
Robertson–Sparck Jones IDF variant and the document-length normalization
term `(1 - b + b * (docLen / avgDocLen))`. On the database side,
[`init/01-init.sql`](../../practice/sql/search-and-indexing-systems/init/01-init.sql)
uses a real, `GENERATED ALWAYS AS ... STORED` `tsvector` column — PostgreSQL
computing and storing the tokenized representation automatically on write —
backed by a real `GIN` index, which
[`fts-vs-like-demo.sh`](../../practice/sql/search-and-indexing-systems/fts-vs-like-demo.sh)
proves via real `EXPLAIN ANALYZE` output resolves as a `Bitmap Index Scan`
rather than a sequential scan.

## Diagrams

```mermaid
flowchart LR
    subgraph Forward Index
    D1["Doc 1: java, garbage, collection"]
    D2["Doc 2: python, garbage, collection"]
    end
    subgraph Inverted Index
    T1["java"] --> P1["[Doc 1]"]
    T2["garbage"] --> P2["[Doc 1, Doc 2]"]
    T3["collection"] --> P3["[Doc 1, Doc 2]"]
    T4["python"] --> P4["[Doc 2]"]
    end
    Forward Index -. "built once, inverted" .-> Inverted Index
```

## Java Examples

The real, decisive inverted-index and Boolean query result:

```
=== Real postings list for the term "java" ===
doc 1 ("GC Tuning Guide"): tf=1
doc 2 ("Keyword-Stuffed Long Page"): tf=4
doc 5 ("Java Concurrency Basics"): tf=1

=== Real Boolean AND query: "java" AND "garbage" AND "collection" ===
Real matching doc IDs: [1]
```

The real, decisive TF-IDF vs. BM25 ranking difference:

```
=== Real TF-IDF ranking (no length normalization) ===
  doc 1  score=3.5835  length=7  "GC Tuning Guide"
  doc 2  score=2.8277  length=20  "Keyword-Stuffed Long Page"
  doc 3  score=1.7918  length=8  "Python GC Internals"

=== Real BM25 ranking (length-normalized, TF-saturated) ===
  doc 1  score=3.8284  length=7  "GC Tuning Guide"
  doc 3  score=1.8134  length=8  "Python GC Internals"
  doc 2  score=1.7191  length=20  "Keyword-Stuffed Long Page"
```

Doc 2 — long, keyword-stuffed, mostly off-topic — drops from real rank #2 to
real rank #3 once BM25's length normalization and TF saturation are applied.

The real, decisive database-native inverted-index result:

```
=== LIKE '%java%' ===
   ->  Parallel Seq Scan on articles (actual time=5.236..8.205 rows=1 loops=3)
         Rows Removed by Filter: 66668
 Execution Time: 9.882 ms

=== tsquery against the real GIN index ===
   ->  Bitmap Index Scan on articles_search_idx (actual time=0.022..0.022 rows=3 loops=1)
         Index Cond: (search_vector @@ '''java'''::tsquery)
 Execution Time: 0.037 ms
```

A real ~270x measured speedup for the identical logical question, because
the two queries use genuinely different execution strategies, not just
different costs for the same one.

## Production Scenarios

**Scenario: a product-search feature built on `LIKE '%term%'` against the
primary database degraded badly as the catalog grew past a few hundred
thousand items.** *(Representative scenario, grounded directly in this
chapter's own measured `LIKE`-vs-GIN mechanism.)* Symptoms: product search
latency grew roughly linearly with catalog size, eventually causing
timeouts during peak traffic, while every other query against the same
database stayed fast. Initial hypothesis: the database needed more CPU or a
read replica. Evidence: `EXPLAIN ANALYZE` on the actual search query showed
a real sequential scan touching every row in the products table for every
search — exactly this chapter's own measured `LIKE '%java%'` behavior,
because the search query used a leading wildcard to support "contains"
matching, which no standard B-tree index can serve. Diagnosis: the search
feature had been built as an ordinary `WHERE description LIKE '%' || :term
|| '%'` query when the catalog was small enough that a full scan was still
fast — a design choice that stopped being appropriate once the catalog grew,
with no explicit revisit trigger. Immediate mitigation: added a read
replica to spread the scan cost, a stopgap that didn't address the
underlying O(n) behavior. Permanent remediation: added a real
`tsvector`/GIN index (this chapter's own proven mechanism) directly on the
existing PostgreSQL database — no new infrastructure needed, since the
catalog's relevance-ranking needs were modest enough that a dedicated search
engine wasn't yet justified. Trade-off accepted: `tsvector`'s tokenization
is less sophisticated than a dedicated search engine's (no fuzzy matching,
weaker relevance tuning), accepted because it was a real, measured
~270x-class improvement with zero new operational surface. Prevention:
added a review checklist item flagging any new `LIKE '%...%'` query against
a table expected to grow past a modest row count. Interview lesson: this is
the concrete production form of "match the tool to the actual query
pattern" — the fix wasn't more hardware, it was recognizing the query needed
a genuinely different index structure, not a faster version of the same
scan.

## Failure Modes and Debugging

- **A `LIKE '%term%'` query whose latency scales with table size** (this
  chapter's own production scenario) — debug signal: `EXPLAIN ANALYZE`
  shows a sequential scan with a high `Rows Removed by Filter` count; the
  fix is a real inverted index (`tsvector`/GIN or a dedicated search
  engine), not a bigger machine.
- **A search index returning stale results after a write to the system of
  record** — debug signal: check the sync mechanism's real lag (CDC
  consumer lag, batch job schedule) — this is the same staleness-window
  class of issue already covered for eventually-consistent read-scaling
  layers generally.
- **Unexpectedly poor relevance ranking despite a working index** — debug
  signal: check whether length normalization is being applied at all (raw
  TF-IDF without it can over-rank long, keyword-dense but off-topic
  documents, exactly as this chapter's own demo reproduces) or whether the
  tokenizer/analyzer is stemming/normalizing terms appropriately for the
  content's language.
- **A query planner unexpectedly choosing a sequential scan over an existing
  inverted index** — debug signal: verify the actual connection/session is
  reaching the intended database instance and that statistics are current;
  a real, honest discovery while building this chapter's own demo was that
  a misdirected connection (to a stale, about-to-restart server instance)
  can produce confusing, seemingly-inconsistent plan choices that have
  nothing to do with the index or statistics at all.

## Trade-offs

A database-native inverted index (PostgreSQL `tsvector`/GIN): no new
infrastructure, transactionally consistent with the data it indexes, real
measured performance gains for "contains"-style queries — at the cost of
less sophisticated relevance tuning and language analysis than a dedicated
search engine. A dedicated search engine (Elasticsearch/OpenSearch): far
more relevance-tuning power, built for horizontal scale, purpose-built
features (fuzzy matching, faceting, aggregations) — at the cost of a
genuinely separate system to operate, and a real staleness window versus the
system of record it's synced from. `LIKE '%term%'` with no index: zero
setup cost — at the cost of the real, unavoidable O(n) scan this chapter's
own production scenario demonstrates directly.

## Decision Framework

| Question | If yes, lean toward |
|---|---|
| Is "contains"-style matching needed against a table already living in a relational database? | Try the database's native full-text search (`tsvector`/GIN) first |
| Does the feature need advanced relevance tuning, fuzzy matching, faceted search, or very high query volume at scale? | A dedicated search engine (Elasticsearch/OpenSearch) |
| Is the search index derived from data owned by another service/system of record? | Design the sync mechanism (CDC, events) and its real staleness window explicitly |
| Is `LIKE '%term%'` currently used against a table expected to keep growing? | Replace it with a real inverted index before it becomes a production incident, per this chapter's own scenario |

## Comparisons

| Approach | Real query mechanism | Relevance ranking | Operational cost |
|---|---|---|---|
| `LIKE '%term%'` | Sequential scan | None | None (until it doesn't scale) |
| Database-native FTS (`tsvector`/GIN) | Real inverted index, in the existing DB | `ts_rank` (real, built-in) | Low — no new system |
| Dedicated search engine (Elasticsearch/OpenSearch) | Real inverted index, purpose-built | BM25 (real, tunable) | High — a separate, synced system |

## Common Mistakes

- Reaching for a dedicated search engine before checking whether the
  database's own native full-text search is sufficient.
- Using `LIKE '%term%'` for a feature expected to scale, without a plan to
  revisit before it becomes the production incident this chapter's scenario
  describes.
- Assuming a search index will always be fully consistent with its source
  data, without accounting for the real staleness window its sync mechanism
  introduces.
- Assuming plain TF-IDF and BM25 are interchangeable, when BM25's length
  normalization and TF saturation produce real, measurable ranking
  differences.

## Anti-Patterns

- **`LIKE '%...%'` against an ever-growing table with no index strategy** —
  the exact anti-pattern behind this chapter's production scenario; move to
  a real inverted index before scale forces the issue.
- **Standing up a dedicated search cluster before confirming the database's
  native full-text search is actually insufficient** — real, unnecessary
  operational overhead for a need that database-native FTS might already
  meet.
- **Treating a synced search index as a strongly-consistent read path** —
  ignores the real staleness window inherent to any sync mechanism.

## Best Practices

- Start with database-native full-text search for "contains"/basic
  relevance needs; graduate to a dedicated search engine only once a real,
  specific limitation (scale, relevance sophistication, feature need) is
  hit.
- Treat any `LIKE '%...%'` query against a growing table as a real,
  scheduled risk, not a permanent design decision.
- Design and document the real staleness window of any search index synced
  from a separate system of record.
- When relevance quality matters, prefer BM25 (or a database's equivalent)
  over plain TF-IDF, given its real, measurable correction for document
  length and term-frequency saturation.

## Interview Answer Framework

### 30-Second Answer

An inverted index maps terms to the documents containing them, turning "find
documents with this word" into a lookup instead of a scan. TF-IDF scores
relevance by term frequency weighted by rarity; BM25 improves on that with
real document-length normalization and term-frequency saturation. `LIKE
'%term%'` can't use a standard index at all, which is why full-text search
needs this different structure.

### 2-Minute Answer

A normal B-tree index can't answer "which documents contain this word"
efficiently, because the word could be anywhere in the text — that's why
`LIKE '%term%'` forces a real sequential scan, which I've measured directly
touching all 200,006 rows in a demo table. An inverted index flips the
problem: term → list of documents, so the same question becomes a map
lookup — I've built one from scratch and shown a Boolean AND query resolve
via real set intersection. Once you have matching documents, TF-IDF scores
relevance by term frequency weighted by inverse document frequency, but has
no length normalization — I've measured this directly producing an
artificially high ranking for a long, keyword-stuffed but off-topic
document. BM25 corrects that with real document-length normalization and
term-frequency saturation, and measurably re-ranks that same document lower.
On the database side, PostgreSQL's `tsvector`/GIN index is a real,
production-grade inverted index built into the database — I've measured a
real ~270x speedup switching a `LIKE` query to it, with zero new
infrastructure, which is often enough before a dedicated search engine like
Elasticsearch is actually justified.

### 10-Minute Deep Dive

Cover: the inverted-index data structure and real Boolean-query mechanism;
TF-IDF's formula and its lack of length normalization, measured directly;
BM25's two real corrections and the measured ranking swap they cause;
`LIKE`'s fundamental incompatibility with standard indexing, measured via a
real query plan and a real ~270x speedup switching to a GIN index; the
decision framework for database-native FTS versus a dedicated search
engine; and the production scenario connecting `LIKE`'s O(n) behavior
directly to a real scaling incident.

### Whiteboard Explanation

Draw a "forward" table: rows are documents, one column lists each
document's words. Draw an arrow flipping it into an "inverted" table: rows
are now words, each pointing to a list of document IDs — label that arrow
"the actual index." Then draw a magnifying glass over one document in the
inverted table's list with a number next to it (term frequency) and a
second number over the whole row (how many documents total contain this
word) — label those two numbers "TF" and "IDF," and note BM25 as "same idea,
plus a discount for very long documents."

### Production Example

Use the `LIKE`-scaling scenario from [Production Scenarios](#production-scenarios):
a product-search feature's latency grew with catalog size because of a
`LIKE '%term%'` sequential scan, fixed with a real `tsvector`/GIN index with
zero new infrastructure.

### Trade-offs to Mention

Database-native FTS's simplicity and consistency vs. a dedicated search
engine's relevance-tuning power and operational cost; TF-IDF's simplicity
vs. BM25's real, measurable ranking-quality improvement; a synced search
index's real staleness window as an unavoidable cost of the sync mechanism
chosen.

### Common Candidate Mistakes

Reaching for Elasticsearch reflexively without checking database-native FTS
first; describing `LIKE '%term%'` as "just slower" rather than
structurally unable to use a standard index; treating TF-IDF and BM25 as
interchangeable.

### Typical Follow-Up Questions

"Why can't a database use an index for `LIKE '%term%'`?" "What does BM25 fix
that TF-IDF doesn't?" "When would you reach for Elasticsearch instead of
your database's own full-text search?" "How would you keep a search index in
sync with your primary database, and what does that cost you?"

### Senior-Level Expectations

Correctly explain the inverted-index mechanism and why `LIKE '%term%'`
can't use a standard index, without prompting.

### Staff-Level Discussion

Give a calibrated database-native-FTS-versus-dedicated-search-engine
recommendation grounded in the feature's actual relevance and scale needs,
and discuss the real staleness-window design question for any search index
synced from a separate system of record, connecting it to this chapter's
own production scenario as a concrete example of matching infrastructure to
actual, measured need rather than assumed future scale.

## Interview Questions

### Question 1: Why can't a database use a standard index for `LIKE '%term%'`?

**Why interviewers ask it.** It tests whether a candidate understands the
structural reason full-text search needs different infrastructure, not just
that it's "slow."

**Expected answer.** A standard B-tree index is ordered and searchable from
a known prefix; a leading wildcard means the term could start anywhere in
the text, so there's no prefix to binary-search from — the database must
check every row, a real sequential scan.

**Minimum acceptable answer.** States that `LIKE '%term%'` is "slow" without
explaining why an index can't help.

**Strong Senior answer.** Explains the prefix-searchability reasoning
precisely and names an inverted index as the structural fix.

**Staff-level extension.** Connects this to a real production scaling
incident and the decision between database-native FTS and a dedicated
search engine as the fix.

**Common mistakes.** Assuming any index would help if the table just had
"the right" index added.

**Likely follow-ups.** "How does an inverted index solve this differently?"

**Evaluation criteria.** Correct prefix-searchability reasoning (3),
names the structural fix (2).

### Question 2: What does BM25 correct that plain TF-IDF doesn't?

**Why interviewers ask it.** It tests whether a candidate understands BM25 as
a real, specific improvement rather than a synonym for "better relevance."

**Expected answer.** BM25 adds document-length normalization (a raw term
count is naturally higher in a longer document, unrelated to relevance) and
term-frequency saturation (extra repeats of a term add diminishing score
past a point) — both real corrections plain TF-IDF lacks.

**Minimum acceptable answer.** States that BM25 is "more accurate" without
naming either specific correction.

**Strong Senior answer.** Names both corrections precisely, ideally with a
concrete example of a long, keyword-stuffed document being over-ranked
under plain TF-IDF.

**Staff-level extension.** Discusses how to verify relevance quality in
practice (measuring real ranking output against known-relevant documents,
as this chapter does directly) rather than assuming a scoring function
works as intended.

**Common mistakes.** Treating BM25 and TF-IDF as interchangeable synonyms
for "search ranking."

**Likely follow-ups.** "How would you tune BM25's k1 and b parameters for a
specific corpus?"

**Evaluation criteria.** Both real corrections named (3), verification
methodology at Staff level (2).

## Summary

An inverted index maps terms to the documents containing them, turning "find
documents containing this word" into a lookup — proven directly with a real,
from-scratch implementation answering a Boolean query via postings-list
intersection. `LIKE '%term%'` cannot use a standard index at all, proven
directly with a real sequential scan touching every row versus a real
~270x-faster `Bitmap Index Scan` against a `tsvector`/GIN index. TF-IDF
scores relevance without length normalization; BM25 adds real document-length
normalization and term-frequency saturation, proven directly with a measured
ranking swap that demotes a long, keyword-stuffed but off-topic document.
PostgreSQL's native full-text search is a real, production-grade inverted
index requiring no new infrastructure, often sufficient before a dedicated
search engine's added relevance-tuning power and operational cost are
actually justified.

## Key Takeaways

- An inverted index turns "which documents contain this term" into a map
  lookup — proven directly with a real Boolean AND query via set
  intersection.
- `LIKE '%term%'` cannot use a standard index — proven directly with a real
  sequential scan and a real ~270x speedup switching to an inverted index.
- BM25's length normalization and TF saturation produce a real, measurable
  ranking difference from plain TF-IDF — proven directly with a document
  that dropped from rank #2 to rank #3.
- PostgreSQL's `tsvector`/GIN index is a real, production-grade inverted
  index requiring no new infrastructure — often sufficient before a
  dedicated search engine is justified.
- A search index synced from a system of record carries a real staleness
  window determined by its sync mechanism — an unavoidable trade-off, not
  an implementation detail.

## Cheat Sheet

- **Inverted index**: term → postings list (documents + frequency) — the
  core structure behind all full-text search.
- **TF-IDF**: term frequency × inverse document frequency — no length
  normalization.
- **BM25**: TF-IDF plus real document-length normalization and TF
  saturation — Elasticsearch/Lucene's actual default.
- **`LIKE '%term%'`**: real sequential scan, can't use a standard index —
  fine at small scale, a real risk as tables grow.
- **PostgreSQL `tsvector`/GIN**: a real, built-in inverted index — try this
  before a dedicated search engine.
- **Search index sync**: always carries a real staleness window relative to
  its system of record.

## Flashcards

### Card: Why does an inverted index make term search fast?

**Prompt:**
Why is "find all documents containing this word" fast with an inverted
index but slow with a forward index (document → words)?

**Answer:**
An inverted index already stores term → documents directly, so the answer
is a single map lookup. A forward index would require checking every
document's word list individually. Measured directly: a real Boolean AND
query resolved via direct postings-list intersection, not a document scan.

**Why it matters:**
It's the entire structural reason full-text search needs different
infrastructure than a standard row-oriented index.

**Common trap:**
Assuming any index on a text column would make `LIKE '%term%'` fast.

**Related:**
[[search-and-indexing-systems]]

### Card: What does BM25 add over plain TF-IDF?

**Prompt:**
A long, keyword-stuffed but mostly off-topic document ranks #2 under plain
TF-IDF for a given query. What happens under BM25, and why?

**Answer:**
It drops to #3 — measured directly. BM25 adds document-length normalization
(discounting raw term counts that are naturally higher in longer documents)
and term-frequency saturation (diminishing returns for repeating a term many
times), both of which plain TF-IDF lacks.

**Why it matters:**
It's the real, measurable reason BM25 replaced plain TF-IDF as the default
ranking function in modern search engines.

**Common trap:**
Treating TF-IDF and BM25 as interchangeable "relevance scoring," with no
specific, nameable difference.

**Related:**
[[search-and-indexing-systems]]

### Card: Why did a `LIKE '%term%'` search feature degrade as the catalog grew?

**Prompt:**
A product-search feature built on `LIKE '%term%'` degraded badly as the
catalog grew. Why, and what's the real fix?

**Answer:**
`LIKE` with a leading wildcard can't use a standard index — every row must
be checked, a real sequential scan whose cost scales with table size.
Measured directly: a real ~270x speedup switching to a `tsvector`/GIN
index (a real, database-native inverted index), with zero new
infrastructure.

**Why it matters:**
It's the concrete, measured proof that the fix is a structurally different
index, not more hardware.

**Common trap:**
Assuming the fix is scaling the database vertically or adding a read
replica, rather than recognizing the query itself can't use standard
indexing at all.

**Related:**
[[search-and-indexing-systems]]

## Practice Exercises

1. Extend `InvertedIndex` with a real Boolean OR query (union of postings
   lists) alongside the existing AND query, and verify it against the
   sample corpus.
2. Add a real phrase-query capability (e.g., "garbage collection" as an
   exact adjacent phrase, not just both terms present) by storing term
   *positions* in the postings list, not just frequency — verify it
   correctly excludes a document containing both terms but not adjacently.
3. Extend the PostgreSQL demo with a second GIN index configuration using a
   different text search configuration (e.g., `simple` instead of
   `english`), and observe the real difference in tokenization (no
   stemming) via a query that only matches under one configuration.

## Solutions

Exercise 1 is a direct extension of `InvertedIndex.booleanAnd`, replacing
`retainAll` with `addAll` across postings lists; left as self-directed
practice since the existing method already isolates the exact pattern to
adapt. Exercise 2 requires changing the postings list's value type from a
plain term-frequency `Integer` to a structure holding real term positions,
and implementing adjacency checking across two terms' position lists; left
as self-directed practice as a genuinely more involved, open extension.
Exercise 3 is a configuration-only change to the existing `init/01-init.sql`
plus a comparison query; left as self-directed practice since the existing
demo already isolates the exact mechanism (`to_tsvector('english', ...)`) to
vary.

## Additional Reading

- PostgreSQL's Full Text Search chapter (see
  [Official References](#official-references)) is the authoritative source
  for `tsvector`/`tsquery` configuration options and text search
  configurations beyond this chapter's scope.
- [Storage Selection Trade-offs](storage-selection-tradeoffs.md) covers the
  broader question of choosing a data store for a given access pattern,
  which this chapter's database-native-FTS-versus-dedicated-search-engine
  decision is one specific instance of.
- [Messaging Patterns and Change Data Capture](../09-messaging-event-driven/messaging-patterns-and-change-data-capture.md)
  covers the real mechanism (CDC) most commonly used to keep a search index
  synced with a system of record, including the same staleness-window
  trade-off this chapter references.

## Official References

- PostgreSQL Documentation, [Full Text Search](https://www.postgresql.org/docs/current/textsearch.html)
- Elasticsearch Reference, [Similarity Module (BM25)](https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-similarity.html)
- Wikipedia, [Okapi BM25](https://en.wikipedia.org/wiki/Okapi_BM25)
