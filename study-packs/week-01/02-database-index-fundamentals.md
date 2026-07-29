---
title: "T-609 · Database Index Structures"
topic_id: T-609
domain: Database
tier: Advanced
iwi: 8.30
prerequisites: []
unlocks: [T-610, T-611]
week: 1
last_reviewed: 2026-07-29
---

# T-609 · Database Index Structures

**IWI 8.30 · Advanced tier · Prerequisite for:** T-610 (query planning), T-611 (isolation levels), most of Chapter 08

**Verification note:** every `EXPLAIN` block in this chapter is real output from PostgreSQL 16, run in a disposable Docker container against a seeded 300,000-row `orders` table (5,000 customers). Nothing here is illustrative — see `MANIFEST.md` and `practice/sql/week-01/` for the exact reproducible command and full output.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [How it works internally — walking a real lookup](#3-how-it-works-internally--walking-a-real-lookup)
4. [Composite indexes and the leftmost-prefix rule](#4-composite-indexes-and-the-leftmost-prefix-rule)
5. [Covering indexes and index-only scans](#5-covering-indexes-and-index-only-scans)
6. [When the planner ignores your index — selectivity](#6-when-the-planner-ignores-your-index--selectivity)
7. [Engine-specific correction](#7-engine-specific-correction--the-feedback-item-this-week-targets)
8. [Trade-offs](#8-trade-offs)
9. [Interview questions](#9-interview-questions)
10. [Common mistakes](#10-common-mistakes)
11. [Staff-level discussion](#11-staff-level-discussion)
12. [Summary](#12-summary)
13. [Key Takeaways](#13-key-takeaways)
14. [Cheat Sheet](#14-cheat-sheet)
15. [Flashcards](#15-flashcards)
16. [Practice Exercises](#16-practice-exercises)
17. [Additional Reading](#17-additional-reading)
18. [Official References](#18-official-references)

---

## 1. The concept

A B+Tree index is a sorted, balanced tree structure that lets the database find a row (or a small range of rows) in `O(log n)` comparisons instead of scanning every row. Every table lookup by an indexed column walks from the root, through internal nodes holding routing keys, down to a leaf node holding either the actual row (a clustered/index-organized table) or a pointer to the row's physical location (a heap tuple ID, in PostgreSQL's case — **every** PostgreSQL index is a secondary index over a heap; there is no clustered-index concept in the InnoDB sense).

```mermaid
graph TD
    Root["Root: [50, 120]"]
    Root --> L1["≤50: [10,25,40]"]
    Root --> L2["50-120: [65,90,110]"]
    Root --> L3[">120: [150,200]"]
    L1 --> Leaf1["Leaf: 10→tid, 25→tid, 40→tid"]
    L2 --> Leaf2["Leaf: 65→tid, 90→tid, 110→tid"]
    L3 --> Leaf3["Leaf: 150→tid, 200→tid"]
```

## 2. Why it exists

Without an index, finding a row by any non-first-inserted criterion requires reading every page of the table — a sequential scan, `O(n)`. This is fine for small tables or when the query touches most of the table anyway (see §6, low-selectivity case), and ruinous for a point lookup on a large table. An index trades write-time cost (every insert/update/delete must also update the index) and storage (the index itself occupies disk and must be cached) for read-time speed on the specific access pattern it was built for.

## 3. How it works internally — walking a real lookup

Before any index exists, a point lookup on `customer_id` must read the entire table:

```sql
EXPLAIN (ANALYZE, BUFFERS) SELECT * FROM orders WHERE customer_id = 42;
```
```
 Gather  (cost=1000.00..5912.88 rows=60 width=35) (actual time=0.107..5.744 rows=60 loops=1)
   Workers Planned: 1
   ->  Parallel Seq Scan on orders  (cost=0.00..4906.88 rows=35 width=35) (actual time=0.053..4.082 rows=30 loops=2)
         Filter: (customer_id = 42)
         Rows Removed by Filter: 149970
         Buffers: shared hit=2701
 Execution Time: 5.754 ms
```

`Rows Removed by Filter: 149970` is the tell — the planner read (its half of) all 300,000 rows to find 60. After `CREATE INDEX idx_orders_customer ON orders(customer_id);`, the same query:

```
 Bitmap Heap Scan on orders  (cost=4.76..218.68 rows=60 width=35) (actual time=0.014..0.101 rows=60 loops=1)
   Recheck Cond: (customer_id = 42)
   Heap Blocks: exact=60
   Buffers: shared hit=60 read=2
   ->  Bitmap Index Scan on idx_orders_customer  (cost=0.00..4.75 rows=60 width=0) (actual time=0.008..0.008 rows=60 loops=1)
         Index Cond: (customer_id = 42)
 Execution Time: 0.111 ms
```

**5.754ms → 0.111ms, ~52x**, and read only the 60 matching rows via the index, then fetched their heap pages (`Bitmap Heap Scan`) to get the actual row data — because a normal index only stores the indexed column(s) plus a heap pointer, not the whole row.

```mermaid
flowchart LR
    Q[Query arrives] --> P{Planner estimates cost}
    P -->|No usable index| SS[Sequential Scan: read every page]
    P -->|Usable index, low selectivity| SS
    P -->|Usable index, high selectivity| IX[Index Scan / Bitmap Index Scan]
    IX --> HF{All needed columns in index?}
    HF -->|Yes, and heap pages all-visible| IOS[Index Only Scan — Heap Fetches: 0]
    HF -->|No| HeapFetch[Fetch matching heap pages]
```

## 4. Composite indexes and the leftmost-prefix rule

`CREATE INDEX idx_orders_customer_created ON orders(customer_id, created_at);` builds one B+Tree keyed on `(customer_id, created_at)` **in that order** — sorted first by customer, then by date within each customer. A query filtering both columns uses it fully:

```sql
SELECT * FROM orders WHERE customer_id = 42 AND created_at > '2025-01-01';
```
```
 Bitmap Heap Scan on orders (actual time=0.015..0.047 rows=30 loops=1)
   ->  Bitmap Index Scan on idx_orders_customer_created
         Index Cond: ((customer_id = 42) AND (created_at > '2025-01-01 00:00:00'::timestamp without time zone))
 Execution Time: 0.056 ms
```

But a query filtering **only the second column** cannot use this index at all — the tree is sorted by `customer_id` first, so "all rows with `created_at` in a range" are scattered across every branch of the tree, in no useful order:

```sql
SELECT * FROM orders WHERE created_at > '2025-06-01' AND created_at < '2025-06-02';
```
```
 Gather (actual time=0.214..6.077 rows=410 loops=1)
   ->  Parallel Seq Scan on orders
         Filter: ((created_at > ...) AND (created_at < ...))
         Rows Removed by Filter: 149795
 Execution Time: 6.094 ms
```

**This is the leftmost-prefix rule**, and it's the single most common index-design mistake: an index on `(a, b)` serves queries on `a` alone and on `(a, b)` together, but not on `b` alone. If both access patterns are real, two indexes (or one on `b` alone in addition) are needed.

## 5. Covering indexes and index-only scans

`CREATE INDEX idx_orders_covering ON orders(customer_id, created_at) INCLUDE (amount);` stores `amount` inside the index leaf pages without making it part of the sort key. If a query only needs columns present in the index, PostgreSQL can answer it **without touching the heap at all** — an index-only scan:

```sql
SET enable_bitmapscan = off;  -- see note below
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id, created_at, amount FROM orders
WHERE customer_id = 42 AND created_at > '2025-01-01';
```
```
 Index Only Scan using idx_orders_covering on orders (actual time=0.015..0.016 rows=28 loops=1)
   Index Cond: ((customer_id = 42) AND (created_at > ...))
   Heap Fetches: 0
   Buffers: shared hit=1 read=3
 Execution Time: ...
```

`Heap Fetches: 0` is the proof — every byte of the answer came from the index. **Why the `SET enable_bitmapscan = off`?** PostgreSQL's cost-based planner, at this table size, estimated a `Bitmap Heap Scan` as marginally cheaper than a plain `Index Only Scan` and chose it by default — and a bitmap scan always revisits the heap for its final recheck, so it doesn't get credited as index-only even when it reads from a covering index. This is itself worth saying out loud in an interview: **the planner is cost-based, not rule-based** — a covering index makes an index-only scan *possible*, not *guaranteed*, and the planner's choice depends on table statistics and configuration, which is exactly why `EXPLAIN ANALYZE` on the real data (not a guess) is the only trustworthy source of truth.

## 6. When the planner ignores your index — selectivity

An index on `status` exists in both queries below. `status = 'completed'` matches 199,767 of 300,000 rows (66%):

```
 Seq Scan on orders (actual time=0.005..14.084 rows=199767 loops=1)
   Filter: (status = 'completed'::text)
   Rows Removed by Filter: 100233
 Execution Time: 17.694 ms
```

The planner correctly ignored the index — walking a B+Tree and then randomly fetching two-thirds of the table's heap pages is slower than reading the heap sequentially once. `status = 'refunded'` (16.6% of rows) crosses the selectivity threshold where the index becomes worthwhile:

```
 Bitmap Heap Scan on orders (actual time=0.702..4.391 rows=49813 loops=1)
   ->  Bitmap Index Scan on idx_orders_status
         Index Cond: (status = 'refunded'::text)
 Execution Time: 5.276 ms
```

**Rule of thumb, not a hard threshold:** somewhere around 5–15% of the table is where an index typically stops paying for itself, depending on how clustered the matching rows are physically and how expensive random I/O is on the storage medium. There is no universal cutoff — this is exactly why `EXPLAIN ANALYZE` on real data beats memorizing a percentage.

## 7. Engine-specific correction — the feedback item this week targets

The named interview feedback used the phrase **"clustered vs non-clustered indexes."** That terminology is precise for SQL Server / MySQL-InnoDB, and **imprecise for PostgreSQL**:

- **InnoDB (MySQL):** the primary key *is* the clustered index — rows are physically stored in primary-key order, and every secondary index stores the PK value as its row pointer. This is why a wide primary key is expensive in InnoDB (it bloats every secondary index) and cheap in PostgreSQL.
- **PostgreSQL:** every table is a heap; every index (including the primary key's) is a secondary structure pointing at a heap tuple ID. `CLUSTER` exists as a one-off command that physically reorders the heap to match an index, but it is **not maintained** — the next `UPDATE` breaks the ordering again.

Naming this distinction — and which engine you're describing — is itself a depth signal worth volunteering unprompted (see §9, Q7).

## 8. Trade-offs

| Benefit | Cost |
|---|---|
| `O(log n)` point lookups and range scans instead of `O(n)` | Every write (`INSERT`/`UPDATE`/`DELETE`) also updates every index on the table |
| Composite index serves multiple query shapes via the leftmost prefix | Wrong column order makes the index invisible to the queries that need it most |
| Covering index eliminates heap access entirely | Duplicates column data into the index, increasing storage and vacuum cost |
| Planner picks the cheapest available plan automatically | The planner can be wrong when statistics are stale — `ANALYZE` after bulk loads matters |

## 9. Interview questions

### Q1. How does a B+Tree index actually find a row? Walk it from root to heap.

- **Expected answer:** root → internal routing nodes → leaf → heap tuple ID → heap page fetch, as in §1/§3.
- **Common mistakes:** describing a binary search tree instead of a B+Tree (wrong fan-out model); forgetting the final heap fetch step for a non-covering index.
- **Follow-up questions:** "Why B+Tree and not a binary search tree or a hash table for this?" *(Hash indexes can't serve range queries or ordering; a plain BST isn't disk-page-aware — a B+Tree's high fan-out is specifically shaped to minimize disk page reads.)*
- **Senior-level expectations:** walks the full path correctly, including the heap fetch.
- **Staff-level expectations:** explains *why* high fan-out matters (minimizing disk page reads, not just "it's a tree"), connecting the structure to physical I/O cost.

### Q2. Index on `(customer_id, created_at)` — which queries does it serve, which does it not, and why?

- **Expected answer:** states the leftmost-prefix rule precisely, with the §4 example.
- **Common mistakes:** claiming the index helps any query touching either column.
- **Follow-up questions:** "What would you add to also serve a `created_at`-only query efficiently?"
- **Senior-level expectations:** states the rule correctly and identifies the failing query shape.
- **Staff-level expectations:** proposes the correct remedy (separate index, or reconsidering the composite order) and discusses the write-cost trade-off of adding it.

### Q3. What is a covering index, and how do you know from `EXPLAIN` that you got one?

- **Expected answer:** names `Index Only Scan` and `Heap Fetches: 0` specifically — not just "it's faster."
- **Common mistakes:** believing a covering index guarantees an index-only scan regardless of planner choice (§5's bitmap-scan caveat).
- **Follow-up questions:** "You built a covering index and still see a Bitmap Heap Scan. Why?"
- **Senior-level expectations:** names the correct `EXPLAIN` markers.
- **Staff-level expectations:** explains the §5 planner-choice nuance unprompted.

### Q4. When is a sequential scan faster than an index scan?

- **Expected answer:** the §6 selectivity argument, with the reasoning (random I/O cost), not a memorized percentage.
- **Common mistakes:** citing a fixed percentage as a hard rule.
- **Follow-up questions:** "Is there a fixed percentage where this flips?"
- **Senior-level expectations:** gives the correct qualitative reasoning.
- **Staff-level expectations:** explicitly rejects the "fixed percentage" framing and explains why (physical clustering, storage medium random-I/O cost vary).

### Q5. You added an index and the query got slower. Give two distinct mechanisms.

- **Expected answer:** (a) write amplification — every insert now updates N+1 structures instead of N; (b) stale statistics leading the planner to a worse plan than before, until the next `ANALYZE`.
- **Common mistakes:** naming only one mechanism, usually just write amplification.
- **Follow-up questions:** "How would you detect which of the two is happening?"
- **Senior-level expectations:** names both mechanisms when prompted.
- **Staff-level expectations:** names both unprompted, and proposes a detection method (`EXPLAIN ANALYZE` before/after, monitoring write latency).

### Q6. Why did the planner ignore your index? Three reasons.

- **Expected answer:** low selectivity (§6); stale statistics; the query needs a function or expression the plain index doesn't match (would need a functional/expression index).
- **Common mistakes:** only naming selectivity.
- **Follow-up questions:** "What's a functional index, and when would you need one?"
- **Senior-level expectations:** names at least two of the three reasons.
- **Staff-level expectations:** names all three and can sketch a functional/expression index example (e.g. `CREATE INDEX ON orders(lower(status))`).

### Q7. Clustered vs non-clustered — and what changes when the engine is PostgreSQL rather than InnoDB?

- **Expected answer:** the §7 distinction, unprompted if possible.
- **Common mistakes:** using the term "clustered index" for PostgreSQL as if it behaved like InnoDB's.
- **Follow-up questions:** "What does PostgreSQL's `CLUSTER` command actually do, then?"
- **Senior-level expectations:** correctly states PostgreSQL has no maintained clustered index.
- **Staff-level expectations:** names the engine explicitly before being asked, and explains the `CLUSTER` command's one-off, unmaintained nature.

### Q8. What is an index-only scan, and what has to be true for PostgreSQL to actually choose one?

- **Expected answer, Staff-level:** the visibility map must mark the relevant heap pages all-visible (recent `VACUUM`), *and* the planner's cost model must favor it over alternatives — not automatic just because a covering index exists (§5).
- **Common mistakes:** stopping at "the index has all the needed columns" without mentioning the visibility map or planner cost model.
- **Follow-up questions:** "What's the visibility map, and why does it matter here?"
- **Senior-level expectations:** names the covering-index requirement correctly.
- **Staff-level expectations:** names the visibility-map/`VACUUM` requirement and the planner-choice caveat together, as demonstrated live in §5.

## 10. Common mistakes

- Assuming "I added an index" is sufficient without checking `EXPLAIN` — the planner may ignore it (§6), or the column order may not match the query (§4).
- Indexing every column "just in case" — each index has a real write-time and storage cost; index what queries actually filter or sort by.
- Forgetting `ANALYZE` after a large bulk load — stale statistics can make the planner choose a sequential scan even when an index would clearly win.
- Confusing "has an index" with "uses that index for this query" — these are different claims and only `EXPLAIN` distinguishes them.

## 11. Staff-level discussion

At scale, index decisions become **write-path capacity planning**, not just read-path optimization: a table with 15 indexes has 15x the write amplification on every insert, which shows up as replication lag, longer transaction hold times, and vacuum pressure — costs that don't appear in a single `EXPLAIN` but dominate at production volume. The Staff-level version of "should we add this index" is "what does this cost the write path, and is the read-side win worth it at our actual write:read ratio" — a question the audit's source material never asked because it never went past the definition of an index at all.

## 12. Summary

Indexes trade write-time cost and storage for read-time speed by letting the planner avoid a full sequential scan. A B+Tree serves point and range lookups in `O(log n)`; a composite index only serves its leftmost-prefix column combinations; a covering index can eliminate heap access entirely, but only when the planner's cost model and the visibility map both cooperate. The planner is always cost-based — every claim in this chapter is backed by real `EXPLAIN ANALYZE` output specifically because a plan can only be trusted when measured, not assumed.

## 13. Key Takeaways

- `Rows Removed by Filter` in a seq scan is the signal an index would help.
- Leftmost-prefix rule: `(A, B)` serves `A` and `(A, B)`, never `B` alone.
- `Index Only Scan` + `Heap Fetches: 0` is the only proof of a true covering-index win.
- The planner will correctly ignore a low-selectivity index — that's not a bug, it's cost-based reasoning working as intended.
- "Clustered index" means something different in PostgreSQL than in InnoDB — name the engine.

## 14. Cheat Sheet

| Situation | What to reach for |
|---|---|
| Point lookup on one column | Single-column B-Tree index |
| Filter on A, sometimes A+B together | Composite index `(A, B)` — never `(B, A)` for this pattern |
| Filter on B alone, unrelated to A | A separate index on `B`, or reconsider the query shape |
| Query only needs columns already in the index | Add the extra columns via `INCLUDE` for an index-only scan |
| Filter matches a large fraction of the table | Don't force an index — a sequential scan may genuinely be faster |
| Plan doesn't match expectation | `ANALYZE` the table first; stale stats are the most common cause |

## 15. Flashcards

1. **Q: B+Tree lookup path — one sentence.** A: Root → internal nodes (routing keys) → leaf node holding either the row or a heap-tuple pointer, in `O(log n)` comparisons.
2. **Q: Leftmost-prefix rule, precisely.** A: An index on `(A, B)` serves queries filtering `A` alone or `A` and `B` together, but not `B` alone.
3. **Q: What proves an index-only scan happened, in `EXPLAIN` output?** A: `Index Only Scan` as the node type, and `Heap Fetches: 0`.
4. **Q: Seq-scan-wins condition — name the mechanism, not a percentage.** A: When the query matches a large enough fraction of the table that random-I/O heap fetches via the index cost more than one sequential read of the whole table.
5. **Q: Clustered vs non-clustered index, by engine.** A: InnoDB: primary key IS the clustered index, rows stored in PK order. PostgreSQL: no clustered-index concept — every table is a heap, every index is secondary.

(Full week-level deck, including T-901 cards: `08-flashcards.md`.)

## 16. Practice Exercises

1. Reproduce §3 yourself: seed a table, run `EXPLAIN ANALYZE` before and after an index, and read the `Rows Removed by Filter` line.
2. Design an index (or set of indexes) for a table queried both as `WHERE customer_id = ? AND status = ?` and `WHERE status = ? AND created_at > ?`. Justify the column order for each.
3. Take a query in a system you know, run `EXPLAIN ANALYZE`, and identify whether it's using an index, and if not, whether that's actually a mistake or the planner making the right call (§6).

## 17. Additional Reading

- Markus Winand, *Use The Index, Luke* — Ch. 1–3 (B-Tree, concatenated indexes, clustering), also free online at [use-the-index-luke.com](https://use-the-index-luke.com/)

## 18. Official References

- PostgreSQL documentation, [Ch. 11 "Indexes"](https://www.postgresql.org/docs/current/indexes.html) (full)
- PostgreSQL documentation, [Ch. 14 "Performance Tips"](https://www.postgresql.org/docs/current/performance-tips.html) — §14.1 "Using EXPLAIN", §14.2 "Statistics Used by the Planner"
