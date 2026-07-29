# Knowledge Base Audit Report
### Java / Backend Interview Preparation — Notion Workspace
**Audit date:** 28 July 2026
**Scope:** All accessible pages and databases relating to Java, backend engineering, algorithms, architecture, and interview preparation
**Target role calibration:** Senior Backend Engineer (L5) / Staff Engineer (L6)
**Notion mutations performed:** **None.** Workspace accessed read-only.

---

## 0. Executive Summary

The workspace contains **eight distinct assets** holding roughly **333 database rows** and **two long-form guide pages**. Effort has clearly gone in — the taxonomy is thoughtful, the DSA guide covers 23 patterns, and the Data Structures guide reaches genuinely advanced structures.

But the audit surfaces one dominant finding that overrides all others:

> **The knowledge base is a recall system built at Junior-to-Mid depth, being used to prepare for interviews that test reasoning at Senior-to-Staff depth.**

The evidence is quantitative, not impressionistic. Across the 262-row flagship database:

| Metric | Value | Implication |
|---|---|---|
| Total questions | 262 | Good breadth |
| Mean answer length | **~110 characters** | One sentence per concept |
| Answers longer than 300 chars | **3 of 262 (1.1%)** | Almost no depth anywhere |
| Rows with a code example | 63 of 262 (24%) | 76% are prose-only |
| Rows marked `Reviewed` | **0 of 262 (0%)** | Spaced repetition never started |
| Rows with page-body content | **0** | All content trapped in properties |

A 110-character answer is a *flashcard*. Senior and Staff interviews are not flashcard exams — they are 45-minute conversations where the first answer is the setup and the next four follow-ups are the actual evaluation. This knowledge base prepares you to survive minute one of a fifteen-minute line of questioning.

The second-order finding: **the coding-practice half of the system is empty.** Both trackers contain 4 rows each, all of it demo data shipped with the template, and **not one problem is solved in Java**.

**Overall knowledge base score: 4.1 / 10**
**Coverage of the Senior/Staff Java backend interview surface: ~22%**
**Verdict: Salvage the taxonomy and the two guide pages; rebuild the depth layer entirely.**

---

## 1. Asset Inventory

| # | Asset | Type | Volume | Status |
|---|---|---|---|---|
| A1 | Java Interview Questions (`…8001`) | Database | 68 rows | Redundant — near-total subset of A3 |
| A2 | Java Interview Questions (`…8005`) | Database | **3 rows** | Abandoned shell, superior schema |
| A3 | Extended Java Interview Questions | Database | **262 rows** | Primary asset |
| A4 | 🧠 DSA Patterns in Java — Tech Lead's Guide | Page | 23 modules | Strongest asset |
| A5 | 📚 Data Structures in Java — Tech Lead's Guide | Page | ~20 structures | Second strongest |
| A6 | LeetCode Practice Tracker → Problem Tracker | Database | **4 rows** | Unused template |
| A7 | Coding Ques. Journal → 🚀 Questions | Database | **4 rows** | Unused template |
| A8 | "Architecture Overview", "Interview Question" (2020) | Pages | Stubs | Unrelated legacy; ignore |

**Structural note:** Every row in A1/A2/A3 stores its entire content inside Notion *properties*, with the page body empty (`<empty-block/>`). This is the mechanical root cause of the shallowness — properties are a poor container for diagrams, multi-step derivations, or follow-up trees. The schema is capping the ceiling.

---

## 2. Per-Asset Evaluation

### A3 — Extended Java Interview Questions (262 rows) ⭐ *Primary asset*

| Dimension | Assessment |
|---|---|
| **Purpose** | Broad-coverage Q&A bank across 20 technology categories |
| **Quality Score** | **4 / 10** |
| **Completeness** | **30%** (breadth 70%, depth 10%) |
| **Difficulty Level** | Easy–Medium |
| **Interview Level** | **Junior → Mid.** Does not reach Senior. |

**Category distribution:**

```
Spring Boot        17  ████████
OOP                16  ████████   ← only category with code on every row
SQL                15  ███████
Spring Framework   15  ███████
SLCP  [sic]        15  ███████
REST API           15  ███████
Kafka              15  ███████
JPA                15  ███████
Java Threads       15  ███████
Java Exceptions    15  ███████
Java Core          15  ███████
Java Collections   15  ███████
Hibernate          15  ███████
Git                15  ███████
Design Patterns    15  ███████
CI/CD              15  ███████
AWS                15  ███████
Java 8              2  █
JVM                 1  ▌
Collections         1  ▌
```

**The "15" pattern is the tell.** Sixteen categories contain exactly 15 rows. This is generated breadth, not accumulated understanding — a fixed quota per topic rather than depth allocated by interview weight. It produces a knowledge base where **Git (15) outranks the JVM (1)** and **CI/CD (15) outranks Java 8 (2)**, which is close to an inversion of actual Senior-interview weighting.

**What the answers actually look like:**

> *Q: How does Kafka ensure message durability?*
> *A: "Kafka writes messages to disk and replicates them across multiple brokers to ensure durability and fault tolerance."*

Correct, and worth zero points at Staff level. The real interview lives in what comes next: `acks=all` vs `acks=1`, `min.insync.replicas`, why `acks=all` alone doesn't prevent data loss, unclean leader election, ISR shrinkage, the difference between durability and delivery semantics, idempotent producers, and the exactly-once transactional protocol. None of it is present.

> *Q: How can you secure a Spring Boot REST API?*
> *A: "You can use Spring Security to configure authentication and authorization, often using JWT tokens or OAuth2."*

This is a topic sentence. Missing: the filter chain, `SecurityFilterChain` vs the deprecated `WebSecurityConfigurerAdapter`, why JWTs cannot be revoked, refresh-token rotation, why `HS256` shared secrets fail in multi-service topologies, OAuth2 grant selection, and method-level security.

**Technical accuracy defects found:**

| Row | Defect | Severity |
|---|---|---|
| *Lifecycle of a thread* | Lists "Running" as a thread state. `Thread.State` has **NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED**. "Running" is an OS scheduling distinction, not a JVM state — and **TIMED_WAITING is omitted entirely**. Reciting this verbatim would be marked wrong. | **High** |
| *volatile keyword* | Reduced to "prevents local caching." The actual guarantee is a **happens-before edge under the Java Memory Model** — visibility *and* ordering. The cache framing is the classic misconception interviewers probe for, and it cannot explain why `volatile` fixes double-checked locking. | **High** |
| *Garbage Collection* | Lists **CMS** alongside G1/ZGC without noting it was deprecated in JDK 9 and **removed in JDK 14**. | Medium |
| Category `SLCP` | Typo for **SDLC** across all 15 rows. Content is correct; the label is not. | Low |
| Categories `Collections` (1) vs `Java Collections` (15) | Duplicate taxonomy branches | Low |

**Missing from this asset:** Every Senior-defining topic. See §4.

---

### A4 — 🧠 DSA Patterns in Java (23 modules) ⭐ *Strongest asset*

| Dimension | Assessment |
|---|---|
| **Purpose** | Pattern-recognition course for coding interviews |
| **Quality Score** | **6.5 / 10** |
| **Completeness** | **55%** |
| **Difficulty Level** | Easy → Hard |
| **Interview Level** | **Mid → Senior** |

**Genuine strengths:** The pattern-first framing is correct pedagogy — recognition beats memorization. Each module carries flashcards, a description, an ASCII visual, a Java implementation, and practice links. Modules 17–22 (Segment Tree, Rolling Hash, AVL, Fenwick, Suffix Array) reach further than most interview material. The concept maps are a legitimately good recall device.

**Code defects — verified by reading the implementations:**

| Module | Defect | Severity |
|---|---|---|
| **23 — LRU Cache** | `put()` unlinks an existing key from the DLL but **never removes it from the map** before the `map.size() == cap` check. Updating an existing key at capacity therefore **evicts an unrelated valid entry.** This is the single most-asked design problem in the set, and the implementation is wrong. | **Critical** |
| **8 — Top-K Frequent** | `new ArrayList<>(heap)` followed by `Collections.reverse()`. `PriorityQueue` iteration order is **unspecified** — only the head is guaranteed. Reversing an arbitrary order produces an arbitrary order. Passes LC 347 by accident (order-agnostic), fails any variant that requires sorted output. | **High** |
| **11 — Backtracking** | `temp.contains(num)` is O(n) per check *and* is value-based, so it **silently produces wrong results whenever the input contains duplicates**. The standard `boolean[] used` index-based approach is required. | **High** |
| **10 — Greedy** | `(a, b) -> a[1] - b[1]` — **integer subtraction overflow** on large bounds. Must be `Integer.compare(a[1], b[1])`. This is itself a classic interview follow-up. | Medium |
| **22 — Suffix Array** | Naive `Arrays.sort(suffixes)` on materialized substrings is **O(n² log n) time and O(n²) space**, presented without caveat. Unusable at the constraints where suffix arrays are actually asked. | Medium |
| **14 — Monotonic Stack** | The Mermaid diagram operates on **indices** (`arr[stack.peek()]`); the code pushes **values**. Diagram and code contradict each other. Method is named `nextGreaterElements` (LC 503, circular) but implements the non-circular variant. | Medium |
| **15 — Topological Sort** | The ASCII visual draws A→B→C **plus a back edge**, i.e. a cycle, while asserting a valid topological order exists. Actively teaches the wrong invariant. | Medium |

**Presentation defects:**

- **Every Mermaid diagram is fenced as `plain text`.** All 20+ "Mermaid Visual" blocks render as raw source. The single highest-value visual asset in the workspace is invisible.
- **Every Java block is fenced as `plain text`** — no syntax highlighting anywhere.
- **~30 practice links are Bing redirect URLs**, not canonical `leetcode.com` links. They carry tracking parameters, will rot, and are unreadable.
- **Taxonomy misfiling:** Dynamic Programming, Bit Manipulation, and Union-Find are filed under *"Trees and Graph Structures"*; Greedy and Prefix Sum under *"Array & Pointer Patterns"*; Rolling Hash and Suffix Array under *"Recursion and Backtracking"*. Since the entire premise is pattern *recognition*, miscategorization trains the wrong retrieval cue.
- **Module 3** carries conflicting colour metadata (green span inside a yellow block).

> ⚠️ **Privacy issue — recommend action.** One "further reading" link on this page is a Medium URL with an embedded **Google OAuth `id_token` (JWT)** in the query string, containing a personal email address, full name, and profile URL. The token is long expired and poses no live account risk, but it should not be sitting in a document. **Recommendation: replace that link with the clean `medium.com` URL.** (This is the one change worth making inside Notion; I have not made it.)

**Missing:** No complexity analysis per pattern. No "how to recognize this in 30 seconds" trigger list. No common-mistakes section. No alternative solutions. No follow-up questions. No quizzes for 18 of 23 modules. Missing patterns: intervals/merge, cyclic sort, in-place linked-list reversal, matrix traversal, k-way merge, subsets, binary search on answer space, difference arrays, Dijkstra, Bellman-Ford, MST, KMP, DP-on-trees, bitmask DP, digit DP.

---

### A5 — 📚 Data Structures in Java (~20 structures)

| Dimension | Assessment |
|---|---|
| **Purpose** | Reference for core and advanced structures with Java mappings |
| **Quality Score** | **6 / 10** |
| **Completeness** | **50%** |
| **Difficulty Level** | Easy → Hard |
| **Interview Level** | **Mid → Senior** |

**Strengths:** The Map-variant and Set-variant sections are the best material in the entire workspace. Covering `EnumMap`, `WeakHashMap`, and `IdentityHashMap` with real-world use cases is above the standard bar, and the two comparison tables are well-constructed and mostly accurate. Reaching Bloom Filter, Red-Black Tree, and Fenwick Tree shows correct ambition.

**Technical defects:**

| Item | Defect | Severity |
|---|---|---|
| **Bloom Filter** | Fundamentally broken as a Bloom filter. Uses exactly **two hash functions, and the second is derived from the first** (`h` and `h*31`) — they are correlated, so the false-positive rate is far worse than the structure implies. No `k` parameter, no bits-per-element sizing, no FPR formula. Also `Math.abs()` applied *after* `%` still returns negative for `Integer.MIN_VALUE`. The one interesting probabilistic structure is the one implemented incorrectly. | **High** |
| **Set hierarchy concept map** | Draws `TreeSet --> NavigableSet` and `TreeSet --> ConcurrentSkipListSet`. **Both inverted or invented.** `NavigableSet` is an *interface* that `TreeSet` implements (`Set` ← `SortedSet` ← `NavigableSet` ← `TreeSet`). `ConcurrentSkipListSet` does not derive from `TreeSet` at all. | **High** |
| **Set comparison table** | Lists **`NavigableSet` as a peer implementation** of HashSet/TreeSet, and assigns it a "Backing Structure: Red-Black Tree." An interface has no backing structure. Category error. | Medium |
| **Circular Queue** | Declares a `size` field that is **never read or written**. Missing `Front()`, `Rear()`, `isEmpty()`, `isFull()` — all required by LC 622. | Medium |
| **Suffix Array** | Same O(n² log n) naive implementation as A4, duplicated verbatim. | Medium |
| **AVL / Red-Black Tree** | "Skeletons" only — a node class and one rotation. No insert, no delete, no rebalance. Not implementable from what's here. | Medium |

*Checked and found correct:* the `LinkedHashMap`-based LRU (`accessOrder=true` + `removeEldestEntry`) is right — `LinkedHashMap.getOrDefault` does update access order in Java 8+. This is the better of the two LRU implementations in the workspace.

**Critical omission — the single most-asked Java data structure question is absent:**
There is **no HashMap internals section**. Nothing on bucket arrays, hash spreading (`h ^ (h >>> 16)`), load factor 0.75, resize/rehash mechanics, **treeification at 8 entries and untreeification at 6**, why capacity is a power of two, or the `equals`/`hashCode` contract. Every Senior Java interview asks this. It is not here.

**Also missing:** `ArrayDeque` (the correct modern stack — the page teaches the legacy synchronized `Stack`), `ConcurrentHashMap` internals, `CopyOnWriteArrayList`, `BlockingQueue` family, `ArrayList` growth policy, **B-Tree / B+Tree** (essential for the database-index interview), consistent hashing, skip lists explained, Count-Min Sketch, HyperLogLog. No per-structure complexity table. No interview questions. No exercises.

---

### A1 — Java Interview Questions, DB #1 (68 rows)

| Dimension | Assessment |
|---|---|
| **Quality Score** | **3 / 10** |
| **Completeness** | 15% |
| **Interview Level** | Junior |

Categories: OOP 16, Java Core 15, JPA 15, Hibernate 15, Java 8 2, Spring Boot 2, JVM 1, Collections 1, uncategorized 1.

**This is functionally a subset of A3.** Every category count matches A3's corresponding count exactly. Search confirms triplicated question titles across the workspace ("What are the principles of OOP in Java?" appears **three times**; `==` vs `equals`, method overriding, constructors, interfaces, and Garbage Collection each appear twice). Zero rows marked reviewed.

**Recommendation: retire.** Its content survives in A3.

---

### A2 — Java Interview Questions, DB #2 (3 rows)

| Dimension | Assessment |
|---|---|
| **Quality Score** | **2 / 10** (content) / **7 / 10** (schema) |
| **Completeness** | **1%** |
| **Interview Level** | n/a |

Three rows: one OOP, one JVM, one Collections. **This is the most interesting failure in the workspace** — it has the *best schema of the three*. Its category enum includes **Multithreading, JDBC, Java EE, and Design Patterns**; it has a proper three-level Confidence enum (Low/Medium/High, where A1/A3 inexplicably offer only High/Medium — you cannot record that you don't know something); it defines a row template; and it has three configured views including Kanban-by-Category and Kanban-by-Difficulty.

Someone designed the right system and then populated a different, weaker one. The schema is worth preserving as the model for the rebuild; the rows are not.

---

### A6 — LeetCode Practice Tracker → Problem Tracker (4 rows)

| Dimension | Assessment |
|---|---|
| **Quality Score** | **1.5 / 10** (content) / **8 / 10** (schema) |
| **Completeness** | **<1%** |
| **Interview Level** | n/a |

The schema is genuinely excellent — Topic (25 options), Time/Space Complexity enums, Companies, Time Spent, Frequency of Review, Problem Progress, five configured views including a spaced-repetition "Need Revision" view and a calendar.

The content is four rows of shipped demo data:

| Problem | Language | Status |
|---|---|---|
| 66. Plus One | **C++** | Completed |
| 64. Minimum Path Sum | **C++** | In Progress |
| 10. Regular Expression Matching | **Python** | Need Review |
| 3. Longest Substring | **JavaScript** | To Do |

**Not one problem is in Java.** The stored code is empty stubs — `// LeetCode accepted Code goes here` above an unimplemented function body. Zero notes on any row.

For a Senior/Staff Java candidate, a coding tracker with four unsolved non-Java problems represents **zero coding preparation**. Against a realistic Senior bar of 150–250 solved problems with written retrospectives, this is 0% complete. This is the **largest single gap in the workspace.**

---

### A7 — Coding Ques. Journal → 🚀 Questions (4 rows)

| Dimension | Assessment |
|---|---|
| **Quality Score** | **1.5 / 10** |
| **Completeness** | **<1%** |
| **Interview Level** | Junior |

A second, overlapping tracker with a *different* schema (Insight, Alternative Method Tags, Spaced Repetition, My Expertise). Also 4 rows of demo data.

Data-integrity errors: **"Search Insert Position" is tagged `Hard`** (it is Easy on LeetCode), and **`No. of times practiced` = −1**, an impossible value.

**Structural problem: A6 and A7 are competing systems for the same job.** Two trackers means neither gets used — which is precisely what the data shows. Consolidate to one.

---

## 3. Cross-Cutting Findings

### 3.1 Redundancy

| Type | Detail |
|---|---|
| Duplicate databases | Three Q&A databases where one is needed; A1 ⊂ A3 |
| Duplicate rows | OOP principles ×3; `==`/`equals`, overriding, constructors, interfaces, GC ×2 each |
| Duplicate trackers | A6 and A7 solve the same problem, differently, neither used |
| Duplicate content across pages | Suffix Array, AVL, Fenwick Tree, LRU Cache appear in **both** A4 and A5 with no cross-reference — and the two LRU implementations **disagree**, one being correct and one buggy |
| Duplicate taxonomy | `Collections` vs `Java Collections`; `Spring` vs `Spring Boot` vs `Spring Framework` |

**Net effect:** ~333 rows collapse to roughly **265 unique items**, ~20% redundancy.

### 3.2 Outdated Content

| Item | Issue |
|---|---|
| CMS garbage collector | Listed as current; deprecated JDK 9, **removed JDK 14** |
| Java version horizon | Content stops at **Java 8**. Java 25 LTS is current. Records, sealed classes, pattern matching, text blocks, virtual threads, structured concurrency, and scoped values are **entirely absent** — and virtual threads are now a standard Senior Java interview question |
| `Stack<E>` | Taught as the stack of choice; `ArrayDeque` has been correct since Java 6 |
| Spring Security | JWT/OAuth2 mentioned with no reference to the modern lambda-DSL `SecurityFilterChain` |
| `WeakHashMap` "for caches" | Common misconception — weak *keys* make it unsuitable for most caching; `Caffeine`/soft references are the real answer |

### 3.3 Systematic Absences

Across all 333 rows and two guide pages, the following appear **zero times**:

- ❌ Any **follow-up question chain** (the actual mechanism of Senior evaluation)
- ❌ Any **trade-off analysis** or decision criterion
- ❌ Any **production scenario, incident, or war story**
- ❌ Any **behavioral / STAR content whatsoever**
- ❌ Any **system design problem**
- ❌ Any **mock interview or evaluation rubric**
- ❌ Any **exercise with a worked solution**
- ❌ Any **rendered diagram** (all Mermaid is dead text)
- ❌ Any **"why does this exist" / historical motivation** framing
- ❌ Any **anti-pattern catalogue**

---

## 4. Gap Map — Senior/Staff Interview Surface

Coverage assessed against the topic list in the project brief.
Legend: 🟢 adequate · 🟡 present but shallow · 🔴 absent

| Domain | Status | Note |
|---|---|---|
| **OOP fundamentals** | 🟡 | Only area with code on every row; still definition-level |
| **Java Core** | 🟡 | 15 shallow rows |
| **Collections** | 🟡 | API-level only; **no HashMap internals** |
| **Streams / Functional** | 🔴 | 2 rows total |
| **Generics** | 🔴 | Absent — no variance, erasure, PECS |
| **JVM internals** | 🔴 | **1 row.** No memory model, JIT, escape analysis, class loading |
| **Garbage Collection** | 🟡 | One good-for-Mid answer; no G1/ZGC internals, no tuning, no log reading |
| **Concurrency** | 🟡 | 15 shallow rows; no JMM, no `CompletableFuture`, no lock-free, no `java.util.concurrent` depth |
| **Virtual Threads / Structured Concurrency / Scoped Values** | 🔴 | Absent |
| **Records / Sealed / Pattern Matching** | 🔴 | Absent |
| **Reflection / Annotations / ClassLoaders** | 🔴 | Absent |
| **VarHandles / Unsafe / FFM API** | 🔴 | Absent |
| **Spring Framework / Boot** | 🟡 | 32 rows, all surface. No bean lifecycle, proxying, AOP, `@Transactional` self-invocation |
| **Spring Security / OAuth2 / JWT** | 🔴 | 1 row |
| **Transactions / Isolation** | 🔴 | Absent |
| **Caching / Resilience** | 🔴 | Absent |
| **JPA / Hibernate** | 🟡 | 30 rows; no N+1, no fetch strategies, no L2 cache, no dirty checking |
| **Kafka** | 🟡 | 15 rows; no delivery semantics, rebalancing, EOS, partition-key design |
| **PostgreSQL / indexes / plans / locks** | 🔴 | 15 generic SQL rows; **no PostgreSQL-specific content at all** |
| **System Design** | 🔴 | **Absent** |
| **Architecture / DDD / CQRS / Event Sourcing** | 🔴 | Absent (2 rows on HLD/LLD) |
| **Microservices** | 🔴 | Absent |
| **Docker / Kubernetes** | 🔴 | Absent |
| **Cloud (AWS / Azure / GCP)** | 🟡 | 15 AWS rows at certification-trivia level; Azure and GCP absent |
| **Testing (JUnit / Mockito / Testcontainers)** | 🔴 | Absent |
| **Performance / profiling** | 🔴 | Absent |
| **Observability** | 🔴 | Absent |
| **CI/CD** | 🟡 | 15 generic rows |
| **Design Patterns / SOLID** | 🟡 | 15 rows, one line each, no code |
| **DSA patterns** | 🟢 | Best-covered domain (A4) |
| **Data structures** | 🟢 | Well covered (A5) |
| **Coding practice** | 🔴 | **4 problems, 0 in Java** |
| **Behavioral / Leadership** | 🔴 | **Absent** |
| **Mock interviews** | 🔴 | Absent |

**Tally: 5 🟢 · 12 🟡 · 21 🔴** → **~22% coverage.**

The pattern is diagnostic: coverage is strongest exactly where interviews are *least* differentiating at Staff level (DSA syntax, framework APIs) and weakest exactly where Staff candidates are *actually* assessed — system design, architectural trade-offs, production judgment, and behavioral leadership evidence.

---

## 5. Scorecard

| Asset | Quality | Complete | Difficulty | Interview Level | Action |
|---|---|---|---|---|---|
| A4 · DSA Patterns | **6.5** | 55% | Easy–Hard | Mid–Senior | **Expand + fix 7 code defects** |
| A5 · Data Structures | **6.0** | 50% | Easy–Hard | Mid–Senior | **Expand + fix 6 defects** |
| A3 · Extended Q&A | **4.0** | 30% | Easy–Med | Junior–Mid | **Keep as index; rewrite depth** |
| A1 · Java Q DB #1 | **3.0** | 15% | Easy | Junior | **Retire (duplicate)** |
| A2 · Java Q DB #2 | **2.0** | 1% | — | — | **Harvest schema; retire rows** |
| A6 · LeetCode Tracker | **1.5** | <1% | — | — | **Keep schema; populate in Java** |
| A7 · Coding Journal | **1.5** | <1% | — | — | **Merge into A6** |
| A8 · 2020 stubs | **0.5** | 0% | — | — | **Ignore** |
| **Weighted overall** | **4.1** | **22%** | **Easy–Med** | **Mid** | — |

---

## 6. What Survives Into the Handbook

**Carry forward (real value):**
1. **A4's 23-pattern taxonomy** — the pattern-recognition framing is correct and is the handbook's `15-LeetCode/` skeleton
2. **A5's Map/Set variant sections and comparison tables** — the best-executed material found; seeds `02-Collections/`
3. **A6's tracker schema** — Topic + Complexity + Frequency of Review is well-designed; becomes the practice-log spec
4. **A2's category enum** — Multithreading / JDBC / Design Patterns branches the populated DB lacks
5. **A3's 262 question titles** — poor as answers, valuable as a **coverage checklist** confirming which topics are on the radar

**Do not carry forward:**
- All 262 answer bodies (rewrite from zero at Senior depth)
- All 7 defective algorithm implementations (§A4, §A5) — these must be corrected, not ported
- The Bing redirect link corpus
- Duplicate databases A1, A2
- The 4 non-Java tracker rows

---

## 7. Risk Register

| # | Risk | Sev | Why it matters |
|---|---|---|---|
| R1 | **Zero behavioral preparation** | 🔴 Critical | Staff loops are typically ≥40% behavioral. Absent evidence of scope, influence, and judgment is the most common Staff-level rejection reason — and it is unrecoverable in the room. |
| R2 | **Zero coding practice in Java** | 🔴 Critical | 4 unsolved non-Java problems. Pattern *knowledge* without volume does not produce a passing coding round. |
| R3 | **Zero system design** | 🔴 Critical | Two-to-three rounds at Senior+. No material exists. |
| R4 | **Wrong LRU implementation** | 🔴 High | The most-asked design problem, memorized incorrectly. Actively harmful. |
| R5 | **Depth ceiling ≈ 110 chars** | 🔴 High | Survives question 1, fails follow-ups 2–5, where the signal is. |
| R6 | **Java 8 horizon** | 🟠 High | Virtual threads and records are now standard Senior questions. |
| R7 | **Incorrect thread-state / volatile model** | 🟠 High | Two of the most probed concurrency concepts, memorized wrong. |
| R8 | **Broken Bloom filter, inverted Set hierarchy** | 🟠 Medium | Confidently-stated wrong facts are worse than gaps. |
| R9 | **Zero rows reviewed; retrieval never practiced** | 🟠 Medium | Recognition ≠ recall. No spaced repetition has occurred. |
| R10 | **Personal JWT embedded in a page link** | 🟡 Low | Expired, no live risk. Scrub for hygiene. |

---

## 8. Recommendations Feeding Phase 2

1. **Invert the effort allocation.** Git and CI/CD do not need 15 rows each while the JVM has 1. Re-weight by interview frequency × differentiating power.
2. **Replace the flashcard as the unit of knowledge.** The atomic unit for Senior prep is a **concept dossier**: definition → why it exists → internals → trade-offs → production failure mode → follow-up chain → Staff-level variant.
3. **Consolidate to one Q&A source and one practice tracker.** Retire A1, A2, A7.
4. **Treat the 7 verified code defects as errata**, corrected explicitly in the handbook so the wrong versions are unlearned rather than silently replaced.
5. **Open three brand-new pillars from zero** — System Design, Behavioral/Leadership, and Testing/Observability. Nothing exists to build on; these are greenfield.
6. **Fix presentation at the source:** real fenced `java` blocks, real `mermaid` blocks, canonical LeetCode URLs.
7. **Set a coding-volume target.** Realistic Senior bar: 150–250 problems in Java with written retrospectives. Current: 0.

---

## 9. Audit Integrity Statement

- Notion accessed **read-only**. No page created, edited, moved, or deleted. No database or database entry created or modified.
- All counts, averages, and category distributions are from direct read-only SQL aggregation over the live data sources, not estimates.
- All code defects were identified by reading the actual implementations stored in the workspace, and each is described with its specific failure mode.
- Cross-database duplicate-title aggregation could not be executed (multi-source SQL requires a Notion Enterprise plan); duplication was instead established via per-source category counts and title search, and is reported as approximate.

---

## Phase 1 Complete — Awaiting Approval

**Proposed Phase 2 deliverable:** a full gap analysis mapping every 🔴 and 🟡 above into concrete topic units, each with interview frequency, difficulty, prerequisites, expected depth-of-answer, and the follow-up chains a Staff interviewer would actually walk down.

**Confirm to proceed to Phase 2 — Gap Analysis.**
