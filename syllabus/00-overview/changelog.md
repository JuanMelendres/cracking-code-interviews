---
title: "Syllabus Changelog"
document_type: syllabus-changelog
status: active
last_updated: 2026-09-03
---

# Syllabus Changelog

Tracks changes to the `syllabus/` tree specifically — domain content migrations, gap-filling, and taxonomy adjustments. Separate from the repository-root `CHANGELOG.md`, which continues to track the whole repository including everything outside `syllabus/`.

## [2026-09-03] — Phase 1: Scaffolding

### Added

- `syllabus/00-overview/` — vision, taxonomy, topic specification, mastery model, and learning paths, extracted verbatim from the approved `00-project/syllabus-transformation-plan.md`.
- All 21 domain directories (`syllabus/01-computer-science-foundations/` through `syllabus/21-frontend-web/`), each with a populated `INDEX.md` listing its mapped topics (topic ID, title, current mastery levels, current `handbook/` location) per `00-project/migration-mapping.md`.

### Not yet done

- No content has physically moved. Every domain `INDEX.md` currently points back to the topic's real, unmoved `handbook/` (or other) location.
- Root `README.md`/`CLAUDE.md` framing rewrite (mentioned in the plan's §2.1 but not in its §13 Definition of Done for Phase 1) was deliberately deferred, per an explicit user decision to keep Phase 1 purely additive — `git diff --stat` against the pre-Phase-1 commit shows only new files.
- Phase 2 (low-risk single-file relocations) and Phase 3 (domain-by-domain handbook migration, starting with `02-java` per the approved plan) have not been authorized.

## [2026-09-03] — Phase 2: Low-risk relocations

### Changed

- Relocated, via `git mv`, the four low-risk files the plan named in §10: `git-internals-and-collaboration-workflows.md` (`handbook/cloud/` → `18-engineering-practices/`), `design-patterns-applied.md` (`handbook/architecture/` → `04-software-design/`), and both `07-api-design/` files (`api-design.md`, `api-gateway-bff-and-edge-concerns.md`, both from `handbook/system-design/`). Each gained a `source_history` field recording its real original path.
- Updated 42 files' inbound references across the repository to the new paths (`cheat-sheets/`, `flashcards/`, `production-cookbook/`, other `handbook/` chapters, `architecture-atlas/`, `practice/`, `study-packs/`) — see the repository-root `CHANGELOG.md`'s matching entry for the full accounting, including two link-breakage classes caught during verification (the moved files' own links to siblings left behind, and staying files that referenced a moved file by bare same-directory filename).
- Updated the three affected domain `INDEX.md` files (`04-software-design/`, `07-api-design/`, `18-engineering-practices/`) to reflect real, physically-present content instead of a scaffolding placeholder.

### Not yet done

- These four relocated files still carry only L3/L4 (Senior/Staff-depth) content — Foundation/Working-Knowledge layers remain Phase 5 gap-filling work, same as every other existing chapter.

## [2026-09-03] — Phase 3: First domain migration (02-java)

### Changed

- Relocated all 49 mapped `02-java` chapters via `git mv`: `handbook/java-core/` (15) → `language-core/`, `handbook/collections/` (9) → `collections/`, `handbook/jvm/` (12 of 13 — `benchmarking-and-jmh-pitfalls.md` stays for `16-performance-jvm`'s own turn) → `jvm-internals/`, `handbook/concurrency/` (13) → `concurrency/`. Each gained `source_history` and an updated `domain` field.
- Built a general link fixer that recomputes every one of these 49 files' own outbound links from their pristine pre-move content, correctly handling both "the target moved too" and "only I moved" cases — the subdomain nesting here is one level deeper than the old `handbook/` layout, so even links to unmoved content needed depth recalculation.
- Fixed 235 other files' inbound references (1,353 individual link fixes) across the rest of the repository. See the repository-root `CHANGELOG.md` for the full account, including a caught-and-fixed regression (7 `practice/` READMEs) and 51 discovered-but-out-of-scope pre-existing broken links unrelated to this migration.
- Updated `syllabus/02-java/INDEX.md` to reflect the real relocation.

### Not yet done

- `02-java`'s Foundation/Working-Knowledge (L1/L2) layers remain Phase 5 work.

## [2026-09-03] — Phase 3 continued: 12 more domains, remainder of backend handbook/

### Changed

- Relocated the entire remainder of the backend `handbook/` tree in one batch: 84 chapters across 12 domains (`05-spring`, `06-databases`, `08-testing`, `09-messaging-event-driven`, `10-distributed-systems`, `11-system-design`, `12-security`, `13-observability`, `14-devops-containers`, `15-cloud`, `16-performance-jvm`, `17-architecture`) via `git mv`. Combined with Phase 2 and the `02-java` batch, **all 137 backend `handbook/` chapters have now relocated**.
- Built the full 84-entry mapping up front rather than one domain at a time, then applied the same pristine-rebuild-plus-repository-wide-fix process proven correct for `02-java`: 466 other files changed, 2,705 link fixes.
- Verified: zero new broken links introduced. The same 51 pre-existing, unrelated broken links from the `02-java` migration were found again, unchanged — no regressions, no new instances.
- Updated all 12 affected domain `INDEX.md` files plus this directory's own `INDEX.md` (domain-status table, "What's next" section). See the repository-root `CHANGELOG.md` for the full account.

### Not yet done

- `01-computer-science-foundations`, `03-data-structures-algorithms`, `19-leadership-staff`, and most of `18-engineering-practices` remain new-writing-only (Phase 5) — no migration step applies.
- Every migrated domain's Foundation/Working-Knowledge (L1/L2) layers remain Phase 5 work.

## [2026-09-03] — Phase 3 completed: 20-interview-preparation and 21-frontend-web

### Changed

- Relocated `behavioral-handbook/` (15 chapters + README, directory now gone entirely — nothing was left behind), 5 non-private `interview-playbook/` entries, and 31 `handbook/frontend/` chapters + 1 `interview-playbook/frontend/` entry — 54 files total.
- Deliberately not moved, per the plan's own rules: `practice/mock-interviews/` (referenced instead) and `interview-playbook/company-prep/` (permanently private, "not migrated by default").
- Fixed a real pre-existing bug as a natural side effect of the move: `behavioral-handbook/`'s self-referential double-path-prefix links (32 instances) — repository-wide broken-link count dropped from 51 to 19, all 19 remaining unrelated to this migration.
- Rewrote `interview-playbook/README.md` to reflect the relocation; updated `syllabus/20-interview-preparation/INDEX.md`, `syllabus/21-frontend-web/INDEX.md`, `syllabus/19-leadership-staff/INDEX.md`, and this directory's own `INDEX.md`.
- **Phase 3 is now complete for every domain that had existing content to migrate.** See the repository-root `CHANGELOG.md` for the full account.

### Not yet done

- Phase 5 (Foundation/Working-Knowledge gap-filling across every migrated domain, plus new writing for the four remaining domains) and Phase 6 (learning-path assembly) — neither authorized.

## [2026-09-03] — Phase 5 begins: first new topic written

### Added

- `syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md` (T-2001) — the first topic written against the new Topic Specification and Mastery Model, with genuine L1–L4 coverage in one file. Built real, measured evidence first (`practice/java/cs-foundations/algorithmic-complexity/`): real wall-clock timings for O(1)/O(log n)/O(n)/O(n log n)/O(n²) on OpenJDK 21.0.12. Links to two already-existing `production-cookbook/` entries for its Production Scenarios section rather than inventing a new incident.
- Updated this domain's `INDEX.md` with the full 5-topic working list (T-2001–T-2005, the plan's own named gap areas) and `syllabus/00-overview/INDEX.md`'s domain-status table.

### Not yet done

- T-2002 through T-2005 (how a computer executes a program, number representation, the OS process/thread model, networking basics) — not yet written.
- Cheat sheet, flashcards, and a production-cookbook entry for T-2001 — deferred to a separate batch, per established session discipline.
- Every other domain's own L1/L2 retrofit, plus new writing for `03-data-structures-algorithms`, `18-engineering-practices` (beyond its git-internals seed), and `19-leadership-staff` — all still pending.

## [2026-09-03] — Phase 5 continues: second new topic written

### Added

- `syllabus/01-computer-science-foundations/how-a-computer-executes-a-program.md` (T-2002) — L1–L4 coverage of the fetch-decode-execute cycle, JVM bytecode vs. real machine code, the interpreter/JIT split, and the call stack's fixed-size, per-thread nature as the layer directly below [JVM Memory Layout and Runtime Regions](../02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md) rather than a restatement of it. Built real, measured evidence first (`practice/java/cs-foundations/program-execution/`): `javap -c` disassembly of a compiled method showing actual JVM bytecode instructions, and real recursion-depth-before-`StackOverflowError` measurements at three `-Xss` values (`256k` → 2,333 calls; platform default `2048k` → 32,949; `8m` → 145,996) on OpenJDK 21.0.12 — the README documents the honest, non-linear reading of that scaling (a fixed per-thread guard-page overhead, not a measurement error). Links two already-existing `production-cookbook/` entries for its Production Scenarios and Staff-level sections rather than inventing a new incident.
- Updated this domain's `INDEX.md` (T-2002 marked fully written, 2 of ~5) and `syllabus/00-overview/INDEX.md`'s domain-status table.

### Not yet done

- T-2003 through T-2005 (number representation, the OS process/thread model, networking basics) — not yet written.
- Cheat sheets, flashcards, and production-cookbook entries for T-2001/T-2002 — deferred to a separate batch, per established session discipline.
- Every other domain's own L1/L2 retrofit, plus new writing for `03-data-structures-algorithms`, `18-engineering-practices` (beyond its git-internals seed), and `19-leadership-staff` — all still pending.

## [2026-09-03] — Phase 5: 01-computer-science-foundations domain complete (T-2003, T-2004, T-2005)

### Added

- `syllabus/01-computer-science-foundations/number-representation.md` (T-2003) — two's complement, IEEE 754 floating point, overflow, and narrowing-cast truncation. Real evidence (`practice/java/cs-foundations/number-representation/`) caught a real methodological mistake before it shipped: `printf("%.20f", 0.1)` does not reveal `double`'s true stored value (it pads the shortest round-trip decimal with zeros); `new BigDecimal(0.1)` does. Production Scenarios cites two real, publicly documented historical incidents (Ariane 5 Flight 501, the Patriot missile failure at Dhahran) rather than inventing a fictionalized incident, since no existing `production-cookbook/` entry has a numeric-representation root cause.
- `syllabus/01-computer-science-foundations/os-process-thread-model.md` (T-2004) — processes, threads, context switching, and the 1:1 (platform thread) vs. M:N (virtual thread) models, deliberately scoped as the OS-level layer below the existing `virtual-threads.md` chapter rather than a duplicate of it. Real evidence (`practice/java/cs-foundations/process-thread-model/`): 200 blocked platform threads cost the OS ~208 real threads (confirming 1:1); 200 blocked virtual threads cost the OS only 10 real threads — exactly this machine's CPU core count, the default virtual-thread carrier-pool size — measured via macOS `top -stats th`, from outside the JVM.
- `syllabus/01-computer-science-foundations/networking-basics.md` (T-2005) — the TCP three-way handshake, HTTP as plain text over a TCP byte stream, and connection pooling, as the layer below `api-design.md` and Spring MVC. Real evidence (`practice/java/cs-foundations/networking-basics/`): a raw `ServerSocket`/`Socket` HTTP exchange with no HTTP library on either end, capturing the exact `\r\n`-terminated request/response bytes and the distinct local/remote TCP ports of one real connection.
- **This completes `01-computer-science-foundations`'s originally-scoped 5-topic list (T-2001–T-2005) from the plan's own Section 2.5/§7.6.** Updated `syllabus/01-computer-science-foundations/INDEX.md` (5/5, domain complete) and `syllabus/00-overview/INDEX.md`'s domain-status table.

### Not yet done

- Cheat sheets, flashcards, and production-cookbook entries for all five T-2001–T-2005 topics — deferred to a separate batch, per established session discipline.
- Every other domain's own L1/L2 retrofit, plus new writing for `03-data-structures-algorithms`, `18-engineering-practices` (beyond its git-internals seed), and `19-leadership-staff` — all still pending.

## [2026-09-03] — Phase 5: 03-data-structures-algorithms begins (T-2101–T-2105, top 5 by IWI)

### Added

- Five canonical chapters in `03-data-structures-algorithms`, corresponding to the Master Topic Register's top-5-by-priority coding-interview patterns (T-1402–T-1406): [Arrays, Two Pointers, and Sliding Window](../03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md) (T-2101), [Hashing Patterns and Frequency Maps](../03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md) (T-2102), [Binary Search, Including Search-on-Answer](../03-data-structures-algorithms/binary-search-and-search-on-answer.md) (T-2103), [Linked Lists and In-Place Manipulation](../03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md) (T-2104), and [Stacks and the Monotonic Stack](../03-data-structures-algorithms/stacks-and-monotonic-stack.md) (T-2105).
- Each chapter **elevates** already-real, already-compiled, already-verified practice code and its study-pack retrospective (`study-packs/week-{20,21,22,23}/...`) rather than writing new algorithm solutions from scratch — exactly the pattern the transformation plan itself named for this domain (§7.6: "practice code exists and is reusable as-is"). Every underlying demo (`practice/java/week-{20,21,22,23}/{linked-lists,stacks,hashing,binary-search,arrays-two-pointers}/`) was re-compiled and re-run on OpenJDK 21.0.12 while writing its chapter, confirming 57/57 real assertions still pass across all five, rather than trusting the study-packs' own prior verification alone.
- New topic IDs `T-2101`–`T-2118` reserved for this domain's full 18-chapter plan (the D14 register's T-1402–T-1419, excluding T-1401 which is already covered by `01-computer-science-foundations`'s T-2001), per the plan's `T-2100`–`T-2199` reserved range (§9).
- Updated `syllabus/03-data-structures-algorithms/INDEX.md` (rewritten from its Phase 1 scaffolding placeholder into a real, populated 18-topic working list, cross-referencing each T-21xx canonical-chapter ID against its corresponding D14 practice-log ID) and `syllabus/00-overview/INDEX.md`'s domain-status table.

### Not yet done

- T-2106 through T-2118 (Heaps, Trees, Graphs ⭐, Backtracking, Dynamic Programming ⭐, Intervals, Greedy, Bit Manipulation, Tries, Design Problems ⭐, Concurrency Problems ⭐, Advanced Structures, Communication Protocol) — not yet written; T-2108 (Graphs) and T-2110 (DP) are the two highest-weighted remaining patterns and the natural next batch.
- Cheat sheets, flashcards, and production-cookbook entries for T-2101–T-2105 — deferred to a separate batch, per established session discipline.
- `18-engineering-practices` (beyond its git-internals seed) and `19-leadership-staff` new writing, plus every migrated domain's own L1/L2 retrofit — all still pending.

## [2026-09-03] — Phase 5: 03-data-structures-algorithms continues (T-2106–T-2109)

### Added

- Four more canonical chapters, continuing the Master Topic Register's priority order: [Heaps, Top-K, and K-Way Merge](../03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md) (T-2106, D14 T-1407), [Trees, BSTs, and Traversal Patterns](../03-data-structures-algorithms/trees-bst-and-traversal-patterns.md) (T-2107, D14 T-1408), [Graphs: BFS, DFS, Topological Sort, Dijkstra, and Union-Find](../03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md) (T-2108, D14 T-1409 — the register's single highest-weighted pattern overall, IWI 6.25), and [Backtracking and Pruning](../03-data-structures-algorithms/backtracking-and-pruning.md) (T-2109, D14 T-1410).
- Same elevation discipline as the first five: each chapter elevates already-real, already-compiled practice code from `study-packs/week-{20,21,23}/...`, re-verified on OpenJDK 21.0.12 while writing (10, 18, 11, and 12 assertions respectively — 51 more, 108 total across all nine chapters now written in this domain).
- The Graphs chapter's Section 5/8 specifically documents a deliberate, real interview trap already present in the source study-pack: plain Dijkstra silently produces a wrong (not crashed) answer on Cheapest Flights Within K Stops, since it has no mechanism to represent an edge-count constraint — a genuine, non-obvious algorithm-selection failure mode elevated intact from the practice code's own retrospective.
- Updated `syllabus/03-data-structures-algorithms/INDEX.md` (9/18) and `syllabus/00-overview/INDEX.md`'s domain-status table.

### Not yet done

- T-2110 through T-2118 (Dynamic Programming ⭐, Intervals, Greedy, Bit Manipulation, Tries, Design Problems ⭐, Concurrency Problems ⭐, Advanced Structures, Communication Protocol) — not yet written. T-2110 (DP) is the next-highest-weighted remaining pattern (IWI 5.85) and has two source study-packs (8 problems combined) rather than one.
- Cheat sheets, flashcards, and production-cookbook entries for T-2101–T-2109 — deferred to a separate batch.
- `18-engineering-practices` (beyond its git-internals seed) and `19-leadership-staff` new writing, plus every migrated domain's own L1/L2 retrofit — all still pending.
