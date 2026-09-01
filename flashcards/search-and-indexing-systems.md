---
title: "Flashcards: Search and Indexing Systems"
slug: search-and-indexing-systems
document_type: flashcard-deck
domain: system-design
topic_id: T-810
canonical: ../handbook/system-design/search-and-indexing-systems.md
last_updated: 2026-09-01
---

# Flashcards: Search and Indexing Systems

**Canonical chapter:** [`handbook/system-design/search-and-indexing-systems.md`](../handbook/system-design/search-and-indexing-systems.md)

## Card: Why does an inverted index make term search fast?

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
[handbook/system-design/search-and-indexing-systems.md](../handbook/system-design/search-and-indexing-systems.md)

## Card: What does BM25 add over plain TF-IDF?

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
[handbook/system-design/search-and-indexing-systems.md](../handbook/system-design/search-and-indexing-systems.md)

## Card: Why did a LIKE '%term%' search feature degrade as the catalog grew?

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
[handbook/system-design/search-and-indexing-systems.md](../handbook/system-design/search-and-indexing-systems.md)
