# Search and indexing systems: inverted index, TF-IDF, BM25 (T-810) — runnable verification

Real, executed Java 21 output backing
[`syllabus/11-system-design/search-and-indexing-systems.md`](../../../../syllabus/11-system-design/search-and-indexing-systems.md)
(T-810). No library, no Lucene/Elasticsearch — a real, from-scratch inverted
index, a real classic TF-IDF scorer, and a real BM25 scorer (the actual
ranking function behind Elasticsearch/Lucene's default relevance scoring),
run against the same corpus so their real behavior can be directly compared.
See also
[`practice/sql/search-and-indexing-systems/`](../../../sql/search-and-indexing-systems/README.md)
for the database-native side of this topic (PostgreSQL's `tsvector`/GIN
index).

## Files

- `Document.java`, `Tokenizer.java`, `SampleCorpus.java` — a small, real
  6-document corpus, deliberately including one short, precisely on-topic
  document and one long, keyword-stuffed one, to make a real ranking
  difference between TF-IDF and BM25 observable.
- `InvertedIndex.java` — the real core data structure: term → (docId → term
  frequency), plus a real Boolean AND query via postings-list intersection.
- `TfIdfScorer.java`, `Bm25Scorer.java` — two real, independent scoring
  implementations over the same index.
- `InvertedIndexDemo.java`, `ScoringComparisonDemo.java` — the two demos
  below.

## Run

```bash
cd practice/java/system-design/search-and-indexing-systems
mkdir -p out
javac -d out *.java
java -cp out InvertedIndexDemo
java -cp out ScoringComparisonDemo
```

## Real observed output (last full run, Java 21)

### 1. `InvertedIndexDemo` — the real postings-list mechanism

```
=== Real postings list for the term "java" ===
doc 1 ("GC Tuning Guide"): tf=1
doc 2 ("Keyword-Stuffed Long Page"): tf=4
doc 5 ("Java Concurrency Basics"): tf=1

=== Real Boolean AND query: "java" AND "garbage" AND "collection" ===
Real matching doc IDs: [1]
  doc 1: "GC Tuning Guide"
```

A real inverted index answers "which documents contain this term" as a
single map lookup — the postings list above is the actual real data
structure, not a description of one. The Boolean AND query is a real set
intersection across postings lists, not a scan of every document's text.

### 2. `ScoringComparisonDemo` — a real, measured TF-IDF vs. BM25 ranking difference

```
Query: [java, garbage, collection, tuning]
Average real document length across the corpus: 9.0 tokens

=== Real TF-IDF ranking (no length normalization) ===
  doc 1  score=3.5835  length=7  "GC Tuning Guide"
  doc 2  score=2.8277  length=20  "Keyword-Stuffed Long Page"
  doc 3  score=1.7918  length=8  "Python GC Internals"
  doc 4  score=1.0986  length=6  "Database Indexing Guide"
  doc 5  score=0.6931  length=5  "Java Concurrency Basics"

=== Real BM25 ranking (length-normalized, TF-saturated) ===
  doc 1  score=3.8284  length=7  "GC Tuning Guide"
  doc 3  score=1.8134  length=8  "Python GC Internals"
  doc 2  score=1.7191  length=20  "Keyword-Stuffed Long Page"
  doc 4  score=1.2113  length=6  "Database Indexing Guide"
  doc 5  score=0.8664  length=5  "Java Concurrency Basics"
```

Doc 2 is more than twice the average document length and repeats "java" and
"collection" many times, but is mostly off-topic filler. Under plain
TF-IDF (no length normalization), it ranks a real #2 — ahead of doc 3, a
short, genuinely on-topic document that just doesn't repeat the query terms
as many times. Under BM25 (real document-length normalization plus real
term-frequency saturation), doc 2 drops to a real #3, swapping places with
doc 3. This is the actual, measured version of the textbook explanation for
why BM25 improved on raw TF-IDF for real search engines — not asserted, but
reproduced with real numbers from a real, if small, corpus.

## Real discoveries made while building this pack

No bugs were hit while building the Java side of this pack — both scorers
produced correct, real output on the first run, including the real ranking
swap between doc 2 and doc 3 that was the specific effect this corpus was
designed to surface. (The real discoveries in this topic's practice code are
on the PostgreSQL side — see
[`practice/sql/search-and-indexing-systems/README.md`](../../../sql/search-and-indexing-systems/README.md).)
