---
title: "Cheat Sheet: Search and Indexing Systems"
slug: search-and-indexing-systems
document_type: cheat-sheet
domain: system-design
topic_id: T-810
canonical: ../handbook/system-design/search-and-indexing-systems.md
last_updated: 2026-09-01
---

# Search and Indexing Systems

**Canonical chapter:** [`handbook/system-design/search-and-indexing-systems.md`](../handbook/system-design/search-and-indexing-systems.md)

## Core Mental Model

A normal database index answers "give me the row for this exact key" fast. Full-text search needs a different question answered fast: "give me every document containing this word," ranked by relevance — a question a B-tree on a text column can't answer efficiently, because the word could be anywhere inside the text. An **inverted index** flips the natural document → words mapping: look up the word once and get back the list of documents that contain it, already computed. Once you have that list, **relevance scoring** (TF-IDF, then BM25) answers which matching documents are probably what the searcher actually wanted.

## Essential Definitions

- **Inverted index** — a mapping from each term to a *postings list* of documents (and typically frequency/position) that contain it — the reverse of document → terms.
- **TF-IDF** — term frequency weighted by inverse document frequency; common terms contribute less, distinctive terms more. No length normalization.
- **BM25** — TF-IDF plus two real corrections: term-frequency saturation (diminishing returns for repeats) and document-length normalization (discounts naturally-higher counts in longer documents). The actual default in Elasticsearch/Lucene.
- **`LIKE '%term%'`** — cannot use a standard B-tree index at all (no prefix to binary-search from); forces a real sequential scan.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Is "contains"-style matching needed against a table already in a relational database? | Try the database's native full-text search (`tsvector`/GIN) first |
| Does the feature need advanced relevance tuning, fuzzy matching, faceting, very high query volume? | A dedicated search engine (Elasticsearch/OpenSearch) |
| Is the search index derived from data owned by another service/system of record? | Design the sync mechanism (CDC, events) and its real staleness window explicitly |
| Is `LIKE '%term%'` used against a table expected to keep growing? | Replace it with a real inverted index before it becomes an incident |

**Trade-offs:**

| Approach | Real query mechanism | Relevance ranking |
|---|---|---|
| `LIKE '%term%'` | Sequential scan | None |
| Database-native FTS (`tsvector`/GIN) | Real inverted index, in the existing DB | `ts_rank` (built-in) |
| Dedicated search engine | Real inverted index, purpose-built | BM25 (tunable) |

## Key Numbers (real, executed PostgreSQL 16 in Docker + from-scratch Java index)

TF-IDF vs. BM25 ranking swap (doc 2 is long, keyword-stuffed, mostly off-topic):

```
TF-IDF: doc 1 score=3.5835, doc 2 score=2.8277 (rank #2), doc 3 score=1.7918
BM25:   doc 1 score=3.8284, doc 3 score=1.8134, doc 2 score=1.7191 (drops to rank #3)
```

`LIKE` vs. GIN index, real `EXPLAIN ANALYZE`:

```
LIKE '%java%':  Parallel Seq Scan, Rows Removed by Filter: 66668, Execution Time: 9.882 ms
tsquery (GIN):  Bitmap Index Scan, rows=3, Execution Time: 0.037 ms
```

A real ~270x measured speedup for the identical logical question.

## Common Pitfalls

- Reaching for a dedicated search engine before checking whether the database's own native full-text search is sufficient.
- Using `LIKE '%term%'` for a feature expected to scale, with no plan to revisit before it becomes a production incident.
- Assuming a search index will always be fully consistent with its source data, ignoring the real staleness window its sync mechanism introduces.
- Assuming plain TF-IDF and BM25 are interchangeable — BM25's corrections produce a real, measurable ranking difference.

## Interview Answer Skeleton

**30-sec:** An inverted index maps terms to documents containing them, turning "find documents with this word" into a lookup instead of a scan. TF-IDF scores relevance by term frequency weighted by rarity; BM25 adds real length normalization and TF saturation. `LIKE '%term%'` can't use a standard index at all.

**2-min:** Add the measured sequential-scan-touching-all-rows result for `LIKE`, the real ~270x speedup via `tsvector`/GIN, and the measured BM25-vs-TF-IDF ranking swap demoting a long, keyword-stuffed but off-topic document.

**Whiteboard:** Draw a "forward" table (documents → word lists), then an arrow flipping it into an "inverted" table (words → document ID lists) — label the arrow "the actual index." Add a magnifying glass over one document with two numbers: term frequency (TF) and how many documents total contain the word (IDF); note BM25 as "same idea, plus a discount for very long documents."

**Staff-level framing:** Give a calibrated database-native-FTS-vs-dedicated-search-engine recommendation grounded in actual relevance and scale needs, and discuss the real staleness-window design question for any search index synced from a separate system of record.

## Production Warning Signs

- Product-search latency growing roughly linearly with catalog size, eventually causing timeouts — `EXPLAIN ANALYZE` showing a sequential scan; fix with a real `tsvector`/GIN index, not more hardware.
- A search index returning stale results after a write to the system of record — check the sync mechanism's real lag (CDC consumer lag, batch job schedule).
- Unexpectedly poor relevance ranking despite a working index — check whether length normalization is applied at all, or whether the tokenizer/analyzer suits the content's language.

## Related

- `handbook/system-design/storage-selection-tradeoffs.md`
- `handbook/databases/index-structures-btree-composite-covering.md`
- `handbook/system-design/messaging-patterns-and-change-data-capture.md`
