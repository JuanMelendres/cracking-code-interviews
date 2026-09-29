---
title: "Syllabus Changelog"
document_type: syllabus-changelog
status: active
last_updated: 2026-09-22
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

## [2026-09-03] — Phase 5: 03-data-structures-algorithms domain complete (T-2110–T-2117)

### Added

- Eight more canonical chapters, completing the domain's full 18-chapter working list: [Dynamic Programming](../03-data-structures-algorithms/dynamic-programming.md) (T-2110, D14 T-1411, ⭐ — elevated from two study-packs, 12 problems total), [Intervals, Merging, and Sweep Line](../03-data-structures-algorithms/intervals-merging-and-sweep-line.md) (T-2111, D14 T-1412), [Greedy and the Exchange Argument](../03-data-structures-algorithms/greedy-and-the-exchange-argument.md) (T-2112, D14 T-1413), [Bit Manipulation](../03-data-structures-algorithms/bit-manipulation.md) (T-2113, D14 T-1414), [Tries and Prefix Structures](../03-data-structures-algorithms/tries-and-prefix-structures.md) (T-2114, D14 T-1415), [Design-Style Coding Problems (LRU, LFU, Iterators)](../03-data-structures-algorithms/design-style-coding-problems.md) (T-2115, D14 T-1416, ⭐), [Concurrency Coding Problems](../03-data-structures-algorithms/concurrency-coding-problems.md) (T-2116, D14 T-1417, ⭐), and [Advanced Structures](../03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md) (T-2117, D14 T-1418 — elevated from `practice/java/advanced-structures/README.md` rather than a study-pack, since none exists for this Expert-tier, roadmap-excluded topic).
- T-2118 (D14 T-1419, Coding Interview Communication Protocol) is **not** written as a new chapter — discovered during this batch to already exist as a fully migrated entry at `syllabus/20-interview-preparation/coding/coding-interview-communication-protocol.md`. The domain's own `INDEX.md` now references it directly rather than duplicating it, the same treatment already given to T-1401.
- **This completes `03-data-structures-algorithms`: 17 of 17 canonical chapters written** (D14's T-1402–T-1418; T-1401 and T-1419 both covered elsewhere and explicitly not duplicated). Every underlying real assertion across all 17 chapters' practice code was re-compiled and re-run on OpenJDK 21.0.12 while writing this domain: 247 total real, passing assertions.
- Updated `syllabus/03-data-structures-algorithms/INDEX.md` (17/17, domain complete) and `syllabus/00-overview/INDEX.md`'s domain-status table.

### Not yet done

- Cheat sheets, flashcards, and production-cookbook entries for all 17 chapters in this domain — deferred to a separate batch, per established session discipline.
- `18-engineering-practices` (beyond its git-internals seed) and `19-leadership-staff` new writing, plus every migrated domain's own L1/L2 retrofit — all still pending.

## [2026-09-03] — Phase 5: 18-engineering-practices domain complete (T-1801–T-1804)

### Added

- Four new canonical chapters, closing every gap the plan's own Section 7.6 named for this domain: [Code Review: Standards and Practice](../18-engineering-practices/code-review-standards-and-practice.md) (T-1801), [Architecture Decision Records and Technical Writing for Engineers](../18-engineering-practices/architecture-decision-records-and-technical-writing.md) (T-1802), [Working with Legacy Code](../18-engineering-practices/working-with-legacy-code.md) (T-1803), and [Refactoring Discipline](../18-engineering-practices/refactoring-discipline.md) (T-1804) — all four assigned IDs in the plan's reserved `T-1800`–`T-1899` range.
- T-1803 and T-1804 are backed by real, compiled, executed Java demos: `practice/java/engineering-practices/legacy-code/` (a two-step characterization-testing workflow that surfaced a genuine, real "discount cliff" quirk — buying 9 vs. 10 units costs identically at one unit price — used as the chapter's central worked example rather than an invented one) and `practice/java/engineering-practices/refactoring-discipline/` (a real three-step Extract Method refactor proven behavior-preserving via a parity test across 10 real cases, all passing).
- T-1801 and T-1802 are grounded in real, existing repository artifacts rather than a compile-and-run demo, since neither topic is itself an algorithm: this repository's own recent commit history (Code Review's convention example) and `templates/adr-template.md` plus `scripts/check_adr_completeness.py` (actually run against both the real template and a deliberately incomplete ADR, confirming a real PASS and a real FAIL naming the exact missing sections).
- T-1802 deliberately does not duplicate [Trade-off Narration and Architecture Decision Records](../20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md) — it is the canonical, general engineering-practice reference that entry's own brief ADR paragraph points to.
- **This completes `18-engineering-practices`.** Updated `syllabus/18-engineering-practices/INDEX.md` (5/5, domain complete) and `syllabus/00-overview/INDEX.md`'s domain-status table.

### Not yet done

- Cheat sheets, flashcards, and production-cookbook entries for T-1801–T-1804 — deferred to a separate batch.
- `19-leadership-staff` new writing, plus every migrated domain's own L1/L2 retrofit (including this domain's own git-internals chapter) — all still pending.

## [2026-09-04] — Phase 5: 19-leadership-staff domain complete (T-1901–T-1905)

### Added

- Five new canonical chapters, the domain's first: [Mentoring and Developing Others](../19-leadership-staff/mentoring-and-developing-others.md) (T-1901), [Cross-Team Influence Without Authority](../19-leadership-staff/cross-team-influence-without-authority.md) (T-1902), [Leading Migrations and Large-Scale Technical Change](../19-leadership-staff/leading-migrations-and-large-technical-change.md) (T-1903), [Technical Debt: Prioritization and Advocacy](../19-leadership-staff/technical-debt-prioritization-and-advocacy.md) (T-1904), and [Design Reviews and RFCs as an Organizational Practice](../19-leadership-staff/design-reviews-and-rfcs-as-organizational-practice.md) (T-1905) — all five assigned IDs in the plan's reserved `T-1900`–`T-1999` range.
- Each chapter is the *working-skill* counterpart to an existing `20-interview-preparation/behavioral/` chapter (07, 09, 10, 11, 12 respectively), per the Section 2.7 decision. All five behavioral chapters were read in full before writing to confirm they are exclusively STAR-narration content with no overlapping working-skill coverage — none was found, so no duplication occurred.
- T-1903 and T-1904 each state an explicit boundary against an existing `17-architecture` chapter that owns the same subject's technical/architectural mechanics ([Strangler Fig, Anti-Corruption Layer, and Migration Patterns](../17-architecture/strangler-fig-and-migration-patterns.md) and [Technical Debt and Evolutionary Architecture](../17-architecture/technical-debt-and-evolutionary-architecture.md) respectively) — this domain's chapters cover only the organizational-leadership layer (sequencing, stakeholder buy-in, prioritization, advocacy), not the pattern or metaphor itself.
- T-1905 states a three-way boundary against [Architecture Decision Records and Technical Writing for Engineers](../18-engineering-practices/architecture-decision-records-and-technical-writing.md) (owns the ADR document format, post-decision) and [Trade-off Narration and Architecture Decision Records](../20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md) (owns verbal interview narration) — this chapter owns only the pre-decision review-process design (decision rights, RFC lifecycle states, reviewer craft, timeboxing).
- Two real, existing `production-cookbook/` entries are cited as grounding evidence: [Shared Customer Entity Requiring a Three-Team Migration](../../production-cookbook/shared-customer-entity-forcing-a-three-team-migration-for-one-field.md) (T-1902, T-1903) and [Gradual Coupling Erosion Turning a Core Class into a Release Bottleneck](../../production-cookbook/gradual-coupling-erosion-turning-a-core-class-into-a-release-bottleneck.md) (T-1904) — both located by grepping the cookbook for topically relevant existing incidents before considering a new one. T-1901 and T-1905 have no matching existing entry (the cookbook is technical-incident-shaped by design); both use an explicitly labeled representative scenario, following this repository's own established convention for illustrative-not-literal examples (the same convention already used in `production-cookbook/gradual-coupling-erosion-turning-a-core-class-into-a-release-bottleneck.md` itself), with a `> Planned reference:` note for a future dedicated entry.
- All five chapters verified structurally: 1 H1, 20 H2 sections each, valid YAML front matter, balanced code fences, and every relative link (front-matter and Markdown-syntax) confirmed resolving via an inline script.
- **This completes `19-leadership-staff`.** Updated `syllabus/19-leadership-staff/INDEX.md` (5/5, domain complete) and `syllabus/00-overview/INDEX.md`'s domain-status table.

### Not yet done

- Cheat sheets, flashcards, and production-cookbook entries for T-1901–T-1905 — deferred to a separate batch.
- L1/L2 (Foundation/Working-Knowledge) retrofit across every already-migrated domain's existing chapters — still the single largest remaining body of Phase 5 work, per the plan's own §7.6.
- Phase 6 (learning-path assembly) — not started, not authorized.

## [2026-09-04] — Phase 5: L1/L2 retrofit begins — 02-java/collections subdomain (T-201–T-209)

### Added

- The plan's own additive Foundation/Working-Knowledge retrofit (§2.4) begins with `02-java/collections`, chosen as the first subdomain because the plan itself names `hashmap-internals.md` as its representative example of the gap. All 9 chapters (T-201 HashMap, T-202 ArrayList/LinkedList, T-203 TreeMap/TreeSet, T-204 ArrayDeque, T-205 ConcurrentHashMap, T-206 CopyOnWriteArrayList, T-207 BlockingQueue, T-208 Fail-Fast/Weakly-Consistent Iterators, T-209 Collection Selection Decision Matrix) gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section each.
- Placement follows the plan's own wording exactly: the two new sections sit "at the top" — inserted between the existing "Why This Matters in Interviews" and "Mental Model" sections — with the existing content otherwise untouched. Verified per file via diff that every change was a pure insertion: zero existing sentences deleted, reworded, or reordered.
- Each chapter's Level 1 section explains the concept in plain language with an everyday analogy and states when a Junior/Mid engineer would reach for it, deliberately without internals; each Level 2 section covers the everyday API surface, common idioms, and a practical default/decision rule — genuinely usable by a reader with no prior background in the topic, verified by re-reading each addition as if encountering the collection for the first time.
- Each chapter gained two new front-matter fields, `topic_id` (matching the ID already stated in its own `> Topic register:` line) and `mastery_levels_covered: [L1, L2, L3, L4]`, per the target schema in `00-overview/topic-specification.md` §4.3 — additive fields only; no existing front-matter field was renamed or removed, preserving the existing `handbook-chapter` document type and template.
- Verified across all 9 files: exactly one H1 each (unchanged); the two new headings resolve via the project's own anchor-slug convention (confirmed by the absence of any `MD051` link-fragment warning after each insertion, versus a warning present beforehand when the TOC referenced them ahead of the heading existing); YAML front matter still parses; every pre-existing cross-reference used within the new sections (to sibling collections chapters and to `../language-core/equals-hashcode-and-comparable-contracts.md`) resolves on disk. A pre-existing class of broken links (several `../../practice/java/week-14/...` references, off by one directory level) was found in five of the nine files during this check — confirmed via `git diff` to predate this batch and not introduced by it, consistent with this repository's already-tracked, deliberately-deferred broken-link backlog; left unfixed as out of scope for this specific retrofit task.
- Updated `syllabus/02-java/INDEX.md` (status line, intro paragraph, a new Phase 5 note, and all 9 affected topic rows) and `syllabus/00-overview/INDEX.md`'s domain-status table.

### Not yet done

- The remaining 40 `02-java` chapters (`language-core` 15, `jvm-internals` 12, `concurrency` 16 minus a name overlap) still carry L3/L4 only — next in this retrofit, subdomain by subdomain.
- Every other migrated domain (`04` through `17`, `20`, `21`) — 132 further chapters — awaits the same retrofit; this is confirmed, by this first batch, to be exactly as large a body of work as the plan's own §7.6 estimated.
- Cheat sheets, flashcards, and production-cookbook entries for T-1901–T-1905, and for every Phase 5 new-writing chapter this session has produced — still deferred to a separate batch.

## [2026-09-04] — Phase 5: L1/L2 retrofit continues — 02-java/language-core subdomain (T-101–T-115)

### Added

- All 15 `02-java/language-core` chapters retrofitted: `equals-hashcode-and-comparable-contracts.md` (T-101), `polymorphism-and-dynamic-dispatch.md` (T-102), `immutability-and-defensive-copying.md` (T-103), `generics-erasure-and-pecs.md` (T-104), `exception-design-and-hierarchy-strategy.md` (T-105), `strings-interning-compact-strings-and-builders.md` (T-106), `streams-and-collectors.md` (T-107), `lambdas-and-functional-interfaces.md` (T-108), `optional-and-null-strategy.md` (T-109), `records-sealed-types-and-pattern-matching.md` (T-110), `enums-enummap-and-enumset.md` (T-111), `annotations-and-annotation-processing.md` (T-112), `reflection-and-dynamic-proxies.md` (T-113), `classloaders-and-class-initialization.md` (T-114), `serialization-hazards-and-alternatives.md` (T-115).
- Same discipline as the `collections` batch: each chapter's full "Why This Matters in Interviews" through "Core Concepts" content was read first to ground the new sections in what the chapter actually teaches (avoiding a generic, copy-pasted Foundation section across dissimilar topics — e.g., polymorphism's Level 1 uses a `Dog`/`Animal` example distinct from generics' labeled-box analogy, distinct from streams' assembly-line analogy). Each Level 1/Level 2 pair was inserted between "Why This Matters in Interviews" and "Mental Model," with zero existing sentences touched, and each chapter gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter.
- `records-sealed-types-and-pattern-matching.md`'s TOC has a non-standard extra section ("Java Version Timeline") between "Definition and Purpose" and "Core Concepts"; the insertion and renumbering correctly accounted for it rather than assuming the standard 23-item TOC shape all 14 other chapters share.
- Verified all 15 files: 1 H1 each; both new headings present and anchor-resolving (no `MD051` warning survived past the body insertion); YAML front matter parses; every cross-reference inside the new sections resolves. The same pre-existing, off-by-one-directory-level `../../practice/java/week-13/...`-style broken-link class (present in 6 of the 15 files) was reconfirmed via `git diff` to predate this batch and was left unfixed, consistent with the `collections` batch and the repository's known backlog.
- Updated `syllabus/02-java/INDEX.md` (status line, intro paragraph, the Phase 5 note, and all 15 affected topic rows) and `syllabus/00-overview/INDEX.md`'s domain-status table — `02-java` is now 24 of 49 chapters retrofitted (collections + language-core complete; jvm-internals and concurrency, 25 chapters, remain).

### Not yet done

- `jvm-internals` (12 chapters) and `concurrency` (16 chapters, minus a name overlap) — next in this retrofit.
- Every other migrated domain (132 chapters across `04`–`17`, `20`, `21`) still awaits the same retrofit.
- Cheat sheets, flashcards, and production-cookbook entries for all Phase 5 new-writing chapters this session has produced — still deferred to a separate batch.

## [2026-09-04] — Phase 5: L1/L2 retrofit continues — 02-java/jvm-internals subdomain (12 chapters)

### Added

- All 12 `02-java/jvm-internals` chapters retrofitted: `jvm-memory-layout-and-runtime-regions.md`, `object-layout-headers-and-compressed-oops.md` (T-302), `gc-roots-reachability-and-reference-strength.md` (T-303), `gc-fundamentals-and-log-analysis.md` (T-303/T-306), `zgc-and-shenandoah-concurrent-collection.md` (T-305), `escape-analysis-and-scalar-replacement.md` (T-309), `safepoints-and-stop-the-world-mechanics.md` (T-310), `native-memory-direct-buffers-and-off-heap.md` (T-311), `g1-remembered-sets-and-write-barriers.md`, `jit-tiered-compilation-and-deoptimization.md`, `jvm-flags-and-container-ergonomics.md`, `memory-leak-diagnosis-and-heap-dump-analysis.md`.
- Five of the twelve (`jvm-memory-layout`, `g1-remembered-sets`, `jit-tiered-compilation`, `jvm-flags-and-container-ergonomics`, `memory-leak-diagnosis`) have no assigned ID in the Master Topic Register (shown as `—` in `syllabus/02-java/INDEX.md`) — confirmed by checking the index before writing, so no `topic_id` field was added to those five (nothing to reuse); all twelve still gained `mastery_levels_covered: [L1, L2, L3, L4]`.
- Every chapter's full existing content (through "Core Concepts") was read first, as in the two prior batches, to ground each Level 1/Level 2 pair in that specific chapter's own subject — e.g., object headers' shipping-label analogy, GC roots' family-tree analogy, safepoints' "rally point, not just GC" distinction, and container ergonomics' two-separate-census-questions framing are each grounded in that chapter's own real content, not a reused template.
- Two chapters needed a genuinely different Level 1 framing than "explain the mechanism simply," since their subject matter is itself internals-only with no direct everyday action for a working engineer to take: `g1-remembered-sets-and-write-barriers.md`'s Level 2 explicitly states this is background knowledge rather than a tunable, and redirects the practical takeaway to a recognizable access-pattern warning sign instead of a false "here's how to configure this" framing.
- Verified all 12 files: 1 H1 each; both new headings present with correctly resolving anchors; YAML still parses; every cross-reference inside the new sections resolves. The same pre-existing broken-link class (`../../practice/java/week-09/...`, off by one directory level) was reconfirmed via `git diff` in one file, consistent with the two prior batches and the repository's known backlog — left unfixed as out of scope.
- Updated `syllabus/02-java/INDEX.md` (status line, intro paragraph, the Phase 5 note, and all 12 affected topic rows) and `syllabus/00-overview/INDEX.md`'s domain-status table: `02-java` is now 36 of 49 chapters retrofitted (`collections`, `language-core`, and `jvm-internals` complete; only `concurrency`, 13 chapters, remains).

### Not yet done

- `concurrency` (13 chapters) — the last `02-java` subdomain, next in this retrofit.
- Every other migrated domain (132 chapters across `04`–`17`, `20`, `21`) still awaits the same retrofit.
- Cheat sheets, flashcards, and production-cookbook entries for all Phase 5 new-writing chapters this session has produced — still deferred to a separate batch.

## [2026-09-04] — Phase 5: L1/L2 retrofit completes 02-java — concurrency subdomain (13 chapters, domain now 49/49)

### Added

- All 13 `02-java/concurrency` chapters retrofitted: `java-memory-model-and-volatile.md` (T-401/T-402), `reentrantlock-readwritelock-and-stampedlock.md` (T-404), `atomics-cas-and-the-aba-problem.md` (T-405), `executors-and-thread-pool-sizing.md` (T-406), `completablefuture-and-async-composition.md` (T-407), `forkjoinpool-and-work-stealing.md` (T-408), `deadlock-race-conditions-and-thread-diagnostics.md` (T-409), `virtual-threads.md` (T-410), `structured-concurrency.md` (T-411), `scoped-values-and-threadlocal-migration.md` (T-412), `threadlocal-mediated-classloader-leaks.md` (T-413), `varhandles-and-unsafe.md` (T-415), `foreign-function-and-memory-api.md` (T-416/T-414) — every one of the 13 already had a Master Topic Register ID, so every chapter also gained a `topic_id` field alongside `mastery_levels_covered: [L1, L2, L3, L4]`.
- Two of the thirteen (`varhandles-and-unsafe.md`, `foreign-function-and-memory-api.md`) are Expert-tier, rare-frequency, explicitly "recognition-level only" topics per their own scope notes — their Level 1/Level 2 sections were deliberately written lighter and narrower than the other eleven, matching that stated scope rather than inflating a false sense of everyday depth for topics the source material itself says most engineers never need directly.
- Several chapters had non-standard TOC lengths (`java-memory-model-and-volatile.md` and `virtual-threads.md` both carry an extra "Historical Context" entry; `deadlock-race-conditions-and-thread-diagnostics.md`, `threadlocal-mediated-classloader-leaks.md`, and `varhandles-and-unsafe.md` each carry extra "Java Examples"/"Failure Modes"/"Comparisons" entries; `foreign-function-and-memory-api.md` has a shorter, non-standard 19-item TOC missing several sections other chapters have) — each was renumbered correctly against its own real structure rather than assuming a uniform 23-item shape; one renumbering mistake on the first file of this batch (`java-memory-model-and-volatile.md`) was caught immediately via the IDE's own `MD029` ordered-list-prefix warnings and corrected before proceeding.
- Verified all 13 files: 1 H1 each; both new headings present with correctly resolving anchors; YAML still parses; every cross-reference inside the new sections resolves. The same pre-existing off-by-one-directory-level broken-link class was reconfirmed via `git diff` in six of the thirteen files, consistent with every prior batch this Phase 5 retrofit effort has run.
- Updated `syllabus/02-java/INDEX.md` (status line, intro paragraph, the Phase 5 note now marked domain-complete, and all 13 affected topic rows) and `syllabus/00-overview/INDEX.md`'s domain-status table. **`02-java` is now 49 of 49 chapters retrofitted — the first fully L1–L4 domain in the syllabus.**

### Not yet done

- Every other migrated domain (132 chapters across `04`–`17`, `20`, `21`) still awaits the same L1/L2 retrofit — this is now the largest remaining body of Phase 5 work, exactly as the plan's own §7.6 anticipated.
- Cheat sheets, flashcards, and production-cookbook entries for all Phase 5 new-writing chapters this session has produced — still deferred to a separate batch.

## [2026-09-04] — Phase 5: L1/L2 retrofit begins next domain — 04-software-design complete (1/1)

### Added

- `design-patterns-applied.md` (T-914), the domain's one existing chapter, retrofitted with the same additive method as every prior batch: a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section inserted between "Why This Matters in Interviews" and "Mental Model," with zero existing sentences touched. Its 25-item TOC (which already carries "Java Examples" and "Comparisons" beyond the 23-item base template) was renumbered correctly.
- Level 1 grounds the concept in `java.util.Comparator` as a real, already-familiar Strategy pattern instance; Level 2 names four everyday, already-visible pattern instances (a fluent Builder chain, a Spring bean as a managed Singleton, wrapping `InputStream`s as Decorator, passing a `Comparator` as Strategy) rather than generic, invented examples.
- Chapter gained `topic_id: T-914` and `mastery_levels_covered: [L1, L2, L3, L4]` front-matter fields, additive only.
- Verified: 1 H1; both new headings present with correctly resolving anchors; YAML parses; zero broken links (checked programmatically — this domain has no pre-existing broken-link class, unlike every `02-java` batch).
- Updated `syllabus/04-software-design/INDEX.md` and `syllabus/00-overview/INDEX.md`'s domain-status table. **`04-software-design` is now fully L1–L4 (1/1)** — being a single-chapter domain, this closes it entirely in one batch, the second fully-retrofitted domain in the syllabus after `02-java`.

### Not yet done

- 14 further migrated domains (`05`–`17` minus `04`, plus `20`, `21`) — 131 chapters — still await the same retrofit.
- Cheat sheets, flashcards, and production-cookbook entries for all Phase 5 new-writing chapters this session has produced — still deferred to a separate batch.

## [2026-09-04] — Phase 5: L1/L2 retrofit continues — 05-spring domain complete (9/9)

### Added

- All 9 `05-spring` chapters retrofitted: `spring-bean-scopes-and-proxy-modes.md` (T-502), `transactional-proxy-mechanics-and-propagation.md` (T-503/T-504/T-505), `auto-configuration-and-bean-lifecycle.md` (T-506/T-501), `spring-framework-vs-spring-boot.md` (T-506/T-501), `spring-webflux-and-reactive-programming.md` (T-509), `security-filter-chain.md` (T-511), `spring-cache-abstraction-and-pitfalls.md` (T-514), `spring-actuator-health-and-observability-hooks.md` (T-516), `spring-testing-slices-and-context-caching.md` (T-517) — same additive method as every prior batch, all 9 gained both `topic_id` and `mastery_levels_covered: [L1, L2, L3, L4]`.
- Each chapter's own content was read in full before writing its new sections, so every analogy is grounded in that chapter's own real subject: an all-or-nothing bank-transfer analogy for `@Transactional`; a dashboard-warning-lights analogy for Actuator; the shared proxy-based self-invocation gotcha stated explicitly for both `@Transactional` and `@Cacheable` (Spring's cache chapter's own text already draws this exact parallel, so the retrofit made it concrete at the Level 2 layer too, not a new claim).
- Several chapters had non-standard TOC lengths, each handled individually against its own real structure: `security-filter-chain.md` has a shorter 24-item TOC (no "Solutions" section); several others carry 26–28-item TOCs with extra Java Examples/Failure Modes/Comparisons entries; `transactional-proxy-mechanics-and-propagation.md` has the domain's largest TOC (34 items, with Historical Context plus five extra "Implications" sections).
- Verified all 9 files: 1 H1 each; both new headings present with correctly resolving anchors; YAML still parses; every cross-reference inside the new sections resolves. One pre-existing broken link was found in `auto-configuration-and-bean-lifecycle.md` (a missing `src/` path segment, confirmed via `git diff` to predate this batch) and left unfixed as out of scope.
- Updated `syllabus/05-spring/INDEX.md` and `syllabus/00-overview/INDEX.md`'s domain-status table. **`05-spring` is now fully L1–L4 (9/9)** — the third fully-retrofitted domain in the syllabus, after `02-java` and `04-software-design`.

### Not yet done

- 13 further migrated domains (`06`–`17`, `20`, `21`) — 122 chapters — still await the same retrofit.
- Cheat sheets, flashcards, and production-cookbook entries for all Phase 5 new-writing chapters this session has produced — still deferred to a separate batch.

## [2026-09-04] — Phase 5: L1/L2 retrofit continues — 06-databases domain complete (14/14)

### Added

- All 14 `06-databases` chapters retrofitted: `jpa-entity-lifecycle-and-the-n1-problem.md` (T-601/T-602), `hibernate-second-level-and-query-cache.md` (T-603), `optimistic-vs-pessimistic-locking.md` (T-604), `data-modelling-and-explicit-join-tables.md` (T-605/T-608), `hibernate-flush-modes-and-batch-writes.md` (T-606), `connection-pooling-and-sizing.md` (T-607), `index-structures-btree-composite-covering.md` (T-609), `query-planning-and-explain-analyze.md` (T-610), `isolation-levels-and-concurrency-anomalies.md` (T-611), `mvcc-vacuum-and-bloat.md` (T-612), `locks-deadlocks-and-lock-escalation.md` (T-613), `table-partitioning-and-sharding-strategies.md` (T-614), `replication-read-replicas-and-replica-lag.md` (T-615), `zero-downtime-schema-migration.md` (T-616) — same additive method as every prior batch, all 14 gained both `topic_id` and `mastery_levels_covered: [L1, L2, L3, L4]`.
- Every chapter's own content was read in full before writing its new sections, grounding each analogy in that chapter's real subject: a book-index analogy for B+Tree indexes; a "diary for one conversation vs. a shared notice board" framing distinguishing Hibernate's L1 from L2 cache; a narrow-doorway standoff for deadlocks; a filing-cabinet-split analogy for partitioning/sharding.
- Non-standard TOC lengths were handled individually against each chapter's real structure — this domain's chapters range from a 23-item base TOC up to 34 items (several chapters carry Historical Context, Execution Flow, and Performance/Memory/Concurrency/Security Implications sections beyond the base template).
- Verified all 14 files: 1 H1 each; both new headings resolving with correct anchors; YAML parses; every cross-reference resolves — zero broken links found in this domain (checked programmatically across all 14 files).
- Updated `syllabus/06-databases/INDEX.md` and `syllabus/00-overview/INDEX.md`'s domain-status table. **`06-databases` is now fully L1–L4 (14/14)** — the fourth fully-retrofitted domain in the syllabus, after `02-java`, `04-software-design`, and `05-spring`.

### Not yet done

- 12 further migrated domains (`07`–`17`, `20`, `21`) — 108 chapters — still await the same retrofit.

## [2026-09-04] — Phase 5: L1/L2 retrofit continues — 07-api-design domain complete (2/2)

### Added

- Both `07-api-design` chapters retrofitted: `api-design.md` (T-803), `api-gateway-bff-and-edge-concerns.md` (T-911) — same additive method as every prior batch, both gained `topic_id` and `mastery_levels_covered: [L1, L2, L3, L4]`.
- Each chapter's own content was read in full before writing its new sections: a phone-book-vs-bookmark analogy distinguishing `OFFSET` from keyset pagination, plus an elevator-call-button analogy for idempotency, in `api-design.md`; an apartment-building-concierge analogy for the API gateway and a personal-assistant analogy for the BFF pattern, in `api-gateway-bff-and-edge-concerns.md`.
- Verified both files: 1 H1 each; both new headings resolving with correct anchors; YAML parses; every cross-reference resolves — zero broken links found in this domain.
- Updated `syllabus/07-api-design/INDEX.md` and `syllabus/00-overview/INDEX.md`'s domain-status table. **`07-api-design` is now fully L1–L4 (2/2)** — the fifth fully-retrofitted domain in the syllabus, after `02-java`, `04-software-design`, `05-spring`, and `06-databases`.

### Not yet done

- 11 further migrated domains (`08`–`17`, `20`, `21`) — 106 chapters — still await the same retrofit.

## [2026-09-04] — Phase 5: L1/L2 retrofit continues — 08-testing domain complete (7/7)

### Added

- All 7 `08-testing` chapters retrofitted: `test-strategy-and-test-doubles.md` (T-1101/T-1103), `junit5-architecture-and-advanced-features.md` (T-1102), `integration-testing-against-real-dependencies.md` (T-1104), `contract-testing-for-services.md` (T-1105), `performance-and-load-testing-methodology.md` (T-1106), `mutation-and-property-based-testing.md` (T-1107), `writing-tests-live-in-an-interview.md` (T-1108) — same additive method as every prior batch, all 7 gained `topic_id` and `mastery_levels_covered: [L1, L2, L3, L4]`.
- Every chapter's own content was read in full before writing its new sections, grounding each analogy in that chapter's real subject: a fire-drill analogy for test doubles and the testing pyramid; a three-room-house-on-one-foundation analogy for JUnit 5's Platform/Jupiter/Vintage split; a "practicing with a fellow learner vs. a native speaker" analogy for mocked vs. real-dependency integration testing; a shared-document analogy for consumer-driven contract ownership; a bridge-load-test analogy distinguishing load/stress/soak testing; a secretly-altered-exam analogy for mutation testing; a furniture-instruction-booklet analogy for live red-green-refactor TDD.
- `mutation-and-property-based-testing.md` (Experimental tier, Rare interview frequency) received deliberately scoped, narrower Level 1/Level 2 content matching its own stated rarity, consistent with how prior Expert-tier chapters (e.g., VarHandles/Unsafe in `02-java/concurrency`) were handled.
- Verified all 7 files: 1 H1 each; both new headings resolving with correct anchors; YAML parses; every cross-reference resolves — zero broken links found in this domain.
- Updated `syllabus/08-testing/INDEX.md` and `syllabus/00-overview/INDEX.md`'s domain-status table. **`08-testing` is now fully L1–L4 (7/7)** — the sixth fully-retrofitted domain in the syllabus, after `02-java`, `04-software-design`, `05-spring`, `06-databases`, and `07-api-design`.

### Not yet done

- 10 further migrated domains (`09`–`17`, `20`, `21`) — 99 chapters — still await the same retrofit.

## [2026-09-04] — Phase 5: L1/L2 retrofit continues — 09-messaging-event-driven domain complete (9/9)

### Added

- All 9 `09-messaging-event-driven` chapters retrofitted: `kafka-architecture-fundamentals.md` (T-701/T-702/T-703/T-704/T-705), `producer-semantics-and-partition-keys.md` (T-702/T-705), `consumer-groups-and-rebalancing.md` (T-703), `delivery-semantics-and-exactly-once.md` (T-704), `consumer-lag-backpressure-and-dlq-strategy.md` (T-707), `schema-registry-and-compatibility-evolution.md` (T-708), `messaging-patterns-and-change-data-capture.md` (T-710), `event-sourcing-and-its-real-costs.md` (T-905), `event-driven-architecture-integration-styles.md` (T-906) — same additive method as every prior batch, all 9 gained `topic_id` and `mastery_levels_covered: [L1, L2, L3, L4]`.
- Every chapter's own content was read in full before writing its new sections, grounding each analogy in that chapter's real subject: a post-office-bins analogy for partitions/keys and replication; a certified-mail analogy for `acks`/idempotence; a restaurant-waitstaff analogy for consumer groups and rebalancing; a to-do-list-checkbox analogy for at-least-once vs. at-most-once delivery; a single-lane-conveyor-belt analogy for consumer lag, poison messages, and DLQs; a shared-paper-form analogy for schema compatibility modes; a security-camera-vs-clerk analogy for CDC vs. the outbox pattern plus a ticket-queue-vs-radio-broadcast analogy for point-to-point vs. publish-subscribe; a checkbook-register analogy for event sourcing and snapshotting; a group-dinner-planning analogy for choreography vs. orchestration.
- Verified all 9 files: 1 H1 each; both new headings resolving with correct anchors; YAML parses; every cross-reference resolves — zero broken links found in this domain.
- Updated `syllabus/09-messaging-event-driven/INDEX.md` and `syllabus/00-overview/INDEX.md`'s domain-status table. **`09-messaging-event-driven` is now fully L1–L4 (9/9)** — the seventh fully-retrofitted domain in the syllabus, after `02-java`, `04-software-design`, `05-spring`, `06-databases`, `07-api-design`, and `08-testing`.

### Not yet done

- 9 further migrated domains (`10`–`17`, `20`, `21`) — 90 chapters — still await the same retrofit.

## [2026-09-04] — Phase 5: L1/L2 retrofit continues — 10-distributed-systems domain complete (5/5)

### Added

- All 5 `10-distributed-systems` chapters retrofitted: `distributed-transactions-saga-and-outbox.md` (T-618), `data-partitioning-and-consistent-hashing.md` (T-806), `cap-theorem-and-consistency-models.md` (T-807), `multi-region-failover-and-disaster-recovery.md` (T-814), `distributed-systems-failure-modes.md` (T-909) — same additive method as every prior batch, all 5 gained `topic_id` and `mastery_levels_covered: [L1, L2, L3, L4]`.
- Every chapter's own content was read in full before writing its new sections, grounding each analogy in that chapter's real subject: a mailed-invitation-plus-text analogy for the dual-write hazard and the outbox pattern; a classroom-locker-assignment analogy for naive modulo hashing versus a ring analogy for consistent hashing; a two-library-branches analogy for CAP; a personal-backup analogy for RPO/RTO plus a two-people-both-watering-the-plants analogy for split-brain; an unanswered-text-message analogy for the general network-ambiguity problem behind retries, idempotency, and fencing tokens.
- Verified all 5 files: 1 H1 each; both new headings resolving with correct anchors; YAML parses; every cross-reference resolves — zero broken links found in this domain.
- Updated `syllabus/10-distributed-systems/INDEX.md` and `syllabus/00-overview/INDEX.md`'s domain-status table. **`10-distributed-systems` is now fully L1–L4 (5/5)** — the eighth fully-retrofitted domain in the syllabus, after `02-java`, `04-software-design`, `05-spring`, `06-databases`, `07-api-design`, `08-testing`, and `09-messaging-event-driven`.

### Not yet done

- 8 further migrated domains (`11`–`17`, `20`, `21`) — 85 chapters — still await the same retrofit.

## [2026-09-04] — Phase 5: L1/L2 retrofit continues — 11-system-design domain complete (9/9)

### Added

- All 9 `11-system-design` chapters retrofitted: `resilience-patterns.md` (T-515), `storage-selection-tradeoffs.md` (T-617/T-811), `system-design-method-and-estimation.md` (T-801/T-802), `caching-strategies-and-invalidation.md` (T-804), `load-balancing-service-discovery-and-health-checking.md` (T-805), `rate-limiting-and-throttling-algorithms.md` (T-808), `idempotency.md` (T-809), `search-and-indexing-systems.md` (T-810), `realtime-delivery-websocket-sse-and-long-polling.md` (T-812) — same additive method as every prior batch, all 9 gained `topic_id` and `mastery_levels_covered: [L1, L2, L3, L4]`.
- Every chapter's own content was read in full before writing its new sections, grounding each analogy in that chapter's real subject: a phone-call analogy for circuit breakers/retry jitter/bulkheads; a physical-storage analogy (filing cabinet, coat-check, warehouse) for storage selection; a birthday-party-planning analogy for the six-phase design method; a sticky-note analogy for caching and stampede; a restaurant-host analogy for load balancing and health checking; a nightclub-bouncer analogy for rate-limiting algorithms; a mailed-form-with-reference-number analogy for idempotency; a library-card-catalog analogy for search indexing; a package-delivery-tracking analogy for the four real-time delivery mechanisms.
- Verified all 9 files: 1 H1 each; both new headings resolving with correct anchors; YAML parses; every cross-reference resolves — zero broken links found in this domain.
- Updated `syllabus/11-system-design/INDEX.md` and `syllabus/00-overview/INDEX.md`'s domain-status table. **`11-system-design` is now fully L1–L4 (9/9)** — the ninth fully-retrofitted domain in the syllabus, after `02-java`, `04-software-design`, `05-spring`, `06-databases`, `07-api-design`, `08-testing`, `09-messaging-event-driven`, and `10-distributed-systems`.

### Not yet done

- 7 further migrated domains (`12`–`17`, `20`, `21`) — 76 chapters — still await the same retrofit.

## [2026-09-04] — Phase 5: L1/L2 retrofit continues — 12-security domain complete (8/8)

### Added

- All 8 `12-security` chapters retrofitted: `owasp-top-10-for-backend-services.md` (T-1301), `authn-authz-rbac-vs-abac.md` (T-1302), `applied-cryptography-hashing-signing-tls.md` (T-1303), `secrets-management-and-key-rotation.md` (T-1304), `injection-input-validation-output-encoding.md` (T-1305), `supply-chain-security-sbom-and-dependency-risk.md` (T-1306), `multi-tenancy-isolation-models.md` (T-1307), `oauth2-oidc-and-jwt.md` (T-512/T-513) — same additive method as every prior batch, all 8 gained `topic_id` and `mastery_levels_covered: [L1, L2, L3, L4]`.
- Every chapter's own content was read in full before writing its new sections, grounding each analogy in that chapter's real subject: a home-burglary-risk-list analogy for the OWASP Top 10; an office-building-badge analogy for AuthN/AuthZ and RBAC/ABAC; a bouncer/notary/tamper-evident-envelope analogy for hashing/signing/TLS; an "instruction hidden in a note" analogy for injection and output encoding; an apartment-building analogy for multi-tenancy isolation models and Row-Level Security; a limited-pass-and-wax-seal analogy for OAuth2/OIDC/JWT; a storage-unit-key-generation analogy for key rotation and envelope encryption; a food-ingredient-label analogy for SBOMs and transitive dependency risk.
- Verified all 8 files: 1 H1 each; both new headings resolving with correct anchors; YAML parses; every cross-reference resolves — zero broken links found in this domain.
- Updated `syllabus/12-security/INDEX.md` and `syllabus/00-overview/INDEX.md`'s domain-status table. **`12-security` is now fully L1–L4 (8/8)** — the tenth fully-retrofitted domain in the syllabus, after `02-java`, `04-software-design`, `05-spring`, `06-databases`, `07-api-design`, `08-testing`, `09-messaging-event-driven`, `10-distributed-systems`, and `11-system-design`.

### Not yet done

- 6 further migrated domains (`13`–`17`, `20`, `21`) — 68 chapters — still await the same retrofit.

## [2026-09-04] — Phase 5: L1/L2 retrofit continues — 13-observability domain complete (4/4)

### Added

- All 4 `13-observability` chapters retrofitted: `performance-methodology-and-slo-error-budgets.md` (T-1201/T-1206), `percentiles-tail-latency-and-coordinated-omission.md` (T-1204), `logging-metrics-tracing-and-opentelemetry.md` (T-1205), `incident-response-and-blameless-postmortems.md` (T-1207) — same additive method as every prior batch, all 4 gained `topic_id` and `mastery_levels_covered: [L1, L2, L3, L4]`.
- Every chapter's own content was read in full before writing its new sections, grounding each analogy in that chapter's real subject: a car-diagnostics analogy for USE/RED plus a monthly-allowance analogy for error budgets; a coffee-shop-wait-time analogy for percentiles and coordinated omission; a package-tracking-number analogy for logs/metrics/traces and the shared traceId mechanism; a leaking-kitchen-sink analogy for mitigate-before-diagnose and blameless postmortems.
- Verified all 4 files: 1 H1 each; both new headings resolving with correct anchors; YAML parses; every cross-reference resolves — zero broken links found in this domain.
- Updated `syllabus/13-observability/INDEX.md` and `syllabus/00-overview/INDEX.md`'s domain-status table. **`13-observability` is now fully L1–L4 (4/4)** — the eleventh fully-retrofitted domain in the syllabus, after `02-java`, `04-software-design`, `05-spring`, `06-databases`, `07-api-design`, `08-testing`, `09-messaging-event-driven`, `10-distributed-systems`, `11-system-design`, and `12-security`.

### Not yet done

- 5 further migrated domains (`14`–`17`, `20`, `21`) — 64 chapters — still await the same retrofit.

## [2026-09-04] — Phase 5: L1/L2 retrofit continues — 14-devops-containers domain complete (4/4)

### Added

- All 4 `14-devops-containers` chapters retrofitted: `container-image-internals.md` (T-1001), `kubernetes-objects-scheduling-and-networking.md` (T-1002), `kubernetes-resource-limits-probes-and-jvm-sizing.md` (T-1003), `cicd-pipeline-design-and-deployment-strategies.md` (T-1009) — same additive method as every prior batch, all 4 gained `topic_id` and `mastery_levels_covered: [L1, L2, L3, L4]`.
- Every chapter's own content was read in full before writing its new sections, grounding each analogy in that chapter's real subject: an overhead-projector-transparency analogy for image layers, a curtain-and-budget analogy for namespaces/cgroups; a restaurant-shift-manager analogy for Deployments/ReplicaSets/Services; a storage-unit-and-loose-items analogy for `OutOfMemoryError` vs. OOM kill, a manager-check-in analogy for liveness/readiness/startup probes; a restaurant-new-menu-rollout analogy for rolling/blue-green/canary deployments.
- Verified all 4 files: 1 H1 each; both new headings resolving with correct anchors; YAML parses; every cross-reference resolves — zero broken links found in this domain.
- Updated `syllabus/14-devops-containers/INDEX.md` and `syllabus/00-overview/INDEX.md`'s domain-status table. **`14-devops-containers` is now fully L1–L4 (4/4)** — the twelfth fully-retrofitted domain in the syllabus, after `02-java`, `04-software-design`, `05-spring`, `06-databases`, `07-api-design`, `08-testing`, `09-messaging-event-driven`, `10-distributed-systems`, `11-system-design`, `12-security`, and `13-observability`.

### Not yet done

- 4 further migrated domains (`15`–`17`, `20`, `21`) — 60 chapters — still await the same retrofit.

## [2026-09-04] — Phase 5: L1/L2 retrofit continues — 15-cloud domain complete (3/3)

### Added

- All 3 `15-cloud` chapters retrofitted: `aws-core-services-for-backend-engineers.md` (T-1006), `cloud-cost-and-scaling-economics.md` (T-1007), `twelve-factor-config.md` (T-1008) — same additive method as every prior batch, all 3 gained `topic_id` and `mastery_levels_covered: [L1, L2, L3, L4]`.
- Every chapter's own content was read in full before writing its new sections, grounding each analogy in that chapter's real subject: a car-ownership-spectrum analogy for the EC2/ECS/EKS/Lambda compute trade-off, plus a safe-deposit-box/external-hard-drive/shared-network-drive analogy for S3/EBS/EFS and a vending-machine-vs-grocery-store analogy for DynamoDB vs. RDS, plus a walkie-talkie-vs-pager analogy touched on for SNS/SQS (AWS core services); a gym-membership analogy for on-demand/reserved/spot pricing and the peak-vs-baseline reservation-sizing mistake (cloud cost economics); a recipe-card-vs-fridge-ingredients analogy for config/code separation, plus a kitchen-readiness analogy for the health-check/config-completeness gap (twelve-factor config).
- Verified all 3 files: 1 H1 each; both new headings resolving with correct anchors; YAML parses; every cross-reference resolves — zero broken links found in this domain.
- Updated `syllabus/15-cloud/INDEX.md` and `syllabus/00-overview/INDEX.md`'s domain-status table. **`15-cloud` is now fully L1–L4 (3/3)** — the thirteenth fully-retrofitted domain in the syllabus, after `02-java`, `04-software-design`, `05-spring`, `06-databases`, `07-api-design`, `08-testing`, `09-messaging-event-driven`, `10-distributed-systems`, `11-system-design`, `12-security`, `13-observability`, and `14-devops-containers`.

### Not yet done

- 3 further migrated domains (`16-performance-jvm`, `17-architecture`, `20-interview-preparation`, `21-frontend-web`) — 63 chapters — still await the same retrofit.

## [2026-09-04] — Phase 5: L1/L2 retrofit continues — 16-performance-jvm domain complete (3/3)

### Added

- All 3 `16-performance-jvm` chapters retrofitted: `profiling-jfr-and-flame-graphs.md` (T-1202), `benchmarking-and-jmh-pitfalls.md` (T-1203), `capacity-planning-and-headroom.md` (T-1208) — same additive method as every prior batch, all 3 gained `topic_id` and `mastery_levels_covered: [L1, L2, L3, L4]`.
- Every chapter's own content was read in full before writing its new sections, grounding each analogy in that chapter's real subject: a traffic-helicopter-photographing-a-highway analogy for sampling profilers and flame-graph width, connected directly to this chapter's own real finding (an autoboxing call outweighing a deliberate O(n²) hotspot); a timing-a-sprinter analogy for JIT warmup, dead-code elimination, and the `Blackhole` mechanism, connected to this chapter's own measured ~22% dead-code-elimination gap; a call-center-hold-time analogy for Little's Law and the saturation cliff, connected to this chapter's own two independently-measured values of `L` agreeing within 0.8%.
- Verified all 3 files: 1 H1 each; both new headings resolving with correct anchors; YAML parses; every cross-reference resolves — zero broken links found in this domain.
- Updated `syllabus/16-performance-jvm/INDEX.md` and `syllabus/00-overview/INDEX.md`'s domain-status table. **`16-performance-jvm` is now fully L1–L4 (3/3)** — the fourteenth fully-retrofitted domain in the syllabus, after `02-java`, `04-software-design`, `05-spring`, `06-databases`, `07-api-design`, `08-testing`, `09-messaging-event-driven`, `10-distributed-systems`, `11-system-design`, `12-security`, `13-observability`, `14-devops-containers`, and `15-cloud`.

### Not yet done

- 3 further migrated domains (`17-architecture`, `20-interview-preparation`, `21-frontend-web`) — 60 chapters — still await the same retrofit.

## [2026-09-04] — Phase 5: L1/L2 retrofit continues — 17-architecture domain complete (9/9)

### Added

- All 9 `17-architecture` chapters retrofitted: `clean-hexagonal-architecture.md` (T-901), `ddd-strategic-bounded-contexts-and-context-mapping.md` (T-902), `ddd-tactical-design-aggregates.md` (T-903), `cqrs-read-write-separation.md` (T-904), `microservice-decomposition-and-monolith-tradeoff.md` (T-907/T-908), `modular-monolith-as-a-deliberate-choice.md` (T-910), `strangler-fig-and-migration-patterns.md` (T-912), `technical-debt-and-evolutionary-architecture.md` (T-913), `architecture-decision-records.md` (T-916) — same additive method as every prior batch, all 9 gained `topic_id` and `mastery_levels_covered: [L1, L2, L3, L4]`.
- Every chapter's own content was read in full before writing its new sections, grounding each analogy in that chapter's real subject: a wall-power-outlet analogy for ports/adapters; a "football means different sports in different countries" analogy for bounded contexts; a packing-boxes-for-a-move analogy for aggregate boundaries; a newsroom-vs-printed-newspaper analogy for the write/read model lag; a roommates-splitting-apartments analogy for service boundaries; a door-sign-vs-real-lock analogy for enforced module boundaries; a renovating-a-house-while-living-in-it analogy for incremental migration and rollback safety; a worn-brake-pads-and-vehicle-inspection analogy for economic debt framing and fitness functions; a scientist's-lab-notebook analogy for durable decision reasoning.
- Verified all 9 files: 1 H1 each; both new headings resolving with correct anchors; YAML parses; every cross-reference resolves — zero broken links found in this domain.
- Updated `syllabus/17-architecture/INDEX.md` and `syllabus/00-overview/INDEX.md`'s domain-status table. **`17-architecture` is now fully L1–L4 (9/9)** — the fifteenth fully-retrofitted domain in the syllabus, after `02-java`, `04-software-design`, `05-spring`, `06-databases`, `07-api-design`, `08-testing`, `09-messaging-event-driven`, `10-distributed-systems`, `11-system-design`, `12-security`, `13-observability`, `14-devops-containers`, `15-cloud`, and `16-performance-jvm`.

### Not yet done

- 2 further migrated domains (`20-interview-preparation`, `21-frontend-web`) — 51 chapters — still await the same retrofit.

## [2026-09-04] — Phase 5: L1/L2 retrofit continues — 20-interview-preparation domain complete (21/21)

### Added

- All 21 `20-interview-preparation` chapters retrofitted across `behavioral/` (16: `01-star-framework-and-delivery.md` through `15-offer-evaluation-and-negotiation.md` plus `company-loop-structures-and-question-pattern-recognition.md`), `coding/` (`coding-interview-communication-protocol.md`), `system-design/` (`system-design-narration-and-whiteboard-discipline.md`, `time-boxing-and-mid-round-changes.md`), and `technical-answers/` (`technical-answer-framework.md`, `trade-off-narration-and-adrs.md`) — the largest single-domain batch this window, spanning two different chapter templates (bullet-style TOC with a "Mental Model" heading for the 15 numbered behavioral chapters; numbered TOC with a "Why This Exists" heading for the four `playbook-technical-answer`-template entries).
- Every chapter's own content was read in full before writing its new sections, grounding each analogy in that chapter's specific subject with 21 distinct images: an ER intake form (STAR structure), a mechanic's labeled socket set (story portfolio), a photograph crop (scope reframing), a doctor's differential diagnosis (incident narratives), buying a real car vs. a magazine dream car (architecture trade-off narration), a debate club's steelman drill (conflict stories), teaching a kid to ride a bike (mentoring), a chef sending back an over-salted dish (failure narratives), a neighborhood fence-repair pitch (cross-team influence), vascular bypass surgery with staged clamping (migrations), a landlord roof-repair pitch (technical debt advocacy), a book editor catching a plot hole (design reviews/RFCs), a diplomat's toast adapted to the host country (company-specific frameworks), house-hunting and asking the right question of the right informant (questions to ask interviewers), a used-car purchase and the dealership's unwritten promises (offer negotiation), a touring comedian's setlist notebook (loop structures), a driving instructor's commentary drive (coding communication protocol), a timed multi-course dinner party with a last-minute allergy (time-boxing), a live city-map tour guide (system-design narration), a restaurant tasting menu with courses already prepped (the nine-layer technical-answer framework), and a hiking-trail daylight constraint (trade-off narration/ADRs) — no analogy reused across the 21.
- All 21 gained `mastery_levels_covered: [L1, L2, L3, L4]`; 20 of 21 gained a real `topic_id` read from each chapter's own register callout or, for the 15 base behavioral chapters (which carry no per-chapter register line of their own), from `02-story-portfolio-design.md`'s own competency-matrix table (T-1501 through T-1515). `time-boxing-and-mid-round-changes.md` is a genuine companion entry to T-801/T-802 with no dedicated T-code of its own — `topic_id` was deliberately left unset there rather than fabricated, per this project's no-fabrication rule. `trade-off-narration-and-adrs.md` carries the dual `T-1505/T-916` ID its own file already documented, shared deliberately with the behavioral architecture-trade-off-narration chapter.
- Verified all 21 files: 1 H1 each; both new headings resolving with correct anchors; YAML parses; every cross-reference resolves — zero broken links found in this domain.
- Updated `syllabus/20-interview-preparation/INDEX.md` (a structurally different index — three separate Old-path/New-path tables rather than the single Topic-ID table other domains use — updated via its status line and a new Phase 5 blockquote rather than per-row edits) and `syllabus/00-overview/INDEX.md`'s domain-status table. **`20-interview-preparation` is now fully L1–L4 (21/21)** — the sixteenth fully-retrofitted domain in the syllabus, after `02-java`, `04-software-design`, `05-spring`, `06-databases`, `07-api-design`, `08-testing`, `09-messaging-event-driven`, `10-distributed-systems`, `11-system-design`, `12-security`, `13-observability`, `14-devops-containers`, `15-cloud`, `16-performance-jvm`, and `17-architecture`.

### Not yet done

- 1 further migrated domain (`21-frontend-web`) — 32 chapters — but see the entry immediately below: this domain does not need the same retrofit and was instead given a formal mastery-equivalence mapping.

## [2026-09-05] — Phase 5: 21-frontend-web mastery equivalence mapped (32/32) — not a content retrofit

### Added

- Formally mapped all 32 `21-frontend-web` chapters' existing Beginner/Intermediate/Advanced/Expert register tiers onto the syllabus-wide L1–L4 mastery model: **Beginner → L1, L2**; **Intermediate → L2, L3**; **Advanced → L3, L4**; **Expert → L4**. The one interview-craft entry with no register tier (`frontend-live-coding-and-debugging-protocol.md`) was mapped to `[L2, L3, L4]` from its own stated `target_levels: [mid, senior, staff]`, with no fabricated `topic_id`.
- Added `topic_id` (the existing F-code from each chapter's own `> **Topic register:**` line) and `mastery_levels_covered` to all 32 files' YAML front matter, and bumped `last_updated` to 2026-09-05. This was deliberately front-matter-only — no chapter body content was added, removed, or reworded.

### Not a Phase 5 content retrofit, and why

- This domain was explicitly exempted from the Level-1/Level-2 content-insertion retrofit applied to the other 16 syllabus domains this phase. Per `00-project/syllabus-transformation-plan.md` §3: "the frontend domain is the exception that proves the rule" — `00-project/frontend-topic-register.md` already spans Beginner/Intermediate/Advanced/Expert by original design (the 2026-08-12 Scope Addendum), with each individual chapter deliberately written at one specific tier rather than needing Foundation/Working-Knowledge layers added underneath existing Senior/Staff-only content, unlike the ~181 backend `handbook/` chapters the retrofit targets. What was genuinely missing, and is what this entry closes, was a stated equivalence between the frontend domain's own four-tier system and the syllabus-wide L1–L4 model — a mapping question, not a missing-content question.
- User-confirmed scope decision (2026-09-05): asked explicitly whether to (a) mark the domain complete as-is, (b) formally map Beginner–Expert to L1–L4, or (c) insert Level 1/Level 2 sections anyway regardless of the exemption — the user chose (b).
- Updated `syllabus/21-frontend-web/INDEX.md` (status line, a new Phase 5 blockquote explaining the mapping rule and rationale, and the Topics table's "Mastery levels covered today" column rewritten per-row from the placeholder "Beginner–Expert (L1–L4 equivalence not yet formally mapped)" text to the actual tier→L-level mapping) and `syllabus/00-overview/INDEX.md`'s domain-status table.

**All 17 backend/interview-prep domains needing the Phase 5 content retrofit are now complete (16/16 retrofitted + this domain's equivalence mapping closes the last open item on the Phase 5 tracking list), alongside the 4 domains that were new-writing-complete from Phase 5's start (`01-computer-science-foundations`, `03-data-structures-algorithms`, `18-engineering-practices`, `19-leadership-staff`) — all 21 syllabus domains now have a stated, non-placeholder mastery-coverage status.**

### Not yet done

- Cheat sheets, flashcards, and production-cookbook entries for all Phase 5 new-writing chapters this session has produced — still deferred to a separate batch.

## [2026-09-05] — Phase 6: Learning-path assembly complete (6/6)

### Added

- All six learning paths named in `00-project/syllabus-transformation-plan.md` §6 assembled as real, short documents in the new `syllabus/00-overview/learning-paths/` directory: `junior-to-mid.md`, `mid-to-senior.md`, `senior-interview-refresh.md`, `senior-to-staff.md`, `interview-emergency-sprint.md`, `backend-java-specialization.md`. Each is an ordered list of real topic links, a stated time budget, and per-topic (or per-domain) stop-at-level guidance, per §6's own definition — never a copy of topic content.
- **Junior → Mid** and **Senior → Staff** curate individual topics across domains (14 and 16 respectively) — genuine cross-domain hand-picking. **Mid → Senior** and **Backend Java specialization** sequence whole domains, pointing to each domain's own `INDEX.md` as the exhaustive topic list rather than re-listing every topic inside it, avoiding duplication of content the domain index already owns. **Senior interview refresh** is a review-mode rotation through existing `cheat-sheets/`/`flashcards/` plus a fixed set of delivery-mechanics chapters, adding zero new topic content by design. **Interview emergency sprint** adds no content at all — it points wholesale at `study-packs/`, exactly as the Phase 1 outline specified.
- Verified all six documents' links resolve on disk — zero broken links.
- Updated `syllabus/00-overview/learning-paths.md` (kept its Phase 1 outline table verbatim per its own provenance note; appended a new "Phase 6 update" section linking to the six real documents and explaining the two structural approaches used) and `syllabus/00-overview/INDEX.md` (updated the stale "What's next" section, last accurate as of 2026-09-03, to reflect Phase 5's completion across all 21 domains and Phase 6's completion, and to correctly describe Phase 4 and Phase 7 as the two remaining, not-yet-executed phases).

### Not yet done

- Phase 7 (Deprecation of old paths) — the only destructive phase — has not started and requires its own separate approval.
- `18-engineering-practices/git-internals-and-collaboration-workflows.md` remains L3/L4 only, flagged for a future retrofit pass.
- Cheat sheets, flashcards, and production-cookbook entries for the Phase 5 new-writing chapters — still deferred to a separate batch (carried over from the prior entry).

## [2026-09-06] — Phase 4: Cross-linking pass complete — 526 files, zero broken links

### Added

- Ran Phase 4 (§10 of `00-project/syllabus-transformation-plan.md`) as its own explicit, audited sweep for the first time — every reference across `cheat-sheets/`, `flashcards/`, `production-cookbook/`, `practice/`, `architecture-atlas/`, and `study-packs/` to a pre-migration path (`handbook/<domain>/`, `behavioral-handbook/`, `interview-playbook/{behavioral,coding,system-design,technical-answers}/`) rewritten to its real, current `syllabus/` canonical location.
- Built the old→new mapping (209 entries) directly from every `syllabus/**/*.md` file's own `source_history:` front-matter field — the authoritative, already-recorded ground truth from each domain's own Phase 3 migration — rather than trusting `00-project/migration-mapping.md`'s prose (which itself documents 3 self-corrected discrepancies) or reconstructing paths by guesswork. 11 additional entries (the 5 permanently-open, no-IWI `jvm` chapters plus 6 more) were added after discovering they predate the `source_history` convention and needed their real destination verified directly on disk before mapping.
- Rewrote 526 files: 159 `cheat-sheets/`, 133 `flashcards/`, 132 `production-cookbook/`, 80 `study-packs/`, 21 `practice/` (14 frontend demo-app comments, a K8s YAML comment, 2 SQL shell-script comments, a Next.js layout comment, and the file this count doesn't include — see manual fixes below), 1 `architecture-atlas/`. Two rewrite modes per match: a real relative link (preceded by `../` or `./`) got its correct new relative path computed via `os.path.relpath`; a bare mention (a `canonical:`/`source:`/`related_handbook:` front-matter field, or a plain-text citation) got the new path substituted directly, anchor preserved.
- **A real gap, caught and fixed before completion, not after:** the first-pass exploratory `grep` used to scope the work had a boundary-regex bug that silently failed to match the extremely common `../handbook/...` form (the character class excluded `/` from what could precede `handbook`, so `../handbook/x.md` never matched but `[handbook/x.md]` did) — this caused `cheat-sheets/` (and, more narrowly, `architecture-atlas/` and `study-packs/`) to be scoped out of the first mechanical rewrite pass entirely, on the false evidence of a "0 matches" count. Caught during the post-fix verification sweep (not assumed clean), corrected by re-running the mechanical rewrite against all three directories, and re-verified at zero remaining stale references before treating the task as done.
- 7 files needed manual, non-mechanical fixes rather than the dictionary-driven rewrite: directory-level `interview-playbook/` pointers with no specific filename in `cheat-sheets/polymorphism-and-dynamic-dispatch.md`, `practice/mock-interviews/README.md`, and `architecture-atlas/README.md`; present-tense factual claims mixing a stale `handbook/` mention with an already-correct `syllabus/` one in the same sentence, in `practice/mock-interviews/kafka-messaging-technical-round.md`, `practice/mock-interviews/spring-technical-round.md`, `study-packs/week-22/03-concurrency-coding-practice.md`, and `study-packs/week-17/MANIFEST.md`.
- **Deliberately left untouched, and why:** several files contain `handbook/<domain>/` mentions inside genuine historical narrative — `cheat-sheets/README.md` and `flashcards/README.md`'s own batch-history sections, and `study-packs/week-{16,17,18,19}/MANIFEST.md`'s "N new `handbook/X/` chapters, written full-depth from the start" provenance lines — describing what was true *at the time the chapter was first written*, before that domain's own Phase 3 migration. Rewriting these to say `syllabus/` would misrepresent the actual history (the chapter did not live at `syllabus/` when that sentence was written), the same reasoning that keeps the root `CHANGELOG.md`'s own historical entries unedited. `CLAUDE.md`/`AGENTS.md` (illustrative cross-reference examples, not real links) and `00-project/` (planning/audit documents, frozen by design) were confirmed out of scope and left alone. `CONTRIBUTING.md` line 45 ("Before committing anything in `study-packs/*/`, `interview-playbook/behavioral/`...") was a genuine, currently-stale instruction — flagged for the user rather than silently edited (since root-file rewrites were an explicit Phase 1 deferral), then fixed on the user's explicit go-ahead: `interview-playbook/behavioral/` → `syllabus/20-interview-preparation/behavioral/`, a single-line, narrowly-scoped correction, not the broader CLAUDE.md/README.md framing rewrite that remains deferred.
- **A distinct, pre-existing defect class found and fixed along the way, unrelated to the path migration:** 10 `production-cookbook/` files had a real, separate relative-path bug — `../../practice/...` where `production-cookbook/` is only one directory below repo root, so the correct depth is `../practice/...` — plus one file with two bare old-domain-name references (`../architecture/`, `../system-design/`) that were never valid relative paths at all. All 17 instances corrected and verified against real files on disk.
- Verified with a full, corrected link-resolution pass across all 526 touched files (both real markdown link URLs and `canonical:`/`source:`/`related_handbook:` front-matter fields): 4,091 references checked, zero broken.

### Not yet done

- Phase 7 (Deprecation of old paths) — the only destructive phase — has not started and requires its own separate approval.
- `18-engineering-practices/git-internals-and-collaboration-workflows.md` remains L3/L4 only, flagged for a future retrofit pass.
- Cheat sheets, flashcards, and production-cookbook entries for the Phase 5 new-writing chapters — still deferred to a separate batch (carried over from the prior entry).

## [2026-09-06] — Phase 7: Deprecation of old paths — redirect stubs removed

### Removed

- Per `00-project/syllabus-transformation-plan.md` §10 ("remove redirect stubs at old `handbook/<domain>/` paths ... Old paths removed"), on the user's explicit go-ahead following Phase 4's completion report. This is the plan's only destructive phase; scope was held to exactly what the plan names — redirect stubs, never content.
- 16 tracked `.gitkeep` redirect stubs removed via `git rm`: all 13 remaining `handbook/<domain>/` stubs (`architecture`, `cloud`, `collections`, `concurrency`, `databases`, `java-core`, `jvm`, `kafka`, `performance`, `security`, `spring`, `system-design`, `testing`) and the 3 remaining `interview-playbook/{coding,system-design,technical-answers}/` stubs. `interview-playbook/behavioral/` had no tracked stub (already bare) and `behavioral-handbook/` had no tracked content at all — both were already-empty, untracked local directories, removed from disk but never part of the git index.
- The `handbook/` directory (including `handbook/frontend/`, which held no content) and `behavioral-handbook/` no longer exist. `interview-playbook/` still exists and is unaffected in substance — it retains `README.md`, `company-prep/` (private, real content, untouched), and the empty `frontend/` directory (a different domain's home per the Scope Addendum, out of Phase 7's scope regardless of its emptiness).
- **Verified safe before removal, not assumed:** re-ran the same live-reference scan used for Phase 4, scoped to the same four old-path patterns, across the whole repository. Every remaining hit was either (a) a `source_history:` front-matter field or planning-document prose recording real migration provenance (`00-project/`, `syllabus/00-overview/vision.md`/`taxonomy.md`, both verbatim plan extracts), (b) historical batch-history narrative already identified and deliberately preserved in Phase 4 (`cheat-sheets/README.md`, `flashcards/README.md`, `production-cookbook/README.md`, `interview-playbook/README.md`), or (c) `CLAUDE.md`/`AGENTS.md` — no live navigational link anywhere in the repository still pointed at a stub path.
- **Explicitly out of scope, left alone:** `CLAUDE.md`'s and `AGENTS.md`'s own Repository Structure sections still document `handbook/`, `behavioral-handbook/`, and `interview-playbook/{behavioral,coding,system-design,technical-answers}/` as the canonical structure — both predate the `syllabus/` migration entirely and were already known-stale before this phase (a pre-existing documentation debt, not something Phase 7 created). `resources/repository-tree.md` is a static, previously-generated directory snapshot that already referenced a stub (`interview-playbook/behavioral/.gitkeep`) that did not even exist in the tracked tree before this phase ran — it was stale independent of this change and was not regenerated, since Phase 7's own scope is "old paths removed," not "regenerate unrelated generated artifacts."

### Not yet done

- `CLAUDE.md`/`AGENTS.md` Repository Structure sections and `resources/repository-tree.md` still describe the pre-migration layout — a documentation-debt item distinct from Phase 7's own scope, flagged here for visibility rather than fixed silently.
- `18-engineering-practices/git-internals-and-collaboration-workflows.md` remains L3/L4 only, flagged for a future retrofit pass.
- Cheat sheets, flashcards, and production-cookbook entries for the Phase 5 new-writing chapters — still deferred to a separate batch.

## [2026-09-06] — Phase 5 gap closed: git-internals-and-collaboration-workflows.md now L1–L4

### Added

- Retrofitted `18-engineering-practices/git-internals-and-collaboration-workflows.md` — the one chapter left L3/L4-only after its domain's 2026-09-03 closure — with real Level 1 — Foundation and Level 2 — Working Knowledge sections, following the same pattern used across the other 16 backend-domain retrofits this transformation plan ran.
- Level 1 builds a library content-deduplication analogy (identical content never stored twice, addressed by what it is) and a bookmark analogy (a branch is a movable pointer, not the content itself) — a fresh analogy, not reused from any prior retrofit. Level 2 extends the same picture to why `reset --hard` and `reflog` are non-destructive in practice, and why `rebase` re-copies commits with new identity while `merge` does not — building directly toward the chapter's own existing Mental Model and Core Concepts sections rather than restating them.
- Front matter gained `mastery_levels_covered: [L1, L2, L3, L4]`; no `topic_id` was added, consistent with the chapter's own stated Topic register note that it has no blueprint topic ID by design. `last_updated` bumped to 2026-09-06. Table of contents renumbered (+2 entries).
- Verified: both new headings' anchors resolve against the table of contents, exactly one H1, YAML parses.
- Updated `syllabus/18-engineering-practices/INDEX.md` and `syllabus/00-overview/INDEX.md`'s domain-status table and "What's next" section. **This closes the last remaining Phase 5 exception — all 21 syllabus domains are now genuinely L1–L4 with zero known gaps.**

### Not yet done

- Cheat sheets, flashcards, and production-cookbook entries for the Phase 5 new-writing chapters (including this one) remain deferred to a separate batch — the one non-blocking item left across the entire transformation plan.

## [2026-09-06] — Cheat-sheets backlog closed for all four new-writing domains (31 files)

### Added

- Closed the standing backlog line carried in every changelog entry since Phase 5 began ("cheat sheets, flashcards, and production-cookbook entries for the Phase 5 new-writing chapters — still deferred to a separate batch") for its cheat-sheets third: 31 new files in `cheat-sheets/`, one per chapter in `01-computer-science-foundations` (5), `03-data-structures-algorithms` (17), `18-engineering-practices` (4 — `git-internals-and-collaboration-workflows.md` already had one), and `19-leadership-staff` (5).
- Built as five parallel, bounded batches (one per domain, `03-data-structures-algorithms` split into two sub-batches of 9 and 8), each reading its assigned chapter fully before writing, per `CLAUDE.md`'s instruction against one giant operation.
- Every fact, complexity claim, recognition signal, and pitfall was extracted directly from its own chapter's text — no invented numbers, scenarios, frameworks, or first-person anecdotes. DSA pattern chapters use a "Recognition Signals / When to Use This Pattern" table plus a Complexity Reference section in place of a generic decision table; leadership chapters use a "Warning Signs" section instead of "Production Warning Signs" and stay general per `CLAUDE.md`'s Behavioral Handbook Standard (no invented company or person).
- Verified after writing: front-matter fields and YAML validity, exactly one H1 per file, balanced code fences, and every `canonical:`/Related path resolved against the real filesystem — 169 references checked across the 31 files, zero broken.
- Updated `cheat-sheets/README.md` with a new "New-Writing Domain Cheat Sheets" table and scope note (total now 194 cheat sheets: 132 backend + 31 frontend + 31 new-writing-domain), and corrected a pre-existing stale `domain` field for `git-internals-and-collaboration-workflows.md`'s own table row (`cloud` → `engineering-practices`, left over from its pre-migration `handbook/cloud/` location).
- Updated all four domains' `INDEX.md` files to reflect that cheat sheets now exist, while leaving the flashcards/production-cookbook portion of the backlog explicitly still open.

### Not yet done

- Flashcards and production-cookbook entries for these same 31 (32 including git-internals) new-writing chapters remain unbuilt — the only item left in the "Phase 5 new-writing chapters" backlog.

## [2026-09-07] — Flashcards backlog closed for all four new-writing domains (31 decks, 174 cards)

### Added

- Closed the flashcards third of the standing "Phase 5 new-writing chapters" backlog (the cheat-sheets third closed 2026-09-06): 31 new decks in `flashcards/`, one per chapter in `01-computer-science-foundations` (5), `03-data-structures-algorithms` (17), `18-engineering-practices` (4), and `19-leadership-staff` (5) that lacked one (`git-internals-and-collaboration-workflows.md` already had one from an earlier batch).
- These chapters use the newer 20-section syllabus topic template, which carries no embedded `## Flashcards` section — unlike the 137 pre-existing decks, every card here is genuinely new, authored from the full chapter text (not copied from a pre-existing section), same no-fabrication discipline as everywhere else in this repository.
- Built as five parallel, bounded batches (one per domain, `03-data-structures-algorithms` split into two sub-batches of 9 and 8). Cards favor the chapter's own concrete numbers, named bugs, and named frameworks over generic restatements (e.g. the knapsack loop-direction rule, the discount-cliff characterization-test finding, GROW/SBI/Fowler's debt quadrant where a chapter actually states them).
- Verified after writing: front-matter fields and YAML validity, exactly one H1 per file, every card's full Prompt/Answer/Why-it-matters/Common-trap/Related quintet present, and every canonical/Related link resolved against the real filesystem — 174 cards across 31 decks, zero broken links.
- Updated `flashcards/README.md` with an expanded "New-Writing Domain Decks" table (168 decks, 593 cards total) and corrected a stale `handbook/` reference in its own "How this relates to other deliverables" section (now `syllabus/`). Updated all four domains' `INDEX.md` files.

### Not yet done

- Production-cookbook entries for these same 32 new-writing chapters (including git-internals) remain unbuilt — the only item left in the "Phase 5 new-writing chapters" backlog.
- Flashcards has no frontend-domain leg (unlike `cheat-sheets/`, which covers 31 frontend chapters) — a separate, pre-existing gap, not part of this backlog and not closed here.

## [2026-09-07] — Production-cookbook backlog investigated and closed (no new files)

### Investigated

- The last item in the "Phase 5 new-writing chapters" backlog was production-cookbook entries for the same 32 chapters covered by the cheat-sheets (2026-09-06) and flashcards (2026-09-07) batches. Before writing anything, checked each chapter's own `production_scenarios` front-matter field (or, for `git-internals-and-collaboration-workflows.md`, its inline `## Production Scenarios` section) against `production-cookbook/README.md`'s own long-standing rule: this deliverable only ever elevates a scenario a chapter has already worked out (symptoms, evidence, diagnosis), never invents one from scratch — stated repeatedly across its batch history, and the reason it declares itself "complete" between batches rather than perpetually open.
- Result: 13 of the 32 chapters already cite an existing entry (no duplication needed, correctly resolved already). The other 19 (14 in `03-data-structures-algorithms`, 2 in `18-engineering-practices`, 2 in `19-leadership-staff`, 1 in `01-computer-science-foundations`) have no citation, but every one of their own Section 14/"Production Scenarios" texts already states this explicitly and gives a `Planned reference` describing the specific future scenario's shape — not a silent placeholder. None has a chapter-internal worked scenario to elevate; writing a new entry from just a `Planned reference` line would mean inventing the incident, which this deliverable's own rule forbids.
- Conclusion: **no new production-cookbook files were warranted or written.** This closes the backlog item honestly, the same way the 5 no-IWI `jvm` cheat-sheet chapters and the 2 people-incident leadership chapters (`mentoring-and-developing-others.md`, `design-reviews-and-rfcs-as-organizational-practice.md`, already resolved when written) were closed — as a documented, permanent gap rather than forced content.
- Updated `production-cookbook/README.md` with a note recording this investigation and its finding, and all four domains' `INDEX.md` files plus `syllabus/00-overview/INDEX.md`'s "What's next" section to state this accurately instead of "still deferred to a separate batch."

### Not yet done

- Nothing remains from the "Phase 5 new-writing chapters" complementary-deliverable backlog. The transformation plan and all of its follow-on gap-filling batches are now fully closed.

## [2026-09-07] — Flashcards frontend leg closed (31 decks, 62 cards, pure extraction)

### Added

- Closed the flashcards/cheat-sheets coverage gap for `21-frontend-web`: unlike `cheat-sheets/`, which got a frontend batch back on 2026-09-03, `flashcards/` had zero frontend decks until now.
- Unlike the Phase 5 new-writing-domain decks (2026-09-07, earlier same day), this was a pure extraction, not new authoring: all 31 F-coded chapters already carry their own embedded `## Flashcards` section, written when each chapter was authored. Cards were copied verbatim; `[[wikilink]]`-style `Related` references (self- and cross-chapter) were mechanically translated to relative Markdown links using each target's own front-matter title, matching the exact convention already established for the 137 pre-existing backend decks.
- Built as two parallel batches (14 React, 17 Next.js — the same split `cheat-sheets/` used for its own frontend batch). `frontend-live-coding-and-debugging-protocol.md` deliberately excluded (no F-code; `playbook-technical-answer` type), matching the identical exclusion already established in `cheat-sheets/README.md`.
- Verified: all 31 files' YAML parses, one H1 each, zero unresolved `[[wikilink]]` markers, every link resolves — 62 cards, zero broken.
- Updated `flashcards/README.md` (new Frontend Decks table, 199 decks / 655 cards total) and `syllabus/21-frontend-web/INDEX.md`. **`flashcards/` now reaches the same domain coverage as `cheat-sheets/`.**

## [2026-09-07] — Junior Fundamentals gap found; T-2201 Java OOP Fundamentals written (1 of 5)

### Investigated

- User asked directly whether OOP itself was covered, in the context of selling this repository as Junior-through-Staff rather than Senior/Staff-only. Audit found: `syllabus/02-java`'s own Master Topic Register scopes "OOP" down to T-102 (Polymorphism and Dynamic Dispatch Mechanics) by original design — a deliberate, already-documented choice targeting the one Senior-differentiating mechanic, not the four pillars — and the same "assumes 5+ years experience, never teaches true basics" pattern repeats across every originally-migrated backend domain: `06-databases` (no SQL/schema basics), `05-spring` (no DI/@Controller basics), `08-testing` (no basic-JUnit-test basics), `07-api-design` (no REST/HTTP-verb basics). Only `01-computer-science-foundations`, `03-data-structures-algorithms`, and `21-frontend-web` were ever designed to be genuinely Junior-inclusive.
- Presented the finding and three scope options via `AskUserQuestion`; user chose the full-scope fix — one true "101" chapter per affected domain (5 total), matching the design already used for the three Junior-inclusive domains.

### Added

- Reserved a new topic-ID range, `T-2200`–`T-2299`, in `00-project/syllabus-transformation-plan.md`, for Junior Fundamentals topics added to existing backend domains that already had a full Master Topic Register allocation and therefore no natural range of their own for a genuinely new topic.
- Wrote `syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md` (T-2201), the first of the five planned chapters — full 20-section Topic Specification template, L1–L4 in one file, matching the same template used by `01-computer-science-foundations`/`03-data-structures-algorithms`/`18-engineering-practices`/`19-leadership-staff`.
- Built 3 new, real, compiled-and-executed Java demos in `practice/java/oop-fundamentals/classes-and-objects/src/` (OpenJDK 21.0.12, 19/19 assertions passing): `EncapsulationDemo.java` (a broken public-field class vs. a fixed, validated one — 7/7), `AbstractionAndInheritanceDemo.java` (abstract class vs. interface, both used on the same problem — 6/6), `CompositionOverInheritanceDemo.java` (a real subclass-explosion comparison: 2 axes of variation need 4 subclasses under inheritance vs. 4 small classes assembled freely under composition — 6/6). No topic content in the chapter states a claim these demos don't back directly.
- Deliberately does not re-teach polymorphism/dynamic dispatch mechanics — cross-links to the existing T-102 chapter instead, per the no-duplication rule, since that mechanism already has its own full, deep treatment.
- Updated `syllabus/02-java/INDEX.md` (50th chapter, new context note explaining the gap and the plan) and reserved the ID range in the transformation plan.
- Verified: TOC anchors resolve, front matter and related/practice paths all resolve on disk, one H1, full `scripts/validate.py` run introduces zero new errors.

### Not yet done

- 4 remaining Junior Fundamentals chapters: SQL and Relational Database Fundamentals (T-2202, `06-databases`), Spring MVC Fundamentals (T-2203, `05-spring`), Unit Testing Fundamentals with JUnit (T-2204, `08-testing`), REST API Fundamentals (T-2205, `07-api-design`) — same template, same real-code-demo bar, to be written in the same bounded-batch discipline as everything else in this repository.
- `CLAUDE.md`'s Target Audience section still states the primary reader "has at least five years of Java and backend experience" — now inconsistent with this initiative's own premise (selling Junior-through-Staff) and not yet reconciled.

## [2026-09-07] — T-2202 SQL and Relational Database Fundamentals written (2 of 5)

### Added

- Second of five planned Junior Fundamentals chapters (see the matching 2026-09-07 entry above for the full audit context): `syllabus/06-databases/sql-and-relational-database-fundamentals.md` (T-2202), full 20-section Topic Specification template, L1-L4 in one file.
- Built a real PostgreSQL 16 lab in `practice/sql/sql-fundamentals/` (disposable Docker container, same convention as `practice/sql/week-01/`'s index lab): `CREATE TABLE` with a real primary/foreign key, `INSERT`/`SELECT`/`UPDATE`/`DELETE`, a foreign key constraint actually rejecting an orphan insert (real, unedited Postgres error text captured), and `INNER JOIN` vs. `LEFT JOIN` run against the identical data to show the actual row-count difference directly rather than describing it.
- Updated `syllabus/06-databases/INDEX.md` (15th chapter, context note). Full validator run: zero new errors.

### Not yet done

- 3 remaining Junior Fundamentals chapters: Spring MVC Fundamentals (T-2203, `05-spring`), Unit Testing Fundamentals with JUnit (T-2204, `08-testing`), REST API Fundamentals (T-2205, `07-api-design`).

## [2026-09-07] — T-2203 Spring MVC Fundamentals written (3 of 5)

### Added

- Third of five planned Junior Fundamentals chapters: `syllabus/05-spring/spring-mvc-fundamentals.md` (T-2203), full 20-section Topic Specification template, L1-L4 in one file.
- Built a real Spring Boot 3.5.16 app in `practice/java/spring-mvc-fundamentals/` (embedded Tomcat, same working dependency set as `practice/java/full-stack-integration-backend`): a `TaskController` → `TaskService` → `TaskRepository` chain wired entirely by constructor-based dependency injection, exercised live with `curl` (`GET`/`POST /tasks`, `GET /tasks/{id}`, both a found and a missing id).
- A genuine bug was hit and kept as real teaching material rather than smoothed over: the original `@PathVariable Long id` (no explicit name) produced a real `500 Internal Server Error` — `IllegalArgumentException: Name for argument of type [java.lang.Long] not specified, and parameter name information not available via reflection` — captured in `curl-transcript-before-fix.txt`. Fixed by naming the binding explicitly (`@PathVariable("id") Long id`); both the before and after transcripts are real, unedited output, and the chapter's Section 8 and Section 17 both use this exact failure rather than inventing a hypothetical one.
- Updated `syllabus/05-spring/INDEX.md` (10th chapter, context note). Full validator run: zero new errors (one link-depth mistake of my own — `../../17-architecture/...` where `../17-architecture/...` was correct from `05-spring/`'s own 2-level depth — caught and fixed before commit).

### Not yet done

- 2 remaining Junior Fundamentals chapters: Unit Testing Fundamentals with JUnit (T-2204, `08-testing`), REST API Fundamentals (T-2205, `07-api-design`).

## [2026-09-07] — T-2204 Unit Testing Fundamentals with JUnit written (4 of 5)

### Added

- Fourth of five planned Junior Fundamentals chapters: `syllabus/08-testing/unit-testing-fundamentals-with-junit.md` (T-2204), full 20-section Topic Specification template, L1-L4 in one file.
- Built a real JUnit 5 suite in `practice/java/testing-fundamentals/junit-basics/` (run via the `junit-platform-console-standalone` shaded jar, no Maven/Gradle): `@Test`, `@BeforeEach`, `assertThrows`, and two `@ParameterizedTest` forms (`@ValueSource`, `@CsvSource`) — 17/17 tests passing.
- A genuine test failure was deliberately produced (not invented) to capture real JUnit failure output: `addsTwoPositiveNumbers()`'s assertion was temporarily changed to an incorrect expected value, the real failure text (`expected: <6> but was: <5>`, full stack trace) captured in `real-failure-output.txt`, then reverted to the correct, passing version before commit. The chapter's Section 7, 8, and 17 all use this exact, real failure.
- Updated `syllabus/08-testing/INDEX.md` (8th chapter, context note). Full validator run: zero new errors.

### Not yet done

- 1 remaining Junior Fundamentals chapter: REST API Fundamentals (T-2205, `07-api-design`).

## [2026-09-07] — T-2205 REST API Fundamentals written — Junior Fundamentals initiative complete (5 of 5)

### Added

- Fifth and final Junior Fundamentals chapter: `syllabus/07-api-design/rest-api-fundamentals.md` (T-2205), full 20-section Topic Specification template, L1-L4 in one file.
- Built a real Spring Boot 3.5.16 app in `practice/java/rest-api-fundamentals/` (embedded Tomcat, `localhost:8081`, separate from T-2203's own demo): a `BookController`/`BookRepository` REST API proving, with real curl transcripts, that `POST` is not idempotent (two identical request bodies produce two distinct resources with different ids) and `PUT` is idempotent (two identical calls produce the identical resulting state and status), plus correct `201 Created` + `Location` header, `204 No Content`, and `404` behavior.
- Updated `syllabus/07-api-design/INDEX.md` (3rd chapter, closing note for the full initiative).
- Full validator run: zero new errors.

### Completed

- **The Junior Fundamentals initiative is complete: all 5 planned chapters (T-2201–T-2205) are written**, covering Java OOP, SQL/relational databases, Spring MVC, JUnit testing, and REST API design — closing the gap found when the user asked whether OOP itself was covered, in the context of selling this repository as Junior-through-Staff rather than Senior/Staff-only. Every chapter includes real, executed evidence (compiled Java demos, a live PostgreSQL lab, or a live Spring Boot app exercised with curl) — no topic content asserts a claim without backing it directly, and two chapters (Spring MVC, JUnit) kept a genuine bug/failure hit while building the demo as real teaching material rather than smoothing it over.
- `CLAUDE.md`'s Target Audience section still states the primary reader "has at least five years of Java and backend experience" — now inconsistent with this completed initiative's own premise and still not reconciled; flagged, not yet fixed.

## [2026-09-07] — CLAUDE.md/AGENTS.md/README.md Target Audience reconciled with Junior Fundamentals

### Fixed

- The Junior Fundamentals initiative (T-2201–T-2205, this same date) was flagged as complete but inconsistent with CLAUDE.md's own Target Audience section, which still assumed the reader "has at least five years of Java and backend experience" and targeted only Senior/Staff interviews. User explicitly asked for the reconciliation.
- Rewrote CLAUDE.md's Target Audience, Project Identity opener, and Project Success Criteria opener to describe a reader anywhere on the Junior-through-Staff spectrum, tying the description directly to the 20-section Topic Specification template's own L1-L4 structure and naming which domains were Junior-inclusive by design versus retrofitted. Regenerated AGENTS.md as an exact mirror. Updated README.md's tagline and "Target roles" list to match.
- Full validator run: zero new errors.

## [2026-09-07] — Junior → Mid learning path updated to route through the 5 new Junior Fundamentals chapters

### Fixed

- `syllabus/00-overview/learning-paths/junior-to-mid.md` predates the Junior Fundamentals initiative (same date, T-2201-T-2205) and never routed through it, despite its own stated audience being exactly who those chapters serve. Inserted all 5 at the points where the existing sequence's own topics silently assumed them: OOP Fundamentals first, SQL Fundamentals before Index Structures, Unit Testing Fundamentals before Test Strategy, Spring MVC Fundamentals before Spring Framework vs. Spring Boot, and REST API Fundamentals (a genuinely new domain addition to this path). Sequence: 14 -> 19 topics, all in-file topic-number references renumbered, time budget updated with a stated reason. Verified all links resolve; full validator run clean.

## [2026-09-07] — Cheat sheets and flashcards for the 5 Junior Fundamentals chapters (T-2201–T-2205)

### Added

- The 5 Junior Fundamentals chapters (built the same day) postdated the cheat-sheets/flashcards backlog closures for the other new-writing domains and were never covered. Closed same-day rather than left as a new backlog: 5 cheat sheets and 5 flashcard decks (25 cards total), one pair per chapter.
- Every fact, decision-table entry, and pitfall was drawn directly from its own chapter's real content (the real compiled demos, the real PostgreSQL error text, the real Spring MVC `@PathVariable` bug, the real JUnit failure, the real POST/PUT idempotency proof) — no invented content.
- Updated `cheat-sheets/README.md` (199 total: 132 backend + 31 frontend + 36 new-writing-domain) and `flashcards/README.md` (204 total: 137 backend + 36 new-writing-domain + 31 frontend, 680 cards). Updated all five domains' `INDEX.md` files with a one-line pointer.
- Full validator run: zero new errors.

## [2026-09-08] — Two more Junior Fundamentals chapters: Java Syntax (T-2206), Collections Usage (T-2207)

### Investigated

- After completing the 5-chapter Junior Fundamentals initiative, the user asked directly whether this repository now genuinely served a reader from low to high seniority. Honest re-check found two more gaps: T-2201 (OOP Fundamentals) itself assumed the reader could already read an `if` statement and a `for` loop — true Java syntax literacy was never taught anywhere in this repository — and `02-java` jumped from "here's a class" straight into `HashMap`/`ArrayList` internals with no "what is a List/Map/Set and when do you use each" usage-level stop first.

### Added

- `syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md` (T-2206) — variables, operators, `if`/`else`/`switch`, `for`/`while`, methods, arrays. Now T-2201's own prerequisite. Real demo: [`GradeReportDemo.java`](../../practice/java/oop-fundamentals/syntax-basics/src/GradeReportDemo.java), 9/9 assertions passing, OpenJDK 21.0.12.
- `syllabus/02-java/collections/java-collections-usage-fundamentals-list-map-and-set.md` (T-2207) — List/Map/Set at a usage level (what and when, not internals), sitting between OOP Fundamentals and this domain's internals-focused Collections chapters. Real demo: [`WordFrequencyDemo.java`](../../practice/java/oop-fundamentals/collections-basics/src/WordFrequencyDemo.java), 14/14 assertions passing.
- Both use the same 20-section Topic Specification template as the other 5 Junior Fundamentals chapters. `syllabus/02-java/INDEX.md` updated (52 chapters total); T-2201's own front matter and Section 2 updated to point at T-2206 as its real prerequisite.
- `syllabus/00-overview/learning-paths/junior-to-mid.md` re-sequenced a third time: T-2206 is now Topic 1 (the true starting point), T-2207 inserted as Topic 6 (right before `equals()`/`hashCode()` and `HashMap` Internals). Sequence: 19 → 21 topics; all in-file topic-number references renumbered; time budget updated to ~7 weeks.
- Cheat sheets and flashcard decks added for both (`cheat-sheets/README.md`, `flashcards/README.md`) — **201 cheat sheets, 206 flashcard decks (690 cards) total, both counts verified against the real file system, not just arithmetic.**
- Full validator run: zero new errors.

## [2026-09-08] — Docker and Containers Fundamentals (T-2208)

### Investigated

- Continuing the same audit pattern, checked `12-security` and `14-devops-containers` for the "jumps straight to advanced content, no usage-level floor" pattern. `12-security`'s `authn-authz-rbac-vs-abac.md`, already L1/L2-retrofitted, was judged an adequate existing entry point — no new chapter added there. `14-devops-containers`'s `container-image-internals.md` — its own title says "internals" — never taught what a container or image actually is first: a real gap, same shape as the others closed this week.

### Added

- `syllabus/14-devops-containers/docker-and-containers-fundamentals.md` (T-2208) — container vs. VM, image vs. container, Dockerfile basics, port publishing. Real demo: a genuine image built and run against Docker Engine 29.6.2 (`practice/docker-fundamentals/`) — a tiny JDK-only HTTP server, containerized, proving both a successful `-p`-mapped run (real `curl` response) and a real, observed network-isolation failure (`curl` from the host refused; the identical request succeeding via `docker exec` from inside the container's own network namespace).
- Updated `syllabus/14-devops-containers/INDEX.md` (5 chapters) and the reserved-range note in `00-project/syllabus-transformation-plan.md` (T-2208 added to the `T-2200`–`T-2299` assignment list).
- Cheat sheet and flashcard deck added. **Final counts, verified directly against the file system: 202 cheat sheets, 207 flashcard decks, 695 cards.**
- Full validator run: zero new errors.

### Not yet done

- No further Junior Fundamentals gaps were found in this pass beyond the two investigated above. `12-security`'s existing entry point was judged sufficient; if a future audit finds otherwise, the same `T-2200`–`T-2299` range and template apply.

## [2026-09-08] — Junior → Mid weekly study pack (`study-packs/junior-to-mid/`)

### Added

- `study-packs/junior-to-mid/README.md` — top-level index for a new 7-week study pack, the scheduled counterpart to [`syllabus/00-overview/learning-paths/junior-to-mid.md`](../../syllabus/00-overview/learning-paths/junior-to-mid.md)'s 22-topic sequence. Built following the newer, leaner `study-packs/week-21`-style convention (README + MANIFEST only, no duplicated chapter content) rather than the older, heavier pre-migration `week-01`-style convention (11+ files per week, duplicated mock-interview scripts) — chosen explicitly because it matches this repository's own no-duplication rule.
- `study-packs/junior-to-mid/week-01/` through `week-07/`, each with a `README.md` (Weekly Outcome through Next Week, per `CLAUDE.md`'s Study Pack Standard) and a `MANIFEST.md` (files table, verification table, scope note, integrity note). Weeks map the 22-topic sequence to 7 themed weeks: Java from the ground up; numbers/complexity/collections-as-a-concept; collections internals; coding patterns part 1; coding patterns part 2 + first SQL; databases and testing; ship something (Spring + REST + Docker).
- Every real, previously-verified assertion count (9/9 `GradeReportDemo`, 19/19 OOP demos, 14/14 `WordFrequencyDemo`, the real Spring MVC `-parameters` bug and fix, the real JUnit passing/failing runs, the real Docker network-isolation proof) was cited exactly as originally verified when each Junior Fundamentals chapter was built — none re-invented or estimated for this study pack. Where a week's topics predate this session's Junior Fundamentals work (Weeks 3, 4, 5's DSA topics, Week 6's database/testing internals topics, Week 7's Spring-vs-Boot topic), the manifest says so explicitly and cites the chapter by link only, without a fabricated assertion count.
- `syllabus/00-overview/learning-paths/junior-to-mid.md` gained a new "Weekly study pack" section linking to the new pack, plus a fourth "Updated" note: Topic 22 (Docker and Containers Fundamentals, T-2208) had been written but never actually added to this path's sequence — caught while building the study pack itself.
- Full validator run: zero new errors from these files (3 pre-existing errors in `AGENTS.md`/`CLAUDE.md`/`templates/adr-template.md` are unrelated).

## [2026-09-08] — Mid → Senior weekly study pack, and `study-packs/` reorganization

### Investigated

- After building the Junior-to-Mid study pack, checked whether `study-packs/` as a whole was easy to navigate. Found two real organization gaps: `syllabus/00-overview/learning-paths/mid-to-senior.md` had no weekly study pack of its own (unlike the newly-built Junior → Mid path), and `study-packs/` itself had no top-level index — a reader would find `week-01` meaning two different things (Interview Emergency Sprint vs. the new Junior-to-Mid pack) with nothing disambiguating them.

### Added

- `study-packs/mid-to-senior/` — a new 10-week study pack scheduling [`syllabus/00-overview/learning-paths/mid-to-senior.md`](learning-paths/mid-to-senior.md)'s 12-domain sequence (concurrency, JVM internals, Spring internals, databases, testing, Kafka, distributed systems, system design, security, observability, DevOps/Kubernetes, architecture — combined into 10 weeks, pairing the two lighter 2-topic domains, security+observability and DevOps+architecture, into shared closing weeks). Same lean README+MANIFEST convention as `junior-to-mid/`. Each week additionally cross-references 1–2 real, verified-to-exist `production-cookbook/` incident entries matching that week's domain, per the learning path's own stated cross-reference principle.
- `syllabus/00-overview/learning-paths/mid-to-senior.md` gained a matching "Weekly study pack" section linking to the new pack.
- `study-packs/README.md` — new top-level index for the whole `study-packs/` directory, explicitly disambiguating the three programs that now live there (Interview Emergency Sprint's `week-01`–`week-25`, `junior-to-mid/`, `mid-to-senior/`) and routing the reader to the right one by stated experience level.
- Root `README.md`: added a "Choosing your starting point" section before the roadmap explanation, and updated the Current Status table's completed-deliverables row, so both new study packs are discoverable from the repository's own entry point rather than only from `syllabus/`.
- Full validator run: zero new errors from these files (the 3 pre-existing errors in `AGENTS.md`, `CLAUDE.md`, and `templates/adr-template.md` are unrelated and predate this work).

## [2026-09-08] — Privacy: anonymized the one file naming a real employer

### Investigated

- User asked what's still missing for a future public/commercial version of this repository. Re-checked a privacy finding first raised in `00-project/syllabus-transformation-plan.md` §2.8 (2026-09-03, never acted on): a file under `interview-playbook/company-prep/` named a real employer in its title, filename, and H1, directly contradicting the root `README.md`'s own privacy claim ("Nothing in it today identifies an employer, client, or colleague"). No other personal identifiers were found in a repository-wide grep.

### Fixed

- Renamed the file to `interview-playbook/company-prep/large-ecommerce-retailer-senior-backend-remote.md`; rewrote its title and H1 to a generic descriptor ("A Large E-Commerce Retailer"); added a one-line note at the top of the file recording the anonymization. No other content changed — the file's technical analysis (idempotency, locking, Kubernetes, resilience patterns) never named the employer beyond the title.
- Updated all 7 cross-references repository-wide: `README.md`, `CHANGELOG.md` (2 historical entries), `interview-playbook/README.md`, `resources/repository-tree.md`, `syllabus/20-interview-preparation/INDEX.md` (2 mentions), `00-project/syllabus-transformation-plan.md` (3 mentions — added a "Resolved 2026-09-08" note to §2.8 rather than deleting the original finding, preserving it as provenance), `00-project/migration-mapping.md`.
- User's own decision, via an explicit choice among anonymize / remove from public repo / leave as-is: anonymize, keeping the file's technical content usable as a generic company-prep example rather than removing it entirely.
- Full validator run: zero new errors (one self-introduced relative-link bug from the prior mid-to-senior changelog entry was caught and fixed in the same pass — `learning-paths/mid-to-senior.md` had an extra `00-overview/` prefix).

## [2026-09-08] — Repository-wide gap audit: domains, phases, structure

### Investigated

- User asked what domains/topics still need work across the whole repository, whether any phase is unfinished, and whether the structure needs more reorganization. Checked: per-domain topic counts and completion status (all 21 `syllabus/` domains' own `INDEX.md` status lines), the 35 "Planned reference" notes across the new-writing domains, the 5 remaining "not yet written"/TODO-style hits, `00-project/syllabus-transformation-plan.md`'s own phase-completion status, and whether `00-project/frontend-topic-register.md` matches the frontend domain's real current state.

### Findings — no action needed

- All 21 `syllabus/` domains are structurally complete: every domain's `INDEX.md` states "fully L1–L4" for its topics. Domains that look thin by file count (`04-software-design` 1 file, `15-cloud`/`16-performance-jvm` 3 files each, `13-observability` 4, `10-distributed-systems` 5) are deliberately narrow-scoped by the transformation plan's own domain-splitting decisions (§3.2–3.3), not unfinished — confirmed by reading each one's own index rather than judging by file count alone.
- All 35 "Planned reference" notes across the Phase-5 new-writing domains (`01-computer-science-foundations`, `03-data-structures-algorithms`, `18-engineering-practices`, `19-leadership-staff`, plus a handful elsewhere) are intentional, already-resolved, honestly-documented gaps per `production-cookbook/README.md`'s own "elevate a real worked scenario, never invent one" rule — not unbuilt work. Each one states exactly what future incident shape would justify a new cookbook entry.

### Fixed

- `syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md` — a stale inline note claimed the sibling chapter [Intervals, Merging, and Sweep Line](../03-data-structures-algorithms/intervals-merging-and-sweep-line.md) was "not yet written"; that chapter has existed since 2026-09-03. Corrected the note and fixed the link to point directly at the chapter instead of its domain `INDEX.md`.
- `00-project/syllabus-transformation-plan.md` — front matter `status` field and its own closing §14 summary both still said "Phase 1 (Scaffolding) has not been authorized," contradicting reality: all phases 0–7 completed by 2026-09-07 per CLAUDE.md's own Structural Update note. Corrected both, with the closing section keeping the original stale text visible (with a dated correction note above it) rather than silently rewriting history.
- `00-project/frontend-topic-register.md` — `status: draft` with "Gap: 🔴 absent (true for every row below — this is a new domain, nothing exists yet)" was accurate on 2026-08-12 but not today: `syllabus/21-frontend-web/` now has 32 real chapter files. Marked the document's `status` field stale rather than silently re-auditing all rows (a properly-done row-by-row Gap re-audit is a real, separate, larger task — flagged as an open follow-up, not done here).

### Open questions for the user (not acted on without explicit direction)

- Three of five backend learning paths — [Senior → Staff](learning-paths/senior-to-staff.md), [Backend Java Specialization](learning-paths/backend-java-specialization.md), and [Senior Interview Refresh](learning-paths/senior-interview-refresh.md) — have no weekly study pack of their own, unlike `junior-to-mid/` and `mid-to-senior/` (both built earlier this week) and the original Interview Emergency Sprint. Whether this is a real gap or an intentional "read straight through, no scheduling needed" design (these paths already read more like curated topic sequences than a 6-10-week onboarding program) is a scope decision for the user, not something to build unprompted.
- `00-project/frontend-topic-register.md`'s row-level Gap markers need a real audit against `syllabus/21-frontend-web/`'s actual 32 chapters before the register can be trusted again — out of scope for this pass.

### Not investigated

- Individual chapter technical accuracy (spot-checked previously across many sessions, not re-verified in full here) and the frontend domain's own internal depth-ladder coverage (whether all of BEG→EXP is actually represented across its 32 chapters) were out of scope for this structural/phase-completion audit.

## [2026-09-08] — Weekly study packs for the remaining 3 backend learning paths

### Added

- Closed the open question from the same day's earlier gap audit: user chose to build study packs for all three remaining backend learning paths, achieving full consistency across all five. Built via 3 parallel agents, one per pack, each independently verifying every cross-reference against the real file system before citing it (no fabricated assertion counts, practice paths, or cookbook filenames).
- `study-packs/senior-to-staff/` (8 weeks, 2 of the path's 16 named topics per week) — schedules [Senior → Staff](learning-paths/senior-to-staff.md). Weeks 6–8 (the Leadership & Staff block) each carry a real Behavioral Exercise tied to a matching `syllabus/20-interview-preparation/behavioral/` chapter, discovered during construction: all five Leadership & Staff topics have a real 1:1 behavioral-narrative counterpart. Weeks 1–6 cite real `production-cookbook/` matches; Weeks 5, 7, 8 honestly state no genuine `practice/` demo exists for their topics rather than inventing one (Week 5 substitutes a real mock-interview round instead).
- `study-packs/backend-java-specialization/` (9 weeks, one per subdomain-sized unit) — schedules [Backend Java Specialization](learning-paths/backend-java-specialization.md)'s 5-domain, full-L1–L4 track. Construction surfaced a real stale-count bug in the source learning path itself: its own table still said "30" Java topics and "13" Database topics from its 2026-09-05 authoring, before the Junior Fundamentals and SQL Fundamentals additions grew both domains to their real current 52 and 15. Fixed the source table directly (see below) rather than just noting the discrepancy in the pack's own manifests.
- `study-packs/senior-interview-refresh/` — not a multi-week program by design (matching its source path's own shape: a short, repeatable 3-5-day recall rotation, not new-material onboarding). Just `README.md` + `MANIFEST.md`, no `week-NN/` subdirectories. Operationalizes the source path's cheat-sheet-sweep → flashcard-pass → explain-it-cold rotation into a concrete 5-day schedule, with real domain groupings verified against `cheat-sheets/README.md` at write time.
- All three source learning-path files gained a matching "Weekly study pack" (or, for the refresh path, "Study pack") section linking to their new pack.
- `study-packs/README.md` rewritten to index all 6 programs (was 3) with an updated "Which program is this?" routing table.
- Root `README.md`'s "Choosing your starting point" table updated to list all 5 level-specific packs plus the Emergency Sprint fallback.

### Fixed

- `syllabus/00-overview/learning-paths/backend-java-specialization.md` — its own Sequence table stated stale topic counts (Java: 30, Databases: 13) from before this session's Junior Fundamentals (T-2201–T-2208) and SQL Fundamentals additions. Corrected to the real, file-system-verified current counts (Java: 52, split 17/10/13/12 across its four subdomains; Databases: 15) and added a dated correction note.
- Full validator run: zero new errors from any of the above (the same 3 pre-existing, unrelated errors remain).

## [2026-09-08] — Frontend Junior Fundamentals initiative: closing the same gap on the other domain

### Investigated

- Continuing the same-day gap audit, re-did `00-project/frontend-topic-register.md`'s row-by-row Gap re-audit (flagged as an open follow-up in an earlier entry today). Confirmed all 37 originally-registered topics have a real, fully-written chapter — zero actual absence, only a stale Gap column that still said "🔴 absent (nothing exists yet)" from 2026-08-12. While re-auditing, opened `react-fundamentals-jsx-components-props-and-state.md` (this domain's supposed "Beginner tier" entry point) directly and found it has `prerequisites: []` and teaches JSX/props/`useState`/events assuming the reader already knows JavaScript functions, arrays, objects, destructuring, and `this` — none of which this domain ever taught. The same pattern held for `react-typescript.md` (F-119, assumes plain TypeScript) and every chapter's assumed HTML/CSS/DOM/HTTP literacy. This is the exact "assumes the basics" pattern already found and fixed on the Java backend side (T-2200–T-2299) — closed here the same way, per the user's explicit request to cover this domain Junior-through-Staff with real JavaScript/TypeScript/web-fundamentals content.

### Added

- Three new chapters, built via 3 parallel agents, each independently verifying every claim against real executed evidence:
  - `syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md` (F-001) — HTML semantics, the CSS box model, the DOM, and the HTTP request/response cycle. Real evidence: a genuine static site served and inspected via `curl -v` (the sandbox blocks TCP loopback, so a Unix-domain-socket transport was used instead and disclosed explicitly, not hidden) — see `practice/frontend/web-fundamentals/curl-transcript.txt`.
  - `syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md` (F-002) — variables/scope, functions and `this`, closures, the event loop, Promises/`async`/`await`. Real evidence: 7 real Node.js v24.18.0 scripts actually executed, captured verbatim in `practice/frontend/javascript-fundamentals/output.txt` — two claims were corrected mid-construction to match what Node actually did rather than what was assumed (a detached method call under optional chaining, and a `setTimeout` callback's real `this`).
  - `syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md` (F-003) — types, interfaces vs. type aliases, structural typing, unions/discriminated unions, generics. Real evidence: a genuine broken/fixed pair — `tsc` actually rejecting a type error (`error TS2345`), then the fixed version compiling cleanly — captured in `practice/frontend/typescript-fundamentals/tsc-output-before-fix.txt` and `-after-fix.txt`. `react-typescript.md` (F-119) updated to name this chapter as its own real prerequisite.
- `00-project/frontend-topic-register.md` — added the 3 new topics as a "D-F0 · Web & Language Fundamentals" section, added a `Gap` + `Chapter` column to every existing table (all 🟢, cross-checked against real files), rewrote the stale "no chapters exist yet" closing section, bumped `status`/`version`.
- `syllabus/21-frontend-web/INDEX.md` — status line updated (35 of 35 chapters), new blockquote documenting the fix.
- Cheat sheets and flashcard decks added for all 3 new topics (5 cards each, verified against the file system, not estimated). **New totals: 205 cheat sheets, 210 flashcard decks, 710 cards.**
- Two new frontend learning paths, since none existed before despite the domain being designed for Beginner-through-Expert from the start: `syllabus/00-overview/learning-paths/frontend-junior-to-mid.md` (14 topics, Beginner→Intermediate) and `frontend-mid-to-senior.md` (20 topics, Intermediate→Advanced, closing on F-214 Full-Stack Integration as the domain's Expert/Staff-equivalent capstone). Both cross-reference their Java-backend counterparts explicitly, framing this as genuine full-stack depth rather than backend-with-frontend-bolted-on. `syllabus/00-overview/learning-paths.md` (the central index) updated to list both.
- Full validator run: zero new errors (the same 3 pre-existing, unrelated errors remain).

### Not yet done

- Neither new frontend learning path has a weekly study pack of its own yet, unlike all 5 backend paths. Flagged explicitly in `learning-paths.md` as an open follow-up, not silently skipped — same category of decision as the backend study-pack gap closed earlier today, left for the user to prioritize.

## [2026-09-08] — Frontend weekly study packs: closing the last operational-layer gap

### Added

- User asked to close the just-flagged gap: `study-packs/frontend-junior-to-mid/` (6 weeks, scheduling the 14-topic Frontend Junior → Mid path) and `study-packs/frontend-mid-to-senior/` (10 weeks, scheduling the 20-topic Frontend Mid → Senior path). Same lean README+MANIFEST convention as every other pack in this repository.
- Construction started via 2 parallel background agents, each briefed with the exact template (junior-to-mid/mid-to-senior week-01 and week-03 as structural and honesty-pattern references) and instructed to verify every practice-demo citation against the real file system before writing it. Both agents hit a session-wide API rate limit mid-construction, after producing only each pack's top-level `README.md` and `week-01/README.md` (both high quality, verified on inspection — no rework needed). The remaining 28 files (week-01's two `MANIFEST.md`s, plus weeks 2–6 and 2–10 in full) were completed directly in the main session rather than re-dispatching agents, to avoid repeating the rate-limit failure.
- Frontend Junior → Mid's 6 weeks: Week 1 (Web/JS/TS fundamentals, citing the real evidence — Node.js scripts, `tsc` output, `curl` transcript — built the same day as those chapters), Week 2 (React Fundamentals), Week 3 (the three hook families), Week 4 (Next.js's role and App Router), Week 5 (build tooling and styling), Week 6 (forms, error boundaries, accessibility — closing on production-shaped UI).
- Frontend Mid → Senior's 10 weeks, 2 topics each except the closing week: Server/Client boundary and data fetching, rendering strategies and Route Handlers, SEO and Web Vitals, component patterns and fiber internals, concurrent rendering and performance, testing and TypeScript, state management and streaming, the edge runtime and auth, Server Actions and deployment models, and a closing Week 10 (Monorepo Layout + Full-Stack Integration) that — uniquely among all packs in this repository — cites specific, verified real evidence read directly from the F-214/F-303 chapters themselves: a real CORS failure/fix between two separately-running processes (`practice/frontend/react-nextjs-fundamentals/` and `practice/java/full-stack-integration-backend/`), the real BFF Route Handler file, and real measured `node_modules` duplication costs (435/39/60/55 MB) from a real monorepo symlink test. Week 10 carries this pack's only full System Design Exercise, matching its Expert-tier capstone status.
- All prior "not yet done" notes about frontend study packs (this entry's own predecessor, `study-packs/README.md`, root `README.md`, and both frontend learning-path files) updated to reflect completion — both frontend learning paths gained a "Weekly study pack" section; `study-packs/README.md` and root `README.md` reorganized into backend/frontend sections listing all 8 programs.
- Full validator run: zero new errors (the same 3 pre-existing, unrelated errors remain).

### Not yet done

- None — this was the last flagged gap from the same-day audit. All 5 backend and both frontend learning paths now have a matching operational study-pack layer.

## [2026-09-08] — Three more `02-java` Junior Fundamentals chapters: platform basics, modifiers, and a version-features timeline

### Investigated

- User asked directly whether `02-java` covered primitive types, JVM/JDK/JRE, basic data structures, access modifiers (`private`/`protected`/`public`/`static`), `final`/`finalize`, abstract-vs-concrete method signatures, and a Java version-features timeline (8/11/17/21/25) — and requested a check of a connected Notion knowledge base (`Java Interview Questions` / `Extended Java Interview Questions` databases) for real, commonly-asked questions on these exact topics.
- Grepped every existing chapter first, per this project's search-before-writing discipline. Found: basic data structures already covered (T-2207); the diamond problem and default/static interface methods already covered (T-2201 §4/§8) — no new content needed for either. Found real, confirmed gaps: no chapter anywhere defined JVM vs. JDK vs. JRE; no chapter listed all 8 primitive types with real ranges; access modifiers appeared only incidentally inside an Advanced-depth chapter (`reflection-and-dynamic-proxies.md`); `final`/`static` appeared only incidentally inside Senior-depth chapters (`immutability-and-defensive-copying.md`, `equals-hashcode-and-comparable-contracts.md`); no chapter gave a single accurate Java-version-features timeline.
- Searched the connected Notion workspace read-only (the exact URL given did not resolve — 404, likely wrong page ID or workspace — but the two real databases it names, `Java Interview Questions` and `Extended Java Interview Questions - With Answers, Code Examples & Notes`, were found and searched directly). Confirmed these are real, commonly-asked questions there (access modifiers, static vs. non-static, `final`/`finally`/`finalize`, wrapper classes, multiple-interface "multiple inheritance", functional interfaces in Java 8) — consistent with the original 2026-07 knowledge-base audit's own finding that this source is shallow (short, uncredited one-paragraph answers), used here only to confirm real interview relevance, never copied. Per `CLAUDE.md`'s Non-Negotiable Notion Safety Rules, nothing was written, created, or modified in Notion — read-only search only.

### Added

- Three new chapters, built directly (not via background agents, to avoid a repeat of an earlier same-day agent rate-limit failure), each with real, compiled, executed evidence on OpenJDK 21.0.12:
  - [Java Platform Basics: JVM, JDK, JRE, and Primitive Types](../02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md) (T-2209) — now the true Topic 1 in [Junior → Mid](learning-paths/junior-to-mid.md). Real evidence: 11/11 assertions (byte/int overflow, the `0.1 + 0.2` surprise, the Integer -128..127 cache `==` gotcha, a real `NullPointerException` from auto-unboxing `null`), plus a real JDK `bin/`/`jmods/` tooling listing confirming a JDK genuinely bundles the compiler and dev tools.
  - [Java Modifiers and Method Signatures: Access Control, static, final, and Abstract vs. Concrete Methods](../02-java/language-core/java-modifiers-and-method-signatures.md) (T-2210) — real evidence: 6/6 assertions (static-vs-instance state, a real abstract class with both an abstract and a concrete method) plus 5 real, captured `javac` compiler errors (reassigning a `final` variable, overriding a `final` method, extending a `final` class, instantiating an `abstract` class, accessing a `private` field from a genuinely different top-level class). Construction surfaced a genuine, unplanned finding: `private` access is scoped to the *top-level* enclosing class, not the immediate class body — a first attempt at the private-access-violation demo accidentally compiled successfully because both classes were nested inside the same outer class, which is itself real, correct JLS behavior and was kept as a deliberate demo rather than discarded.
  - [Java Version Features Timeline: Java 8 Through 25](../02-java/language-core/java-version-features-timeline.md) (T-2211) — a cross-linking reference chapter, not a duplicate of existing Java-8-feature chapters (`lambdas-and-functional-interfaces.md`, `streams-and-collectors.md`, `optional-and-null-strategy.md`). Real evidence: 9/9 assertions spanning `var` (Java 10), text blocks (Java 15), records with a real compact-constructor validation and pattern matching for `instanceof` (Java 16), sealed interfaces (Java 17), exhaustive pattern matching for `switch` with no `default` branch (Java 21), and 1,000 real virtual threads completing concurrently (Java 21). Carries an explicit accuracy caveat (§9) on Java 22–25 feature status, per `CLAUDE.md`'s Modern Java Version Policy, since the newest 1–2 releases are the likeliest place for any version-timeline reference to drift stale.
- `syllabus/02-java/INDEX.md` updated (55 chapters total), `00-project/syllabus-transformation-plan.md`'s `T-2200`–`T-2299` reserved-range note extended, and [Junior → Mid](learning-paths/junior-to-mid.md) re-sequenced a fifth time (22 → 25 topics) — all topic-number cross-references in the file's own prose renumbered to match.
- Cheat sheets and flashcard decks added for all 3 (5 cards each, verified against the file system). **New totals: 208 cheat sheets, 213 flashcard decks, 725 cards.**
- Full validator run: zero new errors (3 pre-existing, unrelated errors remain; 4 transient broken-link errors introduced mid-construction by this same work — two wrong relative paths to `virtual-threads.md` and `spring-webflux-and-reactive-programming.md` — were caught and fixed in the same pass).

### Not yet done

- `study-packs/junior-to-mid/`'s own weekly schedule has not yet been re-sequenced for the three new Topics 1, 3, and 5 — flagged explicitly in the learning path's own file rather than left silently stale.

## [2026-09-08] — Six more Notion databases reviewed: one new chapter, one existing chapter expanded, one existing chapter reinforced

### Investigated

- User asked for a review of 6 additional Notion databases/views (Java Interview Questions & Answers Repository, Data Base Technical Interview Questions, DSA Patterns in Java - Tech Lead's Guide, Architecture & Engineering Interview Guide, Microservices/DevOps/System Design Interview Questions, Spring Ecosystem Technical Interview Questions) for gaps or reinforcement material, read-only per `CLAUDE.md`'s Notion safety rules — nothing written or modified in Notion. None of the 6 named titles matched a distinct database by title search; investigation found they map to `Category` filter values inside the same "Extended Java Interview Questions" database already sampled in an earlier pass (queried directly via SQL: 19 categories, ~230 real questions — AWS, CI/CD, Collections, Design Patterns/SOLID, Git, Hibernate, Java 8, Java Collections, Java Core, Java Exceptions, Java Threads, JPA, JVM, Kafka, OOP, REST API, SLCP/SDLC, Spring Boot, Spring Framework, SQL).
- Cross-checked every category against the repository's existing chapters. Most are already well covered (Spring Boot/Framework, Hibernate/JPA, Kafka, REST API, Git, CI/CD, Design Patterns/SOLID, Design Patterns, AOP/DispatcherServlet specifically checked and confirmed already covered at real depth in `transactional-proxy-mechanics-and-propagation.md` and `spring-mvc-fundamentals.md` — no action needed for either). Found two real gaps: the **SLCP category (SDLC/Agile/Waterfall/Scrum)** had zero coverage anywhere in the repository — only incidental hits in unrelated chapters; the **AWS category** revealed `syllabus/15-cloud/aws-core-services-for-backend-engineers.md` never mentioned VPC/subnets, Security Groups, IAM, CloudFormation, or CloudWatch despite covering compute/storage/database/messaging/traffic in real depth.
- Separately, user asked to expand the default/static interface methods and diamond-problem coverage in `java-oop-fundamentals.md` — correctly noting the diamond problem was already covered but felt under-developed, and confirming it was theirs to keep, not a duplicate to remove. Investigation confirmed: the diamond-problem resolution rule and default/static method behavior were explained accurately in prose (§4, §5, §9) but had zero real executed evidence backing either claim — a real gap in evidentiary rigor, not conceptual coverage.

### Added

- [SDLC and Agile Methodology Fundamentals](../18-engineering-practices/sdlc-and-agile-methodology-fundamentals.md) (T-2212, `18-engineering-practices`) — a genuinely new floor topic: the 6 SDLC phases, Waterfall vs. Agile (the real mechanism being feedback-loop length, not "old vs. modern"), Scrum's roles/artifacts/ceremonies, and when Waterfall remains the defensible choice. Deliberately has no Java demo (a process-methodology topic, not executable code) — uses a clearly-labeled representative scenario instead of a fabricated incident, and cites the real Agile Manifesto and Scrum Guide as official references. `syllabus/18-engineering-practices/INDEX.md` updated (6 chapters).
- `syllabus/15-cloud/aws-core-services-for-backend-engineers.md` expanded in place (not a new topic, since it reinforces an existing chapter's own stated scope) with two new Core Concepts subsections: VPC/subnets/Security Groups/IAM/AMI, and CloudFormation/CloudWatch — including the real, precise stateful-vs-stateless distinction between a Security Group and a Network ACL. Cheat sheet table and a new flashcard card added to match.
- `syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md` expanded with real, compiled, executed evidence for default/static interface methods and the diamond-problem collision: [`DefaultStaticInterfaceMethodsDemo.java`](../../practice/java/oop-fundamentals/classes-and-objects/src/DefaultStaticInterfaceMethodsDemo.java) (a real interface static-method call and a real inherited default method, 2/2 assertions), [`BrokenDefaultMethodDiamondCollision.java`](../../practice/java/oop-fundamentals/classes-and-objects/src/BrokenDefaultMethodDiamondCollision.java) (a real, deliberately broken `Duck implements Flyer, Swimmer` with a genuine `javac` error: `class Duck inherits unrelated defaults for move() from types Flyer and Swimmer`), and [`FixedDefaultMethodDiamondCollision.java`](../../practice/java/oop-fundamentals/classes-and-objects/src/FixedDefaultMethodDiamondCollision.java) (the real fix, using `InterfaceName.super.method()` to reach both parents explicitly, 1/1 assertion). §4, §5, §7, and §9 updated to cite this evidence directly rather than asserting the behavior in prose alone.
- Cheat sheet and flashcard deck added for T-2212. **New totals: 209 cheat sheets, 214 flashcard decks, 730 cards.**
- `00-project/syllabus-transformation-plan.md`'s `T-2200`–`T-2299` reserved-range note extended a third time.
- Full validator run: zero new errors (the same 3 pre-existing, unrelated errors remain).

### Not yet done

- Kanban (named as a real, different Agile framework in T-2212's own text) is not covered in depth — deliberately scoped out as beyond a fundamentals chapter, per that chapter's own Question 2 follow-up note.

## [2026-09-08] — `study-packs/junior-to-mid/` re-sequenced from 7 to 8 weeks

### Fixed

- Closed a flagged follow-up from earlier the same day: the study pack had not been re-sequenced after the learning path grew from 22 to 25 topics (T-2209, T-2210, T-2211 inserted). Split the original Week 1 (Java Syntax Fundamentals, Java OOP Fundamentals, How a Computer Executes a Program) into a new Week 1 (Java Platform Basics, Java Syntax Fundamentals, Java Modifiers and Method Signatures) and Week 2 (Java OOP Fundamentals, Java Version Features Timeline, How a Computer Executes a Program) — both cite the same real, previously-verified assertion counts and compile-error transcripts as their respective chapters.
- Shifted `week-02` through `week-07` to `week-03` through `week-08` via `git mv`, then corrected every internal week-number reference inside each file (front matter `week:` field, title, H1, self/previous/next-week prose mentions, and `week-NN/` link targets) — done with placeholder-token `sed` substitutions per directory to avoid double-shifting collisions where multiple distinct week numbers appear in the same file, followed by a manual pass for the one file (new Week 3, formerly Week 2) where the split of the old Week 1 meant a single "Week 1" prerequisite mention became "Weeks 1–2." One residual bug (Week 8's own front-matter `week:` field left at `7` after the first pass) was caught by a full end-to-end chain verification (every week's `week:` field and every "Next Week" link target checked in sequence) and fixed.
- Updated the top-level `study-packs/junior-to-mid/README.md` (8-week table, ~60 total hours), `syllabus/00-overview/learning-paths/junior-to-mid.md`'s own "Weekly study pack" note (no longer flags the re-sequencing as undone), `study-packs/README.md`, and root `README.md` (both still said "7 weeks" / "22-topic sequence").
- Full validator run: zero new errors (the same 3 pre-existing, unrelated errors remain).

## [2026-09-08] — `00-project/frontend-topic-register.md` row-by-row audit

### Verified

- Ran a row-by-row audit of `00-project/frontend-topic-register.md`'s Gap column, not just its top-level `status` header (which was already corrected in an earlier same-day entry, but every individual row had not been individually re-checked). Extracted all 40 rows' chapter links (35 distinct files, since several D-F1 rows share a grouped chapter), confirmed every one resolves to a real file on disk, confirmed every file in `syllabus/21-frontend-web/` is claimed by some row (no orphans in either direction), and confirmed no file is a stub (smallest file is ~2,900 words). No corrections were needed — the register's content matched reality.
- Full validator run: zero new errors (the same 3 pre-existing, unrelated errors remain).

### Not done (by decision, not oversight)

- A validator check enforcing the `interview-playbook/company-prep/` / `*.private.md` privacy boundary (flagged in `00-project/syllabus-transformation-plan.md` §11/§12 rule 7, 2026-09-03) was built, tested against a real inserted leak, and then explicitly withdrawn the same day at the user's request: this boundary is being handled directly between the user and the assistant, not through repository-tracked tooling. `scripts/validate.py`, `CONTRIBUTING.md`, and this changelog were all reverted to their pre-check state.

## [2026-09-08] — Status-field promotion, exhaustive `practice/` re-verification, frontend depth-ladder confirmation, and a private companion repo

### Fixed (status field promotion: draft → canonical)

- User asked what else the repository needed. Found that every one of the 160 `syllabus/` chapters using the `document_type: handbook-chapter`/`syllabus-topic`/`playbook-technical-answer` template (`topic-specification.md`'s own `status: draft | reviewed | canonical` lifecycle) was still stuck at `draft`, despite every one of the 21 domains' own `INDEX.md` already declaring itself complete and fully L1–L4 — the status field had never once been promoted since scaffolding.
- Promoted all 160 files' front-matter `status` field from `draft` to `canonical` — matching the terminology `CLAUDE.md` already uses everywhere else for this content ("canonical chapter," "canonical topic," "canonical location"). Scope deliberately limited to files using that specific template/lifecycle field; `architecture-atlas/`, `production-cookbook/`, and non-chapter governance files (`CLAUDE.md`, `AGENTS.md`) use `status` for a different purpose and were left untouched.
- Full validator run: zero new errors (the same 3 pre-existing, unrelated errors remain).

### Verified (exhaustive re-execution of `practice/`, not a sampling)

- Re-verified essentially the entire `practice/java/` and `practice/frontend/` corpus by actually recompiling and re-running it, plus a `practice/sql/` spot check — not by re-reading claims. Method: classify every demo directory by its real import graph (JDK-only vs. needing an external library), then rebuild each one's actual documented run environment rather than assume a bare `javac`/`java` would work.
- **Pure-JDK Java (131 directories):** 99 passed immediately; the rest were reconciled one by one — `--add-opens` reflection flags, `--enable-preview --release 21` for Foreign Function/Structured Concurrency/Scoped Values, multi-package classpaths, missing CLI args, and JVM-bounded demos (`-Xmx`, `-XX:MaxMetaspaceSize`) that need a memory cap to terminate — every single one of these was a gap in the verification harness itself (traced to each demo's own README or file-header comment), never a real bug or fabricated claim in the repository.
- **External-dependency Java (Kafka × 4, Avro/Schema Registry, 2× Postgres, OpenTelemetry, JMH, Gson):** fetched the real jars from Maven Central, stood up real Kafka brokers, a real Confluent Schema Registry, and real Postgres 16 containers via Docker, and re-ran each demo end to end. Every one matched its chapter's or README's documented output exactly (or, for the two demos with a legitimately non-deterministic partition assignment, matched the *invariant* the chapter claims rather than one specific number). Torn down cleanly afterward — no dangling containers.
- **Spring (bean scopes/proxies, cache abstraction, bean lifecycle, async+transactional, REST/MVC with embedded Tomcat, Spring Boot autoconfiguration report):** real Spring Framework/Boot jars already cached from prior sessions; recompiled and re-ran each, replaying the exact `curl` sequences documented in `rest-api-fundamentals` and `spring-mvc-fundamentals`'s own transcripts — byte-for-byte match. One genuine near-miss caught and resolved: `spring-cache-abstraction-and-pitfalls` requires the `-parameters` javac flag (the README calls this out explicitly as "not optional" and explains why) — omitting it was a verification-harness mistake, not a repository defect.
- **Frontend (16 React/Vite apps, 1 Next.js app, TypeScript fundamentals, the monorepo demo, JavaScript fundamentals):** every Vite app built clean, the Next.js app's real production build succeeded, `tsc` reported zero errors, the npm-workspaces monorepo demo's two packages ran and shared code correctly, and the JavaScript fundamentals scripts' fresh output matched the committed transcript (aside from blank-line formatting from how the original capture concatenated runs).
- **SQL (`sql-fundamentals` lab):** ran the real lab script against a fresh Postgres 16 container; output matched the committed transcript line for line, including the exact real foreign-key-violation error text.
- **Net finding: zero fabricated claims, zero real regressions found anywhere in this pass.** Every discrepancy traced back to the verification script's own missing flag, missing infrastructure, or missing working directory — never to the repository's content being wrong. This is the first time this exhaustiveness bar (actual re-execution, not re-reading) was applied across the whole `practice/` tree at once rather than chapter-by-chapter as each was built.
- Not independently re-executed this pass (time-boxed, not skipped by oversight): the remaining ~15 Spring/JUnit/Mockito/ArchUnit/DDD-multi-package `practice/java/` directories not named above, and the Docker-layer-caching comparison in `container-image-internals` (its own script runs `docker builder prune -af`, which wipes the shared Docker build cache system-wide — skipped deliberately as a destructive, non-scoped side effect rather than run without asking first).

### Verified (frontend BEG→EXP depth ladder is real, not just labeled)

- Re-checked whether `syllabus/21-frontend-web/`'s 35 chapters actually span Beginner through Expert in substance, not only in their register's tier column. Extracted every chapter's real `mastery_levels_covered` front matter: L1 appears in 6 chapters (the true floor), L4 in 13 (a genuine Staff-depth cohort — full-stack integration, deployment models, the Edge runtime, Server Actions, streaming/Suspense, concurrent rendering, reconciliation/fiber, performance, state management, testing, TypeScript, component patterns, the live-coding protocol). Every L4-tagged chapter has an actual Staff-level heading; every L1-tagged chapter has an actual Foundation/Why-This-Matters heading — checked programmatically, not assumed from the front matter alone.

### Added (private companion repo)

- User's decision on the standing `company-prep`/`*.private.md` privacy question (open since `syllabus-transformation-plan.md` §11, 2026-09-03): rather than repo-internal tooling, real future interview-specific material (company prep, actual interview feedback, personal performance notes) goes in a new, separate, local-only private repository (`cracking-code-interviews-private`, sibling directory, no remote yet), never merged or synced into this one. This repo's own already-anonymized `interview-playbook/company-prep/large-ecommerce-retailer-senior-backend-remote.md` stays here unchanged — that decision (§2.8, resolved 2026-09-08 earlier the same day) is unrelated and not reopened.

## [2026-09-09] — `07-api-design`'s GraphQL/gRPC gap closed; new domain `22-ai-llm-engineering` opened

### Added (`07-api-design/graphql-api-design.md` and `grpc-api-design.md` — T-917, T-918)

- Gap audit found `07-api-design/INDEX.md` and its own T-803 ("API design: REST, gRPC, GraphQL, versioning") named all three protocols, but the physical chapter had zero mentions of either GraphQL or gRPC — confirmed by grep, not assumed. Closed as two new chapters rather than expanding T-803, matching the precedent T-2205 (REST) already set for this same domain.
- Both chapters use the domain's existing `handbook-chapter` template (matching `api-design.md`), backed by real, executed demos: `practice/java/graphql-api-design/` (real graphql-java 26.1, measuring a real 3-call-to-1-call N+1/DataLoader reduction and a real nullability-dependent null-propagation difference) and `practice/java/grpc-api-design/` (a real `protoc`-generated client/server pair exercising all four gRPC call shapes and a real caught `StatusRuntimeException`).
- Registered in `00-project/knowledge-architecture-blueprint.md`'s D9 table with a split-from-T-803 note; `07-api-design/INDEX.md` and `api-design.md`'s `related` front matter updated.

### Added (new domain `syllabus/22-ai-llm-engineering/` — T-2300, LLM API Integration Fundamentals)

- Found during the same gap audit: zero LLM/AI-engineering coverage anywhere in `syllabus/`, an increasingly common 2026 backend-interview topic. Presented the user two placement options (new top-level domain vs. a subsection of `17-architecture` or `11-system-design`) plus a scoping choice for the first chapter; user chose a new domain and "LLM API Integration Fundamentals" as the foundational entry point.
- `syllabus/22-ai-llm-engineering/llm-api-integration-fundamentals.md` (T-2300) — written against the 20-section Topic Specification template (matching `rest-api-fundamentals.md`'s precedent for a genuinely new, L1-from-zero topic), covering statelessness/conversation-growth cost, streaming, function/tool calling's real two-round protocol, and rate-limit backoff.
- Backed by a real, executed demo (`practice/java/llm-api-integration-fundamentals/`): a real `java.net.http.HttpClient` talking real HTTP to a real local `com.sun.net.httpserver.HttpServer` implementing Anthropic's real documented Messages API wire contract (request/response shape, SSE streaming event types, tool-use content blocks, usage accounting, 429 responses) — explicitly not a call to the live API (no key, no network dependency, fully reproducible), stated plainly in both the chapter and the pack's README rather than implied otherwise.
- Reserved ID range `T-2300`–`T-2399` for this domain, documented in `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection. Added `syllabus/22-ai-llm-engineering/INDEX.md` and a new domain row in `syllabus/00-overview/INDEX.md` (now 22 domains). Updated root `CLAUDE.md`'s Repository Structure tree with the new domain directory.

## [2026-09-10] — Second `22-ai-llm-engineering` chapter: RAG and Vector Databases (pgvector)

### Added (`syllabus/22-ai-llm-engineering/rag-and-vector-databases.md` — T-2301)

- User asked to continue with the next planned topic in the domain. Per T-2300's own note, RAG/vector-DB is the natural next topic since it assumes a reader already knows how to call an LLM API.
- Written against the same 20-section Topic Specification template as T-2300. Covers embeddings, distance metrics (cosine vs. L2, and the real normalization pitfall between them), HNSW approximate nearest-neighbor indexing, and RAG prompt assembly.
- Backed by a real, executed demo (`practice/java/rag-and-vector-databases/`): a real PostgreSQL 16 + `pgvector` Docker container, real JDBC calls, real vector storage and distance queries, a real `CREATE INDEX ... USING hnsw`, and real `EXPLAIN ANALYZE` evidence of a measured ~7x execution-time drop (Seq Scan to Index Scan) at 5,000 rows.
- Embeddings are computed by a real, deterministic hashing scheme (feature hashing / "hashing trick", L2-normalized) rather than a live embedding-API call — stated explicitly, the same honesty discipline as T-2300's "not a live API call" framing. The demo goes further: it deliberately proves, with real output, where that toy scheme's limitation shows up — a literal-keyword query retrieves the correct document at rank #1, but a meaning-equivalent paraphrase of the identical question drops that same document to rank #3, behind two unrelated documents. This real, unflattering result is used directly as the chapter's evidence for why production RAG requires a real trained embedding model, rather than being hidden or worked around.
- Docker pull environment note: pulling `pgvector/pgvector:pg16` (and even a small sanity-check `alpine` pull) took several minutes in this session's sandbox — slow, not blocked; confirmed via a direct registry connectivity check before concluding it would eventually succeed, rather than abandoning the real-container approach for a lower-fidelity substitute.
- Updated `syllabus/22-ai-llm-engineering/INDEX.md` (new row, updated status/last_updated, shortened "planned" list), `llm-api-integration-fundamentals.md`'s `related` front matter (cross-link to the new chapter), `syllabus/00-overview/INDEX.md` (2/N topics), and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection (T-2301 assignment recorded).

## [2026-09-10] — Illustration audit started: `02-java/jvm-internals` (11 diagrams), Phase 1 of a larger scope

### Added (real Mermaid diagrams, 11 `jvm-internals` chapters)

- User asked whether more diagrams/tables could be added, feeling the repository under-illustrated. A real census (grep for a fenced mermaid block across every `syllabus/` chapter, not a guess) found 57 chapters with zero diagrams; after excluding 13 legitimately diagram-less `behavioral/` narrative chapters and 3 `00-overview` meta docs, ~41 real candidates remain.
- Closed the single largest, highest-value cluster first: `02-java/jvm-internals`, 11 of 12 chapters. Full detail (which diagram per chapter, and why) is in `syllabus/02-java/INDEX.md`'s own 2026-09-10 note rather than duplicated here.
- Remaining scope, not yet done: `03-data-structures-algorithms` (8 pattern chapters), `02-java/language-core` Junior Fundamentals (5) + 2 more, newer Junior Fundamentals chapters in other domains (5), and single scattered chapters across six more domains including this session's own new `22-ai-llm-engineering` T-2300/T-2301.

### Corrected (illustration audit closed — false-positive caught, 11 real diagrams added, 11 judged adequate)

- The grep-for-mermaid census was a flawed gap detector: spot-checking `03-data-structures-algorithms` before touching it found 7 of 8 chapters already had a real, well-made ASCII diagram and the 8th a real worked-example table — zero actual gap, despite zero Mermaid. Reported this to the user rather than mechanically continuing; user asked for a full manual review of the rest of the scope instead.
- Manually reviewed all ~22 remaining candidates, judging each on whether it lacks a real diagram of a flow/mechanism/relationship (not whether it lacks a table — a comparison table is often the correct format, not a gap). 11 genuinely needed and got one; 11 were already adequate and got none, by decision. Full per-file list is in the root `CHANGELOG.md`'s matching entry. **The illustration audit is now complete — no remaining scope.**

## [2026-09-10] — Third `22-ai-llm-engineering` chapter: Embeddings

### Added (`syllabus/22-ai-llm-engineering/embeddings.md` — T-2302)

- User asked which domain/topic to continue with next; chose Embeddings, the topic T-2301 itself flagged as needing deeper treatment.
- Real demo (pure JDK, `practice/java/embeddings-fundamentals/`): real cosine similarity across 4 sentence-pair categories (true positive, paraphrase, polysemy, true negative) at 32 vs. 512 dimensions. Genuinely striking real finding, not designed in advance: at 32 dimensions the true-negative pair scores 0.5634 — nearly tying the true-positive pair's 0.7591 — while at 512 dimensions it correctly drops to 0.0909. Includes an inline Mermaid diagram of the embed-then-cosine-similarity pipeline, embedded within Section 5 per this domain's established pattern (the 20-section template has no dedicated Diagrams slot).
- Updated `syllabus/22-ai-llm-engineering/INDEX.md` (3/N), `rag-and-vector-databases.md`'s `related` front matter, `syllabus/00-overview/INDEX.md`, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — Fourth `22-ai-llm-engineering` chapter: Prompt Engineering Patterns

### Added (`syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md` — T-2303)

- Continued the domain's own "planned" list. Organized the chapter around a real, useful distinction: which prompting patterns are just careful wording (few-shot, "think step by step") versus real, distinct request parameters (`temperature`, `response_format`, the system/user role split) — no live model call anywhere.
- Real demo (`practice/java/prompt-engineering-patterns/`): a real seeded sampler proving `temperature` genuinely controls output variance (20/20 identical outputs at `temperature=0.0`; a real 3-way split across 20 trials at `0.9`); a real measured 8-vs-0 JSON-parse-failure gap between prompt-wording-only and a real structured-output parameter; real deterministic logic showing a system-role instruction resists an override attempt that the identical instruction, concatenated directly into user text, does not.
- Updated `syllabus/22-ai-llm-engineering/INDEX.md` (4/N), `llm-api-integration-fundamentals.md`'s `related` front matter, `syllabus/00-overview/INDEX.md`, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — Fifth `22-ai-llm-engineering` chapter: Agentic Workflows and Tool Orchestration

### Added (`syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md` — T-2304)

- Continued the domain's own "planned" list. T-2300 covered one tool call's round-trip; this chapter covers what happens when a task needs several chained calls — no live model call anywhere, the "agent's" decisions are real, deterministic Java logic standing in for a real model's `tool_use` choices.
- Real demo (`practice/java/agentic-workflows-and-tool-orchestration/`, pure JDK): a real 3-iteration multi-step chain (capital lookup -> weather lookup, second call's real argument depends on the first call's real result); a real runaway-loop safety demo (a task that never resolves, correctly stopped at a real 5-iteration cap); and a real, measured ~2x wall-clock speedup (408ms sequential vs. 207ms parallel, via a real `ExecutorService`) running two independent tool calls concurrently.
- Updated `syllabus/22-ai-llm-engineering/INDEX.md` (5/N), `llm-api-integration-fundamentals.md`'s `related` front matter, `syllabus/00-overview/INDEX.md`, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — Sixth `22-ai-llm-engineering` chapter: LLM Evaluation and Testing — originally-planned list complete

### Added (`syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md` — T-2305)

- Closed the domain's originally-planned six-topic list. T-2303's own Staff-level note named the real risk (a prompt change is a real behavior change with no test suite unless one exists); this chapter is that suite's real design — no live model call anywhere.
- Real demo (`practice/java/llm-evaluation-and-testing/`, pure JDK + Jackson): real proof `assertEquals`-style exact-match fails a genuinely correct answer purely from wording; real, deterministic rule-based/structured checks (JSON/schema validation); real embedding-based similarity scoring (reusing T-2302's mechanics) that catches a literal-overlap match but produces a real, honest false negative on a genuine paraphrase (0.2860 against a 0.30 threshold); and a real golden-dataset regression matrix across 5 test cases and two simulated model versions, finding a real regression and a real improvement in the same run.
- Updated `syllabus/22-ai-llm-engineering/INDEX.md` (6/6 originally-planned, domain remains open), `prompt-engineering-patterns.md` and `agentic-workflows-and-tool-orchestration.md`'s `related` front matter, `syllabus/00-overview/INDEX.md`, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-12] — `production-cookbook/` gains a 26-chapter backend backlog (136 → 165 entries), 37-chapter frontend gap flagged

### Added (`production-cookbook/`, backend domains)

- Re-diffed every `syllabus/` chapter carrying a genuine `## Production Scenarios` section against `production-cookbook/`'s own citations — never done before for this deliverable — and found a 26-chapter backend backlog (chapters predating this deliverable's original batches, plus every chapter added this session) and a 37-chapter `21-frontend-web` domain never once audited. User chose backend first.
- Closed 25 of the 26 backend chapters as 29 entries (5 chapters had two genuinely distinct scenarios; `ood-interview-problems.md` contributed only 1 of its 2). Deliberately excluded `writing-tests-live-in-an-interview.md` (its two scenarios are about candidate behavior in a live-coding interview, not a production system incident) and `ood-interview-problems.md`'s interviewer-follow-up scenario (interview evaluation, not a production incident) — the same honest-exclusion discipline already established for `foreign-function-and-memory-api.md`.
- Every entry's fields were built by expanding the source chapter's own `## Production Scenarios` text — no invented incidents.
- Updated `production-cookbook/README.md` (136 → 165 entries). `scripts/validate.py` shows the same pre-existing 3 errors, zero new.
- The 37-chapter `21-frontend-web` gap remains open, flagged for a future batch.

## [2026-09-13] — `practice/mock-interviews/` gains an AI/LLM Engineering round (12 → 13), reopening a closed deliverable for a fresh domain

### Added (`practice/mock-interviews/ai-llm-engineering-technical-round.md`)

- User asked whether the project structure was missing anything. Identified that `practice/mock-interviews/` (closed 12/12, 2026-08-11) had never been re-checked against domains added afterward — the same blind spot already found and fixed in `study-packs/`, `cheat-sheets/`, and `production-cookbook/` this session. Confirmed via grep: zero existing rounds mention frontend, GraphQL, gRPC, or AI/LLM.
- Built the first new round with no prior study-pack mock source to elevate (this domain postdates every study-pack program) — instead built from the 6 `22-ai-llm-engineering` chapters' own already-written Interview Questions (Section 15 of each), restructured into the Mock Interview Standard. 6 technical questions (one per chapter: cost mechanics, RAG diagnosis, cross-model embeddings, prompt-injection trust boundary, agentic-loop cost, golden-dataset regression) plus a closing production/technical story question.
- Updated `practice/mock-interviews/README.md` (12 → 13 rounds). `scripts/validate.py` shows the same pre-existing 3 errors, zero new.
- GraphQL/gRPC and frontend (F-401–F-403) mock rounds remain open, flagged items.

## [2026-09-13] — `practice/mock-interviews/` gains a GraphQL and gRPC round (13 → 14)

### Added (`practice/mock-interviews/graphql-grpc-api-design-round.md`)

- Continuing the same freshness pass: built with no prior study-pack mock source (both chapters added 2026-09-09, after every study-pack program). 4 of 6 technical questions are each chapter's own already-written Interview Questions; the other 2 (HTTP-200-on-error, shared-codegen guarantee) are drawn from each chapter's own already-written `flashcards/` deck.
- Cross-references the two real `production-cookbook/` entries already covering these exact failure modes (`graphql-n-plus-one-overloading-a-downstream-author-service.md`, `deadline-less-grpc-call-cascading-into-thread-pool-exhaustion.md`).
- Updated `practice/mock-interviews/README.md` (13 → 14 rounds). `scripts/validate.py` shows the same pre-existing 3 errors, zero new.
- Frontend (F-401–F-403) mock round remains the last open, flagged item.

## [2026-09-13] — `practice/mock-interviews/` gains a Frontend Security/Real-Time/Micro-Frontends round (14 → 15), closing the mock-interview freshness audit

### Added (`practice/mock-interviews/frontend-security-realtime-and-microfrontends-round.md`)

- Closed the last flagged item from the two entries above. Built covering F-401 (Frontend Security), F-402 (WebSocket/SSE), and F-403 (Micro-Frontends) — the newest `21-frontend-web` chapters, again with no prior study-pack mock source. All 6 technical questions are each chapter's own already-written Interview Questions (2 per chapter); cross-references all 3 real `production-cookbook/` entries already covering these exact scenarios (`reflected-xss-from-a-bypassed-default-escaping-path.md`, `websocket-dashboard-silently-freezing-after-a-dropped-connection.md`, `two-teams-colliding-on-release-schedules-for-one-shared-frontend-bundle.md`).
- Updated `practice/mock-interviews/README.md` (14 → 15 rounds). `scripts/validate.py` shows the same pre-existing 3 errors, zero new.
- **This closes the mock-interview freshness audit in full — no known outstanding gap remains across any domain.**

## [2026-09-13] — 4 UI improvements to the published docs site (`mkdocs.yml`)

### Fixed

- User asked whether the site's UI could be improved. Found checklist items (`- [ ] ...`, present in every syllabus-topic chapter's Mastery Checklist and every study-pack week's Completion Criteria) rendered as literal `[ ] text` in the built HTML rather than real checkboxes — `pymdownx.tasklist` was never enabled. Fixed, verified via a real local `mkdocs build` that `<li class="task-list-item">` with a real `<input>` checkbox now renders.
- Added `repo_url`/`edit_uri` (GitHub header link, "edit this page" on every chapter) — verified the generated edit link resolves to the exact real source file, since `docs_dir`'s mirrored directories share an identical relative path with the repo root.
- Added `navigation.footer` (prev/next chapter links) and `search.highlight`/`search.share`. Both verified present in the built site's HTML/JS output.
- Verified with a real, local `mkdocs build` matching the CI deploy workflow's exact command — exit 0, only pre-existing, unrelated TOC-anchor warnings.

## [2026-09-13] — 4 more UI improvements to the published docs site (`mkdocs.yml`)

### Added

- Enabled 4 more free/open-source `theme.features` from the installed `mkdocs-material` 9.7.7 package (checked the package's own template guards and JS bundle first — none Insiders-gated): `navigation.path` (breadcrumb trail), `navigation.tracking` (URL hash follows scroll), `header.autohide` (header hides on scroll-down), `content.action.view` ("view source" link next to "edit this page").
- Checked `syllabus/` for other unrendered-syntax bugs like the tasklist one before adding anything else (footnotes, content tabs, strikethrough/mark/superscript, keyboard-key syntax) — all zero real hits, so no unused extension added.
- Verified with a real local `mkdocs build` (exit 0, same pre-existing warnings only): confirmed `class="md-path"` breadcrumb markup, `header.autohide`/`navigation.tracking` reaching the page's JS config, and a "View source of this page" link.

## [2026-09-13] — 2 more UI improvements to the published docs site (`mkdocs.yml`)

### Added

- Enabled `navigation.prune` (trims the rendered nav DOM to the current section — a real win given 271+ syllabus chapters across 22 domains) and `navigation.instant.progress` (progress bar during instant page transitions).
- Deliberately skipped `navigation.expand` (would auto-expand the whole 271-chapter tree — worse at this size) and `toc.integrate` (debatable layout change, no evidence of improvement). Re-checked `syllabus/` for `attr_list` syntax first — all matches were mermaid arrows/code braces, not real usage, so not added.
- Verified with a real local `mkdocs build` (exit 0, same pre-existing warnings only).

## [2026-09-13] — Real per-page "last updated" dates (`mkdocs-git-revision-date-localized-plugin`)

### Added

- Before wiring the plugin in, inspected its source: it derives each page's date from `git log` on the page's `docs_dir` path. Since `docs/` is an rsync copy (gitignored, rebuilt every run), the mirrored files have no git history — the plugin would have fallen back to the build date for every one of 1,626 pages, a false "identical freshness" signal.
- Fixed in `scripts/build_docs_site.sh`: any mirrored `.md` file byte-identical to its real source (1,431 of 1,626) is now replaced with a relative symlink to that source, so `os.path.realpath()` resolves to the real, git-tracked file and the plugin sees its actual commit history. The 195 files the README.md link-rewrite step changes are left as real copies (symlinking would undo that fix) and fall back to the build date — a documented, narrow (12%) gap.
- Added `git-revision-date-localized` to `mkdocs.yml` (`type: date`, `fallback_to_build_date: true`); Material's `source-file.html` renders it automatically.
- Verified: a symlinked file rendered "September 8, 2026", matching `git log` on the real file exactly; a rewritten (non-symlinked) file fell back to the build date with no error. `validate.py`: same pre-existing 3 errors, 0 new.
- Also evaluated `mkdocs-redirects` for the `handbook/` → `syllabus/` migration — `mkdocs.yml` was first added 2026-09-08, one day after the 2026-09-07 migration, so the published site never served `handbook/` URLs. No real broken link exists; installed the package to check, then uninstalled it rather than fix a non-existent problem.

## [2026-09-13] — Search tokenization fix + custom 404 page

### Added

- Confirmed via the installed `mkdocs-material` package's own bundled English config that the real default search `separator` is `[\s\-]+` (whitespace/hyphen only) — technical tokens like `@Transactional`, `java.util.concurrent`, `getUser()` each index as one glued token. Configured `plugins.search.separator` to also split on punctuation and camelCase/PascalCase boundaries; verified the built `search_index.json` carries the new value.
- Added `docs/404.md` — the default 404 page had no guidance text (just "404 - Not found", though header/search/nav were already present via `overrides/main.html`). New copy explains the likely cause (the `syllabus/` migration) and links to the front page, the Syllabus index, and search.
- `validate.py`: same pre-existing 3 errors, 0 new.

## [2026-09-13] — TOC depth cap + printable chapters (`mkdocs-print-site-plugin`)

### Added

- Capped `toc_depth: 2` — some chapters (64 headings down to H3) made the floating TOC unusable. Verified the same chapter's TOC dropped from 64 to 35 links (H2-only); deep headings still work as direct permalinks.
- Added `mkdocs-print-site-plugin` for printable/PDF chapters. Its only real feature combines every nav page into one file (22MB for 1,626+ pages here) — not wanted, so `add_to_navigation: false` keeps it built but unlinked. The actually useful part is its site-wide `@media print` CSS (verified present on a regular page), which hides sidebar/search/nav chrome so printing/saving-as-PDF any single chapter now works cleanly.
- Incidental finding, not fixed here: `site/practice/` is 430MB of the build's 664MB total (vs. 61MB for `syllabus/`) — likely a `build_docs_site.sh` rsync-exclude gap for frontend build artifacts, flagged for a separate pass.
- Verified with a real build: exit 0, same pre-existing warnings. `validate.py`: same pre-existing 3 errors, 0 new.

## [2026-09-13] — 430MB of build-only jars excluded + checklist checkboxes made clickable and persistent

### Fixed

- `site/practice/` bloat traced to `practice/java/**/lib/*.jar` — already-`.gitignore`d Maven/Gradle dependency jars (419 files, 394MB) the rsync mirror copied wholesale, since only `*.class` was excluded, not the binary dependencies themselves. A blanket `--exclude='lib'` was considered and rejected — `practice/frontend/react-nextjs-fundamentals/lib/` holds real demo source (`dal.js`, `notes-store.js`), a legitimate Next.js convention. Used `--exclude='*.jar'` instead. `docs/practice/` dropped from 430MB to 21MB; total `site/` from 664MB to 270MB. Verified with `diff` that real `.md`/`.java` content is unaffected.
- User reported Mastery Checklist checkboxes couldn't be marked. `pymdownx.tasklist.custom_checkbox: true` alone still leaves the checkbox `disabled` — added `clickable_checkbox: true`. A clickable-but-unpersisted checkbox still forgets its state on reload, so added `docs/javascripts/checklist-progress.js`: saves checked state to the reader's own `localStorage`, keyed by page path + position, restoring on load and on every Material `document$` instant-nav event.
- Verified end-to-end with a real headless-browser test (Playwright/chromium, already available under `practice/frontend/react-testing/`) against a real local build: clicked a checkbox, confirmed it toggled and survived a reload, and confirmed a second checkbox on the same page stayed independently unchecked. Along the way confirmed Material's `custom_checkbox` CSS (real `<input>` at `opacity:0`, visible checkmark drawn by a sibling `::before`) still responds correctly to a real, unforced mouse click at its own on-screen coordinates.
- `validate.py`: same pre-existing 3 errors, 0 new.

## [2026-09-13] — Real per-page dates were silently going to break in CI (`deploy-docs.yml`)

### Fixed

- Re-examined `git-revision-date-localized`'s actual CI conditions rather than trusting the earlier local verification. `actions/checkout@v4` defaults to a shallow (`depth: 1`) clone — confirmed by simulating one locally: `git log` on a known file returned today's date, not its real 2026-09-08 commit. All 1,431 "real-dated" pages would have silently shown the same wrong (latest-push) date instead of the documented, honest build-date fallback for the other 195.
- Added `fetch-depth: 0` to the checkout step. Verified directly: a full local clone shows the correct real date for the same file.

## [2026-09-13] — `06-databases` content-depth audit: PgBouncer gap found and closed

### Added

- Deep-dive of all 16 `06-databases` chapters against `CLAUDE.md`'s Handbook Writing Standard (the domain picked from the pending content-depth-audit offer). Fully read 7 chapters end to end, verified Definition/Core Concepts/Historical Context on the other 9, confirmed every referenced practice-evidence directory exists. Zero factual errors found — every checked version claim (SSI in PG 9.1, `INCLUDE`/index-only scans in PG 11, JSONB in 9.4, BRIN in 9.5, window functions/recursive CTEs in PG 8.4, no PostgreSQL lock escalation) matched real behavior.
- One real gap: PgBouncer (database-tier pooling) was absent domain-wide despite [`connection-pooling-and-sizing.md`](../06-databases/connection-pooling-and-sizing.md) (T-607) covering HikariCP (app-tier pooling) in full.
- Closed by extending T-607: a new PgBouncer subsection (`session`/`transaction`/`statement` pool-mode distinction), backed by a real new lab — [`practice/sql/pgbouncer-transaction-pooling/`](../../practice/sql/pgbouncer-transaction-pooling/README.md), real PgBouncer 1.25.2 + PostgreSQL 16. Proved `pool_mode = transaction`'s real session-state-leak risk directly (two sequential, unrelated clients landed on the identical real backend, PID 79 both times; an unreleased advisory lock silently carried over) and that it still works correctly under genuine concurrency (two clients got two distinct backends, 79 vs. 98; a lock request correctly blocked for a real, measured ~2.96 seconds).
- Updated the chapter's front matter, Learning Objectives, Cheat Sheet, and 2 new Flashcards (mirrored into `cheat-sheets/` and `flashcards/`). Updated `syllabus/06-databases/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new.

## [2026-09-13] — Interview Question Bank: new deliverable, `06-databases` pilot

### Added

- User request: each `syllabus/` domain needs a ~150-question compendium organized by seniority tier (Junior/Mid/Senior/Staff), related questions consolidated across domains rather than duplicated. Scoped given the scale (22 × ~150): a project-level plan plus one pilot domain first.
- New `00-project/interview-question-bank-plan.md` — the compendium is a rapid-review layer distinct from (and linking to, not duplicating) each chapter's own deep `## Interview Questions` section; mandates mining real chapter content before writing anything new; requires reporting the real achieved count rather than padding to quota.
- New `syllabus/20-interview-preparation/question-bank/06-databases.md` (pilot): 40 deep questions (mined from all 16 chapters' `## Interview Questions`, re-organized into a 4-tier "what's expected" view, honestly marking tiers that genuinely don't apply) + 5 already-leveled Junior/Mid questions from the domain's Junior Fundamentals chapter + 48 quick-fire questions (from Flashcards) = **93 real questions**, short of ~150 by design (documented why).
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, same pre-existing warning categories only.

## [2026-09-13] — Interview Question Bank: `02-java` complete, 249 real questions

### Added

- User approved the pilot's format and asked to continue. `02-java` (61 chapters, the largest domain) split into 4 files matching its own real subdirectory structure: `question-bank/02-java-collections.md` (52), `question-bank/02-java-concurrency.md` (70, no Junior Fundamentals chapter in this subdomain — honestly marked where Junior/Mid genuinely don't apply), `question-bank/02-java-jvm-internals.md` (31, all 13 chapters genuinely target senior/staff only), `question-bank/02-java-language-core.md` (96) — **249 real questions total**.
- Caught and fixed a real bug during verification: two chapters use the older numbered-section template (`## 15. Interview Questions`), giving a different anchor (`#15-interview-questions`) than the rest of the domain — a real local `mkdocs build` surfaced both as broken-anchor warnings, fixed before commit.
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, same pre-existing warning categories only.

## [2026-09-13] — Interview Question Bank: `03-data-structures-algorithms`, 36 real questions

### Added

- New `question-bank/03-data-structures-algorithms.md`. This domain's 18 chapters have no Flashcards sections at all (they follow the Coding Interview Standard template, not the canonical Handbook Chapter template), so there's no quick-fire layer here, and no Junior Fundamentals chapter — Junior/Mid tiers are honestly derived from each chapter's own "Minimum acceptable answer" bar rather than invented as separate questions.
- 18 chapters × 2 deep questions = 36 real questions, all from each chapter's `## 15. Interview Questions` section (the same numbered-heading anchor pattern found in `02-java` applies domain-wide here — checked before writing).
- **472 real questions across 3 of 22 domains so far** (`06-databases` 93, `02-java` 249, `03-data-structures-algorithms` 36).
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, same pre-existing warning categories only.

## [2026-09-13] — Interview Question Bank: `04-software-design`, 10 real questions

### Added

- New `question-bank/04-software-design.md`. Genuinely the domain's smallest — only 3 canonical chapters exist — yielding 6 deep + 4 quick-fire (only one of the three chapters has a Flashcards section) = **10 real questions**, honestly reported as the domain's real, small size rather than padded.
- **482 real questions across 4 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, same pre-existing warning categories only.

## [2026-09-13] — Interview Question Bank: `05-spring`, 61 real questions

### Added

- New `question-bank/05-spring.md`. 12 chapters yielded 24 deep questions + 5 already-leveled Junior/Mid questions (from the domain's Junior Fundamentals chapter, `spring-mvc-fundamentals.md`) + 32 quick-fire questions = **61 real questions**.
- **543 real questions across 5 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, same pre-existing warning categories only.

## [2026-09-13] — Interview Question Bank: `07-api-design`, 29 real questions

### Added

- New `question-bank/07-api-design.md`. 5 chapters yielded 8 deep questions + 9 already-leveled Junior/Mid questions (from the domain's Junior Fundamentals chapter, `rest-api-fundamentals.md`) + 12 quick-fire questions = **29 real questions**.
- **572 real questions across 6 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, same pre-existing warning categories only.

## [2026-09-13] — Interview Question Bank: `08-testing`, 25 real questions

### Added

- New `question-bank/08-testing.md`. 8 chapters yielded 14 deep questions + 5 already-leveled Junior/Mid questions (from the domain's Junior Fundamentals chapter, `unit-testing-fundamentals-with-junit.md`) + 6 quick-fire questions (only 2 of 8 chapters have a Flashcards section) = **25 real questions**.
- **597 real questions across 7 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, same pre-existing warning categories only.

## [2026-09-13] — Interview Question Bank: `09-messaging-event-driven`, 57 real questions

### Added

- New `question-bank/09-messaging-event-driven.md`. 12 chapters yielded 24 deep questions + 33 quick-fire questions = **57 real questions**. No Junior Fundamentals chapter in this domain.
- Incidental fix while mining: `schema-registry-and-compatibility-evolution.md`'s Flashcards used `## Card:` (heading level 2) for all three cards instead of the file's own `### Card:` convention — corrected.
- **654 real questions across 8 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-13] — Interview Question Bank: `10-distributed-systems`, 39 real questions

### Added

- New `question-bank/10-distributed-systems.md`. 7 chapters yielded 15 deep questions + 24 quick-fire questions = **39 real questions**. No Junior Fundamentals chapter in this domain.
- Same class of incidental fix found again: `multi-region-failover-and-disaster-recovery.md`'s Flashcards used `## Card:` instead of `### Card:` for all three cards — corrected.
- **693 real questions across 9 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-13] — Repo-wide Flashcards heading-level bug fixed (42 files)

### Fixed

- Finding the `## Card:`-instead-of-`### Card:` bug twice while mining two different question-bank domains prompted a repo-wide check: `grep -rln "^## Card:" syllabus/` found 42 files (98 headings), including all 37 files in `syllabus/21-frontend-web/` (the entire frontend domain), plus 3 in `17-architecture/`, 1 in `11-system-design/`, 1 in `18-engineering-practices/`, and 2 in `02-java/`.
- Mechanical single-character-per-heading fix (`sed 's/^## Card:/### Card:/'`), 98 headings total. Verified: `validate.py` same pre-existing 3 errors/0 new; real local `mkdocs build` exit 0, same two pre-existing warning categories only.

## [2026-09-13] — Interview Question Bank: `11-system-design`, 48 real questions

### Added

- New `question-bank/11-system-design.md`. 9 chapters yielded 18 deep questions + 30 quick-fire questions = **48 real questions**. No Junior Fundamentals chapter in this domain.
- **741 real questions across 10 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-13] — Interview Question Bank: `12-security`, 46 real questions

### Added

- New `question-bank/12-security.md`. 9 chapters yielded 18 deep questions + 28 quick-fire questions = **46 real questions**. No Junior Fundamentals chapter in this domain. 8 of 9 chapters use a plain-bold-question Flashcards format instead of the `### Card:` template; 8 of 9 deep-question chapters lack an explicit "Minimum acceptable answer" tier, so Junior/Mid was derived from each question's "Common mistakes" field.
- **787 real questions across 11 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-13] — Interview Question Bank: `13-observability`, 24 real questions

### Added

- New `question-bank/13-observability.md`. 5 chapters yielded 10 deep questions + 14 quick-fire questions = **24 real questions**. No Junior Fundamentals chapter in this domain; genuinely the smallest domain covered so far after `04-software-design`.
- **811 real questions across 12 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-13] — Interview Question Bank: `14-devops-containers`, 22 real questions

### Added

- New `question-bank/14-devops-containers.md`. 5 chapters yielded 8 deep questions + 5 leveled Junior/Mid questions + 9 quick-fire questions = **22 real questions**. `docker-and-containers-fundamentals.md` is this domain's Junior Fundamentals chapter (older numbered `## 15. Interview Questions` template).
- **833 real questions across 13 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-13] — Interview Question Bank: `15-cloud`, 21 real questions

### Added

- New `question-bank/15-cloud.md`. 4 chapters yielded 8 deep questions + 13 quick-fire questions = **21 real questions**. No Junior Fundamentals chapter in this domain; genuinely small (only 4 chapters).
- **854 real questions across 14 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-13] — Interview Question Bank: `16-performance-jvm`, 15 real questions

### Added

- New `question-bank/16-performance-jvm.md`. 3 chapters yielded 6 deep questions + 9 quick-fire questions = **15 real questions**. No Junior Fundamentals chapter in this domain; genuinely small (only 3 chapters), a companion to `02-java-jvm-internals.md`. 2 of 3 chapters use a plain `**Q:** ... **A:** ...` Flashcards format instead of the `### Card:` template.
- **869 real questions across 15 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-13] — Interview Question Bank: `17-architecture`, 56 real questions

### Added

- New `question-bank/17-architecture.md`. 9 chapters yielded 28 deep questions + 28 quick-fire questions = **56 real questions**. No Junior Fundamentals chapter in this domain. `clean-hexagonal-architecture.md` genuinely has 10 deep questions (plain template, not a leveled format).
- **925 real questions across 16 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-13] — Interview Question Bank: `18-engineering-practices`, 14 real questions

### Added

- New `question-bank/18-engineering-practices.md`. 6 chapters yielded 12 deep questions + 2 quick-fire questions = **14 real questions**. No Junior Fundamentals chapter in this domain. 5 of 6 chapters use the older numbered `## 15. Interview Questions` template with no Flashcards section at all; only `git-internals-and-collaboration-workflows.md` has a `### Card:` section.
- **939 real questions across 17 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-13] — Interview Question Bank: `19-leadership-staff`, 12 real questions

### Added

- New `question-bank/19-leadership-staff.md`. 7 chapters yielded 12 deep questions = **12 real questions**. No Junior Fundamentals chapter (inherently Senior/Staff-scoped material). All 7 chapters use the older numbered `## 15. Interview Questions` template with no Flashcards section at all — no quick-fire layer exists in this domain.
- **951 real questions across 18 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-13] — Interview Question Bank: `01-computer-science-foundations`, 10 real questions

### Added

- New `question-bank/01-computer-science-foundations.md`. 5 chapters yielded 10 deep questions = **10 real questions**. All 5 chapters use the older numbered `## 15. Interview Questions` template with no Flashcards section at all. Junior/Mid tiers derived from each question's own "Minimum acceptable answer" and "Common mistakes" fields, no separate leveled format present.
- **961 real questions across 19 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Interview Question Bank: `20-interview-preparation`, 28 real questions

### Added

- New `question-bank/20-interview-preparation.md`, the self-referential domain where the compendium itself lives. Scope decision: the 15 numbered `behavioral/` chapters plus README use the Behavioral Handbook template with no `## Interview Questions` section, so nothing to mine; the 6 "interview craft" chapters (loop structures, coding communication protocol, both system-design delivery chapters, both technical-answers chapters) use the standard template and were mined normally. `mock-interviews/` and `company-prep/` stayed unmined (reference-only/private). 6 chapters yielded 12 deep questions + 16 quick-fire questions = **28 real questions**. No Junior Fundamentals chapter.
- **989 real questions across 20 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Interview Question Bank: `21-frontend-web`, 162 real questions, 3-file split

### Added

- `21-frontend-web` (38 chapters, the largest remaining domain) split into 3 files matching its own natural topic groups, same precedent as `02-java`'s 4-file split: `question-bank/21-frontend-web-foundations.md` (7 chapters — JS/TS/browser fundamentals, security, micro-frontends, WebSocket/SSE, live-coding protocol — 38 real questions), `21-frontend-web-react.md` (14 React chapters — 56 real questions), `21-frontend-web-nextjs.md` (17 Next.js chapters — 68 real questions). All 38 chapters use the plain template with no structural variance; the Next.js chapters are unusually evidence-heavy, grounded in real captured tests rather than documentation claims.
- **162 real questions for this domain alone. 1151 real questions across 21 of 22 domains so far.**
- Updated `syllabus/20-interview-preparation/INDEX.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Interview Question Bank: `22-ai-llm-engineering`, 30 real questions — initiative complete, all 22 domains done

### Added

- New `question-bank/22-ai-llm-engineering.md`, the final domain in the 22-domain plan. All 6 chapters use the older numbered `## 15. Interview Questions` template with a Junior→Staff leveled Q&A set (5 questions per chapter) and no Flashcards section — a genuinely new domain built with the leveled format from the start.
- **1181 real questions across all 22 of 22 domains — the Interview Question Bank initiative is complete.** Rewrote `syllabus/20-interview-preparation/INDEX.md`'s question-bank callout to a completion summary with the full per-domain breakdown.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Site UI bug fixes and three content gaps, from reader feedback on the live docs site

### Fixed (`mkdocs.yml`, `overrides/`, `docs/javascripts/`, `docs/stylesheets/`)

- Reader-reported: footer/prev-next nav visually stuck overlapping the next page's content after clicking through a chapter. Root cause: `navigation.instant`'s SPA-style content swap getting caught mid-transition (worse combined with `header.autohide`'s scroll-position transform). Removed `navigation.instant`/`navigation.instant.progress` from `mkdocs.yml` — trades instant-swap speed for eliminating this class of bug; a full page load cannot get stuck this way.
- Reader-reported: the "edit this page" pencil icon linking straight to GitHub's edit-in-browser flow read as "anyone can modify our content," though it only opens a fork+PR (no direct write to `main`). New `overrides/partials/actions.html` repurposes that icon into a printer icon calling `window.print()` — a visible way to reach the per-chapter PDF export that was already working silently via the `print-site` plugin's site-wide `@media print` CSS. The "view raw source" icon is unchanged.
- New `docs/javascripts/diagram-zoom.js` + CSS in `docs/stylesheets/extra.css`: click-to-enlarge overlay for Mermaid diagrams, closed by clicking again, the backdrop, or Escape — dense diagrams were unreadable at the article's fixed content width.

### Added (`syllabus/02-java/language-core/`, `syllabus/05-spring/`)

- `java-modifiers-and-method-signatures.md` — new "Overloading vs. overriding" definitions-plus-comparison-table subsection, linking onward to `polymorphism-and-dynamic-dispatch.md` for the dispatch-mechanism depth rather than duplicating it.
- `java-version-features-timeline.md` — added a full release-by-release table (Java 9 through 25, every non-LTS release included) beneath the existing LTS-only table; cross-linked to `../concurrency/foreign-function-and-memory-api.md` (Java 22's FFM API), previously unreferenced from this chapter.
- `05-spring/spring-mvc-fundamentals.md` — two new comparison tables: stereotype annotations side by side (`@Component`/`@Service`/`@Repository`/`@Controller`/`@RestController`) and the common companion annotations (`@Configuration`/`@Bean`/`@Autowired`/`@Qualifier`/`@Primary`/`@Value`) this chapter's own demo doesn't use.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns; print-button override confirmed rendering.

## [2026-09-14] — `spring-mvc-fundamentals.md`: dependency injection types and design patterns

### Added

- New "The three ways to get a dependency injected" subsection — definitions plus a constructor/setter/field injection comparison table (`@Autowired` placement, `final`-field support, immutability, unit-testability with plain `new`, whether it visibly exposes "too many dependencies").
- New "Design patterns Spring resolves for you" subsection — Singleton, Factory, Proxy, Template Method, Observer, Strategy, and Decorator, each row linking onward to this domain's existing deep-dive chapters (`spring-bean-scopes-and-proxy-modes.md`, `transactional-proxy-mechanics-and-propagation.md`) rather than re-explaining the mechanism.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — `cheat-sheets/`/`flashcards/` synced for the 3 chapters edited earlier today

### Fixed

- `java-modifiers-and-method-signatures.md`, `java-version-features-timeline.md`, `spring-mvc-fundamentals.md` gained new canonical content earlier today (overload/override table, full Java 9-25 version table, Spring DI-types and design-patterns tables) that their own standalone `cheat-sheets/*.md`/`flashcards/*.md` files never picked up — a known recurring gap (see this project's own complementary-deliverable-lag pattern). Synced all six files: new "Overloading vs. Overriding" table + pitfall + 1 flashcard on java-modifiers; a non-LTS-highlights line + 1 flashcard (JPMS) on java-version-features-timeline; new DI-types and design-patterns tables + 2 flashcards (setter injection's real use case, `@Transactional` as Proxy) on spring-mvc-fundamentals.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Content-quality audit: `12-security` and `10-distributed-systems`, and a real OWASP Top 10:2025 edition update

### Fixed

- Ran the same content-quality deep-dive method already used on `02-java`/`05-spring`/`06-databases` against the two remaining flagged domains. `10-distributed-systems` (7 chapters) came back fully clean — no factual errors, no fabrication, all quorum/Raft/consistent-hashing math verified correct, no coverage gaps. `12-security` (9 chapters) came back clean on 8 of 9 chapters, with one real, verified-live finding on the 9th.
- `owasp-top-10-for-backend-services.md` stated "2021 is the current published edition" — confirmed via live `WebSearch`/`WebFetch` against `owasp.org` that OWASP published a genuine Top 10:2025 edition. Real changes: Security Misconfiguration #5→#2 (now A02); SSRF folded into Broken Access Control (A01), losing its standalone A10 slot; "Vulnerable and Outdated Components" renamed/widened to "Software Supply Chain Failures" (A03); a genuinely new category, "Mishandling of Exceptional Conditions" (A10), added for fail-open error handling; remaining categories renumbered or minor-renamed only.
- Rewrote the chapter throughout to the 2025 numbering (diagrams, category mapping table with an explicit 2021→2025 column, Production Scenarios — new A10:2025 fail-open payment-authorization scenario, Comparisons, Interview Answer Framework, a new Interview Question 3 on the taxonomy change itself, Cheat Sheet, Flashcards), and synced the standalone `cheat-sheets/`/`flashcards/` files.
- Fixed three stale 2021-era A-number citations found elsewhere: `study-packs/week-17/01-owasp-top-10-for-backend-services.md`, `production-cookbook/credential-stuffing-undetected-from-missing-security-event-logging.md`, `production-cookbook/like-clause-sql-injection-surviving-an-automated-scan.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — `incident-response-and-blameless-postmortems.md` gains a general production-debugging process

### Added

- User request, from two real interview questions they'd been asked: a production-error debugging process (new vs. legacy app) and the full HTTP request/response lifecycle. The second was already fully answered as Interview Question 1 in `syllabus/01-computer-science-foundations/networking-basics.md` — confirmed, no change needed. The first had no canonical home: this chapter covered the mitigate-vs-diagnose decision and postmortem culture, but never the concrete steps "diagnosis" itself consists of.
- New "The Diagnosis Process, Step by Step" section — an 8-step methodology (scope, evidence, hypothesis, isolate, confirm, fix, verify, close the loop) with a mermaid diagram and an explicit legacy-code branch (a characterization test before the fix step, cross-linked to `working-with-legacy-code.md`), a new Interview Question 3, 2 new Flashcards, and matching Cheat Sheet updates — synced to the standalone `cheat-sheets/`/`flashcards/` files and cross-linked bidirectionally with `working-with-legacy-code.md`.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — `exception-design-and-hierarchy-strategy.md` gains the full checked/unchecked hierarchy

### Added

- User request: this chapter's title promised "hierarchy strategy" but only had one paragraph on checked vs. unchecked — no mention of `Throwable` as the actual root, no mention of `Error` at all, no list of common derived exception classes.
- New hierarchy section in Level 1 — Foundation (mermaid diagram: `Throwable` → `Error`/`Exception` → `RuntimeException`, plus the exact structural rule for checked vs. unchecked) and a new Core Concepts table of common derived exceptions by category. New Interview Question 0 (the hierarchy itself) placed before the existing cause-chaining/try-with-resources questions. 2 new Flashcards, Cheat Sheet updates, synced to the standalone `cheat-sheets/`/`flashcards/` files.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — `jpa-entity-lifecycle-and-the-n1-problem.md` gains the full lifecycle state machine + a JPA/Hibernate annotations reference

### Added

- User request: the chapter's own title promised "Entity Lifecycle" but never actually enumerated the state machine; also asked for JPA/Hibernate annotation coverage.
- New Core Concepts subsections: the four-state lifecycle (Transient/Persistent/Detached/Removed) with a mermaid state diagram and the `merge()`-returns-a-new-copy gotcha; cascade types (`orphanRemoval` vs. `CascadeType.REMOVE`); ID generation strategy (`IDENTITY` structurally blocks JDBC batch inserts, `SEQUENCE` doesn't); `equals()`/`hashCode()` on entities.
- New "JPA and Hibernate Annotations Reference" section: core JPA (`jakarta.persistence`, portable) vs. Hibernate-specific (`org.hibernate.annotations`) tables, including lifecycle-callback annotations.
- 2 new Interview Questions, 4 new Flashcards, Cheat Sheet updates, synced to the standalone `cheat-sheets/`/`flashcards/` files.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — `bean-validation-and-global-exception-handling.md` gains `@ControllerAdvice` vs. `@RestControllerAdvice`

### Added

- User request: real gap found. The chapter uses `@RestControllerAdvice` throughout but never named or explained plain `@ControllerAdvice`, nor stated the relationship between the two.
- New Core Concepts subsection with a comparison table (composition, how `@ExceptionHandler` return values resolve, scoping via `basePackages`/`assignableTypes`), cross-linked to `spring-mvc-fundamentals.md`'s parallel `@Controller`/`@RestController` pattern. 1 new Flashcard, Cheat Sheet row, synced to the standalone `cheat-sheets/`/`flashcards/` files.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Content-quality audit: `01-computer-science-foundations` (6th domain), one citation fixed

### Fixed

- User-requested content-quality deep-dive of the 6th domain, same method as the prior 5. Read all 5 chapters in full. Found one real, minor citation error: `how-a-computer-executes-a-program.md` labeled JVMS SE 21 Chapter 6 as "The `javac` Compiler" — its real title, verified live against `docs.oracle.com`, is "The Java Virtual Machine Instruction Set." The URL and the choice to cite that chapter were both correct; only the label was wrong. Fixed.
- Every other claim across all 5 chapters checked out: Big-O tables, merge-sort recurrence, fetch-decode-execute cycle, two's complement/IEEE 754 mechanics, the Ariane 5 and Patriot-missile historical incidents, the OS process/thread model and virtual-thread M:N mechanics — no fabrication, every "real, measured" table's internal arithmetic verified consistent.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Spring Profiles, `pom.xml`, and `application.yml` structure added

### Added

- User request: real gap found. No chapter covered `@Profile`/`spring.profiles.active`/`application-{profile}.yml`, and no chapter covered a real `pom.xml`/`application.yml`'s structure.
- `twelve-factor-config.md`: new "Spring Profiles" section connecting this chapter's generic config-precedence story to Spring Boot's concrete implementation. New Interview Question 3, 1 new Flashcard, Cheat Sheet line.
- `spring-mvc-fundamentals.md`: new subsection at the start of Foundation (L1) — a real, minimal `pom.xml` and `application.yml`, each element explained (starters, dependency scopes, `spring-boot-maven-plugin`, `${VAR:default}` placeholders, `ddl-auto: validate`, Actuator endpoint exposure). New Interview Question Q6, 2 new Flashcards, 2 new Cheat Sheet tables — synced to the standalone `cheat-sheets/`/`flashcards/` files for both chapters.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — `spring-framework-vs-spring-boot.md`: `@SpringBootApplication` unpacked

### Added

- User request: `@SpringBootApplication` only ever appeared as a one-line code comment, never actually explained. New Core Concepts subsection breaking down all three composed annotations: `@SpringBootConfiguration`, `@EnableAutoConfiguration`, `@ComponentScan` — what each does, and what breaks without it — correcting an imprecise prior comment that said `@Configuration` instead of `@SpringBootConfiguration`.
- New Interview Question 3, 2 new Flashcards, Cheat Sheet table and rows, synced to the standalone `cheat-sheets/`/`flashcards/` files.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Content-quality audit: `03-data-structures-algorithms` (7th domain), 5 non-compiling snippets fixed

### Fixed

- Continued the domain-by-domain content-quality audit via two parallel background agents (9 chapters each, 18 total). All complexity claims, algorithm mechanics, and edge-case handling verified correct across every chapter — no fabrication, no broken links.
- Found and fixed one recurring bug class: 5 Java snippets across 5 chapters were fully-signed, non-void methods with bodies elided by a comment instead of a real `return` — none would compile as written. Fixed: `hashing-patterns-and-frequency-maps.md` (Intersection of Two Arrays), `tries-and-prefix-structures.md` (Replace Words), `heaps-top-k-and-k-way-merge.md` (Furthest Building, also invalid enhanced-for syntax), `design-style-coding-problems.md` (Design Twitter), `dynamic-programming.md` (Stock IV's `k >= n/2` early-exit — also made the chapter's own stated complexity claim false until implemented).
- Minor terminology fix in `hashing-patterns-and-frequency-maps.md` (Happy Number's complexity misused "amortized").
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — `12-security` gains a 10th chapter: Enterprise SSO, SAML, and Federated Identity

### Added

- User request, from a real job-posting requirement naming SSO/Federated SSO/SAML/OAuth2/OIDC and commercial IAM solutions (PingFederate). OAuth2/OIDC were already covered; SSO, Federated SSO, SAML, and the commercial IAM vendor landscape had zero coverage anywhere in the repository.
- New chapter `enterprise-sso-saml-and-federated-identity.md` (T-1309): SSO vs. Federated SSO, SAML 2.0 core artifacts, a SAML/OAuth2/OIDC comparison table, the commercial IAM landscape (PingFederate/Okta/Azure AD/Keycloak/ADFS), and real Spring Security SAML2 integration (dependency, `application.yml` structure verified live against Spring Security's current docs, `SecurityConfig` wiring) continuing directly from `spring-mvc-fundamentals.md`'s own `pom.xml`/`application.yml` anatomy. Explicitly labeled as real, accurate configuration — not a locally executed multi-party demo — matching `oauth2-oidc-and-jwt.md`'s own established honesty pattern.
- Cross-linked with `oauth2-oidc-and-jwt.md`; updated `syllabus/12-security/INDEX.md` (9 → 10). New standalone `cheat-sheets/`/`flashcards/` files.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, new page confirmed rendered.

## [2026-09-15] — `study-packs/mid-to-senior/` gains Week 13: CSRF/CORS and Enterprise SSO, closing a real scheduling gap

### Added

- The prior meta-documentation audit (below) flagged that `12-security`'s two newest chapters — `csrf-cors-and-session-security.md` (added 2026-09-10) and `enterprise-sso-saml-and-federated-identity.md` (added 2026-09-14, from a real named job-posting requirement) — were never scheduled in any `study-packs/` program, despite both existing in the domain's own `INDEX.md`. The user asked for this closed specifically in `study-packs/mid-to-senior/`.
- Added `study-packs/mid-to-senior/week-13/` (`README.md` + `MANIFEST.md`): a new, final week covering both chapters — the real CSRF/CORS forged-request-vs-synchronizer-token demo (`practice/java/week-17/csrf-cors-session/`), the SAML assertion's four-check validation discipline, and the commercial IAM landscape. Appended at the end (not inserted mid-sequence) to avoid renumbering Weeks 1–12, the same pattern this pack already used when Weeks 11–12 were added 2026-09-12.
- Updated `study-packs/mid-to-senior/week-12/README.md`'s "Next Week" link and `week-12/MANIFEST.md`'s "final week" label to point at the new Week 13; updated `study-packs/mid-to-senior/README.md`'s week table, hour total (93–116h → 101–125h), and week count (12 → 13); updated `study-packs/README.md`'s program summary and its own stale `mid-to-senior/week-01 through week-10` numbering claim (was already wrong before this change — corrected to `week-13`, and while there, corrected three other programs' own stale week-count claims in the same sentence: `junior-to-mid` week-08→week-09, `senior-to-staff` week-08→week-11, `frontend-mid-to-senior` week-10→week-11).
- Updated the source `syllabus/00-overview/learning-paths/mid-to-senior.md`: Domain 9 (Security)'s priority-topics list now names all 4 scheduled chapters; time budget ~12 weeks → ~13 weeks; added a dated 2026-09-15 update note.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0.

## [2026-09-15] — `syllabus/00-overview/` meta-documentation audit: top-level INDEX.md undercounted 5 domains, learning-path counts corrected

### Fixed

- Following the just-completed 22-domain content-quality audit, checked whether `syllabus/00-overview/INDEX.md`, `taxonomy.md`, `learning-paths.md`, and every file in `learning-paths/` still matched the real, current file counts on disk — they had drifted for several domains whose chapter count grew after this table was last touched.
- Verified real chapter counts for all 22 domains directly (`find syllabus/<domain>/ -maxdepth 1 -name "*.md" ! -name INDEX.md`, subdomain-aware for `02-java` and `20-interview-preparation`, `question-bank/` excluded) against both `syllabus/00-overview/INDEX.md`'s domain table and each domain's own `INDEX.md`. Every one of the 22 per-domain `INDEX.md` files was already accurate — no fixes needed there.
- `syllabus/00-overview/INDEX.md`'s domain table was stale for 5 domains, each undercounting a chapter added after the table's last edit: `07-api-design` (2/2 → real 5, missing REST API Fundamentals, GraphQL API Design, gRPC API Design), `08-testing` (7/7 → real 8, missing Writing Tests Live in an Interview), `12-security` (9/9 → real 10, missing Enterprise SSO/SAML/Federated Identity added 2026-09-14), `14-devops-containers` (4/4 → real 5, missing Docker and Containers Fundamentals), `18-engineering-practices` (5/5 → real 6, missing SDLC and Agile Methodology Fundamentals). Corrected all 5 rows with an honestly-dated note; bumped front matter `last_updated` to 2026-09-15.
- `learning-paths/backend-java-specialization.md`'s per-domain topic-count table hadn't been re-verified since its own 2026-09-08 correction: Java (52 → real 61), Spring (10 → real 12), Databases (15 → real 17), and Messaging & Event-Driven Systems (9 → real 12) had all grown from later gap-audit chapters never folded in; Performance & JVM Tuning (3) was already correct. Fixed the table and its provenance note; bumped `last_updated`.
- Fixed a front-matter/body inconsistency in 4 learning-path documents where the body's own dated "Updated" note was newer than the front-matter `last_updated` field: `junior-to-mid.md` (2026-09-08 → 2026-09-12), `mid-to-senior.md` and `senior-to-staff.md` (2026-09-05 → 2026-09-12), `frontend-mid-to-senior.md` (2026-09-08 → 2026-09-12).

### Verified, left unchanged

- `taxonomy.md` — explicitly a verbatim extraction from the approved transformation plan (its own provenance note), correctly describing the *original* plan's domain structure and topic counts (e.g., `handbook/testing/ (7 topics)`) as historical record, not the current state. Per `CLAUDE.md`'s Structural Update note, a file explicitly framed as historical record is not rewritten to match current counts. No changes.
- `learning-paths.md` — the Phase 1 outline table and the Phase 6/2026-09-08 update sections carry no specific topic counts to go stale; only points at the six (plus two frontend) real path documents, which were checked individually. No changes.
- `learning-paths/junior-to-mid.md` (26/26 topics), `senior-to-staff.md` (21/21), `mid-to-senior.md` (15/15 domains), `frontend-junior-to-mid.md` (14/14), `frontend-mid-to-senior.md` (23/23) — every path's own stated topic count matched its actual sequence table. Left alone.
- `interview-emergency-sprint.md`, `senior-interview-refresh.md` — reference `study-packs/`, `cheat-sheets/`, and `flashcards/` wholesale rather than enumerating topics; nothing to drift. Left alone.
- Deliberately did not add the new Enterprise SSO (`12-security`), CI/CD Pipeline Design (`14-devops-containers`), or Writing Tests Live in an Interview (`08-testing`) chapters to any learning path: none declare an `interview_paths` front-matter field naming a gap (unlike, e.g., `sdlc-and-agile-methodology-fundamentals.md`, which self-declared `[junior-to-mid, interview-emergency-sprint]` and was already correctly threaded into `junior-to-mid.md` as Topic 26), and the paths whose domains they'd belong to (`mid-to-senior.md`, `senior-to-staff.md`) are explicitly non-exhaustive by design, pointing at each domain's own `INDEX.md` rather than re-listing every topic.
- `python3 scripts/validate.py`: same pre-existing 3 broken-link errors (unrelated to this pass), 0 new.

## [2026-09-15] — Content-quality audit: `22-ai-llm-engineering` (22nd and final domain), fully clean — audit initiative complete

### Verified

- Completed the domain-by-domain content-quality audit initiative: all 22 syllabus domains now audited. Read all 6 chapters directly (`llm-api-integration-fundamentals.md`, `rag-and-vector-databases.md`, `embeddings.md`, `prompt-engineering-patterns.md`, `agentic-workflows-and-tool-orchestration.md`, `llm-evaluation-and-testing.md`). All fully clean — every "real, measured" claim hand-recomputed and confirmed correct, every referenced practice directory confirmed to exist. No fixes needed.
- `validate.py`: same pre-existing 3 errors, 0 new.

## [2026-09-15] — Content-quality audit: `21-frontend-web` (21st domain), two files fixed

### Fixed

- Continued the domain-by-domain content-quality audit (21st domain, 38 chapters, via four parallel background agents). 36 of 38 chapters verified fully clean — all "real, measured" claims cross-checked against actual captured output in `practice/frontend/*/README.md`.
- `react-testing.md`: fixed a stale `prerequisites` field (pointed at the wrong sibling chapter in the numbered register sequence).
- `frontend-live-coding-and-debugging-protocol.md`: fixed a stale pre-migration "Canonical location" reference and a real typo.
- Verified live via WebSearch: the Next.js 16 Middleware→Proxy rename claimed in several chapters is real and accurate.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Content-quality audit: `20-interview-preparation` (20th domain), 5 files fixed

### Fixed

- Continued the domain-by-domain content-quality audit (20th domain, 21 chapters, via two parallel background agents — `question-bank/` excluded, already complete). 16 of 21 chapters verified fully clean — every behavioral scenario confirmed correctly labeled illustrative, no fabricated personal experience.
- `behavioral/10-migrations-and-large-technical-change.md`: fixed 2 occurrences of the recurring link-text/href mismatch bug.
- 4 files (`system-design/time-boxing-and-mid-round-changes.md`, `technical-answers/technical-answer-framework.md`, `coding/coding-interview-communication-protocol.md`, `behavioral/company-loop-structures-and-question-pattern-recognition.md`) had stale pre-migration `handbook/`/`interview-playbook/<domain>/` path references left over from the 2026-09-07 syllabus migration.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Content-quality audit: `19-leadership-staff` (19th domain), one recurring link fix

### Fixed

- Continued the domain-by-domain content-quality audit (19th domain, 7 chapters, via two parallel background agents). 6 of 7 chapters verified fully clean — external citations (Google SRE book, Google re:Work) verified live, every scenario correctly labeled representative/illustrative per the Behavioral Handbook Standard.
- `leading-migrations-and-large-technical-change.md`: fixed 3 occurrences of a link-text/href mismatch (stale chapter title, missing "Anti-Corruption Layer" from the real title).
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Content-quality audit: `18-engineering-practices` (18th domain), one typo fixed

### Fixed

- Continued the domain-by-domain content-quality audit (18th domain, 6 chapters, read directly). 5 of 6 chapters verified fully clean, all executed-output claims checked (git blob hashes, bisect complexity, characterization-test arithmetic, refactoring parity tests).
- `sdlc-and-agile-methodology-fundamentals.md`: fixed a duplicated-word typo.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Content-quality audit: `17-architecture` (17th domain), three real fixes

### Fixed

- Continued the domain-by-domain content-quality audit (17th domain, 9 chapters, via two parallel background agents). 6 of 9 chapters verified fully clean, every measured number cross-checked against real practice code.
- `ddd-strategic-bounded-contexts-and-context-mapping.md`: fixed 3 occurrences of a link-text/href mismatch (stale chapter title in the display text).
- `cqrs-read-write-separation.md`: fixed a wrong link target (pointed at the wrong T-906 chapter) and two stale "planned"/T-905-not-yet-written references to Event Sourcing, which already exists as a canonical chapter.
- `architecture-decision-records.md`: fixed 2 occurrences of a link-text/href mismatch (text named the real script path, href pointed at a README instead).
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Content-quality audit: `15-cloud` (16th domain), fully clean

### Verified

- Continued the domain-by-domain content-quality audit (16th domain, 4 chapters, read directly). All 4 chapters verified fully clean — all worked cost calculations re-derived by hand and confirmed correct, provider-service cross-references checked, previously-verified rename claims (Azure AD → Microsoft Entra ID, GCP Deployment Manager → Infrastructure Manager) still hold. No fixes needed.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Content-quality audit: `14-devops-containers` (15th domain), two real fixes

### Fixed

- Continued the domain-by-domain content-quality audit (15th domain, 5 chapters, read directly). 3 of 5 chapters verified fully clean.
- `container-image-internals.md`: fixed a link-text/href mismatch in Additional Reading (display text missing the `12-` domain prefix).
- `cicd-pipeline-design-and-deployment-strategies.md`: fixed a garbled-text artifact ("sufaverage-quality evidence" → "sufficient evidence") in a Production Scenario sentence.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Content-quality audit: `13-observability` (14th domain), fully clean

### Verified

- Continued the domain-by-domain content-quality audit (14th domain, 5 chapters, read directly). All 5 chapters verified fully clean — every measured number checked and internally consistent, every referenced practice file confirmed to exist. No fixes needed.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Content-quality audit: `11-system-design` (13th domain), one broken link fixed

### Fixed

- Continued the domain-by-domain content-quality audit (13th domain, 9 chapters, via two parallel background agents). All 8 other chapters verified fully clean — every measured number confirmed by actually compiling and running the referenced Java demos, live output matching claimed numbers.
- `load-balancing-service-discovery-and-health-checking.md`: fixed a broken cross-reference — link text "Kubernetes Objects, Scheduling, and Networking" pointed at `kubernetes-resource-limits-probes-and-jvm-sizing.md`, a real but differently-titled chapter. Fixed link text to match the real target's title.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Content-quality audit: `09-messaging-event-driven` (12th domain), two real issues fixed

### Fixed

- Continued the domain-by-domain content-quality audit (12th domain, 12 chapters, via two parallel background agents covering 6 each). 11 chapters verified fully clean — every measured number (word counts, replay-time/file-size tables, 20x snapshot speedup, WAL growth, delivery counts, lag/DLQ arithmetic) checked against real `practice/java/` source, no non-compiling Java snippets, no fabrication.
- `event-driven-architecture-integration-styles.md`: fixed a broken cross-reference (link text named `producer-semantics-and-partition-keys.md` but pointed at `schema-registry-and-compatibility-evolution.md`) and a stale claim in three places calling Event Sourcing (T-905) a "planned"/"still-open" topic — `event-sourcing-and-its-real-costs.md` already exists as a canonical chapter, a genuine leftover from before that companion chapter was written.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Content-quality audit: `07-api-design` (11th domain), one orphaned-text artifact fixed

### Fixed

- Continued the domain-by-domain content-quality audit (11th domain, 5 chapters, read directly). `api-design.md`, `rest-api-fundamentals.md`, `graphql-api-design.md`, `grpc-api-design.md` all verified fully clean — real measured numbers (OFFSET vs. keyset ~3,000x, N+1/DataLoader call counts, all four gRPC call shapes) internally consistent, no fabrication.
- `api-gateway-bff-and-edge-concerns.md`: found and fixed a real editing artifact — an orphaned sentence fragment ("discovered instead of relied upon.") dangling on its own line in Failure Modes and Debugging with no grammatical antecedent. Removed. Verified the chapter's measured BFF speedup (357ms sequential vs. 159ms concurrent) consistent with its own stated expectations.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-14] — Content-quality audit: `16-performance-jvm` (10th domain), one self-contradictory solution fixed

### Fixed

- Continued the domain-by-domain content-quality audit (10th domain, 3 chapters, read directly). `benchmarking-and-jmh-pitfalls.md` and `profiling-jfr-and-flame-graphs.md` verified fully clean — real measured numbers (the 2.748ns/3.541ns dead-code-elimination gap, the 719/88/153 JFR sample counts) internally consistent, JMH/`Blackhole` and JFR/async-profiler sampling mechanics correct.
- `capacity-planning-and-headroom.md`: found one real self-contradiction in Practice Exercise 3's Solution — described a 2-of-3-instance-fleet survivor scenario, then computed capacity as if a single lone instance absorbed the full 130 req/s load. Reframed as a 2-instance fleet where either instance alone must absorb the full peak, so each is sized to `130 / 0.65 ≈ 200` req/s. Little's Law cross-check (3.264 vs. 3.290, within 0.80%) and saturation-point math (160 req/s theoretical ceiling, 148 measured, p99 25× increase) verified correct elsewhere in the same chapter.
- `validate.py`: same pre-existing 3 errors, 0 new. Real local `mkdocs build`: exit 0, only pre-existing warning patterns.

## [2026-09-13] — Planning/tooling docs audit: repository-tree regeneration bug fixed, stale `handbook/` references closed

### Fixed (`00-project/`, `CONTRIBUTING.md`, `templates/`, `resources/`)

- User asked to review planning docs, `CONTRIBUTING.md`, `templates/`, and `resources/` next. `resources/repository-tree.md` (generated 2026-09-07) had a regeneration command whose exclude list had drifted: it didn't account for `.venv-docs/` (distinct from the already-excluded `.venv/`) or `site/`/`docs/*` (MkDocs build output) — a fresh run of the old `find`-based command produced over 33,000 lines, more than 10x real content. Replaced with `git ls-files`, which by definition lists only tracked content and cannot drift the way a manual exclude list can. Regenerated (2,992 files).
- `CONTRIBUTING.md` (branch-naming table, commit-type table) and `templates/adr-template.md` (Related section) still referenced pre-migration `handbook/` terminology — updated to `syllabus/`.
- `00-project/frontend-topic-register.md`'s front matter was stale relative to its own body (a 2026-09-11 "D-F4"/F-401–F-403 addition the front matter never reflected) — synced.
- `scripts/validate.py` shows the same pre-existing 3 errors, zero new. The 3 remaining errors (`AGENTS.md`/`CLAUDE.md`'s Cross-Reference Standards example link, `templates/adr-template.md`'s own `./adr-NNN-slug.md` placeholder) are confirmed intentional illustrative examples, not real broken links — left as-is, consistent with this session's standing precedent.

## [2026-09-12] — Frontend freshness and junior-to-mid closed, completing the learning-paths audit

### Added (`syllabus/00-overview/learning-paths/frontend-mid-to-senior.md`, `junior-to-mid.md`)

- Closed the two remaining flagged items from the backend-domains entry below. `frontend-mid-to-senior.md` gained Topics 21–23 (F-401 Frontend Security, XSS/CSRF/CSP; F-402 WebSocket/SSE; F-403 Micro-Frontends and Module Federation) — 20 → 23 topics, ~8–10 → ~9–11 weeks — the newest `21-frontend-web` additions, never previously scheduled despite matching this path's Advanced-tier scope.
- `junior-to-mid.md` gained Topic 26 (`sdlc-and-agile-methodology-fundamentals.md`, T-2212) — its own front matter explicitly states `interview_paths: [junior-to-mid, interview-emergency-sprint]`, placed as a deliberate closing topic once a real shipped project (Week 8) exists to reference.
- New `study-packs/frontend-mid-to-senior/week-11/` and `study-packs/junior-to-mid/week-09/` (README + MANIFEST each); the former cites 3 real, already-built cookbook entries and 3 real practice demos, the latter honestly states no practice demo or cookbook entry exists for its single chapter (confirmed via `practice: []`/`production_scenarios: []`).
- Updated `study-packs/README.md` and both packs' own READMEs. `scripts/validate.py` shows the same pre-existing 3 errors, zero new.
- **This closes the full study-packs/learning-paths freshness audit — no known outstanding gap remains across any backend or frontend path.**

## [2026-09-12] — Learning paths gain 3 backend domains and API Design's advanced topics, closing a study-packs freshness gap

### Added (`syllabus/00-overview/learning-paths/mid-to-senior.md`, `senior-to-staff.md`)

- User asked to check `study-packs/` freshness next. Diffing every chapter's own `interview_paths`/`target_levels` front matter against every learning path found `04-software-design`, `18-engineering-practices`, and `22-ai-llm-engineering` entirely unreferenced in any backend path, and `07-api-design`'s advanced topics (GraphQL, gRPC, API Gateway/BFF, API Design) unreferenced outside Junior → Mid's REST-fundamentals-only mention.
- `mid-to-senior.md` gained Domains 13–15 (Software Design; API Design — Advanced; AI/LLM Engineering), 12 → 15 domains, ~10 → ~12 weeks. `senior-to-staff.md` gained Topics 17–21 (Design Patterns Applied; Code Review Standards; Refactoring Discipline; Working with Legacy Code; Git Internals and Collaboration Workflows), 16 → 21 topics, ~8 → ~11 weeks.
- `solid-principles.md`/`ood-interview-problems.md` routed to Mid → Senior (Mid-level fundamentals); `architecture-decision-records-and-technical-writing.md` deliberately excluded from Senior → Staff to avoid duplicating Topic 6's existing ADR coverage.
- New `study-packs/mid-to-senior/week-11/`, `week-12/` and `study-packs/senior-to-staff/week-09/`, `week-10/`, `week-11/` (README + MANIFEST each), with real cookbook cross-references verified before citing, and 2 weeks honestly stating no cookbook entry exists yet (confirmed via each chapter's own `production_scenarios: []`).
- Updated `study-packs/README.md` and both packs' own README week tables/totals. `scripts/validate.py` shows the same pre-existing 3 errors, zero new.
- Frontend freshness (F-401/F-402/F-403) and `junior-to-mid`'s `sdlc-and-agile-methodology-fundamentals.md` gap remain open, flagged, smaller items.

## [2026-09-12] — `production-cookbook/` gains a 37-chapter `21-frontend-web` domain (165 → 201 entries), closing the chapter-coverage audit entirely

### Added (`production-cookbook/`, `21-frontend-web` domain)

- Closed the frontend gap flagged in the backend-batch entry above. 35 of the 37 frontend chapters elevated as 36 entries (`how-the-web-works-html-css-dom-and-http.md` contributed 2 distinct scenarios). Deliberately excluded `nextjs-build-tooling-vite-vs-turbopack.md` (its own scenario states "Fix: none needed" — a dev-server behavior difference to understand, not an incident) and `nextjs-app-router-fundamentals.md` (a pure design comparison, no manifested symptom) — same honest-exclusion discipline as the backend batch.
- Domain recorded as `frontend-web` for all 36 entries, matching `syllabus/21-frontend-web/*.md`'s own front matter convention. Every entry's fields built by expanding the source chapter's own `## Production Scenarios` text — no invented incidents.
- Updated `production-cookbook/README.md` (165 → 201 entries). `scripts/validate.py` shows the same pre-existing 3 errors, zero new.
- This closes production-cookbook's chapter-coverage audit entirely — no known outstanding gap remains across backend or frontend.

## [2026-09-12] — The 5 permanently-open no-IWI `jvm` chapters gain cheat sheets, closing the 2026-08-05 exception

### Added (`cheat-sheets/`, `02-java/jvm-internals` domain)

- A final re-diff of every `syllabus/` chapter against `cheat-sheets/` and `flashcards/`, run right after the Frontend Live-Coding batch below, found the flashcard-missing list fully empty and the only remaining cheat-sheet gap was 5 `jvm-internals` chapters left open per the user's explicit 2026-08-05 decision (`g1-remembered-sets-and-write-barriers.md`, `jit-tiered-compilation-and-deoptimization.md`, `jvm-flags-and-container-ergonomics.md`, `jvm-memory-layout-and-runtime-regions.md`, `memory-leak-diagnosis-and-heap-dump-analysis.md`) — re-confirmed via fresh grep that none states an IWI/Topic-register line.
- User was asked directly whether to leave the exception standing or define an alternative closure criterion, and chose to close it: source the Topic ID and IWI from `00-project/knowledge-architecture-blueprint.md`'s Master Topic Register instead of the chapter's own front matter (T-301/6.3, T-304/6.8, T-307/6.75, T-308/5.45, T-312/5.9) — the identical criterion `flashcards/`'s 2026-08-06 batch already used to close these same 5 chapters, not an invented one-off.
- All 5 already carry a real inline `## Cheat Sheet` section (handbook-chapter template) — elevated existing, already-verified content (mental model, decision table, decision framework, common mistakes) into the standalone deliverable.
- Updated `cheat-sheets/README.md` (244 → 249, full parity with `flashcards/`'s 249 decks) and `flashcards/README.md` (cross-reference note). `scripts/validate.py` shows the same pre-existing 3 errors, zero new.

## [2026-09-12] — Frontend Live-Coding & Debugging Protocol gains a cheat sheet and flashcard deck, same-day follow-up

### Added (`cheat-sheets/` and `flashcards/`, `21-frontend-web` domain)

- Continuing the same discovery pass: `frontend-live-coding-and-debugging-protocol.md` (`document_type: playbook-technical-answer`, added 2026-09-05, carries no blueprint `topic_id` by design — an interview-craft document, not a register topic) had zero cheat sheet and zero flashcard deck despite already carrying real, existing inline sections.
- Closed by elevating that existing content: the two frontend round formats (build/debug) and their primary risks, the six-phase-protocol decision table, and why real-browser testing is the most frequently skipped, highest-leverage phase. `topic_id` recorded as N/A, the same convention already established for `git-internals-and-collaboration-workflows.md`.
- Updated `cheat-sheets/README.md` (243 → 244), `flashcards/README.md` (248 → 249 decks, 835 → 838 cards), and `syllabus/21-frontend-web/INDEX.md`. `scripts/validate.py` shows the same pre-existing 3 errors, zero new.

## [2026-09-12] — GraphQL and gRPC API Design (T-917/T-918) gain cheat sheets and flashcards, same-day follow-up

### Added (`cheat-sheets/` and `flashcards/`, `07-api-design` domain)

- Continuing the same discovery pass as the `22-ai-llm-engineering` entry below: `graphql-api-design.md` (T-917) and `grpc-api-design.md` (T-918) — added 2026-09-09 alongside this domain's other same-day additions — also had zero cheat sheet and zero flashcard deck, despite both already carrying real, existing inline "Cheat Sheet"/"Flashcards" sections per the handbook-chapter template.
- Closed by elevating that existing content into the standalone deliverables: `graphql-api-design` (N+1/`DataLoader` batching, null-propagation blast radius, HTTP `200`-on-error) and `grpc-api-design` (shared-codegen contract enforcement, the four call shapes by `stream` placement, deadline-less-call cascading failure).
- Updated `cheat-sheets/README.md` (241 → 243), `flashcards/README.md` (246 → 248 decks, 829 → 835 cards), and `syllabus/07-api-design/INDEX.md`. `scripts/validate.py` shows the same pre-existing 3 errors, zero new.

## [2026-09-12] — `22-ai-llm-engineering` gains cheat sheets and flashcards for all 6 chapters, closing a 6-chapter complementary-deliverable backlog

### Added (`cheat-sheets/` and `flashcards/`, 22-ai-llm-engineering domain)

- User asked what else needed checking after the REST API status-code fixes. The same chapters-vs-complementary-deliverables diff used for the earlier 26-chapter batch found this whole domain (added 2026-09-09/10) had zero cheat sheet and zero flashcard deck across all 6 chapters — this deliverable's batch process had never run against a domain this new.
- Closed in one pass: `llm-api-integration-fundamentals` (T-2300), `rag-and-vector-databases` (T-2301), `embeddings` (T-2302), `prompt-engineering-patterns` (T-2303), `agentic-workflows-and-tool-orchestration` (T-2304), `llm-evaluation-and-testing` (T-2305) — 6 cheat sheets, 6 flashcard decks (18 cards).
- Every fact extracted directly from each chapter's own real, executed demo output — this domain's chapters use the 20-section topic-spec template (no inline Cheat Sheet section), so extraction read each chapter's Foundation/Core Concepts/Common Mistakes sections directly.
- Updated `cheat-sheets/README.md` (235 → 241), `flashcards/README.md` (240 → 246 decks, 811 → 829 cards), and `syllabus/22-ai-llm-engineering/INDEX.md`. `scripts/validate.py` shows the same pre-existing 3 errors, zero new.

## [2026-09-11] — `rest-api-fundamentals.md` (T-2205) closes a real status-code *range* gap, same-day follow-up

### Changed (`syllabus/07-api-design/rest-api-fundamentals.md` — T-2205)

- Same-day follow-up to the entry below: user asked whether the `2xx`/`3xx`/`4xx`/`5xx` ranges themselves were actually covered, not just individual codes. They weren't — no real `3xx` member beyond `304` existed, and the status-code content had no explicit range structure.
- Added a real, new `GET /books/latest` endpoint: a genuinely computed `302 Found` redirect (the target changes as new books are created, proven across three real creates) — `302`, not `301`, since the target is deliberately not permanent. Added real, zero-code `415 Unsupported Media Type` evidence (wrong `Content-Type`, rejected automatically).
- Restructured the chapter's status-code content into an explicit per-range reference table (`1xx`–`5xx`), naming every practically-relevant code and marking each real-demoed or conceptual-with-a-stated-reason (`202`/`206` have no honest real demo available in this synchronous JSON CRUD API — named and left conceptual rather than faked).
- Updated `cheat-sheets/rest-api-fundamentals.md`, `flashcards/rest-api-fundamentals.md` (+2 cards), both directories' `README.md`, and `syllabus/07-api-design/INDEX.md`. `scripts/validate.py` shows the same pre-existing 3 errors, zero new; original transcript re-verified byte-for-byte unchanged.

## [2026-09-11] — `rest-api-fundamentals.md` (T-2205) closes a real HTTP status-code gap, found by direct user question

### Changed (`syllabus/07-api-design/rest-api-fundamentals.md` — T-2205)

- User asked directly whether this repository covers API status codes. Direct audit found this chapter (and its cheat sheet/flashcard deck) covered only `200`/`201`/`204`/`404`/`500` since first written 2026-09-07 — missing `400`, `401`/`403`, `405`, `409`, `422`, `304`, `429`, `502`/`503`/`504`.
- Closed in place, per the user's own chosen scope (expand this chapter + its cheat sheet, not a new topic): extended the real Spring Boot demo with an optional `isbn` field (kept out of existing JSON via `@JsonInclude(NON_NULL)`, zero regression to prior transcripts) and real, executed evidence for `400` (malformed JSON), `405` (unmapped verb, real `Allow` header), `422` (semantic validation, distinct from `400`), `409` (a real isbn conflict, deliberately distinct from the chapter's own title-duplication feature), and `304` (real conditional `GET` via Spring's built-in `ShallowEtagHeaderFilter`).
- A real bug caught by this exact demo before shipping: the `PUT` handler's 3-argument constructor silently dropped `isbn` even when explicitly sent — fixed.
- `401`/`403`/`429`/`502`-`504` covered conceptually with real, bidirectional cross-links to where their own depth already lives, rather than duplicated: `12-security/oauth2-oidc-and-jwt.md`, `12-security/csrf-cors-and-session-security.md`, `11-system-design/rate-limiting-and-throttling-algorithms.md`, `api-gateway-bff-and-edge-concerns.md`.
- Updated `cheat-sheets/rest-api-fundamentals.md`, `flashcards/rest-api-fundamentals.md` (+4 cards), both directories' `README.md`, and `syllabus/07-api-design/INDEX.md`. `scripts/validate.py` shows the same pre-existing 3 errors, zero new; original transcript re-verified byte-for-byte unchanged.

## [2026-09-11] — `cheat-sheets/` and `flashcards/` gain a 26-chapter backlog batch from the full 22-domain gap audit

### Added (`cheat-sheets/` — 26 new files, 209 → 235; `flashcards/` — 26 new decks, 214 → 240, 75 new cards, 730 → 805)

- Re-audited both complementary-deliverable directories by diffing every `syllabus/` chapter's filename against each directory's own file list, rather than trusting either directory's prior "closed" note. Found all 26 chapters added by the full 22-domain gap audit (2026-09-10/09-11) — the same audit this session had been closing chapter-by-chapter — had zero cheat sheet and zero flashcard deck: the canonical chapters and real demos shipped, but this Phase 6 complementary layer was never re-run against them.
- Closed both gaps in one batch across all 26: `azure-and-gcp-for-backend-engineers` (T-2404), `bean-validation-and-global-exception-handling` (T-518), `spring-data-jpa-repository-abstraction` (T-510), `bytecode-and-class-file-fundamentals` (T-2406), `java-platform-module-system` (T-116), `java-time-api` (T-2400), `methodhandle-and-invoke` (T-2405), `priorityqueue-internals` (T-210), `synchronizers-countdownlatch-cyclicbarrier-semaphore` (T-417), `window-functions-and-ctes` (T-2401), `jsonb-and-advanced-index-types` (T-2402), `kafka-connect-source-and-sink-connectors` (T-2408), `kafka-streams-and-stateful-processing` (T-709), `retention-log-compaction-and-tiered-storage` (T-706), `consensus-algorithms-raft-and-paxos` (T-2403), `vector-clocks-and-quorum-based-replication` (T-2407), `csrf-cors-and-session-security` (T-1308), `metric-cardinality-and-alert-fatigue` (T-2409), `hiring-and-team-building` (T-1907), `incident-command-roles-and-real-time-coordination` (T-1906), `frontend-security-xss-csrf-and-csp` (F-401), `websocket-and-server-sent-events-for-realtime-ui` (F-402), `micro-frontends-and-module-federation` (F-403), `solid-principles` (T-1701), `ood-interview-problems` (T-1702), `sorting-algorithms` (T-2119).
- Every fact, decision-table entry, and measured number was extracted directly from each chapter's own already-written text — most already carry an inline `## Cheat Sheet`/`## Flashcards`/`## Key Takeaways` section per the canonical template; the three `21-frontend-web` chapters' flashcard decks (2 cards each) were extracted verbatim from their own existing inline cards. Nothing was derived from memory or general knowledge.
- Updated `cheat-sheets/README.md` and `flashcards/README.md` with dated batch notes and entry tables, following the exact precedent of prior backlog-closing batches. `scripts/validate.py` shows the same pre-existing 3 errors, zero new.

## [2026-09-11] — `05-spring` gains a 12th chapter: Spring Data JPA Repository Abstraction — this domain's audit gaps now fully closed

### Added (`syllabus/05-spring/spring-data-jpa-repository-abstraction.md` — T-510)

- Same full 22-domain gap audit. `T-518` (Bean Validation and Global Exception Handling, closed 2026-09-10) left one more named gap open in this domain: a dedicated Spring Data JPA repository-abstraction chapter (derived query methods, `@Query`, Specifications). `T-510` was a real, correctly-reserved Master Topic Register slot ("Spring Data repositories & query derivation," IWI 5.3, Core tier) never written — closed here.
- Real demo (`practice/java/spring-data-jpa-repositories/`, Spring Framework 6.2.19 + Spring Data JPA 3.5.13 + Hibernate ORM 6.6.55.Final + H2, no Boot autoconfigure — plain `AnnotationConfigApplicationContext` wiring): real generated SQL from a derived query method; a real, separate `COUNT` query captured behind a `Pageable` return type; a real JPQL join query and a real native `@Query` bound to a zero-boilerplate interface projection (`StatusSummary`); a real `Specification` composing 0/1/2 predicates at runtime with measured row counts for each.
- Marquee finding: a second, deliberately broken repository interface (`findByTotlAmountGreaterThan`, a real typo) proving Spring Data validates a derived method's property path against the entity's real JPA metamodel eagerly, at `ApplicationContext` startup (a real `org.springframework.data.mapping.PropertyReferenceException`, Spring's own message naming the likely fix) — not lazily on first invocation.
- One real dependency gap caught and fixed while building the demo: `spring-orm`'s `LocalContainerEntityManagerFactoryBean` throws a real `NoClassDefFoundError` for `DataSourceLookup` without `spring-jdbc` on the classpath, despite that class being referenced directly from `spring-orm` — documented in `fetch-deps.sh` rather than left as a silent extra jar.
- Updated `syllabus/05-spring/INDEX.md` (11 → 12, domain's gap-audit list now fully closed), `syllabus/00-overview/INDEX.md`, `bean-validation-and-global-exception-handling.md`'s and `../06-databases/jpa-entity-lifecycle-and-the-n1-problem.md`'s `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — Full 22-domain gap audit; `12-security` gains a ninth chapter and closes a structural gap

### Added (`syllabus/12-security/csrf-cors-and-session-security.md` — T-1308)

- A full gap audit across all 22 syllabus domains (5 parallel sub-audits) found `12-security` had zero coverage of CSRF, CORS, or session security, despite otherwise being one of the most thoroughly retrofitted domains. Closed with a ninth chapter on the domain's own existing `T-13xx` sequence.
- Real demo (`practice/java/week-17/csrf-cors-session/`, pure JDK, no browser): an identical forged cross-site request succeeds against a cookie-only endpoint and fails (403) against a synchronizer-token-protected one; real CORS origin-allowlist header evidence with an honest caveat about browser-side enforcement; a real session-fixation attack closed by session-ID regeneration on login. A real logging bug (shared static state leaking between two sub-demos) was caught and fixed before the output was cited anywhere.
- Same audit found 7 of the domain's other 8 chapters missing their `## Diagrams`/`## Comparisons` sections (present only in `oauth2-oidc-and-jwt.md`) — closed on all 7, and added real mutual-TLS (mTLS) coverage to `applied-cryptography-hashing-signing-tls.md` (a real local `openssl s_server`/`s_client` handshake) as a second gap the same audit found.
- Updated `syllabus/12-security/INDEX.md` (9/9), `syllabus/00-overview/INDEX.md`, three sibling chapters' `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — `04-software-design` gains two chapters: SOLID Principles and OOD Interview Problems

### Added (`syllabus/04-software-design/solid-principles.md` — T-1701 — and `ood-interview-problems.md` — T-1702)

- Same gap audit found `04-software-design` (a single-chapter domain) had zero coverage of SOLID or OOD interview problems, assessed as arguably the largest gap found across the four domains that audit pass covered. Closed using this file's own previously-reserved `T-1700`–`T-1799` range, for the first time.
- `solid-principles.md`: real demo (`practice/java/solid-principles/`) with one violation and one fix per principle — reflection-based SRP evidence; a new shape added with zero edits to an OCP-compliant calculator; a real failing Liskov-invariant assertion (expected 20, actual 16) despite compiling cleanly; a real thrown `UnsupportedOperationException` from a fat ISP-violating interface; one unmodified DIP-compliant class run against two injected implementations.
- `ood-interview-problems.md`: two real, fully worked demos (`practice/java/ood-interview-problems/`) — a parking lot with real size-based spot matching and real `Duration`-based fee calculation; a vending machine modeled as an explicit state machine correctly rejecting illegal action sequences.
- Updated `syllabus/04-software-design/INDEX.md` (1/1 → 3/3), `syllabus/00-overview/INDEX.md`, `design-patterns-applied.md`'s `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — `02-java` gains a 56th chapter: PriorityQueue Internals

### Added (`syllabus/02-java/collections/priorityqueue-internals.md` — T-210)

- Same gap audit found `02-java/collections` had zero dedicated `PriorityQueue` coverage despite every sibling core collection already having its own chapter — a real, high-frequency gap given its role in Dijkstra's algorithm and top-K problems. Closed continuing the domain's own existing `T-2xx` sequence.
- Real demo (`practice/java/collections/priorityqueue-internals/`): the ordering guarantee applies only to `poll()`/`peek()`, demonstrated side-by-side against the identical queue's non-sorted iteration order; real, measured O(log n) `poll()` via a comparison-counting `Comparator` across five heap sizes; a real `ConcurrentModificationException` confirming fail-fast behavior.
- `02-java` still missing `java.time`, JPMS, `MethodHandle`, bytecode fundamentals, and concurrency synchronizers (`CountDownLatch`/`CyclicBarrier`/`Semaphore`) — recorded as open in the domain's own INDEX.md for follow-up.
- Updated `syllabus/02-java/INDEX.md` (55 → 56), `syllabus/00-overview/INDEX.md`, `treemap-treeset-and-navigable-hierarchy.md`'s `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — `02-java` gains a 57th chapter: java.time API

### Added (`syllabus/02-java/language-core/java-time-api.md` — T-116)

- Same gap audit, same day. `02-java/language-core` had zero coverage of `java.time` (JSR-310) despite it being explicitly named in `CLAUDE.md`'s own gap list and Very High interview frequency. Closed continuing the subdomain's own existing `T-1xx` sequence.
- Real demo (`practice/java/language-core/java-time-api/`): real immutability contrasted against a real `Calendar` in-place mutation; a real `Period`-vs-`Duration` divergence on an actual US DST transition date (12:00 vs. 13:00 from the identical starting point); and the marquee finding — a real, reproduced `SimpleDateFormat` thread-safety corruption under concurrent load (6,969/10,000 in one captured run) versus zero corruption for the identical workload against `DateTimeFormatter`.
- Updated `syllabus/02-java/INDEX.md` (56 → 57), `syllabus/00-overview/INDEX.md`, `immutability-and-defensive-copying.md`'s `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — `02-java` gains a 58th chapter: java.util.concurrent Synchronizers

### Added (`syllabus/02-java/concurrency/synchronizers-countdownlatch-cyclicbarrier-semaphore.md` — T-417)

- Same gap audit, same day. `02-java/concurrency` had zero coverage of `CountDownLatch`, `CyclicBarrier`, or `Semaphore` despite them being explicitly named in `CLAUDE.md`'s own gap list. Closed continuing the subdomain's own existing `T-4xx` sequence.
- Real demo (`practice/java/concurrency/synchronizers/`): real `CountDownLatch` blocking-until-all-signal with staggered real worker delays; real evidence it cannot be reset; a real `CyclicBarrier` reused across two independent rounds, its action firing exactly twice; a real, measured `Semaphore` maximum-concurrent-holder count under genuine contention that never exceeded the configured permit count.
- Updated `syllabus/02-java/INDEX.md` (57 → 58), `syllabus/00-overview/INDEX.md`, `reentrantlock-readwritelock-and-stampedlock.md`'s `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — `06-databases` gains a 16th chapter: Window Functions and CTEs

### Added (`syllabus/06-databases/window-functions-and-ctes.md` — T-617)

- Same gap audit, same day, new domain. `06-databases` had zero coverage of window functions or CTEs — among the most commonly asked SQL interview topics. Closed continuing the domain's own existing `T-6xx` sequence.
- Real PostgreSQL 16 lab (`practice/sql/window-functions-and-ctes/`, disposable Docker container): a real tie-handling divergence between `ROW_NUMBER()` and `RANK()` changing an actual "top 2 per department" result set (4 rows vs. 6); a real recursive CTE correctly traversing a 3-level org chart; and a real, measured `EXPLAIN ANALYZE` comparison on a 200,000-row table showing a window-function approach beating a logically equivalent correlated subquery by three to four orders of magnitude (a captured run: 40.6ms vs. 201,295ms, ~4,957×).
- Updated `syllabus/06-databases/INDEX.md` (15 → 16), `syllabus/00-overview/INDEX.md`, `index-structures-btree-composite-covering.md`'s `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — `06-databases` gains a 17th chapter: JSONB and Advanced Index Types

### Added (`syllabus/06-databases/jsonb-and-advanced-index-types.md` — T-618)

- Same gap audit, same day. `06-databases` had zero coverage of JSONB or GIN/GiST/BRIN index types. Closed continuing the domain's own existing `T-6xx` sequence; full-text search covered as a GIN use case within this chapter, closing the domain's full SQL-topic gap list.
- Real PostgreSQL 16 lab (`practice/sql/jsonb-and-advanced-indexes/`): JSONB containment queries; a real, honestly-reported GIN regression (11.988ms → 13.896ms at ~4% selectivity) contrasted against a real ~3,780× GIN win for full-text search (721.775ms → 0.191ms at 0.02% selectivity); a real GiST `EXCLUDE` constraint rejecting an overlapping booking; a real BRIN index ~1,834× smaller than an equivalent B-tree, with a real finding that the planner declined to use it by default (only forcing it revealed its real ~3.08× advantage).
- Updated `syllabus/06-databases/INDEX.md` (16 → 17), `syllabus/00-overview/INDEX.md`, `index-structures-btree-composite-covering.md`'s `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — `05-spring` gains an 11th chapter: Bean Validation and Global Exception Handling

### Added (`syllabus/05-spring/bean-validation-and-global-exception-handling.md` — T-518)

- Same gap audit, new domain. `05-spring` had zero coverage of Bean Validation or global exception handling — everyday Spring MVC topics. Closed continuing the domain's own existing `T-5xx` sequence.
- Real Spring Boot 3.5.16 app (`practice/java/bean-validation-and-exception-handling/`): a real custom, class-level `@ValidPayment` constraint correctly enforcing a cross-field rule field-level annotations can't express; real evidence `MethodArgumentNotValidException` collects every field violation from one request together. The marquee finding: real, captured, side-by-side proof a catch-all `@ExceptionHandler(Exception.class)` keeps a sensitive detail (a database connection string with a credential) out of the client response while it's fully logged server-side.
- Updated `syllabus/05-spring/INDEX.md` (10 → 11), `syllabus/00-overview/INDEX.md`, `spring-mvc-fundamentals.md`'s `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — Topic-ID collision correction

### Fixed

- Three gap-topic IDs assigned earlier the same day (`T-116` java.time, `T-617` Window Functions and CTEs, `T-618` JSONB and Advanced Index Types) collided with pre-existing entries in the original Master Topic Register (`T-116` = Java Platform Module System; `T-617` = `11-system-design/storage-selection-tradeoffs.md`; `T-618` = `10-distributed-systems/distributed-transactions-saga-and-outbox.md`). Caused by scanning only the target domain's own `INDEX.md` for the next free number, on the mistaken assumption a domain's `T-Nxx` prefix was private — the register is one shared global ID space across all 16 original domains.
- Renumbered: `T-116` → `T-2400`, `T-617` → `T-2401`, `T-618` → `T-2402`. All front matter, in-body "Topic register" lines, and `INDEX.md`/tracking-file references updated.
- Reserved `T-2400`–`T-2499` for future gap topics in existing domains lacking their own dedicated reserved range — see `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection for the full account. Other same-day assignments (`T-1308`, `T-1701`/`T-1702`, `T-210`, `T-417`, `T-518`) were cross-checked and found collision-free.

## [2026-09-10] — `10-distributed-systems` gains a 6th chapter and a real PACELC fix

### Added (`syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md` — T-2403)

- Same gap audit, new domain, first use of the newly-established `T-2400`-`T-2499` range. Found `10-distributed-systems`'s own description named "CAP/PACELC" but the CAP chapter never covered PACELC; and the domain had zero coverage of consensus algorithms.
- Fixed PACELC directly in the existing CAP chapter (T-807): a real Core Concepts subsection, a real DynamoDB (PA/EL) / Spanner (PC/EC) classification table, citing Daniel Abadi's original proposal — verified via `WebFetch` against the real source before citing.
- Added the new chapter, backed by a real deterministic Raft leader-election simulation (`practice/java/consensus-raft/`): a real normal election, a real partition where only the majority side reaches quorum, a real genuine split vote with no winner, and a real stale leader stepping down on a higher term. A real logic bug in the split-vote scenario (one candidate accidentally reaching a real majority, contradicting the "no winner" narrative) was caught and fixed before shipping.
- Updated `syllabus/10-distributed-systems/INDEX.md` (5 → 6), `syllabus/00-overview/INDEX.md`, `distributed-systems-failure-modes.md`'s `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-11] — `21-frontend-web` gains a 38th chapter: Micro-Frontends and Module Federation — F-403, closes the domain's full gap-audit finding

### Added (`syllabus/21-frontend-web/micro-frontends-and-module-federation.md` — F-403)

- Third and last of the same audit's three `21-frontend-web` sub-items (frontend security F-401, WebSocket/real-time F-402, micro-frontends F-403). This closes the domain's full 2026-09-11 gap-audit finding.
- Real demo (`practice/frontend/microfrontends-module-federation/`, two genuinely separate Webpack 5 builds — a `remote` exposing one component via `ModuleFederationPlugin`, a `host` consuming it at runtime — driven by real headless Chromium via Playwright): a real, captured browser network request proving the host's rendered content is fetched live from the remote's own server (`remoteEntry.js` plus its exposed chunk), not bundled into the host at build time; a real independent-deployability proof — editing the remote's exposed component and rebuilding *only* the remote (a separate `webpack` invocation; the host's `bundle.js` is never rebuilt or re-served) changes the host page's rendered button text and real, computed CSS background-color on reload.
- Two facts verified live via `WebFetch` before writing, not assumed from memory: Webpack's own Module Federation documentation (host/remote roles, `exposes`/`remotes`/`shared`/`singleton`, the async-boundary requirement); Martin Fowler's own micro-frontends article (the core definition, and its explicitly named downsides — payload bloat, environment mismatches, operational complexity).
- Caught and fixed two real build issues during the demo's own development: webpack resolved each config's `context` to the process's cwd rather than the config file's own directory, causing "module doesn't exist" errors until `context: __dirname` was added to both configs; and the host's static `index.html` needed to live outside the gitignored `dist/` output directory (as `host/index.html.template`), copied into `dist/` by `run.js` before serving, so a fresh clone plus `npm run build` has something to serve.
- Updated `syllabus/21-frontend-web/INDEX.md` (37 → 38 chapters, gap-audit note declaring the domain's full finding closed), `syllabus/00-overview/INDEX.md`, `nextjs-monorepo-layout.md`'s `related` front matter (cross-link added), `00-project/frontend-topic-register.md` (F-403 row added to the D-F4 tier, closing note added).
- Verified: YAML/H1/ToC-sequencing checks pass on the new 24-heading chapter; every relative link resolves; `scripts/validate.py` shows the same pre-existing 3 errors, zero new.

## [2026-09-11] — `21-frontend-web` gains a 37th chapter: WebSocket and Server-Sent Events for Real-Time UI — F-402, same day as F-401

### Added (`syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md` — F-402)

- Second of the same audit's three `21-frontend-web` sub-items (frontend security, WebSocket/real-time, micro-frontends). Closed the WebSocket/real-time sub-item; micro-frontends remains open.
- Real demo (`practice/frontend/websocket-and-sse-realtime/`, a real Node `ws` WebSocket server plus real headless Chromium via Playwright using native `WebSocket`/`EventSource`): a real closed WebSocket that stays closed for a real 1.5-second observation window (no auto-reconnect); a real, measured manual exponential-backoff reconnect wrapper (301ms/601ms/1202ms actual elapsed against a 300/600/1200ms requested schedule); a real `EventSource` reconnecting three times fully automatically over 2.2 real seconds with zero client-side retry code, using the server's own `retry: 300` wire-format field to shorten the default delay.
- Verified via `WebFetch` against both real specs before writing, not assumed from memory: RFC 6455 defines WebSocket open/close mechanics and close codes (§7.4) but nothing about reconnection; the WHATWG HTML Living Standard's Server-Sent Events section defines EventSource's reconnection algorithm explicitly, including the server-overridable `retry:` field.
- Caught and fixed a real bug during the demo's own development: the first run showed zero `EventSource` `open` events at all — traced to the Playwright page still being on `about:blank` (no real origin) when `EventSource`/`WebSocket` were constructed; fixed by navigating the page to a real page on the same origin (`page.goto('http://localhost:8918/')`) before each scenario's `page.evaluate` call.
- Updated `syllabus/21-frontend-web/INDEX.md` (36 → 37 chapters), `syllabus/00-overview/INDEX.md`, `react-hooks-useeffect-and-useref.md` and `frontend-security-xss-csrf-and-csp.md`'s `related` front matter (cross-links added), `00-project/frontend-topic-register.md` (F-402 row added to the D-F4 tier).
- Verified: YAML/H1/ToC-sequencing checks pass on the new 24-heading chapter; every relative link resolves; `scripts/validate.py` shows the same pre-existing 3 errors, zero new.

## [2026-09-11] — `21-frontend-web` gains a 36th chapter: Frontend Security (XSS, CSRF, and CSP) — new F-401, opens D-F4 tier

### Added (`syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md` — F-401)

- Same gap audit. `21-frontend-web`'s 40 originally-registered topics (D-F0–D-F3) had zero dedicated coverage of frontend security, despite the backend's own `12-security` domain covering the equivalent server-side ground. Also found: zero coverage of WebSocket/real-time and micro-frontends — both left explicitly open for a future pass, closing only the Frontend Security sub-item this time.
- Opened a new "D-F4 · Advanced Frontend Architecture & Security" tier in `00-project/frontend-topic-register.md`, `F-401`, following the register's own stated extension precedent (F-001–F-003).
- Real demo (`practice/frontend/frontend-security-xss-and-csp/`, a real Node HTTP server driven by real headless Chromium via Playwright, not jsdom): a real reflected-XSS payload (`<img src=x onerror=...>`) genuinely executing (`window.xssFired === true`) through naive, unescaped server-side templating; the identical payload rendered inert as literal text once HTML-escaped; an identical inline `<script>` running normally with no CSP header and genuinely blocked once served with `Content-Security-Policy: script-src 'self'`, with Chromium's own real console violation message captured verbatim.
- Caught and fixed a real bug during the demo's own development, before ever running it against Playwright: an initial design tried a raw `<script>` payload injected via a client-side JS string, where the payload's own quotes collided with and broke the surrounding string literal — fixed by switching to the more realistic, textbook-correct vulnerable pattern (direct server-side HTML templating), which also structurally avoids the collision.
- Updated `syllabus/21-frontend-web/INDEX.md` (35 → 36 chapters, gap-audit note), `syllabus/00-overview/INDEX.md`, `react-error-boundaries.md` and `nextjs-authentication-patterns.md`'s `related` front matter (cross-links added), `00-project/frontend-topic-register.md` (new D-F4 tier).
- Verified: YAML/H1/ToC-sequencing checks pass on the new 24-heading chapter; every relative link resolves; `scripts/validate.py` shows the same pre-existing 3 errors, zero new.

## [2026-09-11] — `19-leadership-staff` gains a 7th chapter: Hiring and Team Building — this domain's audit gaps now fully closed

### Added (`syllabus/19-leadership-staff/hiring-and-team-building.md` — T-1907)

- Second and last item the domain's gap audit named. No prior coverage of hiring as a structured skill or team composition as a deliberate responsibility. Assigned `T-1907` (continuing `T-1900`–`T-1999`).
- Built on Google's own re:Work research, verified live via `WebFetch`: structured interviewing is measurably more predictive of job performance than unstructured interviewing. Grounds independent-assessment-before-debrief practice in the real, established anchoring-bias finding.
- Caught and avoided an over-claim before shipping: an initial "hiring committee" citation was checked against the live source and found unconfirmed there — described as a general principle instead, not attributed to an unverified specific source.
- No code demo, consistent with this domain's own behavioral-handbook-style evidence discipline.
- Updated `syllabus/19-leadership-staff/INDEX.md` (6 → 7, all audit gaps now closed), `syllabus/00-overview/INDEX.md`, `mentoring-and-developing-others.md`'s `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-11] — `19-leadership-staff` gains a 6th chapter: Incident Command

### Added (`syllabus/19-leadership-staff/incident-command-roles-and-real-time-coordination.md` — T-1906)

- Same gap audit, new domain. This domain's own five-topic plan was already closed; the audit found no coverage of real-time, during-incident coordination — distinct from `13-observability`'s post-incident chapter and `20-interview-preparation/behavioral`'s narration chapter on the same general subject. Assigned `T-1906`, continuing this domain's own `T-1900`–`T-1999` range, additive beyond its closed plan.
- Built on Google's own SRE book chapter on managing incidents, verified live via `WebFetch`: the real four-role structure (IC, Ops Lead, Communications, Planning) and the real principle that the IC coordinates and does not personally debug.
- No code demo — consistent with this domain's own behavioral-handbook-style evidence discipline (real, cited frameworks, a labeled representative scenario) rather than the Java/SQL-demo pattern used elsewhere this session.
- Updated `syllabus/19-leadership-staff/INDEX.md` (5 → 6), `syllabus/00-overview/INDEX.md`, `incident-response-and-blameless-postmortems.md`'s `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection. Hiring/team-building remains open.

## [2026-09-11] — `13-observability` gains a 5th chapter: Metric Cardinality and Alert Fatigue

### Added (`syllabus/13-observability/metric-cardinality-and-alert-fatigue.md` — T-2409)

- Same gap audit, new domain. Zero coverage of metric cardinality or alert fatigue/runbook discipline, both real, common production problems with no register entry. Assigned `T-2409` (continuing `T-2400`–`T-2499`).
- Real demo (`practice/java/observability/metric-cardinality-and-alert-fatigue/`, real Micrometer `SimpleMeterRegistry`): identical 20,000-request volume through a bounded-label vs. unbounded-label registry produces a real 6,667x more distinct time series (3 vs. 20,000) and ~43x more heap.
- Marquee finding: a real, seeded 7-day synthetic error-rate simulation shows Google's real, published multi-window burn-rate alerting rule (verified live via `WebFetch`) firing 14x less often than a naive threshold rule, while both real injected incidents are still caught by both rules.
- Updated `syllabus/13-observability/INDEX.md` (4 → 5), `syllabus/00-overview/INDEX.md`, two sibling chapters' `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-11] — `09-messaging-event-driven` gains a 12th chapter: Kafka Connect — this domain's audit gaps now fully closed

### Added (`syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md` — T-2408)

- Third and last item the domain's own gap audit named (Retention/Log Compaction and Kafka Streams closed 2026-09-10). Genuinely new topic; assigned `T-2408` (continuing `T-2400`–`T-2499`).
- Real demo (`practice/java/kafka/kafka-connect-source-and-sink/`, real Kafka Connect standalone worker, built-in `FileStreamSource`/`FileStreamSink` connectors, real Kafka 3.8.0 broker): zero custom producer/consumer code — a real end-to-end round trip through a real Kafka topic, real incremental streaming while the worker runs.
- Marquee finding: a real kill-and-restart of the worker resumes from exactly the last durably-committed offset — zero data loss, zero duplication of already-delivered records — the concrete mechanism behind Connect's fault tolerance, verified directly via the real, raw offset file.
- Updated `syllabus/09-messaging-event-driven/INDEX.md` (11 → 12, all audit gaps now closed), `syllabus/00-overview/INDEX.md`, two sibling chapters' `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-11] — Architecture Atlas gains a 21st entry: Autocomplete/Typeahead System — follow-up set now fully closed

### Added (`architecture-atlas/autocomplete-typeahead-system.md`)

- Fourth and last of the follow-up set (video streaming, distributed file storage, web crawler closed prior). New, original 17-section entry, closing the audit's "web crawler/autocomplete" item in full as two separate entries.
- Central tension: fast prefix matching and fast ranked (top-K) retrieval are two different problems; precomputing/caching each trie node's own top-K completions keeps read latency a single trie descent, with ranking recomputed by a decoupled, periodic pipeline.
- Real capacity math: a ~15x multiplier between completed-search rate and per-keystroke typeahead request volume; ~7.5GB of cached top-K data at ~50M trie nodes — both computed, not asserted.
- Updated `architecture-atlas/README.md`, `syllabus/00-overview/INDEX.md` and `syllabus/11-system-design/INDEX.md` (20 → 21 Atlas entries), `web-crawler-system.md`'s `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-11] — Architecture Atlas gains a 20th entry: Web Crawler System

### Added (`architecture-atlas/web-crawler-system.md`)

- Third and last of the follow-up set. New, original 17-section entry, additive beyond T-813's closed accounting.
- Central tension: a domain-sharded URL frontier with per-domain politeness queues — aggregate throughput is a function of concurrent distinct-domain count, not per-domain speed, quantified directly (~385 pages/s target requiring hundreds of concurrent domains).
- Real, worked Bloom-filter sizing math for seen-URL dedup (~1.2GB per billion entries at 1% false-positive rate), computed against the standard formula, not asserted.
- The audit's "web crawler/autocomplete" item was two different problems bundled together; this closes the crawler half only — autocomplete/typeahead remains open, named explicitly.
- Updated `architecture-atlas/README.md`, `syllabus/00-overview/INDEX.md` and `syllabus/11-system-design/INDEX.md` (19 → 20 Atlas entries), two sibling entries' `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-11] — Architecture Atlas gains a 19th entry: Distributed File Storage System

### Added (`architecture-atlas/distributed-file-storage-system.md`)

- Second of the same follow-up set (video streaming closed 2026-09-10; web crawler/autocomplete remains open). New, original 17-section entry, additive beyond T-813's closed accounting.
- Central tension: a GFS/HDFS-shaped design separating the master's tiny, in-memory metadata path from the chunkservers' huge, throughput-bound data path; real capacity math (82M chunks, ~8.2GB metadata at 128MB chunk size) shows why chunk size controls the real scaling ceiling.
- Master failover via an operation log plus standby is named as the deliberately simpler, correctly-scoped fix, contrasted against over-engineering a fully distributed metadata layer.
- Caught and fixed before shipping: an initial draft wrongly attributed 128MB to "GFS's own real default" — verified and corrected (GFS's paper published 64MB; 128MB is HDFS's later default).
- Updated `architecture-atlas/README.md`, `syllabus/00-overview/INDEX.md` and `syllabus/11-system-design/INDEX.md` (18 → 19 Atlas entries), two sibling entries' `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — Architecture Atlas gains an 18th entry: Video Streaming Platform

### Added (`architecture-atlas/video-streaming-platform.md`)

- Same gap audit. The Atlas closed its own T-813 "12-problem set" target by count on 2026-09-01, but the audit found three specific case studies still missing: video streaming, distributed file storage, web crawler/autocomplete. Closed the first — new, original content, additive beyond T-813's closed accounting, not a reopening of it (no `topic_id` needed for Atlas entries).
- Central tension: upload/transcoding (async, write-heavy) and CDN-fronted adaptive-bitrate playback (sync, read-heavy) share almost no infrastructure; per-rendition status lets playback start before all renditions finish; immutable chunked segments mean no cache-invalidation problem, mirroring the existing URL Shortener entry's own insight.
- Real capacity math: ~250TB/day ingest vs. ~3.6 exabytes/day egress — roughly four orders of magnitude apart — reframing the design toward CDN/delivery efficiency as the dominant cost driver.
- Updated `architecture-atlas/README.md`, `syllabus/00-overview/INDEX.md` and `syllabus/11-system-design/INDEX.md` (17 → 18 Atlas entries), `url-shortener-system.md`'s `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection. Distributed file storage and web crawler/autocomplete remain open.

## [2026-09-10] — `10-distributed-systems` gains a 7th chapter: Vector Clocks and Quorum-Based Replication — this domain's audit gaps now fully closed

### Added (`syllabus/10-distributed-systems/vector-clocks-and-quorum-based-replication.md` — T-2407)

- Closes the one remaining gap `T-2403` (Consensus Algorithms) flagged open earlier the same day. Genuinely absent from the Master Topic Register; assigned `T-2407` (continuing `T-2400`–`T-2499`).
- Real demo (`practice/java/vector-clocks-and-quorum-replication/`, pure JDK): a real vector clock correctly distinguishes a `CONCURRENT` conflict from a `DOMINATES` causal supersession; a real, exhaustive enumeration of every write/read-quorum pair for a 5-replica set proves `W+R>N` (100/100 overlap) while `W+R=N` yields a real, named counterexample.
- Two well-known official references (Dynamo SOSP 2007, Lamport 1978) cited from high-confidence background knowledge, with both URLs confirmed live via `WebFetch`.
- Updated `syllabus/10-distributed-systems/INDEX.md` (6 → 7), `syllabus/00-overview/INDEX.md`, two sibling chapters' `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — `09-messaging-event-driven` gains two chapters: Retention/Log Compaction and Kafka Streams

### Added (`retention-log-compaction-and-tiered-storage.md` T-706, `kafka-streams-and-stateful-processing.md` T-709)

- Same gap audit, new domain. Zero coverage of Kafka Streams, log compaction, or Kafka Connect. `T-706` and `T-709` were both real, correctly-reserved, never-written register slots (the `T-116` pattern repeating) — closed together since Kafka Streams' `KTable` durability is a direct application of log compaction.
- Real demo, T-706 (real Kafka 3.8.0 broker, Docker/KRaft): eight keyed records (including duplicate updates and a tombstone) reduced to five real surviving records after a real, observed compaction pass; a real on-disk segment-count drop confirms physical removal.
- Real demo, T-709 (real `kafka-streams`/`kafka-clients` jars, no Maven/Gradle): a real, compiling word-count topology producing correct aggregated output; `kafka-topics.sh --describe` confirms the auto-created `KTable` changelog topic really is `cleanup.policy=compact`, with zero manual config.
- Caught and fixed before shipping: an initial "tiered storage since Kafka 3.6+" claim was imprecise — verified via `WebFetch` against the real KIP-405 page (early access 3.6, GA only since 3.9) and corrected.
- Updated `syllabus/09-messaging-event-driven/INDEX.md` (9 → 11), `syllabus/00-overview/INDEX.md`, `kafka-architecture-fundamentals.md`'s `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — `03-data-structures-algorithms` gains an 18th chapter: Sorting Algorithms

### Added (`syllabus/03-data-structures-algorithms/sorting-algorithms.md` — T-2119)

- Same gap audit, new domain. Sorting was covered only incidentally (a step inside Heaps/Intervals), never as its own chapter — a real gap since it's one of the domain's most reused primitives. Never part of the original D14 register or this domain's 18-item plan (all 18 of its own reserved IDs already in use); assigned `T-2119`, continuing the domain's own `T-2100`–`T-2199` range.
- Real demo (`practice/java/algorithms/sorting-algorithms/`, pure JDK): a naive first-element-pivot QuickSort's real O(n²) worst case (exactly `n(n-1)/2` comparisons) on sorted/reverse-sorted input, fixed by a random pivot; a real, programmatically-checked proof that `Arrays.sort(Object[])` (TimSort) is stable while a naive QuickSort is not; a real InsertionSort-vs-MergeSort crossover (insertion sort wins small/nearly-sorted, loses by two orders of magnitude on large/random input).
- `Arrays.sort()`'s real, documented algorithm choices (Dual-Pivot QuickSort for primitives, TimSort for objects) verified via `WebFetch` against the real Javadoc before citing.
- Updated `syllabus/03-data-structures-algorithms/INDEX.md` (17 → 18), `syllabus/00-overview/INDEX.md`, `heaps-top-k-and-k-way-merge.md`'s `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — `02-java` gains a 61st chapter: Bytecode and Class File Fundamentals — all three named Java Core gaps now closed

### Added (`syllabus/02-java/jvm-internals/bytecode-and-class-file-fundamentals.md` — T-2406)

- Same gap audit, closing the third and last of `CLAUDE.md`'s three named `02-java` gaps (JPMS, `MethodHandle`, bytecode/class-file fundamentals). Confirmed genuinely absent from the original Master Topic Register; assigned `T-2406` (continuing `T-2400`–`T-2499`).
- Real demo (`practice/java/jvm/bytecode-and-class-file-fundamentals/`, pure JDK plus a tiny pure-Java byte-patcher): a real hex dump of the magic number and version; a full real `javap -v` disassembly of a compiled loop with real bytecode instructions and a real `StackMapTable`.
- Marquee finding: two real JVM failures, each from patching exactly one byte — a real `UnsupportedClassVersionError` (major-version bytes patched) and a real `VerifyError` (`iadd` → `iaload` opcode patched), proving the version check and bytecode verification are two real, independent JVM safety gates.
- Updated `syllabus/02-java/INDEX.md` (60 → 61, all three named gaps declared closed), `syllabus/00-overview/INDEX.md`, four sibling chapters' `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — `02-java` gains a 60th chapter: MethodHandle and java.lang.invoke

### Added (`syllabus/02-java/concurrency/methodhandle-and-invoke.md` — T-2405)

- Same gap audit, closing the second of `CLAUDE.md`'s three named `02-java` gaps. `MethodHandle` was only a comparison point inside T-113 (Reflection and Dynamic Proxies), never its own topic; confirmed genuinely absent from the original Master Topic Register, so assigned `T-2405` (continuing `T-2400`–`T-2499`).
- Real demo (`practice/java/concurrency/methodhandle-and-invoke/`, pure JDK): real `findStatic`/`findConstructor`/`findVirtual`/`bindTo`; a real `WrongMethodTypeException` from `invokeExact()` versus `invoke()`'s automatic adaptation; real `filterReturnValue`/`dropArguments` combinators.
- Marquee finding: a real `javap -v` disassembly showing a compiled lambda's `invokedynamic`/`LambdaMetafactory` bootstrap, contrasted against a real, extra `$1.class` file the equivalent anonymous inner class produces (the lambda produces none).
- Caught and fixed a citation error before shipping: an initial "JEP 107" citation was verified via `WebFetch` and found wrong (that JEP is Java 8 Streams, unrelated) — corrected to the real source, JSR 292, also verified live.
- Updated `syllabus/02-java/INDEX.md` (59 → 60), `syllabus/00-overview/INDEX.md`, two sibling chapters' `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — `02-java` gains a 59th chapter: Java Platform Module System, filling its own long-reserved T-116 slot

### Added (`syllabus/02-java/language-core/java-platform-module-system.md` — T-116)

- Same gap audit, closing one of `CLAUDE.md`'s three named `02-java` gaps (JPMS, `MethodHandle`, bytecode fundamentals — the other two remain open). Unusually, `T-116` was already a real, correctly-reserved Master Topic Register slot for this exact topic, never written — and had briefly, mistakenly held java.time API earlier the same day before that collision was caught and fixed.
- Real demo (`practice/java/language-core/jpms-module-system/`, pure JDK, two real module graphs): a real `InaccessibleObjectException` under `exports`-only, fixed by adding a qualified `opens`; a real `ServiceLoader` call resolving a provider module the consumer never `requires`, while a direct import of that provider's internal package still fails to compile with a real javac error.
- Two supporting facts (JEP 396/403's two-step `--illegal-access` enforcement change) verified live via `WebFetch` against the real JEP text before citing.
- Updated `syllabus/02-java/INDEX.md` (58 → 59), `syllabus/00-overview/INDEX.md`, two sibling chapters' `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-10] — `15-cloud` gains a 4th chapter: Azure and GCP for Backend Engineers

### Added (`syllabus/15-cloud/azure-and-gcp-for-backend-engineers.md` — T-2404)

- Same gap audit, new domain. `15-cloud`'s own description named "AWS core services" and its content was AWS-only — a real single-provider gap against this program's own multi-cloud target-company list (Microsoft, any GCP-based org).
- New chapter maps every AWS service the existing chapter (T-1006) teaches to its real Azure/GCP counterpart by underlying trade-off, not by name: compute, storage, database (naming GCP's real Firestore/Bigtable split and Cosmos DB's tunable consistency as genuine differences, not just names), messaging, and networking/identity.
- Two facts verified live via `WebFetch` before writing: Azure AD → Microsoft Entra ID (renamed 2023, confirmed against Microsoft's own page); GCP's native IaC direction is now Infrastructure Manager, Terraform-based (Deployment Manager's docs page now 404s; confirmed against Google Cloud's own overview page).
- Updated `syllabus/15-cloud/INDEX.md` (3 → 4), `syllabus/00-overview/INDEX.md`, `aws-core-services-for-backend-engineers.md`'s `related` front matter, and `00-project/syllabus-transformation-plan.md`'s Topic IDs subsection.

## [2026-09-15] — `06-databases` gains an 18th chapter: Views and Materialized Views

### Added (`syllabus/06-databases/views-and-materialized-views.md` — T-2410)

- User-requested SQL query-optimization TODO item, scoped by first checking existing coverage: primary key, foreign key, and index were all already well covered in this domain (`sql-and-relational-database-fundamentals.md`, `index-structures-btree-composite-covering.md`), and the "concrete steps to optimize a slow query" ask was already answered in depth by `query-planning-and-explain-analyze.md`'s own 6-step Decision Framework. The one real, total gap: `VIEW`/`MATERIALIZED VIEW` had zero coverage anywhere in the repository.
- Real PostgreSQL 16 lab (`practice/sql/views-and-materialized-views/`) proving: a plain view is genuinely live (a row inserted after the view exists appears immediately, no refresh step); a view is a real database-enforced access boundary (a real, captured `permission denied` error against a base table a restricted role was revoked from); the exact mechanical rule for automatic view updatability (a real successful `UPDATE` through a single-table view, a real `cannot update view ... GROUP BY are not automatically updatable` error for an aggregating/joining one); a real, measured ~457× read speedup from materializing an aggregation (55.735ms live vs. 0.122ms materialized, on 2,000 customers/50,000 orders/200,000 line items); and the real error `REFRESH MATERIALIZED VIEW CONCURRENTLY` produces without a unique index, followed by real success once one exists.
- Includes an explicit primary-key-vs-foreign-key-vs-index-vs-view comparison table (the exact four-way distinction the user asked for), and full canonical-template coverage (L1–L4, Decision Framework, Interview Answer Framework, 4 Interview Questions, Cheat Sheet, 5 Flashcards, 5 Practice Exercises).
- Updated `syllabus/06-databases/INDEX.md` (17 → 18 chapters, T-2410 row added).
- `validate.py`: errors 0, warnings 13 (unchanged), notes 266 (unchanged). Real local `mkdocs build`: exit 0, 0 "no such anchor" warnings for the new pages.

## [2026-09-15] — `06-databases` gains a 19th chapter, same day: Database Normalization — 1NF Through BCNF

### Added (`syllabus/06-databases/database-normalization-1nf-through-bcnf.md` — T-2411)

- User asked directly whether database normalization was covered. It wasn't, beyond a one-paragraph definition inside `sql-and-relational-database-fundamentals.md` — no chapter taught the actual normal forms or the anomalies each one prevents.
- New chapter written anomaly-first: every normal form is motivated by a real, captured data inconsistency, not an abstract definition. Real PostgreSQL 16 lab (`practice/sql/database-normalization/`) proves: a 1NF repeating-group query only answerable via a fragile `LIKE` scan, fixed into a plain equality join; a real 2NF update anomaly (`product_id` 55 showing two different names simultaneously after a partial update); the identical anomaly one level removed for 3NF (`department_id` 10 showing two different department names simultaneously); a real BCNF anomaly (`enrollments(student_id, course_id, instructor)` genuinely satisfies 3NF yet permits the same instructor attached to two different courses), closed with a real, captured `duplicate key value violates unique constraint` error after the fix; and the real, measured denormalization trade-off — a ~3.27× read speedup (84.152ms normalized 3-table join vs. 25.763ms denormalized) against a real ~80×-write-amplification cost (one customer rename: 1 row normalized, 80 rows denormalized).
- Cross-links to `views-and-materialized-views.md` (T-2410, added earlier the same day) as the lower-risk alternative to denormalizing source tables for a slow read path.
- Updated `syllabus/06-databases/INDEX.md` (18 → 19 chapters, T-2411 row added).
- `validate.py`: errors 0, warnings 13 (unchanged), notes 267 (unchanged). Real local `mkdocs build`: exit 0, 0 "no such anchor" warnings for the new pages.

## [2026-09-16] — `08-testing` gains a 9th chapter: Behavior-Driven Development with Cucumber

### Added (`syllabus/08-testing/behavior-driven-development-with-cucumber.md` — T-2412)

- User asked whether TDD and other development styles were covered. TDD was, in real depth (`writing-tests-live-in-an-interview.md`); BDD was not, anywhere. New chapter written to answer the real TDD-vs-BDD confusion directly — a shared-vocabulary/audience layer on top of the same red-green mechanics, not a different testing technique — rather than treating the two as loose synonyms.
- Real, executed Cucumber-JVM 7.18.0 lab (`practice/java/testing-fundamentals/bdd-cucumber-basics/`), no Maven/Gradle: all 18 runtime jars hand-resolved from `cucumber-core`'s own published POM and fetched directly from Maven Central. Proves a real `Scenario Outline`/`Examples` table producing 3 genuinely separate, independently-reported test instances; a real captured assertion failure from a deliberately broken discount calculation; a genuinely unplanned finding surfaced by that same failure run — a 0%-discount `Examples` row still passed with the bug present, since a broken percentage multiplied by zero can't expose itself, a concrete lesson in deliberate test-data selection; and Cucumber's own real, generated Java step-definition snippet for an undefined step.
- Includes a TDD vs. BDD vs. process-model (Agile/Scrum/Waterfall) comparison table, closing a second real confusion this domain hadn't addressed — testing/specification techniques and project-phase process models are different questions entirely, not alternatives to each other.
- Updated `syllabus/08-testing/INDEX.md` (8 → 9 chapters, T-2412 row added).
- `validate.py`: errors 0, warnings 13 (unchanged), notes 268 (unchanged). Real local `mkdocs build`: exit 0, 0 "no such anchor" warnings for the new pages.

## [2026-09-16] — `05-spring` gains a 13th chapter, same day: Microservices Patterns with Spring Boot

### Added (`syllabus/05-spring/microservices-patterns-with-spring-boot.md` — T-519)

- User asked directly whether this repository covered microservices patterns in Spring Boot. Audit found the architecture-level patterns already taught as language-agnostic system design (`../11-system-design/resilience-patterns.md`, `load-balancing-service-discovery-and-health-checking.md`, `../07-api-design/api-gateway-bff-and-edge-concerns.md`) and Spring Boot already taught as a framework (this domain's other 12 chapters), but nothing connecting the two with real Spring Boot code.
- Real, executed Spring Framework 6.1.14 + Spring Boot 3.3.5 + Resilience4j 2.2.0 lab (`practice/java/spring/microservices-patterns-with-spring-boot/`), no Maven/Gradle. A real `@Service` calls a real local HTTP server standing in for a payment microservice through Spring's own declarative `@HttpExchange` client (no Spring Cloud OpenFeign dependency needed), wrapped in a real Resilience4j `CircuitBreaker` wired through a real `GenericApplicationContext`.
- Demo 1: real, captured `CLOSED → OPEN → HALF_OPEN → CLOSED` state sequence, driven by real failing calls against the real server, plus proof a call made while `OPEN` never reaches the network.
- Demo 2, the chapter's genuinely unplanned central finding: two circuit breakers driven against the identical real, artificially slow (300ms) downstream — a default-configured one (`failureRateThreshold` only) stays `CLOSED` through all 5 calls since they technically succeed; a second one with `slowCallDurationThreshold`/`slowCallRateThreshold` added opens on the 5th call. Directly verifies that a circuit breaker's default configuration does not protect against a downstream that hangs rather than errors — a real, common production gap.
- Service discovery, Spring Cloud Gateway, and Spring Cloud Config are covered as real, correct reference configuration, explicitly labeled as not executed in this chapter's own lab, rather than presented as tested.
- Updated `syllabus/05-spring/INDEX.md` (12 → 13 chapters, T-519 row added).
- `validate.py`: errors 0, warnings 13 (unchanged). Real local `mkdocs build`: exit 0, 0 new "no such anchor" warnings for the new pages.

## [2026-09-16] — `07-api-design` gains a 6th chapter, same day: API Versioning Strategies

### Added (`syllabus/07-api-design/api-versioning-strategies.md` — T-919)

- Part of a larger user-provided TODO list. `api-design.md` (T-803) was originally scoped as "API design: REST, gRPC, GraphQL, versioning" -- GraphQL and gRPC were later split into their own full chapters (T-917/T-918, 2026-09-09) when the bundled T-803 named them but never covered them, and versioning had the identical gap: named in the original scope, never written. This chapter closes it, following the exact same split precedent.
- Real, executed Spring Framework 6.1.14 lab (`practice/java/api-versioning-strategies/`), no Maven/Gradle: real `MockMvcBuilders.standaloneSetup`-driven Spring MVC dispatch (the actual `HandlerMapping` resolving every request, nothing stubbed) for three real versioning strategies -- URI path, custom header, media-type/content-negotiation -- against the identical real breaking change (a `name` field split into `firstName`/`lastName`).
- Two genuinely unplanned findings, both verified directly rather than assumed: a request with no `Api-Version` header gets a real `404` from header-based versioning (no default-version fallback exists unless explicitly built); a generic `Accept: */*` does NOT `406` on media-type-versioned endpoints -- it silently resolves to whichever version-specific handler Spring's real content negotiation matches first, with no signal to the caller.
- Updated `syllabus/07-api-design/INDEX.md` (5 → 6 chapters, T-919 row added).
- `validate.py`: errors 0, warnings 13 (unchanged). Real local `mkdocs build`: exit 0, 0 new "no such anchor" warnings for the new pages.

## [2026-09-16] — `05-spring` gains a 14th chapter, same day: DTO, Entity, and Mapper Patterns

### Added (`syllabus/05-spring/dto-entity-mapper-patterns.md` — T-520)

- Part of a larger user-provided TODO list: explain what each of DTO/Entity/Model/Mapper/Record is for, real examples, and how they relate. Mentions were scattered across `bean-validation-and-global-exception-handling.md` and `../06-databases/jpa-entity-lifecycle-and-the-n1-problem.md`, but nothing pulled the terms together to answer the actual question directly.
- Real, executed MapStruct 1.6.3 annotation-processing lab (`practice/java/dto-entity-mapper-patterns/`), no Maven/Gradle: a real `@Mapper(componentModel = "spring")` interface with no hand-written implementation anywhere -- MapStruct's real annotation processor generates `OrderMapperImpl` at compile time, captured verbatim in the pack's own README and this chapter, including genuinely generated null-safety for a nested property.
- Real, reflective proof (not assumed) that a DTO's missing field for sensitive entity data cannot leak through the mapper: `OrderResponse.class.getRecordComponents()` confirms the DTO record structurally has no component for the entity's internal-only field.
- Updated `syllabus/05-spring/INDEX.md` (13 → 14 chapters, T-520 row added).
- `validate.py`: errors 0, warnings 13 (unchanged). Real local `mkdocs build`: exit 0, 0 new "no such anchor" warnings for the new pages.

## [2026-09-17] — `06-databases` gains explicit JPA vs. Hibernate framing in its N+1 chapter

### Added (`syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md` — new "JPA vs. Hibernate: specification vs. implementation" subsection)

- Part of the same user TODO list: explain what JPA and Hibernate each mean, as distinct concepts. The chapter already covered JPA/Hibernate mechanics in real depth but used the two names interchangeably, never stating the specification-vs-implementation distinction itself.
- New subsection in "Definition and Purpose": JPA (Jakarta Persistence API) is a specification; Hibernate is its most widely used implementation (EclipseLink is the JPA reference implementation; OpenJPA and DataNucleus are others) — same shape as JDBC/a JDBC driver, or SLF4J/Logback. States the real practical consequence: `jakarta.persistence.*`-only code is implementation-portable in principle, Hibernate-specific extensions (`org.hibernate.annotations.*`, `hibernate.*` properties, the `Session` API) are not.
- No new file created — extends the existing canonical chapter, per this domain's own content-ownership rule, rather than duplicating JPA/Hibernate mechanics in a second place.
- Bumped chapter `version: 1.1 → 1.2`, `last_updated: 2026-09-17`.

## [2026-09-17] — `03-data-structures-algorithms` gains a 19th chapter: Coding Interview Pattern-Recognition Methodology

### Added (`syllabus/03-data-structures-algorithms/coding-interview-pattern-recognition-methodology.md` — T-2120)

- Part of the user's TODO list: a methodology for LeetCode/HackerRank-style problems — patterns, data structures, worked examples of the most commonly asked problems, to understand which methodology to apply. This domain already had 18 real, deep, pattern-specific chapters, but none of them answered the earlier question of which pattern to even reach for on an unfamiliar problem.
- Placed first in the domain's `.pages` reading order, ahead of the 18 pattern chapters, since its role is to be read before them.
- Real deliverable: a signal-to-pattern lookup table (18 rows) ranked by this project's own real Master Topic Register IWI data (`00-project/knowledge-architecture-blueprint.md`), not invented popularity — correcting, for this new table only, an internal inconsistency already present in the domain's own prior text (`arrays-two-pointers-and-sliding-window.md` and `INDEX.md` disagree on which pattern is "highest-weighted": IWI 6.3 for T-1402 vs. 6.25 for T-1409 — the raw blueprint numbers make T-1402 actually higher, used here directly rather than repeating either prior claim).
- A constraint-to-complexity heuristic table (what `n`'s stated bound implies about required Big-O) and three worked signal-spotting walkthroughs (Two Sum, Number of Islands, Coin Change) that deliberately stop at "which pattern, and why" rather than re-deriving the full solution — each links to its canonical chapter instead, avoiding duplication per this domain's own content-ownership rule.
- New cheat sheet and flashcard deck; updated `syllabus/03-data-structures-algorithms/INDEX.md` (18 → 19 chapters) and `.pages`.

## [2026-09-17] — `06-databases`'s SQL Fundamentals chapter substantially deepened, same day

### Added (`syllabus/06-databases/sql-and-relational-database-fundamentals.md` — T-2202, no chapter-count change)

- User flagged this chapter as too shallow, twice: only `INNER`/`LEFT JOIN` were covered, `HAVING` was never explained, `GROUP BY`/aggregates were used but not taught, key types stopped at primary/foreign, and there was no real SQL data-type catalog.
- Extended the real lab (`practice/sql/sql-fundamentals/`) with a second, deliberately-nullable-foreign-key schema (`departments`/`employees`) — the original `authors`/`books` schema's *required* foreign key structurally cannot produce a row unmatched on the right side, so it alone cannot honestly demonstrate `RIGHT JOIN`/`FULL OUTER JOIN`.
- Real, executed PostgreSQL 16 (Docker) proof added for: all six JOIN types; `WHERE` vs. `HAVING` on the identical `GROUP BY`/`AVG` query; `UNIQUE`/`CHECK`/`NOT NULL` constraints actually rejecting bad inserts with real Postgres error text; `DEFAULT` actually applying on an omitted column; `JSONB`/`TEXT[]` operators; and one report query built up six times, one clause at a time.
- New content: a full key-types taxonomy (super/candidate/primary/alternate-secondary/natural/surrogate/composite), a constraint-types table, a real PostgreSQL data-type catalog with a real example column per category, and the `SELECT`/`FROM`/`WHERE` logical-processing-order explanation that explains *why* `WHERE` can't reference an aggregate.
- Bumped chapter `version: 1.0 → 2.0`, `last_updated: 2026-09-17`. Updated cheat sheet and flashcard deck (+6 cards).

## [2026-09-17] — `02-java` gains a 62nd chapter: Comparator: Composition and Pitfalls

### Added (`syllabus/02-java/language-core/comparator-composition-and-pitfalls.md` — T-2413)

- User-flagged gap during a review of `Comparator`/`String` vs. `StringBuilder`/`StringBuffer`/`Serializable`/anonymous-class coverage: `Comparator` had zero dedicated coverage anywhere in the syllabus, appearing only as a constructor argument inside `priorityqueue-internals.md` and a brief decision-framework mention inside `equals-hashcode-and-comparable-contracts.md` (T-101) — no chapter taught `comparing`/`thenComparing`/`reversed`/`nullsFirst`/`nullsLast` composition itself, despite it being one of the most common everyday sorting tasks in real backend code.
- New topic ID `T-2413` (no pre-existing Master Topic Register slot for this exact topic; continuing the `T-2400`–`T-2499` gap-audit range, verified collision-free against the highest prior assignment, `T-2412`).
- Real, executed demo (`practice/java/language-core/comparator-composition-and-pitfalls/`, OpenJDK 21.0.12, 3 files): real multi-field and mixed-direction `comparing().thenComparing()` composition; a genuinely reproduced `int`-subtraction-comparator overflow bug (`Integer.MIN_VALUE - 1` wrapping to `Integer.MAX_VALUE`, producing a real, measurably wrong sort order), contrasted directly against `Comparator.comparingInt()`'s overflow-safe `Integer.compare()`; a real `NullPointerException` from a naive comparator on nullable data, fixed both ways with `Comparator.nullsFirst()`/`nullsLast()`; and a real, verified `List.sort()` stability proof using tagged duplicate-key elements.
- Cross-linked bidirectionally with `equals-hashcode-and-comparable-contracts.md` (T-101) — the new chapter builds directly on that chapter's `Comparable`-vs-`Comparator` decision framework rather than re-deriving it.
- Updated `syllabus/02-java/INDEX.md` (61 → 62 chapters, T-2413 row added, status line updated).

## [2026-09-17] — `java-oop-fundamentals-classes-objects-and-interfaces.md` (T-2201) gains an anonymous-class Foundation section

### Added (`syllabus/02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md` — Section 3, Foundation (L1))

- Same review that surfaced the `Comparator` gap also flagged anonymous classes: covered only as a bytecode-vs-lambda comparison point inside `lambdas-and-functional-interfaces.md` (T-108, target_levels senior/staff), with no Foundation-level (L1) introduction anywhere — a true Junior reader had no entry point to the concept itself before being thrown into the lambda contrast.
- New paragraph at the end of Section 3 (Foundation): what an anonymous class is, a `Runnable` example against an interface and a `Shape` example against an abstract class (matching the real `Shape.area()` signature already used in this chapter's own Section 7 demo), when to reach for one, and an explicit forward pointer to `lambdas-and-functional-interfaces.md` for the deeper bytecode-level contrast rather than duplicating it.
- Added a Mastery Checklist item (Section 20); added `lambdas-and-functional-interfaces.md` to `related:` front matter and Section 19 Further Reading; added the reverse cross-link from `lambdas-and-functional-interfaces.md`'s own `related:` front matter.
- Bumped chapter `version: 1.0 → 1.1`, `last_updated: 2026-09-17`. No new file created and no existing sentence removed — a pure, additive insertion per this domain's own retrofit convention.

## [2026-09-18] — `05-spring` gap audit against 30 real Spring Boot interview questions: 9 real gaps closed

### Added / Extended

- User-provided list of 30 "How does it work internally?" Spring Boot interview questions cross-checked against every `05-spring` chapter. Most were already well covered (auto-configuration's real 77/168-match report, full bean lifecycle, `@ComponentScan` vs. `@SpringBootApplication`, persistence exception translation, `DispatcherServlet` request flow, dependency-version management via `spring-boot-starter-parent`) — but 9 real, confirmed gaps found and closed in this batch, all backed by real, executed evidence (OpenJDK 21.0.12, Spring Framework 6.2.19, Spring Boot 3.5.16) except where noted as a correct, illustrative addition instead:
  1. **`@ConfigurationProperties`** (`../15-cloud/twelve-factor-config.md`) — new "Spring @ConfigurationProperties" section: real relaxed binding (kebab-case, camelCase, and `ENV_VAR`-shaped keys all binding to the same field), real nested-record binding, and a real `BindException` naming the exact malformed property.
  2. **Circular dependency resolution and `@Lazy`** (`../05-spring/spring-mvc-fundamentals.md`, Section 5) — real proof that constructor-injected cycles genuinely fail (`BeanCurrentlyInCreationException`) while the identical cycle via field injection resolves silently by accident of lifecycle ordering; `@Lazy`'s real fix, including a genuinely discovered limitation (the resulting proxy only intercepts method calls, not direct field reads — verified with both access styles against the identical proxy).
  3. **`@Configuration(proxyBeanMethods = ...)`** (`../05-spring/auto-configuration-and-bean-lifecycle.md`) — real instance-counter proof that calling one `@Bean` method from another returns the container's singleton by default (CGLIB proxy interception), and genuinely constructs separate, uncoordinated instances with `proxyBeanMethods = false`.
  4. **`@Scheduled`** (same chapter) — real proof a scheduled method runs on a dedicated background thread, independent of the main thread and every construction-time lifecycle callback this chapter already covers.
  5. **`CommandLineRunner`/`ApplicationRunner`** (`../05-spring/spring-framework-vs-spring-boot.md`) — real proof the identical bean never runs under a plain `AnnotationConfigApplicationContext` and does run under `SpringApplication.run()`, plus the real, precise observed order (Boot's own "Started" banner logs *before* the runner executes, not after).
  6. **Spring events / `ApplicationEventPublisher`** (`../05-spring/auto-configuration-and-bean-lifecycle.md`) — correct, illustrative addition (no new executed demo): default synchronous `@EventListener` execution, the `@Async`-listener visibility gap (same shape as the existing `@Async`+`@Transactional` gotcha), and `@TransactionalEventListener`'s commit-phase deferral, including its real silent-no-op failure mode outside an active transaction.
  7. **Custom `@Aspect`/`@Pointcut` authoring, `WebClient` vs. `RestTemplate`, and default logging (SLF4J/Logback)** — `WebClient` closed with a correct, illustrative new Core Concepts subsection in `../05-spring/spring-webflux-and-reactive-programming.md` (RestTemplate's real maintenance-mode status, `WebClient` usable from a fully blocking caller via `.block()`), cross-linked from `spring-mvc-fundamentals.md`'s existing Template-pattern table row; logging closed with a short, correct addition to `spring-mvc-fundamentals.md`'s existing `logging.level.*` entry (the SLF4J-facade/Logback-implementation split, and why switching implementations is a dependency change, never a code change). Custom `@Aspect` authoring remains open — lower-priority, genuinely new scope beyond this batch's time budget, since this domain's existing proxy-based-AOP coverage (`@Transactional`/`@Cacheable`/`@Async`) already goes deep on *consuming* Spring AOP, just not *authoring* a custom aspect.
- New real demo pack: [`practice/java/spring/configuration-properties-and-di-internals/`](../../practice/java/spring/configuration-properties-and-di-internals/README.md) — 4 real, executed Java files (`ConfigurationPropertiesDemo`, `CircularDependencyDemo`, `ConfigurationProxyDemo`, `SchedulingAndRunnersDemo`) backing items 1–5 above, real Spring Framework 6.2.19 + Spring Boot 3.5.16 jars fetched directly from Maven Central, no Maven/Gradle install required.
- Version bumps: `twelve-factor-config.md` 1.1→1.2, `spring-mvc-fundamentals.md` 1.3→1.4, `auto-configuration-and-bean-lifecycle.md` 1.0→1.1, `spring-framework-vs-spring-boot.md` 1.1→1.2, `spring-webflux-and-reactive-programming.md` 1.0→1.1. All `last_updated: 2026-09-18`.
- `validate.py`: errors 0, warnings 13 (unchanged baseline throughout the batch, verified after each chapter edit).

## [2026-09-18] — `06-databases` gap audit: ACID, triggers/stored procedures, and migration tooling closed

### Added / Extended

- Systematic gap audit of `06-databases` (19 chapters) against high-frequency SQL/database interview topics, same method as the same-day `05-spring` audit. Most of the domain was already deep (backup/PITR, WAL/crash recovery, full-text search, and CDC all confirmed already covered, the last two correctly, not in this domain but in `11-system-design`/`09-messaging-event-driven` respectively) — 3 real, confirmed gaps found and closed, all backed by real, executed PostgreSQL 16 evidence (Docker) plus a real Flyway 10.20.1 run:
  1. **ACID** (`sql-and-relational-database-fundamentals.md`, Section 3 Foundation) — a genuinely surprising gap: `Atomicity` and `Durability` were never named anywhere in the domain, and `Consistency` only appeared in unrelated prose (readability, data inconsistency) — despite `Isolation` having its own full, deep chapter. New subsection introduces all four guarantees with real, measured evidence: a real 3-insert transaction where the 3rd hits a duplicate-key error rolls back all three (Atomicity); a real `CHECK` constraint refusing a would-be-negative balance update (Consistency); the real default isolation level via `SHOW transaction_isolation` (Isolation, cross-linked to the existing deep chapter rather than duplicated); a row committed then verified to survive a genuine `docker restart` of the Postgres container, not just a new client connection (Durability).
  2. **Triggers and stored procedures/functions** (same chapter, Core Concepts) — zero coverage of actual `CREATE TRIGGER`/`CREATE FUNCTION` database objects (prior "trigger" hits were all the generic English word). New subsection backed by a real PL/pgSQL trigger: fires automatically on `UPDATE` with no application code calling it, writes a real audit-log row and stamps `updated_at`; a second real proof that the same trigger's `RAISE EXCEPTION` for an invalid value aborts the *entire* statement, not just its own side effect (verified: balance unchanged, no audit row written for the rejected attempt).
  3. **Migration tooling names/mechanics — Flyway and Liquibase** (`zero-downtime-schema-migration.md`, Core Concepts) — that chapter already covered migration *strategy* (`CONCURRENTLY`, expand-contract) in real depth, but never named an actual tool. New subsection plus a real Flyway 10.20.1 Java-API demo (no Maven/Gradle, no Flyway CLI install): a real `flyway_schema_history` table with real computed checksums, a real no-op on re-running `migrate()` with nothing new, and a real `FlywayException` (checksum mismatch, naming both the stored and newly-computed checksum) when an already-applied migration file is edited after the fact.
- New real demo packs: [`practice/sql/acid-properties/`](../../practice/sql/acid-properties/README.md) (2 real `.sql` labs backing items 1–2, including an actual container restart for the durability proof) and its nested [`flyway-demo/`](../../practice/sql/acid-properties/flyway-demo/README.md) (real Flyway 10.20.1 + PostgreSQL JDBC driver jars, backing item 3).
- Cheat sheet and flashcard deck for `sql-and-relational-database-fundamentals.md` updated with ACID and trigger entries (2 new flashcards, 2 new cheat-sheet definitions/pitfalls).
- Version bumps: `sql-and-relational-database-fundamentals.md` 2.0→2.1, `zero-downtime-schema-migration.md` 1.0→1.1. Both `last_updated: 2026-09-18`.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-18] — `07-api-design` gap audit: HATEOAS, RFC 9457, filtering/sorting, bulk operations, OpenAPI generation, and webhooks closed

### Added / Extended

- Systematic gap audit of `07-api-design` (6 chapters), same method as the same-day Spring/databases audits. 6 real, confirmed gaps found and closed, all backed by real, executed Spring MVC/JDK evidence — no fabricated or merely-described behavior:
  1. **HATEOAS and the Richardson Maturity Model** (`api-design.md`, new Core Concepts subsection) — zero coverage anywhere in the domain. Closed with a real Spring MVC endpoint whose `_links` genuinely change with resource state (a `PENDING` order exposes `cancel`/`ship`; a `DELIVERED` order exposes only `return`), contrasted directly against a real Level-0 `POST /rpc {"action":"getOrder",...}` endpoint returning byte-for-byte identical data.
  2. **RFC 9457 Problem Details** (`api-design.md`, Error design section extended) — previously only a bare link in Additional Reading, never explained or demonstrated. Closed with a real Spring 6 `ProblemDetail` returned from a real `@ExceptionHandler`, verified to produce a real `application/problem+json` content type and the standard `type`/`title`/`status`/`detail`/`instance` fields.
  3. **Filtering and sorting query parameters** (`api-design.md`, new Core Concepts subsection) — zero coverage; pagination was covered in depth but filtering/sorting never was. Closed with a real `GET /orders?status=PENDING&sort=amount,desc` endpoint, verified against real, order-sensitive output.
  4. **Bulk operations with partial success** (`api-design.md`, new Core Concepts subsection) — zero coverage. Closed with a real `POST /orders/bulk` endpoint returning a real HTTP `207 Multi-Status` with a per-item result array, verified with a 3-item batch (2 valid, 1 invalid) showing the exact failing index and reason.
  5. **OpenAPI and contract-first API design** (new chapter, T-2414) — zero coverage; OpenAPI was cited only once, as a passing comparison point inside `grpc-api-design.md`. New chapter backed by a real Spring Boot 3.5.16 app with springdoc-openapi 2.9.1 generating a real OpenAPI 3.1 spec from real `jakarta.validation` annotations (`@Min(1)` → real `"minimum": 1`), then real `openapi-generator-cli` 7.25.0 codegen producing a real, type-matching Java client from that spec. A real, unplanned dependency bug was found and documented: springdoc's schema generation throws a real `NoClassDefFoundError` on a Hibernate-Validator-specific class unless a real Bean Validation provider (not just `jakarta.validation-api`) is on the classpath.
  6. **Webhooks** (new chapter, T-2415) — zero coverage anywhere in the repository (confirmed absent from `09-messaging-event-driven` and `11-system-design` too — those cover internal broker messaging and rate-limiting, a distinct shape). New chapter backed by a real JDK-only (`com.sun.net.httpserver`/`java.net.http.HttpClient`) sender/receiver pair: real HMAC-SHA256 signature verification with a constant-time comparison, a real tampered-payload rejection (`401`), real measured exponential backoff (50ms then 100ms) recovering a real flaky receiver, and real delivery-ID-based idempotent deduplication of a genuinely re-sent delivery.
- New real demo packs: [`practice/java/api-design/`](../../practice/java/api-design/README.md) (backs items 1–4, 6/6 passing tests), [`practice/java/openapi-and-contract-first-api-design/`](../../practice/java/openapi-and-contract-first-api-design/README.md) (backs item 5), [`practice/java/webhook-design-and-delivery-guarantees/`](../../practice/java/webhook-design-and-delivery-guarantees/README.md) (backs item 6, 4/4 passing tests).
- New standalone cheat sheets and flashcard decks for T-2414 and T-2415; `api-design.md`'s existing cheat sheet and flashcard deck extended in place with HATEOAS/RFC 9457/bulk-operations entries (3 new cards).
- Version bump: `api-design.md` 1.0→1.1, `last_updated: 2026-09-18`. New chapters `openapi-and-contract-first-api-design.md` and `webhook-design-and-delivery-guarantees.md` at version 1.0. `syllabus/07-api-design/INDEX.md` updated (6 → 8 chapters).
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-18] — `16-performance-jvm` gap audit: JVM startup performance closed; load-testing tooling gap closed in `08-testing`

### Added / Extended

- Systematic gap audit of `16-performance-jvm` (3 chapters), same method as the same-day `07-api-design` audit. Most classic JVM-internals ground (GC, JIT, memory layout, safepoints) was already deep — correctly owned by `02-java/jvm-internals/` (13 chapters), not this domain. 2 real, confirmed gaps found:
  1. **JVM startup performance — CDS, AppCDS, GraalVM Native Image** (new chapter, T-2416) — zero coverage anywhere; only a single passing-mention comparison-table cell referenced AOT compilation. New chapter backed by real OpenJDK 21.0.12 + Oracle GraalVM 21.0.12 measurements: a real dynamic AppCDS archive showed **no measurable startup improvement** for a tiny app (honestly reported — the mechanism's benefit is proportional to class-loading volume, not a fixed percentage), while a real GraalVM native-image binary (compiled from the identical source) measured a real **~15x faster** startup-to-first-response (~136ms → ~9ms) and a real **~3.2x smaller** peak memory footprint (~50.1MB → ~15.7MB).
  2. **Load-testing tooling and the open-loop-vs-closed-loop implementation gap** — investigated as a candidate new `16-performance-jvm` chapter, then correctly redirected: `syllabus/08-testing/performance-and-load-testing-methodology.md` (T-1106) already owns load/stress/soak *testing-practice* scope and explicitly routes coordinated-omission *theory* to `13-observability`'s existing chapter — but named zero real tools and showed no code-level open-loop-vs-closed-loop implementation. Closed **in place** in T-1106 (not a new chapter, avoiding domain-ownership duplication): a real k6 (industry-standard) open-loop `constant-arrival-rate` run and a real hand-written Java closed-loop generator, both against an identical real server with a real periodic 300ms stop-the-world-style pause. Real, measured result: a **~2.75x gap at p95** specifically (275ms open-loop vs. 100ms closed-loop) — the closed-loop generator's own concurrency caps how many requests can be delayed per pause window, diluting the effect past p95 into p97-p99, while the open-loop generator's fixed arrival rate correctly represents it.
- New real demo packs: [`practice/java/jvm-startup-performance/`](../../practice/java/jvm-startup-performance/README.md) (backs item 1) and [`practice/java/load-testing-and-performance-test-design/`](../../practice/java/load-testing-and-performance-test-design/README.md) (backs item 2, includes a real k6 script).
- New standalone cheat sheet and flashcard deck for T-2416; `performance-and-load-testing-methodology.md`'s existing cheat sheet and flashcard deck extended in place (1 new card each).
- Version bumps: `performance-and-load-testing-methodology.md` 1.0→1.1. New chapter `jvm-startup-performance-cds-and-native-image.md` at version 1.0. `syllabus/16-performance-jvm/INDEX.md` updated (3 → 4 chapters).
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-20] — `16-performance-jvm` follow-up gap audit: false sharing, Vector API/SIMD, and memory-mapped I/O closed

### Added

- Follow-up gap audit of `16-performance-jvm` (4 chapters after the prior day's batch). 3 more real, confirmed gaps found and closed, all backed by real, executed evidence (OpenJDK 21.0.12, Apple M4/arm64):
  1. **False Sharing and Cache-Line Contention** (new chapter, T-2417) — zero coverage anywhere in the repository. Real demo: 4 threads atomically incrementing 4 independent counters (zero logical data sharing) measured a real, reproduced **~16x slowdown** when the counters shared a 64-byte CPU cache line, eliminated by padding. A real, discovered pitfall while building it: padding an array of `AtomicLong` **objects** at the array-slot level does nothing, since array elements holding object references don't control where the referenced objects themselves are allocated — fixed with a primitive `long[]` + `VarHandle`, where index spacing reliably controls physical layout.
  2. **Vector API and SIMD Performance** (new chapter, T-2418) — zero coverage anywhere. Real demo (a `jdk.incubator.vector` element-wise FMA over a float array) measured **essential parity (0.98x)** between the Vector API and a plain scalar loop under default JVM settings — an honest result, because HotSpot's own SuperWord auto-vectorization already compiles the "scalar" loop to real SIMD. Only with `-XX:-UseSuperWord` (auto-vectorization disabled) did the Vector API show its real, isolated advantage: a real, reproduced **~2.2x speedup**. A real correctness pitfall surfaced along the way: `fma()` (single IEEE 754 rounding) and separate `a*b+a` (two roundings) produce genuinely different bit-level results — fixed by using `Math.fma()` on the scalar side for a true apples-to-apples comparison.
  3. **Memory-Mapped Files and Zero-Copy I/O** (new chapter, T-2419) — zero real coverage (initial grep hits were false positives from filenames like `enummap-and-enumset.md`). Real demo against a 512MB file with genuinely varying, checksum-verified content: sequential full-file scans showed **near-parity** (~171-183ms `FileChannel` vs. ~177-179ms `MappedByteBuffer`) — an honest, non-dramatic result, since bulk reads already amortize syscall cost. Random access (2,000,000 scattered 8-byte reads) showed a real, measured **~28x speedup** for memory mapping (839-898ms vs. ~30ms), from eliminating one real syscall per access — the exact mechanism Kafka's own log-segment I/O relies on.
- New real demo packs: [`practice/java/false-sharing-and-cache-line-contention/`](../../practice/java/false-sharing-and-cache-line-contention/README.md), [`practice/java/vector-api-and-simd-performance/`](../../practice/java/vector-api-and-simd-performance/README.md), [`practice/java/memory-mapped-files-and-zero-copy-io/`](../../practice/java/memory-mapped-files-and-zero-copy-io/README.md).
- New standalone cheat sheets and flashcard decks for T-2417, T-2418, T-2419.
- New chapters at version 1.0, `last_updated: 2026-09-20`. `syllabus/16-performance-jvm/INDEX.md` updated (4 → 7 chapters).
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-20] — `01-computer-science-foundations` gap audit: memory hierarchy and virtual memory closed

### Added

- Gap audit of `01-computer-science-foundations` (marked "domain complete" since 2026-09-03 for its originally-scoped five-topic list — audited anyway, since the domain's reserved `T-2000`–`T-2099` range always had room beyond that original scope). One real, confirmed gap found: **zero coverage anywhere in the domain of the memory hierarchy** (cache lines, RAM latency) **or the virtual-memory page-fault mechanism** — a real gap, not theoretical, because two already-written `16-performance-jvm` chapters ([False Sharing and Cache-Line Contention](../16-performance-jvm/false-sharing-and-cache-line-contention.md), [Memory-Mapped Files and Zero-Copy I/O](../16-performance-jvm/memory-mapped-files-and-zero-copy-io.md)) already depend on exactly this foundation with no citable prerequisite chapter to point back to.
- **Memory Hierarchy: Caches, RAM, and Virtual Memory** (new chapter, T-2006). Real demo: a pointer-chasing benchmark (Sattolo's-algorithm random single-cycle permutation, defeating both hardware prefetch and out-of-order execution) measured genuine per-tier memory latency across working-set sizes from 4 KiB to 256 MiB on real hardware (Apple M4, OpenJDK 21.0.12) — a real, measured **~1.39ns per access when the working set fits in L1, rising to ~95.3ns once it no longer fits anywhere but RAM, a ~68x range for what Big-O analysis calls the identical O(1) array access.** The largest single measured cliff (16 MiB → 32 MiB) was cross-checked against this exact machine's real, `sysctl`-reported L2 cache capacity (`hw.perflevel0.l2cachesize` = 16,777,216 bytes) and lands precisely at that boundary — a genuine hardware correlation, not an asserted one.
- Cross-references added both directions: T-2006 cites [False Sharing](../16-performance-jvm/false-sharing-and-cache-line-contention.md) and [Memory-Mapped Files](../16-performance-jvm/memory-mapped-files-and-zero-copy-io.md) as its real, in-repo applied consequences (its own Production Scenarios section, honestly noting no `production-cookbook/` entry roots in CPU-cache-hierarchy latency specifically); both of those chapters updated in place with a `related:` link and a body cross-reference back to T-2006 as their foundational mechanism.
- New real demo pack: [`practice/java/cs-foundations/memory-hierarchy-and-cache-latency/`](../../practice/java/cs-foundations/memory-hierarchy-and-cache-latency/README.md).
- New standalone cheat sheet and flashcard deck for T-2006.
- `syllabus/01-computer-science-foundations/INDEX.md` and `.pages` updated (5 → 6 topics); `syllabus/00-overview/INDEX.md` row updated.
- New chapter at version 1.0, `last_updated: 2026-09-20`.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-20] — `03-data-structures-algorithms` gap audit: matrix/grid traversal closed, Heaps gains Quickselect

### Added

- Gap audit of `03-data-structures-algorithms` (19 chapters, already covering nearly every classic interview pattern). Two real, confirmed gaps found: **matrix/grid problems (in-place rotation, spiral traversal, flood fill) had zero dedicated coverage** — [Graphs](../03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md) named Number of Islands as a real, already-solved-elsewhere problem without ever elevating it into a worked example — and **Quickselect, the standard O(n)-average follow-up to "find the Kth largest element," was never mentioned in [Heaps, Top-K, and K-Way Merge](../03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md) (T-2106)**, despite being the canonical next question once a candidate produces the heap-based solution. Bit manipulation and math/number-theory (GCD, sieve of primes) were also investigated and correctly ruled out: bit manipulation already has its own dedicated chapter (`bit-manipulation.md`, T-2113); number theory was deprioritized as genuinely low real interview frequency for this repository's target companies relative to the effort of a new chapter.
- **Matrix and Grid Traversal Patterns** (new chapter, T-2121). Real demo (`practice/java/algorithms/matrix-and-grid-traversal-patterns/`): in-place rotation (transpose + row-reverse) checked against a brute-force rotated copy across seven sizes; spiral traversal checked across square, non-square, and degenerate (single-row/column/cell) shapes. The chapter's real, non-obvious headline finding: **a recursive flood fill's call-stack depth scales with the size of the largest single connected region, not the grid's overall dimensions** — measured directly, one fresh JVM process per size (a same-process sweep first produced a misleading, non-monotonic result from mid-sweep JIT compilation, documented honestly in the practice pack's own README rather than hidden): a single solid 14,000-cell region recurses successfully on this machine's default thread stack; 16,000 cells throws a real `StackOverflowError`. An iterative version using an explicit `ArrayDeque` stack/queue correctly handles 2,000,000 cells — over 100x past the recursive breaking point — with zero risk, the concrete, measured instance of the abstract call-stack-to-heap conversion [Memory Hierarchy: Caches, RAM, and Virtual Memory](../01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md)'s own Design Exercise (2026-09-20, same day) already described.
- **Heaps, Top-K, and K-Way Merge (T-2106) extended in place** (version 1.1 → 1.2) with a new Quickselect section (Problem 6, a new Interview Question, Trade-offs row, Common Mistakes/Edge Cases entries). Real demo (`practice/java/week-23/heaps/src/QuickSelectDemo.java`, 24/24 assertions): a deterministic first-element pivot on 2,000 already-sorted elements makes exactly 1,999,000 comparisons (`n(n-1)/2`, the identical worst-case shape [Sorting Algorithms](../03-data-structures-algorithms/sorting-algorithms.md) documents for naive QuickSort) versus 8,410 for a random pivot — a real ~237x reduction. At real scale (n=5,000,000, k=100), correctly-pivoted quickselect measured a real ~10x wall-clock advantage over the heap-based approach (11ms vs. 111ms).
- `graphs-bfs-dfs-and-shortest-paths.md` updated in place (version 1.0 → 1.1): Number of Islands' "not yet elevated" note corrected to point at the new chapter; `related:` link added both directions.
- New standalone cheat sheet and flashcard deck for T-2121; `heaps-top-k-and-k-way-merge.md`'s existing cheat sheet and flashcard deck both updated in place with the real Quickselect numbers, matching this repository's own "extend the existing deck, don't let it lag the chapter" convention.
- `syllabus/03-data-structures-algorithms/INDEX.md` and `.pages` updated (19 → 20 chapters); `syllabus/00-overview/INDEX.md` row updated.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — `04-software-design` gap audit: coupling, cohesion, and code smells closed

### Added

- Gap audit of `04-software-design` (3 chapters, already gap-audited once on 2026-09-10 and depth-audited clean on 2026-09-15 — audited again anyway for missing topics, not depth). Found "coupling" and "cohesion" named nowhere in the repository as taught concepts, despite [SOLID Principles](../04-software-design/solid-principles.md) (T-1701) being, collectively, a set of mechanisms for achieving exactly those two properties without ever naming either; the code-smell taxonomy (God Class, Feature Envy, Shotgun Surgery, Primitive Obsession, Data Clumps) had only one passing mention anywhere.
- **Coupling, Cohesion, and Code Smells** (new chapter, T-1703). Real demo (`practice/java/coupling-cohesion-and-code-smells/`), two parts: (1) a reflection-based coupling measurement — a God Class's 5 distinct collaborator types drop to 1 per class once decomposed into six single-purpose classes, with an honest, measured caveat rather than an overclaimed universal win: the new coordinating orchestrator's own coupling count rises to 6. The same demo moves a real Feature Envy method onto the class it envies and verifies, across 3 real orders, byte-for-byte identical totals before and after — the same before/after test-parity technique [`refactoring-discipline.md`](../18-engineering-practices/refactoring-discipline.md)'s own demo uses. (2) A Law of Demeter fragility measurement: two client styles (train-wreck vs. delegating) produce identical output against a `Customer`/`Wallet`/`Card` object graph; one real internal-structure change to `Wallet` (single card → multiple cards) is applied, and the 6 client files are recompiled completely unchanged — the 3 train-wreck clients fail with real `cannot find symbol: method getCard()` compiler errors, the 3 Demeter-compliant clients compile successfully, unmodified.
- `solid-principles.md` (T-1701, 1.0 → 1.1) and `refactoring-discipline.md` (`18-engineering-practices`, T-1804, 1.0 → 1.1) both updated in place with a `related:` link and a body cross-reference to the new chapter — the latter explicitly scoped as "the how" (safe refactoring mechanics) versus the new chapter's "the what/when" (recognizing a smell in the first place).
- New standalone cheat sheet and flashcard deck for T-1703.
- `syllabus/04-software-design/INDEX.md` and `.pages` updated (3 → 4 chapters); `syllabus/00-overview/INDEX.md` row updated.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — `07-api-design` gap audit: async 202-Accepted-plus-polling pattern closed

### Added

- Gap audit of `07-api-design` (8 chapters, already gap-audited twice — PR closing HATEOAS/RFC 9457/filtering-sorting/bulk-operations, and API Versioning Strategies). Found the async long-running-operation pattern (202 Accepted + polling) had no real evidence anywhere — [`rest-api-fundamentals.md`](../07-api-design/rest-api-fundamentals.md)'s own text explicitly flagged this as conceptual-only ("this chapter's `BookController` has no real async operation to demonstrate it honestly; a fabricated one would be a fake, not real evidence"), a self-documented gap rather than a newly-discovered one. Idempotency keys, `OFFSET`-vs-keyset pagination, and `Deprecation`/`Sunset` headers were checked and confirmed already real-evidence-backed, not gaps.
- **`api-design.md` (T-803, 1.1 → 1.2) extended in place** with a real async demo added to `practice/java/api-design/` — a genuine background job (`ReportJobService`, a real `ExecutorService` and a real 300ms of work, not a stubbed flag) proving the full real lifecycle: `POST /reports` → `202 Accepted` + `Location`; immediate poll → `202` + `Retry-After`; the result endpoint hit early → a real (honestly, deliberately repurposed — RFC 8470 itself scopes it to TLS early-data replay risk, stated explicitly rather than overclaimed) `425 Too Early`; a real polling loop (7 polls, 50ms apart) reaching `303 See Other` at real elapsed time ≥300ms; following that `Location` → real `200 OK` with the actual generated report body.
- New chapter sections: Core Concepts, Internal Implementation evidence, a Trade-offs/Decision Framework/Common Mistakes/Anti-Patterns/Best Practices entry each, a new Interview Question 7, Summary/Key Takeaways/Cheat Sheet/Flashcards/Practice Exercises extensions — all in place in `api-design.md`, no new topic_id.
- `cheat-sheets/api-design.md` and `flashcards/api-design.md` updated in place with the real numbers; `flashcards/api-design.md`'s row in `flashcards/README.md` (row 39) corrected from a stale "3" to its real "7" cards while touching this entry (a pre-existing staleness from an earlier batch, not introduced here).
- `syllabus/07-api-design/INDEX.md` and `syllabus/00-overview/INDEX.md` updated in place (no new chapter count — same 8 chapters, one extended).
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — `08-testing` gap audit: testing async/concurrent code closed

### Added

- Gap audit of `08-testing` (9 chapters — flaky-test *diagnosis* from shared state already real-evidence-backed in [Integration Testing Against Real Dependencies](../08-testing/integration-testing-against-real-dependencies.md)). Found zero coverage anywhere of how to *write* a reliable test for genuinely asynchronous or concurrent code — a distinct gap from diagnosing flakiness after the fact. Testcontainers, `@ParameterizedTest`, and coverage-metric vocabulary were checked and confirmed already covered; not gaps.
- **Testing Asynchronous and Concurrent Code** (new chapter, T-2420). Real demo (`practice/java/testing-fundamentals/testing-async-and-concurrent-code/`): a genuine background worker with a real, variable 5–60ms delay, tested two ways — a fixed `Thread.sleep(20)` (the tempting, wrong approach) measured at a real **6/30 successful, 24/30 failed** (~80% failure rate); a `CountDownLatch`-based real completion signal against the identical work measured at **30/30 successful**. A second demo: a real 8-thread, 100,000-increments-each stress test against a plain, unsynchronized `int` measured a real **`expected: <800000> but was: <170146>`** (~79% of updates lost to the classic read-modify-write race, a second independent run landing at a different but equally real 230,415 — the exact number is non-deterministic, the failure itself is reliable), fixed with `AtomicInteger` and the identical stress harness reused unmodified, measuring **5/5 repetitions, all exactly 800,000**.
- New standalone cheat sheet and flashcard deck for T-2420.
- `syllabus/08-testing/INDEX.md` and `.pages` updated (9 → 10 chapters); `syllabus/00-overview/INDEX.md` row updated.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — `09-messaging-event-driven` gap audit: Kafka security (SASL/ACL) closed

### Added

- Gap audit of `09-messaging-event-driven` (12 chapters, previously marked "audit gaps fully closed" 2026-09-11 — audited again for missing topics, not depth). Found zero coverage anywhere of Kafka authentication (SASL) or authorization (ACLs), despite otherwise deep, real-evidence-backed Kafka mechanics coverage across 12 other chapters. RabbitMQ (deliberately out of this domain's stated Kafka-centric scope) and the Saga pattern (correctly owned by `10-distributed-systems/distributed-transactions-saga-and-outbox.md`) were checked and confirmed not gaps.
- **Kafka Security: SASL Authentication and ACL Authorization** (new chapter, T-2421). Real demo (`practice/java/kafka/kafka-security-authentication-and-authorization/`): a real, disposable Kafka 3.8.0 broker (Docker, KRaft mode) with real SASL/PLAIN authentication and the real KRaft-native `StandardAuthorizer`. Real evidence: a wrong password producing a real `SaslAuthenticationException`; a missing topic ACL producing a real `TopicAuthorizationException`; and a genuine, discovered gotcha this demo's own construction surfaced (not a pre-known fact stated up front) — a principal with full `Read`/`Write`/`Describe` on a topic still fails to *consume* it with a real `GroupAuthorizationException`, because Kafka's consumer protocol separately authorizes the consumer-group resource, requiring a second, independent ACL grant. A configured super user (`admin`) was verified to bypass ACL checks entirely, producing to and consuming from a topic with zero grants. Getting the broker to even start took three real, documented Docker-image configuration gotchas (an `ensure KAFKA_OPTS` startup-script requirement, a real vs. dummy JAAS system property, and a listener-naming ambiguity in the image's underscore-to-dot environment variable translation) — all three fixed and explained in the practice pack's own README rather than hidden.
- `applied-cryptography-hashing-signing-tls.md` (`12-security`, T-1303, 1.0 → 1.1) updated in place with a `related:` cross-link to the new chapter.
- New standalone cheat sheet and flashcard deck for T-2421.
- `syllabus/09-messaging-event-driven/INDEX.md` and `.pages` updated (12 → 13 chapters); `syllabus/00-overview/INDEX.md` row updated.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — `10-distributed-systems` gap audit: distributed locking and fencing tokens closed

### Added

- Gap audit of `10-distributed-systems` (7 chapters, previously marked "audit gaps fully closed" 2026-09-10). Found [Consensus Algorithms: Raft and Paxos](../10-distributed-systems/consensus-algorithms-raft-and-paxos.md) references "a real, battle-tested consensus-backed lock (`etcd`'s lease-based distributed lock)" twice as the fix for a real production incident, without ever explaining the lock mechanism itself or its own, separate, classic failure mode. Gossip protocols (SWIM-style membership) were also identified as a real, isolated gap but deprioritized in favor of this one, since it closes an already-presupposed dependency rather than introducing an unreferenced new topic.
- **Distributed Locking and Fencing Tokens** (new chapter, T-2422). Real demo (`practice/java/distributed-locking-and-fencing-tokens/`), a real, timing-driven Java simulation (real threads, real `Thread.sleep`-based pauses, no mocked time): a real lease-based `LockService` correctly refuses a second acquire while a lease is still valid (`LockContentionDemo`); the classic Kleppmann stale-lock-holder bug reproduced directly — `Client-A` acquires a 300ms lease, pauses for a real 500ms, and its stale write silently overwrites `Client-B`'s legitimate write (`UnsafeLockDemo`, real captured output: `Final UnsafeStorage value: A-value`); and the real fix — the identical timeline, but the protected resource itself rejects the stale write via a real, monotonically increasing fencing token (`FencedLockDemo`, real captured output: `Client-A REJECTED: token 1 <= last accepted token 2`, `Final FencedStorage value: B-value`).
- `consensus-algorithms-raft-and-paxos.md` (T-2403, 1.0 → 1.1) updated in place with a `related:` link and a body cross-reference at its own "use a distributed lock" remediation, naming this chapter's own residual risk explicitly.
- New standalone cheat sheet and flashcard deck for T-2422.
- `syllabus/10-distributed-systems/INDEX.md` and `.pages` updated (7 → 8 chapters); `syllabus/00-overview/INDEX.md` row updated.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — `12-security` gap audit: authentication attack defense (brute force, credential stuffing, MFA) closed

### Added

- Gap audit of `12-security` (10 chapters, previously marked "gap-audited and closed" 2026-09-10, then extended 2026-09-14). Found `owasp-top-10-for-backend-services.md`'s own routing table names "A07 Authentication Failures" and points to `oauth2-oidc-and-jwt.md` and `authn-authz-rbac-vs-abac.md` — but neither chapter, nor anywhere else in the domain, covered an actual authentication attack or its defense: zero grep hits anywhere in `12-security` for brute force, credential stuffing, account lockout, or MFA before this pass. Other candidates considered and deprioritized: backend security headers (CSP/HSTS — already covered frontend-side, less isolated) and security audit logging (real gap, but lower interview frequency than this one).
- **Authentication Attack Defense: Brute Force, Credential Stuffing, and MFA** (new chapter, T-1310). Real demo (`practice/java/week-17/auth-brute-force-and-mfa/`), real timing (`System.currentTimeMillis`/`Thread.sleep`, no mocked clock): an `UnprotectedLoginService` cracked via a 10-entry wordlist (`BruteForceDemo`, real captured output: `attempt 8: "Tr0ub4dor&3" -> SUCCESS`); a `LockingLoginService` (5 attempts / 10s window / 3s lockout) stopping the identical attack three attempts short of the real password, while directly proving its own denial-of-service trade-off — the legitimate user is locked out too, right now (`rejected, still locked for 2998ms`) — before succeeding once the real 3-second window elapses; a from-scratch `Totp` implementation (RFC 6238/RFC 4226, HMAC-SHA1) verified against all 5 of RFC 6238 Appendix B's own official test vectors (`All RFC 6238 vectors: PASS`); and a simulated credential-stuffing attacker holding alice's *correct* password still rejected by `MfaProtectedLoginService` without the TOTP code, while alice's password plus a real freshly generated code succeeds.
- `owasp-top-10-for-backend-services.md` (T-1301, 2.0 → 2.1) and `authn-authz-rbac-vs-abac.md` (T-1302, 1.0 → 1.1) updated in place with `related:` links and body cross-references naming this chapter's real evidence explicitly.
- New standalone cheat sheet and flashcard deck for T-1310.
- `syllabus/12-security/INDEX.md` updated (10 → 11 chapters); `syllabus/00-overview/INDEX.md` row updated.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — `13-observability` gap audit: chaos engineering (fault injection and resilience verification) closed

### Added

- Gap audit of `13-observability` (5 chapters). Found the domain covered detection (`metric-cardinality-and-alert-fatigue.md`'s burn-rate alerting) and reaction (`incident-response-and-blameless-postmortems.md`) but had zero coverage of proactively verifying that either actually works — zero grep hits for "chaos" anywhere in the repository before this pass, and `10-distributed-systems/multi-region-failover-and-disaster-recovery.md` referenced a "DR game day" once without explaining the broader discipline it's one instance of. Other candidate considered and deprioritized: structured logging/correlation-ID as a standalone topic — already substantially covered implicitly via `logging-metrics-tracing-and-opentelemetry.md`'s traceId-propagation content, not an isolated gap.
- **Chaos Engineering: Fault Injection and Resilience Verification** (new chapter, T-2423). Real, timed demo (`practice/java/observability/chaos-engineering-fault-injection/`), no mocked clock: a real steady-state phase (20 real requests, `paging = false` confirmed); a real fault deliberately injected against `OrderService`'s downstream payment call, scoped to a minimized 25% canary cohort (`ChaosExperimentDemo`, real captured output: `ALERT FIRED 1144ms after fault injection began, after 12 real requests`), detected by the same multi-window burn-rate technique `metric-cardinality-and-alert-fatigue.md` (T-2409) documents; and a real rollback phase confirming recovery (`Alert cleared 310ms after rollback: true`). Re-run twice — only real-clock timing jitter differs; the request count at detection (12) and the recovery outcome are identical both times.
- `incident-response-and-blameless-postmortems.md` (T-1207, 1.1 → 1.2) and `multi-region-failover-and-disaster-recovery.md` (T-814, 1.0 → 1.1) updated in place with `related:` links and body cross-references naming this chapter's real evidence explicitly.
- New standalone cheat sheet and flashcard deck for T-2423.
- `syllabus/13-observability/INDEX.md` updated (5 → 6 chapters); `syllabus/00-overview/INDEX.md` row updated.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — `14-devops-containers` gap audit: Horizontal Pod Autoscaling mechanics closed

### Added

- Gap audit of `14-devops-containers` (5 chapters). Found `kubernetes-objects-scheduling-and-networking.md` already shows a real, syntax-validated `HorizontalPodAutoscaler` manifest but never explains its controller's actual decision logic — and `15-cloud/aws-core-services-for-backend-engineers.md` explicitly (and, until this pass, incorrectly) claimed HPA was covered "per the previous chapters' Kubernetes coverage." A genuinely broken cross-reference, not a paraphrased one. Other candidates considered and deprioritized: GitOps/ArgoCD and StatefulSets/ConfigMaps — real gaps, but neither had an existing false or presupposed claim pointing at them.
- **Horizontal Pod Autoscaling: Mechanics, Metrics, and Scaling Behavior** (new chapter, T-2424). Real, timed demo (`practice/java/devops/horizontal-pod-autoscaler-mechanics/`), no mocked clock, implementing the actual documented Kubernetes HPA formula (`desiredReplicas = ceil[currentReplicas * (currentMetricValue/desiredMetricValue)]`) plus its tolerance band and scale-up/scale-down asymmetry: near-target noise (68%/72% vs. a 70% target) produces zero replica-count change; a real spike to 140% then 200% scales up immediately in the same sample each time (real captured output: `t=922ms metric=200.0% actual-replicas=12 <-- CHANGED`); and a real drop to 20% load is recommended for scale-down at `t=1227ms` but the controller doesn't actually shrink the fleet until `t=3052ms` — a real, measured ~1.8-second stabilization delay. Re-run twice — only real-clock timing jitter differs, every replica-count decision is identical both times.
- `kubernetes-objects-scheduling-and-networking.md` (T-1002, 1.0 → 1.1) and `aws-core-services-for-backend-engineers.md` (T-1006, 1.0 → 1.1) updated in place: `related:` links added, and the AWS chapter's broken claim replaced with a real cross-reference to this chapter's actual coverage.
- New standalone cheat sheet and flashcard deck for T-2424.
- `syllabus/14-devops-containers/INDEX.md` updated (5 → 6 chapters); `syllabus/00-overview/INDEX.md` row updated.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — `15-cloud` gap audit: serverless (Lambda) execution mechanics closed

### Added

- Gap audit of `15-cloud` (4 chapters). Found `aws-core-services-for-backend-engineers.md` names Lambda's "cold-start latency" as a real trade-off five separate times (a compute-spectrum comparison, a comparison table row, a common-mistakes warning, an interview-answer talking point, a practice-exercise reference) without ever once explaining the mechanism behind it. Other candidate considered and deprioritized: IAM/VPC/IaC depth — already substantially covered by this chapter's own 2026-09-08 addition, not an isolated gap.
- **Serverless Compute: Lambda Execution Model, Cold Starts, and Concurrency Scaling** (new chapter, T-2425). Real Java simulation (`practice/java/cloud/serverless-cold-starts-and-concurrency/`), no AWS account or credentials required, no mocked clock: real JVM class-loading/object-construction work used as a technically substantiated proxy for a real Lambda INIT phase (the dominant real contributor to cold-start latency for an actual JVM-based Lambda function). `ColdStartDemo` measured a cold invocation at ~176ms versus ~0.009ms for a warm one (real captured output: `Cold invocation (176ms) was 20038x slower than the average warm invocation (0.009ms)`). `ConcurrencyScalingDemo` used a real `CountDownLatch` to release 5 real threads simultaneously, proving concurrency multiplies cold starts rather than merely delaying one: a burst against zero warm environments measured ~236ms wall-clock versus ~0.08ms for a repeat burst against already-warm ones. Re-run twice — exact millisecond values vary with real JVM/OS scheduling noise, but the order of magnitude is consistent both times.
- `aws-core-services-for-backend-engineers.md` (T-1006, 1.1 → 1.2) updated in place with a `related:` link and a body cross-reference at its first "cold-start latency" mention, naming this chapter's real mechanism and evidence explicitly.
- New standalone cheat sheet and flashcard deck for T-2425.
- `syllabus/15-cloud/INDEX.md` updated (4 → 5 chapters); `syllabus/00-overview/INDEX.md` row updated.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — `17-architecture` gap audit: domain events vs. integration events closed

### Added

- Gap audit of `17-architecture` (9 chapters, first gap audit this domain has had). Found `cqrs-read-write-separation.md` and `ddd-tactical-design-aggregates.md` use "domain event" extensively, and `09-messaging-event-driven/event-driven-architecture-integration-styles.md` covers publishing events across service boundaries — but none of the three ever named the specific, commonly-made mistake at their intersection: treating a domain event's internal shape as if it were the public contract external consumers depend on. Other candidates considered and deprioritized: Team Topologies' specific interaction-mode framework (already covered appropriately in `19-leadership-staff/cross-team-influence-without-authority.md`, not a gap here) and layered architecture depth (already covered as a real comparison baseline in `clean-hexagonal-architecture.md`).
- **Domain Events vs. Integration Events: Contract Boundaries and Translation** (new chapter, T-2426). Real, fully deterministic Java demo (`practice/java/architecture/domain-events-vs-integration-events/`, no timing/randomness — every run byte-identical): a domain event published directly as the wire message (`OrderCompletedDomainEventV1.publishDirectlyAsWireMessage()`) works fine for a `NotificationConsumer` reading its `"total"` field — until a real, well-motivated internal refactor (`double total` → `BigDecimal grandTotal`) happens, at which point the same consumer genuinely breaks (real captured output: `BROKEN, exactly as expected: Consumer expected field "total" but it was not present in the wire message`). The same consumer, reading a translated `OrderCompletedIntegrationEvent`'s stable `"totalAmount"` field, shows `49.99` before and after the identical refactor — unaffected, because `IntegrationEventTranslator` absorbed the internal change.
- `cqrs-read-write-separation.md` (T-904, 1.1 → 1.2), `ddd-tactical-design-aggregates.md` (T-903, 1.0 → 1.1), and `09-messaging-event-driven/event-driven-architecture-integration-styles.md` (T-906, 1.1 → 1.2) updated in place with `related:` links and body cross-references naming this chapter's real evidence explicitly.
- New standalone cheat sheet and flashcard deck for T-2426.
- `syllabus/17-architecture/INDEX.md` updated (9 → 10 chapters); `syllabus/00-overview/INDEX.md` row updated.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — `18-engineering-practices` gap audit: estimation and story points closed

### Added

- Gap audit of `18-engineering-practices` (6 chapters). Found [SDLC and Agile Methodology Fundamentals](../18-engineering-practices/sdlc-and-agile-methodology-fundamentals.md) covers Scrum's artifacts, roles, and sprint cadence in depth, but had zero coverage of estimation itself — story points, velocity, and how both get misused organizationally, despite being among the most commonly asked and most commonly misunderstood real Agile practice topics. Other candidates considered and deprioritized: pair/mob programming (real gap, lower interview frequency than estimation) and TDD as a general practice (already substantially covered in `08-testing/writing-tests-live-in-an-interview.md`, not an isolated gap).
- **Estimation and Story Points: Relative Sizing, Velocity, and Its Misuses** (new chapter, T-1805). Real, fully deterministic Java demo (`practice/java/engineering-practices/estimation-and-story-points/`, no timing/randomness — every run byte-identical), proving numerically, not just by assertion, why comparing velocity across two teams is invalid: two teams estimate the identical 8 tasks (135 real ground-truth hours) using two different, each internally consistent, point-per-hour calibrations against the standard Fibonacci-like scale (1/2/3/5/8/13/20/40/100) — real captured output: Team A's velocity is 61 points, Team B's velocity for the exact same work is 127 points, a real 2.1x difference explained entirely by calibration, not delivered output.
- `sdlc-and-agile-methodology-fundamentals.md` (T-2212, 1.1 → 1.2) updated in place with a `related:` link and a body cross-reference at its Scrum-artifacts section, naming this chapter's real evidence explicitly.
- New standalone cheat sheet and flashcard deck for T-1805.
- `syllabus/18-engineering-practices/INDEX.md` updated (6 → 7 chapters); `syllabus/00-overview/INDEX.md` row updated.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — `18-engineering-practices` follow-up gap audit: sprint retrospectives closed

### Added

- Second follow-up gap audit of `18-engineering-practices` (7 chapters, same day as the estimation-and-story-points closure above). Found [SDLC and Agile Methodology Fundamentals](../18-engineering-practices/sdlc-and-agile-methodology-fundamentals.md) names the sprint retrospective three separate times (as a Scrum ceremony, a common-mistakes note, and an interview answer) without ever explaining what actually happens in one, how to facilitate it, or the specific, common way retrospectives fail (retro theater).
- **Sprint Retrospectives: Structure, Facilitation, and Avoiding Retro Theater** (new chapter, T-1806). Follows this domain's own real-artifact discipline (established by its ADR chapter's `templates/adr-template.md`/`scripts/check_adr_completeness.py`) rather than a Java demo: a new real template (`templates/retro-action-item-template.md`) and a new real, executed mechanical checker (`scripts/check_retro_action_items.py`) verifying every retrospective action item names an explicit Owner and Deadline. Real captured output: `PASS` against both the template and a real, filled-out example retrospective (`practice/engineering-practices/retro-examples/sprint-14-retrospective.md`, 2 action items each, both complete); `FAIL` against a deliberately incomplete retrospective, naming exactly the missing fields (`item missing Owner, Deadline: "- [ ] Figure out a better reviewer rotation"`).
- `sdlc-and-agile-methodology-fundamentals.md` (T-2212, 1.2 → 1.3) and `architecture-decision-records-and-technical-writing.md` (T-1802, 1.0 → 1.1) updated in place with `related:` links and a body cross-reference naming this chapter's real evidence explicitly.
- New standalone cheat sheet and flashcard deck for T-1806.
- `syllabus/18-engineering-practices/INDEX.md` updated (7 → 8 chapters); `syllabus/00-overview/INDEX.md` row updated.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — `19-leadership-staff` follow-up gap audit: handling underperformance closed

### Added

- Follow-up gap audit of `19-leadership-staff` (7 chapters, previously marked "audit gaps fully closed" 2026-09-11). Found zero coverage anywhere in the repository of one of the most commonly asked real leadership topics — [Mentoring and Developing Others](../19-leadership-staff/mentoring-and-developing-others.md) covers positive growth and delegation, never the harder half of addressing a genuine, sustained performance problem directly, and `20-interview-preparation/behavioral/`'s closest chapter (Conflict and Technical Disagreement) is about disagreement over technical decisions, not a team member's performance.
- **Handling Underperformance and Difficult Feedback Conversations** (new chapter, T-1908). Follows this domain's own established convention (a representative, explicitly-labeled scenario rather than a Java demo, since this is an interpersonal leadership skill): the pattern-versus-single-instance distinction for identifying a genuine performance problem; separating a specific, behavioral observation from a character-based interpretation when giving feedback; and why an unaddressed performance problem compounds rather than resolves itself. Connects to `18-engineering-practices/sprint-retrospectives.md` and `estimation-and-story-points.md` at the Staff-level section, for recognizing when an apparently individual performance problem actually has a systemic, team-level cause.
- `mentoring-and-developing-others.md` (T-1901, 1.0 → 1.1) updated in place with a `related:` link and a body cross-reference naming this chapter's coverage of the harder half explicitly.
- New standalone cheat sheet and flashcard deck for T-1908.
- `syllabus/19-leadership-staff/INDEX.md` updated (7 → 8 chapters); `syllabus/00-overview/INDEX.md` row updated.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — `20-interview-preparation` gap audit: underperformance and difficult feedback narratives closed

### Added

- Gap audit of `20-interview-preparation` (21 chapters), triggered directly by the same-day `19-leadership-staff` closure above: that chapter explicitly named this domain's own missing counterpart. Confirmed zero coverage across all 16 `behavioral/` chapters of narrating a real conversation about addressing a team member's genuine performance problem — distinct from [Conflict and Technical Disagreement](../20-interview-preparation/behavioral/06-conflict-and-technical-disagreement.md) (disagreement between peers each performing well) and [Mentoring and Developing Others](../20-interview-preparation/behavioral/07-mentoring-and-developing-others.md) (proactive growth coaching, not a performance problem).
- **Underperformance and Difficult Feedback Narratives** (new chapter, T-1516). Follows this domain's established `behavioral-handbook-chapter` template exactly (Mental Model, Story Structure table, an explicitly-labeled illustrative example, a Distinguishing section, Interview Question with full evaluator/candidate framing, Common Mistakes, Self-Review Checklist, Summary, Related) — the mental model centers a *fair, specific process*, not just a favorable outcome, explicitly normalizing an honestly-told story where the outcome wasn't a clean turnaround.
- `06-conflict-and-technical-disagreement.md` (T-1506, 1.0 → 1.1) and `07-mentoring-and-developing-others.md` (T-1507, 1.0 → 1.1) updated in place with `related:` links distinguishing each from this new chapter. `19-leadership-staff/handling-underperformance-and-difficult-feedback.md` (T-1908, 1.0 → 1.1) updated in place, replacing its own forward-looking "if built" note with a direct link now that the counterpart exists.
- No cheat sheet or flashcard deck added for T-1516, matching this domain's own established convention — none of the 16 sibling `behavioral/` STAR-narration chapters have one (their own Self-Review Checklist serves that role instead). **Correction (2026-09-21, same day):** this entry originally, incorrectly, said a cheat sheet and flashcard deck were added for T-1516 — they were not; fixed here rather than left silently wrong.
- `syllabus/20-interview-preparation/INDEX.md` updated (21 → 22 chapters); `syllabus/00-overview/INDEX.md` row updated.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — `21-frontend-web` follow-up gap audit: Service Workers and PWA closed

### Added

- Follow-up gap audit of `21-frontend-web` (38 chapters). Found zero coverage anywhere in the domain of service workers, caching strategies, or offline support — the D-F4 "Advanced Frontend Architecture & Security" tier's original 2026-09-11 audit closed Security (F-401), WebSocket/Real-time (F-402), and Micro-Frontends (F-403), but this fourth, commonly-asked D-F4-shaped topic remained open.
- **Service Workers and PWA: Caching Strategies and Offline Support** (new chapter, F-404). Real, headless-Chromium-executed evidence (Playwright) at `practice/frontend/service-workers-and-pwa/`: a real registered service worker using `clients.claim()` for immediate control without a reload; a real cache-first strategy proven by a server-side request counter staying flat (2 → 2) across 3 repeated fetches; a real network-first strategy proven by the same kind of counter incrementing on every single online fetch (0 → 2); and Playwright's own genuine browser-context-level offline mode (`setOffline(true)`, not a mocked `fetch` failure) proving offline support is real and selective — a cached asset (`/app.css`) succeeds offline, a network-first endpoint (`/api/data`) falls back to its last real cached value, and a never-cached resource (`/never-cached.txt`) genuinely fails with `TypeError: Failed to fetch`, all in the identical offline context, with server request counters confirmed unchanged throughout. Re-run twice — byte-identical output both times, fully deterministic.
- `websocket-and-server-sent-events-for-realtime-ui.md` (F-402, 1.0 → 1.1) updated in place with a `related:` link to this chapter.
- New standalone cheat sheet and flashcard deck for F-404, matching this domain's own convention (unlike `19-leadership-staff`/`20-interview-preparation`'s behavioral chapters, `21-frontend-web`'s F-coded chapters do get standalone decks).
- `syllabus/21-frontend-web/INDEX.md` updated (38 → 39 chapters); `syllabus/00-overview/INDEX.md` row updated.
- Also corrected, same day: the `20-interview-preparation` entry above's cheat-sheet/flashcard claim for T-1516 (see the correction note there).
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — `22-ai-llm-engineering` gap audit: prompt injection and agentic security closed

### Added

- Gap audit of `22-ai-llm-engineering` (6 chapters, domain's originally-planned set, explicitly left open to further topics). Found [LLM API Integration Fundamentals](../22-ai-llm-engineering/llm-api-integration-fundamentals.md) (T-2300) already names prompt injection explicitly — "a real, documented risk category... not a hypothetical" — but never explains or demonstrates it, and confirmed no chapter anywhere in the repository does either.
- **Prompt Injection and Agentic Security: Attack Vectors and Defenses** (new chapter, T-2306). Real, fully deterministic Java demo (`practice/java/prompt-injection-and-agentic-security/`, no live LLM call, no timing/randomness — every run byte-identical): a real indirect-injection payload embedded in a tool's own output (`SearchTool`) genuinely wipes a simulated database when an `UnsafeAgent` re-parses tool output for embedded directives (real captured output: `Database wiped: true`); the identical payload against a `SafeAgent` that never re-parses tool output for instructions leaves it untouched (`Database wiped: false`); and a real, bidirectional human-approval gate is proven to genuinely block without approval and genuinely allow with it.
- `llm-api-integration-fundamentals.md` (T-2300, 1.0 → 1.1) and `agentic-workflows-and-tool-orchestration.md` (T-2304, 1.0 → 1.1) updated in place with `related:` links and body cross-references naming this chapter's real evidence explicitly.
- New standalone cheat sheet and flashcard deck for T-2306.
- `syllabus/22-ai-llm-engineering/INDEX.md` updated (6 → 7 chapters); `syllabus/00-overview/INDEX.md` row updated.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-21] — Real Anki export for all 279 flashcard decks

### Added

- Not a content gap — a retention-layer gap, raised after the full 22/22-domain content audit closed: `flashcards/` had no actual spaced-repetition scheduling, only static Markdown (verified: no `anki`/`export`/`srs` tooling existed anywhere in `scripts/`).
- `scripts/export_flashcards_anki.py`: parses all 279 `flashcards/*.md` decks (976 cards total, verified against `grep -c "^## Card: "` across the directory) and writes Anki's plain-text import format to `dist/anki/` (gitignored, regenerable — `flashcards/*.md` stays the source of truth). Produces one `<slug>.txt` per deck plus a combined `all-decks.txt` using Anki's `#deck column` directive, routing every card into a `Cracking Code Interviews::<domain>::<deck title>` deck automatically on import. Multi-paragraph answers use `<br>` (HTML) instead of real newlines, since the plain-text import format is one note per line.
- Verified real: ran the script against the full current `flashcards/` directory — `279/279` decks exported, `976` cards, output card/line counts checked against independent `grep` counts before and after.
- `flashcards/README.md` documents the new "Real spaced repetition: export to Anki" workflow.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-22] — Interactive System Design Canvas (real practice tool, not a chapter)

### Added

- Another retention/practice-layer gap, same post-audit review as the Anki export above: every diagram in `architecture-atlas/` is static — no way to actually place and rearrange system-design components short of physical paper, despite [System Design Method and Estimation](../11-system-design/system-design-method-and-estimation.md) explicitly teaching a Whiteboard Explanation that's meant to be drawn, not just read.
- `docs/javascripts/design-canvas.js` (new, vanilla JS, no dependency, matching this repo's existing `docs/javascripts/` scripts): a real drag-and-drop canvas — click a palette button to add a component, drag to reposition, double-click to rename, click a connect-mode toggle then two components in turn to draw a connection, export the current layout as a real PNG via an offscreen `<canvas>` render. Layout persists per reader per page in `localStorage` (same pattern as `checklist-progress.js`), never sent anywhere. No-op on every page except the one embedding it.
- `architecture-atlas/interactive-system-design-canvas.md` (new): explicitly labeled a practice tool, not an Architecture Atlas Standard entry (doesn't follow the 15-element reference template) — embeds the canvas widget directly, with real usage instructions and an explicit "What this isn't" section (doesn't grade a design, doesn't replace a real mock interview's narration requirement).
- `architecture-atlas/README.md` gets a new "Practice Tools" section, deliberately separate from the Entries reference table — this doesn't count toward the Atlas's existing case-study totals.
- `system-design-method-and-estimation.md` (T-801/T-802, 1.0 → 1.1) updated in place with a `related:` link and a body cross-reference from its own Whiteboard Explanation section.
- `mkdocs.yml` registers the new script (`extra_javascript`); `docs/stylesheets/extra.css` gets the widget's styling.
- Verified real, via a headless-Chromium harness (Playwright, reusing the dependency already installed for `practice/frontend/service-workers-and-pwa/`) driving the actual widget markup and script: adding two components, dragging one (position genuinely changed), toggling connect mode and drawing a real connection (an SVG edge element actually appeared), renaming via the double-click prompt, exporting a real downloaded PNG (valid `89 50 4E 47` PNG signature, 16,555 bytes, non-blank), deleting a component (its connection was removed with it), reloading the page (layout survived via `localStorage`), and clearing the canvas. Not verified inside the live Material/mkdocs theme itself — `mkdocs`/`mkdocs-material` aren't installed in this environment, so theme-level rendering (fonts, dark mode, sidebar layout) is unverified; only the widget's own DOM/script/CSS logic is confirmed working.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-22] — Timed Whiteboard Practice (real six-phase countdown, second practice-layer follow-up)

### Added

- Second practice-tool follow-up from the same post-audit review as the Interactive System Design Canvas above (this one is the kinesthetic/time-pressure half, not the visual-spatial half): [System Design Method and Estimation](../11-system-design/system-design-method-and-estimation.md)'s own cheat sheet states real per-phase minute ranges (Clarify 2–3, Estimate 3–5, API 2–3, Data 3–5, Architecture 10–15, Bottlenecks 5–10) and names running out of time before Bottlenecks as a scored gap — but there was no way to actually rehearse against a real, ticking clock.
- `docs/javascripts/whiteboard-timer.js` (new, vanilla JS, no dependency): a real six-phase countdown timer using those exact minute ranges. Two presets load the lower-bound (25 min total) or upper-bound (41 min total) pace exactly as stated in the chapter's table; any phase's minutes can also be hand-edited. Auto-advances phase to phase with a Web Audio beep on each transition, Pause/Resume, Reset. No state persistence (a timer session isn't meant to survive a reload) — no-op on every page except the one embedding it.
- Fixed a real off-by-one bug found during verification: the initial tick logic advanced a phase one second later than its configured duration (a 1-second test phase actually ran ~2 seconds) — corrected before shipping (`remainingSeconds <= 0` check instead of `< 0`), then re-verified.
- `architecture-atlas/timed-whiteboard-practice.md` (new): same "not a full Atlas entry" framing as the canvas tool, embeds the timer widget with real usage instructions, explicitly recommends pairing it with the Interactive System Design Canvas for a fuller rehearsal.
- `architecture-atlas/README.md`'s "Practice Tools" section gets a second row. `interactive-system-design-canvas.md` (1.0 → 1.1) and `system-design-method-and-estimation.md` (1.1 → 1.2) cross-linked in both directions, including a direct link from the chapter's own cheat-sheet table.
- `mkdocs.yml` registers the new script; `docs/stylesheets/extra.css` gets the widget's styling.
- Verified real, via the same kind of headless-Chromium harness as the canvas tool: set all six phases to ~1-second durations, ran a full session start-to-finish (6/6 segments marked done, 6 beeps fired — one per phase transition — confirmed via a spied `AudioContext.createOscillator` call count), confirmed Pause genuinely freezes the displayed clock (`00:04` unchanged across a 2-second paused wait) and Resume genuinely continues it (`00:04 → 00:03` after resuming), and confirmed both presets set the exact minute values from the chapter's table (25 and 41 total). Same scope limitation as the canvas tool: not verified inside the live Material/mkdocs theme, since `mkdocs`/`mkdocs-material` aren't installed in this environment.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-22] — `11-system-design` gains a new pattern-recognition chapter (user-requested, not a gap audit)

### Added

- User asked for real examples and visual examples for this domain's system-design patterns. This domain's 9 existing chapters (caching, load balancing, rate limiting, idempotency, resilience, storage selection, search/indexing, real-time delivery, plus the six-phase method) each already have real, deep coverage with their own diagrams and production scenarios — the genuine gap, confirmed by inspection, was a *recognition* layer: nothing answered "which of these 8 patterns applies to a given plain-English requirement," at a glance, before diving into any one chapter's full depth.
- **System Design Patterns: Recognition and Quick-Reference Guide** (new chapter, T-2427 — next free ID in the `T-2400`–`T-2499` reserved range). Mirrors `03-data-structures-algorithms`'s existing T-2120 pattern-recognition chapter's own precedent (a signal-to-pattern lookup routing across sibling chapters, not a 9th pattern itself, and not duplicating any of their depth): a Core Concepts recognition table (signal → pattern → canonical chapter) for all 8 patterns, a Pattern Gallery section giving each pattern one new, concrete example plus one compact Mermaid diagram distinct from that pattern's own canonical chapter's deeper treatment, and a Decision Framework section resolving the three most commonly confused overlapping-signal pairs (caching vs. read replica, rate limiting vs. circuit breaker, idempotency vs. message deduplication).
- `syllabus/11-system-design/.pages` updated: the new chapter sits second, right after `system-design-method-and-estimation.md` and before the 8 pattern chapters, matching where a reader would actually want it — after learning the method, before diving into any one pattern.
- New standalone cheat sheet and flashcard deck (3 cards) for T-2427, matching this domain's own established convention.
- `syllabus/11-system-design/INDEX.md` updated (9 → 10 chapters); `syllabus/00-overview/INDEX.md` row updated.
- Unlike a gap-audit addition, no existing chapter was cross-linked back to this one — matching T-2120's own precedent, where none of its 18 routed-to sibling chapters were retrofitted with a backlink either; the routing is one-directional by design.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-23] — `04-software-design/solid-principles.md` gains an at-a-glance visual summary (T-1701, user-requested)

### Added

- User shared a third-party "SOLID Principles in Java" infographic and asked whether this chapter already covers the same ground. It does, and goes further (real compiled/executed Java evidence for all five demos, a runtime-proven LSP failure, production scenarios, a decision framework, full interview Q&A) — the one thing the infographic had that this chapter didn't was a single-glance visual overview of all five principles side by side; the chapter's own `## Cheat Sheet` table already covers the same content but as a table, not a diagram.
- New `mermaid` `mindmap` diagram (`## Diagrams`, first entry) giving all five principles — smell and fix — in one compact visual, the same "poster" role the infographic served, without duplicating the chapter's own existing depth. Verified real: rendered in actual Chromium against `mermaid@10` (the exact version pinned in `mkdocs.yml`), confirmed it parses with zero errors and produces a real SVG, before shipping.
- Considered and rejected: adding the same diagram to the standalone `cheat-sheets/solid-principles.md` instead — no cheat sheet in this repository currently uses a Mermaid diagram (verified via grep), and the Cheat Sheet Standard's "aim for a one-page equivalent" framing fits this domain's existing chapters, which all carry their diagrams in the canonical chapter's own `## Diagrams` section, not in the cheat sheet.
- `version: 1.1 -> 1.2`, `last_reviewed: 2026-09-23`.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-23] — `20-interview-preparation/technical-answers` gains Project Ownership Narrative (T-1605, user-requested)

### Added

- User shared a Java-developer job-posting checklist naming Java 8-21/Spring Boot/Spring Cloud/microservices/REST/SQL-NoSQL/Kafka/Docker-Kubernetes/AWS-Azure-GCP/CI-CD, and asked to audit coverage. All nine were confirmed real, deep, already-covered ground (verified via grep across `syllabus/`, not assumed) — the genuine gap was the checklist's own explicit tip: *"be prepared to explain how you designed, developed, deployed, and supported applications — not just the technologies you used."* Confirmed via grep: no existing chapter, mock-interview round, or worksheet taught narrating one real project's full lifecycle as a single coherent story before this addition — [Technical Answer Framework](../20-interview-preparation/technical-answers/technical-answer-framework.md) teaches answering one question, [Trade-off Narration and ADRs](../20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md) covers only the design decision, [Production Incident Narratives](../20-interview-preparation/behavioral/04-production-incident-narratives.md) covers only support, and [System Design Narration and Whiteboard Discipline](../20-interview-preparation/system-design/system-design-narration-and-whiteboard-discipline.md) is for designing a *new* system live, not narrating one already built.
- **Project Ownership Narrative: Design, Build, Deploy, Support** (new chapter, T-1605, continuing the `T-1601`–`T-1604` Interview Craft cluster). A four-phase structure (Design/Develop/Deploy/Support), with Support named explicitly as the most commonly skipped phase — the same skip pattern [Trade-off Narration](../20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md)'s own "cost" beat suffers. Per explicit user request, the same representative example project (a Kafka-consuming order-notification service, reusing this program's own `OrderCompletedIntegrationEvent`/idempotency universe from `17-architecture/domain-events-vs-integration-events.md` and `11-system-design/idempotency.md`) is narrated four times — once each at Junior, Mid, Senior, and Staff scope — showing how the *same real facts* escalate in ownership framing without fabricating a bigger role, plus a phase-by-phase scope-escalation table. Labeled explicitly as a representative, illustrative example, not a real person's experience, per this project's standing rule against fabricating personal history.
- New mock-interview round, [Project Ownership Deep-Dive Round](../../practice/mock-interviews/project-ownership-deep-dive-round.md) (`practice/mock-interviews/`, round 16) — unlike every other round in that deliverable, it runs against the candidate's own real project rather than a repository-supplied prompt, with six probes (Design/Develop/Deploy/Support plus two scope-honesty checks) and target levels Junior through Staff, since the question format itself is level-agnostic — only the expected answer scope changes.
- Cross-linked in both directions with [Technical Answer Framework](../20-interview-preparation/technical-answers/technical-answer-framework.md) and [Trade-off Narration and ADRs](../20-interview-preparation/technical-answers/trade-off-narration-and-adrs.md) (both `related:` updated in place).
- No standalone cheat sheet or flashcard deck added — matching this domain's own established `technical-answers/`/`behavioral/` convention (neither `technical-answer-framework.md` nor `trade-off-narration-and-adrs.md` has one; both keep Cheat Sheet and Flashcards sections inline). This new chapter follows the same pattern.
- `syllabus/20-interview-preparation/INDEX.md` updated (22 → 23 chapters), including a real correction: its own "mock-interviews/" section had drifted stale at "12 real mock-interview transcripts" since the Round-13-15 freshness-audit batch (2026-09-13) never updated it — corrected to 16 while adding this round, rather than compounding the drift with a 17th silently-wrong count.
- `syllabus/00-overview/INDEX.md` row updated; `practice/mock-interviews/README.md` updated (round 16 added to the table and its own addition history).
- `validate.py`: errors 0, warnings 13 (unchanged baseline); all new cross-references verified resolving.

## [2026-09-23] — `14-devops-containers` gains Docker Compose: Multi-Service Orchestration (T-2428, user-requested)

### Added

- User shared a social-media infographic naming Docker Compose as its own topic and asked whether it's covered. Confirmed via grep: this repository's own practice labs already run 16 real `docker-compose.yml` files (Kafka, Postgres, RAG demos) as infrastructure scaffolding, but no chapter ever taught what the file actually does — `container-image-internals.md` mentions `docker-compose up` exactly once, in passing.
- **Docker Compose: Multi-Service Orchestration** (new chapter, T-2428 — next free ID in the `T-2400`–`T-2499` reserved range). Real, reproducible demo (`practice/docker-compose-multi-service-orchestration/`, Docker Compose v5.3.1): on a genuinely fresh Postgres volume (`docker compose down -v` before each run), plain `depends_on: [db]` only waits for the database's container to start, not for Postgres to finish `initdb` and accept connections — a real `api` service's startup-time TCP connect attempt fails with a captured `Connection refused` (`FAILED to connect to db:5432 after 10ms`). Adding a `healthcheck` (`pg_isready`) to `db` plus `condition: service_healthy` on `api`'s `depends_on` entry fixes it: Compose's own log shows `db-1: Waiting` then `db-1: Healthy` before `api-1` ever starts, and the same startup check connects on the first attempt (`CONNECTED to db:5432 in 9ms`). Re-run twice on a fresh volume each time — the race reproduced identically both times, not a one-off fluke.
- Also demonstrates real Compose service-name DNS resolution directly: `api` reaches Postgres at the literal hostname `db` — never an IP address, never `localhost` — with zero manual network configuration.
- New standalone cheat sheet and flashcard deck (3 cards) for T-2428, matching this domain's own established convention (this domain's chapters get standalone decks, unlike `technical-answers/`/`behavioral/`'s inline-only convention).
- `syllabus/14-devops-containers/.pages` updated: the new chapter sits second, right after `docker-and-containers-fundamentals.md` and before `container-image-internals.md` — single container, then multi-container, then image internals and Kubernetes.
- `syllabus/14-devops-containers/INDEX.md` updated (6 → 7 chapters); `syllabus/00-overview/INDEX.md` row updated.
- **Also corrected, found while updating these same files:** `flashcards/README.md`'s dated note-chain had silently skipped the T-2427 (System Design Patterns) deck entirely — its table row existed but no dated note was ever added for it. Added the missing note (with a correction line) alongside this entry's own, both using file-system-verified counts.
- `validate.py`: errors 0, warnings 13 (unchanged baseline).

## [2026-09-23] — `09-messaging-event-driven` gap closed in place: serialization step + end-to-end lifecycle diagram (user-requested, no new chapter)

### Added

- User shared a social-media infographic covering the full Kafka producer-to-consumer message lifecycle (9 steps) and asked whether it's covered. Confirmed via grep: every individual stage already has real, deep coverage across 3 chapters — but two genuine, narrow gaps existed: serialization (the record-to-bytes step, before partitioning) had zero mentions anywhere in `producer-semantics-and-partition-keys.md` (443 lines, verified), and no single diagram tied all 9 stages together as one ordered sequence — each chapter only diagrams its own concern.
- Unlike the two most recent user-requested additions (T-2427, T-2428, both new chapters), this gap was small enough to close by extending two existing chapters in place rather than creating a new one:
  - [Producer Semantics: acks, Idempotence, and Partition Key Design](../09-messaging-event-driven/producer-semantics-and-partition-keys.md) (`1.0 → 1.1`): new "Serialization happens before partitioning, not after" section — `Serializer<K>`/`Serializer<V>` convert typed key/value to bytes before anything else in the send path, with a real code snippet (`KafkaProducer` configured with `StringSerializer`) and the concrete consequence that `DefaultPartitioner` hashes the key's *serialized bytes*, not the key object — an inconsistent custom serializer can silently break per-entity ordering.
  - [Kafka Architecture Fundamentals](../09-messaging-event-driven/kafka-architecture-fundamentals.md) (`1.0 → 1.1`): new end-to-end `sequenceDiagram` (serialize → partition → send to leader → append/offset → replicate → consumer fetch → deserialize → process → commit), the first place in this domain all 9 stages appear as one flow. Cross-links each stage to the chapter that actually teaches it in depth ([Producer Semantics](../09-messaging-event-driven/producer-semantics-and-partition-keys.md), [Consumer Groups and Rebalancing](../09-messaging-event-driven/consumer-groups-and-rebalancing.md)) rather than duplicating any of them. Also notes explicitly that Kafka has no "consumed" concept at the broker level — a second consumer group re-reads the same partition independently, per [Retention, Log Compaction, and Tiered Storage](../09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md).
- Both diagrams verified real: rendered in actual Chromium against `mermaid@10` (the exact version pinned in `mkdocs.yml`) before shipping — zero parse errors, real SVG produced.
- Both chapters' Key Takeaways sections updated with one new bullet each; `syllabus/09-messaging-event-driven/INDEX.md` status/note updated (chapter count unchanged at 13 — this was an in-place fix, not a new topic).
- `validate.py`: errors 0, warnings 13 (unchanged baseline); all relative links and anchor fragments verified resolving.

## [2026-09-26] — `17-architecture` gap closed in place: Spring Boot wiring for Clean/Hexagonal Architecture (user-requested, no new chapter)

### Added

- User shared a social-media infographic showing Clean Architecture wired with real Spring Boot code (`@Service`/`@Repository` implementing a domain-owned port) and asked whether it's covered. [Clean and Hexagonal Architecture](../17-architecture/clean-hexagonal-architecture.md) (T-901) already teaches ports/adapters/dependency inversion in real depth — but deliberately framework-agnostic, using a Stripe/`PaymentGateway` example. Confirmed via grep: `05-spring/` had only three one-line cross-references to hexagonal/clean architecture (in `transactional-proxy-mechanics-and-propagation.md`, `dto-entity-mapper-patterns.md`, `spring-mvc-fundamentals.md`), no worked example anywhere of wiring the pattern with Spring's own annotations and DI container.
- Closed in place (no new chapter, matching the same-week Kafka precedent): [Clean and Hexagonal Architecture](../17-architecture/clean-hexagonal-architecture.md) (`1.0 → 1.1`) gained a new "Wiring this with Spring Boot" section — a domain-owned `CustomerRepositoryPort` with zero Spring/JPA imports, a `@Service`-annotated use case depending only on the port, and a `@Repository` adapter wrapping Spring Data JPA, with the concrete payoff stated explicitly: `GetCustomerUseCase`'s own source never names the adapter's concrete class, so swapping databases or substituting an in-memory fake in a test costs zero changes to the use case.
- New Anti-Patterns entry: injecting Spring Data's own `JpaRepository<T, ID>` directly into a use case, treating it as if it were the domain's deliberately-defined port — it already looks like an interface, which is exactly why this is a real, common temptation.
- New `related:` cross-links to `05-spring/spring-bean-scopes-and-proxy-modes.md` and `05-spring/spring-data-jpa-repository-abstraction.md`.
- Standalone `cheat-sheets/clean-hexagonal-architecture.md` gained a matching pitfall bullet; `flashcards/clean-hexagonal-architecture.md` gained one new card ("What Spring Boot actually contributes to this pattern") — an existing deck gaining a card, not a new deck file (282 decks unchanged, 985 → 986 cards).
- `syllabus/17-architecture/INDEX.md` status/note updated (chapter count unchanged at 10 — an in-place fix, not a new topic).
- `validate.py`: errors 0, warnings 13 (unchanged baseline); all relative links and anchor fragments verified resolving.

## [2026-09-26] — `05-spring` gains Spring and Spring Boot Fundamentals (T-2213, user-requested)

### Added

- User shared a widely-circulated "Spring Fundamentals — High Priority" 20-question interview checklist (IoC, DI, constructor vs. field injection, beans, bean lifecycle, bean scopes, stereotype annotations, `@RestController` vs. `@Controller`, `@Autowired` internals, ambiguous-bean resolution, `@Primary`/`@Qualifier`, `@Configuration`/`@Bean`, component scanning, `@SpringBootApplication`, auto-configuration, `@EnableAutoConfiguration`, starters, profiles, config management) and asked whether each question was covered, "muy detalladas y explicadas para cada seniority." Audited via grep: every concept was already taught somewhere in this domain's prose, several in real depth (`spring-mvc-fundamentals.md`'s stereotype-annotation and three-way-injection comparison tables in particular) — but none of the twenty existed as an actual, answerable interview question with a Junior baseline through a Staff extension, and this domain's existing "Interview Questions" sections skip straight to intermediate/advanced scenarios everywhere.
- Also found, narrower: **IoC** is named as `auto-configuration-and-bean-lifecycle.md`'s own topic-register tag (T-501, "IoC container & bean lifecycle") but was never actually defined in body text anywhere in the domain (confirmed: the tag was the only hit); **bean scopes** were only ever explained two at a time (`singleton`/`prototype`) — `request`/`session`/`application`/`websocket` were named only as analogous risk categories, never defined on their own terms; and **Spring Profiles** (`@Profile`, config-per-environment) is real, deep, correctly-covered content that lives entirely in `../15-cloud/twelve-factor-config.md` with no `05-spring` chapter's `related:` list pointing to it.
- **Spring and Spring Boot Fundamentals** (new chapter, T-2213, continuing the cross-domain `T-2200`–`T-2299` Junior Fundamentals range this domain didn't have a slot in yet). All twenty questions answered in full CLAUDE.md Interview Question Standard form (why asked / Junior expected / Strong Mid / Strong Senior / Staff extension / common mistakes / follow-ups / 1–5 rubric), each citing this domain's own existing real evidence rather than re-deriving it (the real, captured constructor-vs-field circular-dependency divergence from [Spring MVC Fundamentals](../05-spring/spring-mvc-fundamentals.md), the real `@ConditionalOnMissingBean`/lifecycle-callback mechanics from [Auto-Configuration and Bean Lifecycle](../05-spring/auto-configuration-and-bean-lifecycle.md)). New Core Concepts content closes the two real gaps directly: an explicit IoC definition (the restaurant-expediter analogy) and a complete six-scope table (`singleton`/`prototype`/`request`/`session`/`application`/`websocket`, including the real `BeanCreationException` a request/session-scoped bean throws outside an active request/session).
- Fixed the Profiles cross-link gap: added `../15-cloud/twelve-factor-config.md` to `spring-framework-vs-spring-boot.md`'s `related:` list (it was already present on `spring-mvc-fundamentals.md` and `auto-configuration-and-bean-lifecycle.md`, just missing on this one); added the new fundamentals chapter to all four related chapters' `related:` lists in both directions.
- New standalone cheat sheet and 5-card flashcard deck for T-2213, matching this domain's own established convention.
- `syllabus/05-spring/.pages` updated: the new chapter sits first, before `spring-mvc-fundamentals.md` — the true floor beneath this domain's existing Junior on-ramp, which already assumes IoC/DI/beans are understood before building a REST controller.
- `syllabus/05-spring/INDEX.md` updated (14 → 15 chapters); `syllabus/00-overview/INDEX.md` row updated.
- `validate.py`: errors 0, warnings 13 (unchanged baseline); all relative links and anchor fragments verified resolving.

## [2026-09-26] — `02-java` gains Java File I/O and NIO.2 (T-2429) and Java Regular Expressions (T-2430), same day

### Added

- User shared a generic, widely-circulated "27 Java/Spring interview topics" checklist (Java Language Fundamentals through Java 21 Features, Spring MVC, Hibernate, SDLC/Agile, Docker/Kubernetes, AWS, etc.) and asked whether all points were covered "bien detallados" in the questions section. Audited via grep against every listed topic: 25 of 27 were already covered, several far deeper than the checklist itself (all of `02-java/jvm-internals`, `02-java/concurrency`, `06-databases`'s Hibernate chapters, `04-software-design/solid-principles.md`, `18-engineering-practices/sdlc-and-agile-methodology-fundamentals.md`). Two real, zero-coverage gaps confirmed: **Java File I/O** (`java.io`/`java.nio.file` had no dedicated chapter anywhere, only incidental mentions inside `serialization-hazards-and-alternatives.md` and `exception-design-and-hierarchy-strategy.md`) and **Java Regular Expressions** (`java.util.regex` had zero mentions anywhere in the syllabus or question bank).
- **Java File I/O and NIO.2** (new chapter, T-2429, `02-java/language-core`, following the same handbook-chapter template as this domain's other gap-audit additions — `java-time-api.md`, `java-platform-module-system.md`). Real demo (`practice/java/language-core/java-file-io-and-nio2/`) proving: a measured 4.6x speedup wrapping a raw `FileReader` in a `BufferedReader` (79ms vs. 17ms reading an identical 200,000-line file character-by-character); a real, silent charset-mismatch corruption (`"café résumé naïve"` written as UTF-8, read back as `ISO-8859-1`, producing genuine mojibake with zero exception thrown); a real captured suppressed exception from a `try`-with-resources body and a failing `close()` both throwing; and NIO.2's `Files.write()`/`Files.readAllLines()` round-tripping real content.
- **Java Regular Expressions** (new chapter, T-2430, `02-java/language-core`). Real demo (`practice/java/language-core/java-regular-expressions/`) proving: named-group extraction; all three quantifier behaviors (greedy/reluctant/possessive) run against the identical input, including a real case where the possessive quantifier genuinely fails to match input the greedy version matches; a measured 5.3x speedup precompiling a `Pattern` versus repeated `String.matches()` calls (which recompile every time); and real, measured catastrophic backtracking — a pattern with 15 adjacent `a*` quantifiers showing genuine exponential time growth (34ms at 10 characters to 13.4 seconds at 19). The investigation also produced an honest negative result, kept in the chapter rather than discarded: the textbook `^(a+)+$` and `^(a|aa)+$` "evil regex" ReDoS shapes did **not** reproduce catastrophic blowup on this exact JDK (OpenJDK 21.0.12) even at 45 repetitions — a real, current, verified finding, not folklore carried forward unverified.
- `syllabus/02-java/language-core/.pages` updated (both chapters inserted); `syllabus/02-java/INDEX.md` updated (62 → 64 chapters, plus a correction noting the table had undercounted the domain at 62 since 2026-09-17); `syllabus/00-overview/INDEX.md` row updated.
- New standalone cheat sheets and 3-card flashcard decks for both chapters, plus two new sections in `syllabus/20-interview-preparation/question-bank/02-java-language-core.md` with full Junior/Mid/Senior/Staff canonical-treatment entries.
- `validate.py`: errors 0, warnings 13 (unchanged baseline); all relative links and anchor fragments verified resolving.

## [2026-09-27] — Coding Interview Standard applied to 6 exercises across `03-data-structures-algorithms`; wait/sleep/join and a staging-vs-prod scenario closed; 3 stale question-bank indices fixed

### Added

- User shared a generic "Honeywell Java Developer" interview checklist (Java Core, Multithreading, Spring Boot, Microservices, Database & SQL, REST & Security, Scenario-Based, and 8 named coding exercises) and asked whether the Q&A section covered it, plus asked for the repo's own Coding Interview Standard (recognition signals, clarifying questions, brute-force approach, optimized approach, pseudocode, compiling Java, complexity, edge cases, common bugs) applied to the coding exercises specifically. Audited via a dedicated Explore subagent, checking actual file contents (not just headings) against all 50 checklist items.
- Confirmed genuinely missing among the 8 named coding exercises: **Reverse a String** without built-ins, **Valid Palindrome**, and **First Unique Character** had zero coverage anywhere; **Find Duplicates** only had the boolean Contains-Duplicate variant, not the "return the duplicates" form; **LRU Cache** existed only as prose in an Interview Question, pointing to non-syllabus `practice/` code, never as a real worked Problem in the syllabus itself; **Top K Frequent Elements** (LC 347, integers) existed only as its string cousin (LC 692, Top K Frequent Words). Producer-Consumer and Detect-Cycle-in-Linked-List were already fully covered with the complete Standard.
- Closed all six with the full Coding Interview Standard — recognition signal, clarifying question, brute-force approach, pseudocode, a real compiling Java implementation (`practice/java/week-22/`, `practice/java/week-23/`), common bugs, test cases, and complexity — added to the existing pattern-organized chapters rather than new files, per this domain's own "organize by pattern, not by individual problem" convention:
  - [Arrays, Two Pointers, and Sliding Window](../03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md) (`1.0 → 1.1`): Problem 6 (LC 344, Reverse String) and Problem 7 (LC 125, Valid Palindrome) added. 16/16 assertions passing (was 11/11).
  - [Hashing Patterns and Frequency Maps](../03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md) (`1.1 → 1.2`): Problem 6 (LC 442, Find All Duplicates — the "index as a hash key" in-place technique) and Problem 7 (LC 387, First Unique Character) added. 16/16 assertions passing (was 11/11).
  - [Design-Style Coding Problems](../03-data-structures-algorithms/design-style-coding-problems.md) (`1.1 → 1.2`): LRU Cache promoted to Problem 1 (all later problems renumbered), with a real `HashMap` + doubly-linked-list, dummy-sentinel implementation. 28/28 assertions passing (was 23/23).
  - [Heaps, Top-K, and K-Way Merge](../03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md) (`1.2 → 1.3`): Top K Frequent Elements added as Problem 3 (renumbering Problems 3-6 to 4-7), solved via O(n) bucket sort rather than the O(n log k) heap approach Top K Frequent Words uses — a real, teachable contrast on when a bounded-range property beats a comparison-based structure. 12/12 assertions passing (was 10/10).
- The same audit found two more real, genuinely-missing Q&A gaps beyond the coding exercises, closed the same way:
  - [Deadlock, Race Conditions, and Thread Diagnostics](../02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md) (`1.0 → 1.1`): `wait()` vs. `sleep()` vs. `join()` — a very commonly asked basic concurrency comparison — had zero coverage in this domain. Added a new Core Concepts comparison table (does each release the monitor lock? who resumes it? typical use) and a new Interview Question 3, full Junior-through-Staff standard.
  - [The Twelve-Factor App: Config, Precedence, and Fail-Fast Validation](../15-cloud/twelve-factor-config.md) (`1.2 → 1.3`): "your service works in staging but fails in production, no code difference" — a very commonly asked scenario question — had zero coverage anywhere. Added a new Interview Question 5: an ordered troubleshooting methodology (config/env vars → secrets/credentials → feature flags → data/schema → infrastructure → scale-only limits), grounded in this chapter's own existing fail-fast and precedence content rather than invented fresh.
- Fixed 3 real stale-index gaps in the interview question bank (content already existed and was already correct; the index simply never picked it up):
  - `syllabus/20-interview-preparation/question-bank/02-java-language-core.md`: added `comparator-composition-and-pitfalls.md`'s complete Interview Questions section (written 2026-09-17, never indexed).
  - `syllabus/20-interview-preparation/question-bank/05-spring.md`: added `microservices-patterns-with-spring-boot.md`'s complete Interview Questions section (never indexed), plus a cross-domain note pointing to `17-architecture.md` for the Microservices-vs-Monolith trade-off itself. Flagged, not yet fixed: `dto-entity-mapper-patterns.md` and `spring-and-spring-boot-fundamentals.md` are also unindexed in this file — left as an honest known gap rather than silently expanding scope further.
  - `syllabus/20-interview-preparation/question-bank/02-java-concurrency.md`: added the new wait/sleep/join question and quick-fire card.
- Standalone cheat sheets and flashcard decks for `deadlock-race-conditions-and-thread-diagnostics.md` and `twelve-factor-config.md` updated in place (each gained one card/row, no new deck files). New flashcard total, verified directly against the file system: **285 decks, 999 cards** (997 prior + 2). Cheat sheet count unchanged at 285 (no new files, only in-place edits).
- `validate.py`: errors 0, warnings 13 (unchanged baseline); all internal anchor links in every edited chapter verified programmatically against their own headings.

## [2026-09-27] — `immutability-and-defensive-copying.md` gains a shallow-vs-deep-copy / `Object.clone()` section, same day

### Added

- User shared a generic "50 Java Interview Questions for Experienced Developers" checklist (Core Java, OOP & Design, Streams/Collections/Generics, Multithreading & JVM, Spring Boot & Backend) and asked, this time, to also check whether **all** previously-audited checklist questions across this whole session are correctly reflected in `syllabus/20-interview-preparation/question-bank/` and their respective canonical chapters — not just this one checklist. Audited the 50-item checklist first: confirmed the overwhelming majority already covered (deeper than the checklist, consistent with every prior audit this session). One genuine, confirmed gap: **shallow copy vs. deep copy / `Object.clone()`**. `immutability-and-defensive-copying.md` covers defensive copying (the general fix for leaking mutable state) in real depth, but `clone()`/`Cloneable` — a related but structurally distinct mechanism, and a classic interview question in its own right — was never explained anywhere: no coverage of `Cloneable`'s marker-interface role, `CloneNotSupportedException`, or `super.clone()`'s default shallow-copy behavior.
- [Immutability and Defensive Copying](../02-java/language-core/immutability-and-defensive-copying.md) (`1.0 → 1.1`): new Core Concepts subsection ("`Object.clone()`'s default behavior is a shallow copy, not a deep one") and a new Interview Question 3, full Junior-through-Staff standard. Real demo (`practice/java/week-13/immutability/src/CloneDemo.java`) proving: a class not implementing `Cloneable` throws a real `CloneNotSupportedException`; a shallow `clone()` (`super.clone()` alone) produces a clone that shares the exact same `List` instance as the original (mutating one mutates both, confirmed by reference equality); overriding `clone()` to deep-copy that field fixes it (confirmed independent by reference equality). New Anti-Patterns entry, Key Takeaways bullet, Cheat Sheet row, and flashcard card added to match.
- `syllabus/20-interview-preparation/question-bank/02-java-language-core.md` updated with the new Q3 and a quick-fire row; standalone cheat sheet and flashcard deck for `immutability-and-defensive-copying.md` updated in place (no new files). New flashcard total, verified directly against the file system: **285 decks, 1000 cards** (999 prior + 1).
- **Repo-wide question-bank completeness audit, requested directly by the user, dispatched to a subagent in parallel with the clone() fix above.** Widened the previous "stale index" detector to catch both the newer plain `## Interview Questions` heading and the older numbered `## N. Interview Questions` heading (the naive grep used for the 3 gaps found 2026-09-27 earlier the same day only caught the newer style). Result: **a much larger gap population than previously known — roughly 35 canonical chapters across 20 of the 22 domains** have a real, complete Interview Questions section with zero appearance in their domain's question-bank file (full list recorded in the session; not yet acted on — this entry records the finding, the fix is a separate, explicitly-scoped follow-up). Confirmed clean domains: `02-java-concurrency`, `02-java-jvm-internals`, `02-java-language-core` (as of this update), `21-frontend-web-nextjs`, `21-frontend-web-react`. Inverse check also run: zero broken file references or anchor-format mismatches across all 26 question-bank files' existing `**Canonical treatment:**` links.
- `validate.py`: errors 0, warnings 13 (unchanged baseline); all internal anchor links in the edited chapter verified programmatically against its own headings.

## [2026-09-27] — Repo-wide question-bank completeness sweep closed: all 33 flagged chapters across 20 domains indexed

### Added

- User asked directly to close the full gap population the same-day audit (previous entry) found — 33 canonical chapters across 20 of the 22 domains with a complete, real Interview Questions section but zero appearance in their domain's `question-bank/*.md` index file. Dispatched to 4 parallel background subagents (5-6 domains each, non-overlapping files), each reading every flagged chapter's actual Interview Questions/Flashcards content and writing an index entry summarizing the real Junior/Mid/Senior/Staff answer per question — no invented content.
- All 4 subagents hit a session-wide API rate limit mid-run; verified via `git status`/`git diff` exactly which of the 20 target files each had completed versus left partial (some had the deep-question sections but missing quick-fire rows and header counts; a few files were entirely untouched). Finished the remaining work directly: `03-data-structures-algorithms.md`, `04-software-design.md`, `05-spring.md` (2 chapters, including the 20-question `spring-and-spring-boot-fundamentals.md`, plus removing its now-stale "known gap" disclaimer), `12-security.md`, `13-observability.md`, `14-devops-containers.md`, `15-cloud.md`, `16-performance-jvm.md` (4 chapters), `20-interview-preparation.md`, `21-frontend-web-foundations.md`, and `22-ai-llm-engineering.md` — plus completed the quick-fire rows and header counts the partially-finished files (`06-databases.md`, `07-api-design.md`, `08-testing.md`, `09-messaging-event-driven.md`, `10-distributed-systems.md`, `11-system-design.md`) were still missing.
- Caught and fixed one real defect from the subagent batch: all 20 questions indexed for `spring-and-spring-boot-fundamentals.md` used the anchor `#interview-questions`, but that chapter's own heading is the older numbered `## 15. Interview Questions` style — the correct anchor is `#15-interview-questions`. Found by a repo-wide programmatic anchor-resolution check (parsing every `**Canonical treatment:**`/quick-fire link across all 26 question-bank files against the real headings in each target chapter) written specifically to verify this batch before shipping — confirmed zero broken links afterward, including a full re-sweep confirming all 33 originally-flagged chapters now appear in their domain's index.
- Net result: 22 of 26 question-bank files updated in this pass (the 3 fixed earlier the same day plus `05-spring.md`/`15-cloud.md`, already current, were untouched here). No canonical chapter content changed — this was purely closing index gaps against already-correct, already-written source material.
- `validate.py`: errors 0, warnings 13 (unchanged baseline); programmatic anchor-resolution check clean across all 26 question-bank files (not just the ones touched this pass).


## [2026-09-28] — Four checklist-audit gaps closed: nested/inner classes (T-2431), GoF catalog beyond the core four (T-2432), varargs and signature rules, broker selection (T-2433)

### Added

- User asked to run the established loop with a self-generated checklist: build a realistic Java/backend interview question list (~70 questions, target-company shape), audit it against **real repository content** rather than memory, report only genuine gaps, then close them in one pass. The audit confirmed most items were already covered more deeply than the checklist asks, and explicitly discarded several near-miss false positives after verifying the actual files — the `Integer` −128..127 autoboxing cache is genuinely covered in [Java Platform Basics](../02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md) §15 Q2, and static-initialization triggers in [Classloaders and Class Initialization](../02-java/language-core/classloaders-and-class-initialization.md). Four genuine gaps remained.
- **[Nested and Inner Classes](../02-java/language-core/nested-and-inner-classes.md)** (new chapter, T-2431, `02-java/language-core`): static nested vs. inner vs. local vs. anonymous classes had no canonical treatment anywhere in the repository — only incidental mentions in three unrelated files, despite being a High-frequency interview topic and the mechanism behind one of Java's most common memory leaks. Full handbook-chapter template, Junior through Staff. Real demos at `practice/java/language-core/nested-and-inner-classes/` (OpenJDK 21.0.12): reflection printing the synthetic `this$0` field and proving it points at the exact enclosing instance; a real `NotSerializableException: NestedClassesDemo` thrown while serializing an inner class whose own declared fields are all serializable; and a `WeakReference`-tracked 8 MB service that survives GC when its inner-class callback is registered in a long-lived list. **Two genuine, unplanned findings kept as content:** (1) javac 21 elides `this$0` entirely when the inner class body never uses its enclosing instance — confirmed with `javap` on two otherwise-identical callbacks — so the standard blanket claim that an inner class always retains its outer object is conditional on a modern JDK, and the chapter says so explicitly while still recommending `static` as the design default; (2) the first version of the leak demo did not reproduce because a `private final String` initialized to a literal is a compile-time constant that javac folds into the inner class body, removing the outer access entirely.
- **[Design Patterns Catalog Beyond the Core Four](../04-software-design/design-patterns-catalog-beyond-the-core-four.md)** (new chapter, T-2432, `04-software-design`): [Design Patterns Applied](../04-software-design/design-patterns-applied.md) covers Strategy, Builder, Decorator, and Singleton in depth, and nothing else in the repository covered Factory Method, Abstract Factory, Adapter, Proxy, Facade, Observer, Command, Chain of Responsibility, or Template Method (`Proxy pattern`, `Command pattern`, and `Chain of Responsibility` each had zero occurrences repository-wide). The new chapter is a companion, not a replacement — it explicitly does not restate the four. Real demos at `practice/java/design-patterns/catalog-beyond-core-four/`: a real JDK dynamic proxy (runtime class `$Proxy0`) retrying a deterministically flaky client to success on attempt 3 with zero retry code in the implementation, plus the bounded case where the last error genuinely propagates; a matched-family abstract factory run for DE and US; a working Command undo stack; an ordered four-handler chain; and a `final`-skeleton Template Method. **Unplanned real finding kept as content:** a listener that unsubscribes itself during Observer dispatch threw **no** `ConcurrentModificationException` at all — `ArrayList`'s `hasNext()` is only `cursor != size`, and removing the second of three elements made them equal, so iteration ended cleanly and a third listener was *silently skipped*. That is strictly worse than the exception the textbook predicts, and it means fail-fast iteration cannot be relied on to surface listener bugs.
- **[Java Modifiers and Method Signatures](../02-java/language-core/java-modifiers-and-method-signatures.md)** (`1.1 → 1.2`, `mastery_levels_covered` now `[L1, L2, L3]`): `varargs` had **zero** occurrences anywhere in the repository. Added a Core Concepts block (varargs arity and the empty-array guarantee, the three-phase overload resolution order, array-versus-varargs spreading, covariant return types, static hiding vs. instance overriding), an Internals block on varargs as compile-time array sugar and generic-varargs heap pollution, three new Interview Questions (3–5) at the full standard, four new Common Mistakes, four new Mastery Checklist items, and three Further Reading links. Real evidence from a new demo (`MethodSignatureRulesDemo.java`): a boxing overload genuinely beating a varargs overload; `Arrays.asList(int[]).size() == 1` versus `Arrays.asList(String[]).size() == 3` from the identical-looking call; heap pollution ending in a real `ClassCastException` on a source line containing no cast; and the static-vs-instance dispatch contrast. Two new deliberately-broken programs produce real `javac` errors, appended to that directory's existing transcript: `reference to handle is ambiguous` between two varargs overloads, and `static methods cannot be annotated with @Override`.
- **[Broker Selection: Kafka vs. RabbitMQ vs. SQS](../09-messaging-event-driven/broker-selection-kafka-rabbitmq-and-sqs.md)** (new chapter, T-2433, `09-messaging-event-driven`): the domain taught Kafka thoroughly across 13 chapters but never answered the comparative question interviewers actually ask. `RabbitMQ` had two incidental mentions repository-wide and no comparison anywhere. The chapter is explicitly a comparison and selection guide rather than a tutorial, built on the log-versus-queue root difference, with a full comparison matrix, a seven-step decision framework (replay first, operational ownership as a legitimate override), and three production scenarios including one where a queue is correct and Kafka is actively harmful. All behavioral claims are sourced from official Kafka, RabbitMQ, and AWS documentation, cited inline; **no performance numbers are asserted**, since none were measured on the systems being compared.
- Companion deliverables: three new cheat sheets and three new flashcard decks (5 cards each). New totals, counted directly against the file system rather than carried forward: **288 cheat sheets, 288 decks, 1015 cards** (285/285/1000 before this change). The flashcards README's 2026-09-26 entry stated 997 cards where the tree actually held 1000 — corrected in place by direct measurement, not a loss of cards.
- Question-bank entries added: `02-java-language-core.md` (+6 — three for the new nested-classes chapter, three for the modifiers chapter's new questions; subdomain total 115 → 121, `02-java` domain total 270 → 276), `04-software-design.md` (+3, domain total 12 → 15), `09-messaging-event-driven.md` (+3, domain total 62 → 65). Domain INDEX tables updated for `02-java` (65th chapter), `04-software-design`, and `09-messaging-event-driven`.
- `validate.py`: errors 0, warnings 13 (unchanged baseline). Every internal anchor added in this change was verified programmatically against the real headings of its target file, using the same slugification `validate.py` does not check — including the two live anchor styles (`#interview-questions` for handbook-chapter files, `#15-interview-questions` for the older numbered syllabus-topic style).

## [2026-09-28] — Audit queue items 2–4: three zero-coverage gaps closed, `22-ai-llm-engineering` deepened, frontend ladder convention documented

### Added

- Second change from the full-project audit the user requested on 2026-09-28 (the first, visual explanations for 10 `03-data-structures-algorithms` chapters, is the previous entry). The user approved working the queue in order and asked for items 2, 3, and 4 together: the three zero-coverage gaps, the thinnest domain, and the one structural inconsistency.
- **[Graceful Shutdown and Connection Draining](../14-devops-containers/graceful-shutdown-and-connection-draining.md)** (new chapter, T-2434, `14-devops-containers`). `graceful shutdown`/`SIGTERM`/`preStop` had **one** mention across the entire repository, despite being a standard Kubernetes-plus-Spring-Boot interview question and the cause of the "every deploy produces a burst of 502s" symptom. Real demo (`practice/java/devops/graceful-shutdown/`, OpenJDK 21.0.12): two servers identical except for their `SIGTERM` handling, each sent `SIGTERM` one second into a three-second request, driven by a committed shell script. The unhandled server's client received `curl: (52) Empty reply from server` with HTTP status **000** at 1.38 s — not a 5xx, no status code at all, so a retry policy keyed on status has nothing to key on. The three-phase server returned a normal **200** at 3.00 s and exited 2006 ms after `SIGTERM` with `completed=1 rejected=0`. The chapter's core argument is the ordering — readiness flips *first*, while still serving, because endpoint removal is eventually consistent and runs concurrently with the signal — plus the non-HTTP cases (consumers commit and leave the group; there is no load balancer to be removed from) and the closing point that graceful shutdown is a frequency reduction, not a correctness guarantee, so idempotency is what makes the residual retries safe.
- **[HTTP Caching for APIs](../07-api-design/http-caching-for-apis.md)** (new chapter, T-2435, `07-api-design`). `Cache-Control` appeared exactly **once** in the repository, in a frontend deployment chapter; [Caching Strategies and Invalidation](../11-system-design/caching-strategies-and-invalidation.md) is server-side (Redis, stampedes, hot keys) and explicitly stays that way — this chapter owns the protocol layer and says so in its scope note. Real demo (`practice/java/api-design/http-caching/`): a real `com.sun.net.httpserver` origin and a real `java.net.http.HttpClient` exchanging conditional requests, measuring a 50-byte body against a 0-byte `304`, **2 server-side renders across 4 requests**, and the detail candidates miss — both `304`s still cost a full round trip, so validation saves bytes while only freshness saves the request. Also covers the three misread directives (`no-cache` is not `no-store`; `private` versus `public`; `must-revalidate` applies after expiry), `Vary` and the shared-cache confidentiality failure, strong versus weak validators, body-hash versus version-derived ETags, and `If-Match`/`412` as optimistic concurrency.
- **[Structured Logging, Correlation IDs, and Log Hygiene](../13-observability/structured-logging-correlation-ids-and-log-hygiene.md)** (new chapter, T-2436, `13-observability`). [Logging, Metrics, Tracing, and OpenTelemetry](../13-observability/logging-metrics-tracing-and-opentelemetry.md) covers the three-signal model and traces well, but `structured log`, `log level`, `MDC`, and `correlation id` did not appear in it at all — record shape, context propagation, level semantics, sensitive data, and volume were uncovered. Real demo (`practice/java/observability/structured-logging/`, deliberately dependency-free, with pinned timestamps so the transcript is byte-stable): the same event as prose and as a structured record, four records sharing a correlation ID without any call site passing it, and — the finding worth keeping — a **reproduced silent context loss**, where a task submitted to a thread pool logged with no `correlation_id` at all because MDC is a `ThreadLocal`. Nothing throws; the request's trail simply appears to stop. The chapter also covers why clearing context matters as much as setting it (pooled threads are reused, and stale context attributes work to the wrong request), redaction as a net rather than the design, and the logs-versus-metrics split.
- **`22-ai-llm-engineering` deepened.** The audit found it the thinnest domain in the repository: 7 chapters at 2606–3361 words against a median of 4565, with zero coverage of hallucination, guardrails, context-window management, MCP, or the fine-tuning-versus-RAG decision. Closed with two new chapters — **[Hallucination, Groundedness, and Output Guardrails](../22-ai-llm-engineering/hallucination-groundedness-and-output-guardrails.md)** (T-2307), whose central artifact is a layer-by-layer table of what each guardrail catches *and misses*, plus the one diagnostic question (were the right documents in the prompt?) and a cost-ordered guardrail list; and **[Context Window Management, Token Budgets, and Cost](../22-ai-llm-engineering/context-window-management-token-budgets-and-cost.md)** (T-2308), covering the budget arithmetic including reserved output space, why more context is not monotonically better, history-management strategies and summary drift, prompt caching and what invalidates it, and cost levers in order of return. Two existing chapters were extended in place rather than duplicated: [Agentic Workflows and Tool Orchestration](../22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md) (`1.1 → 1.2`) gained a Model Context Protocol section framed around what it standardizes (tool description and invocation, so tools become reusable across hosts) and what it emphatically does not (authorization, safety, or correctness — and a third-party server's tool *description* is itself untrusted text the model reads); [RAG and Vector Databases](../22-ai-llm-engineering/rag-and-vector-databases.md) (`1.0 → 1.1`) gained a retrieval-versus-fine-tuning comparison whose three decisive consequences are that changing facts belong in retrieval, per-tenant data must never be fine-tuned in (weights have no access control), and fine-tuning is the right tool for *how* the model answers rather than *what* it knows.
- **Frontend ladder convention documented, not retrofitted.** The audit flagged that 33 of 39 `21-frontend-web` chapters declare no `L1` in `mastery_levels_covered`, against this repository's stated rule that every canonical topic file serves the full Junior-through-Staff spectrum in one place. Reviewed and **kept deliberately**: the domain uses a prerequisite-chained ladder (dedicated L1 foundation chapters plus accurate `prerequisites` front matter downstream), which the domain's own 2026-09-05 Phase 5 note already recorded. What was missing was a statement of the exception in `CLAUDE.md` itself, so a future reader of the rule would not treat the divergence as drift — now added to the Scope Addendum, with a matching dated note in `syllabus/21-frontend-web/INDEX.md` and an explicit instruction not to "fix" it by adding L1 sections. No chapter content changed.
- Companions: 5 new cheat sheets and 5 new flashcard decks (5 cards each). New totals, counted directly against the file system: **293 cheat sheets, 293 decks, 1040 cards** (288/288/1015 before this change). Question-bank entries added to `07-api-design.md`, `13-observability.md`, and `14-devops-containers.md` (+3 questions each; domain totals 43→46, 26→29, 29→32) and `22-ai-llm-engineering.md` (+6; 32→38). Domain INDEX tables updated for `07-api-design`, `13-observability`, `14-devops-containers`, and `22-ai-llm-engineering`, plus the `00-overview` INDEX chapter counts for all four.
- `validate.py`: errors 0, warnings 13 (unchanged baseline) — the run mid-change legitimately reported 6 errors for the not-yet-written cheat-sheet and flashcard companions, which is the validator working as intended. Anchor-resolution check clean across every file touched.

### Remaining from the audit, not acted on here

- Lower-priority gaps, explicitly deferred and not yet approved: PII/GDPR retention (4 incidental mentions, no chapter), infrastructure as code (3 mentions), branching strategy/semver/release management (1 mention each), and two `01-computer-science-foundations` holes (concurrency-versus-parallelism, file systems).
## [2026-09-28] — Visual-explanation gap closed for 10 `03-data-structures-algorithms` chapters

### Added

- User asked for a full-project audit — what topics are still missing, whether the Junior-through-Staff ladder really holds, and whether the material is digestible or needs rewriting for comprehension. The audit scanned all 309 chapters programmatically (word counts, diagram counts, L1 coverage, long-paragraph counts, reading-estimate honesty, practice-code references) rather than sampling by memory. Its single strongest comprehension finding: **10 of the 20 chapters in `03-data-structures-algorithms` carried no diagram at all** — no Mermaid, no real ASCII (the few matching lines were table borders) — in the one domain whose own Coding Interview Standard in `CLAUDE.md` explicitly requires a "visual explanation" per algorithm. The user approved closing it first, as its own PR.
- Nine chapters gained a real, executed step-by-step trace, placed at the end of each chapter's Section 5 (How It Works Internally) where the mechanism is already being explained, rather than collected into a separate gallery: [Arrays, Two Pointers, and Sliding Window](../03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md) (LC 11 pointer walk, showing the shorter side always moving), [Binary Search and Search on Answer](../03-data-structures-algorithms/binary-search-and-search-on-answer.md) (LC 1011, the `[10, 55]` candidate-answer window collapsing over five probes with the feasibility verdict at each), [Bit Manipulation](../03-data-structures-algorithms/bit-manipulation.md) (LC 136, the running XOR in 4-bit lanes with each duplicate cancelling itself), [Dynamic Programming](../03-data-structures-algorithms/dynamic-programming.md) (LC 72, the full filled edit-distance grid for `"horse" -> "ros"`), [Greedy and the Exchange Argument](../03-data-structures-algorithms/greedy-and-the-exchange-argument.md) (LC 45, `farthest` versus `currentEnd` with the exact moments a jump is committed), [Hashing Patterns and Frequency Maps](../03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md) (LC 560, the prefix-sum map after every element, including the step that adds two subarrays at once because the map stores counts), [Intervals, Merging, and Sweep Line](../03-data-structures-algorithms/intervals-merging-and-sweep-line.md) (LC 253, six sorted events with running and peak active counts), [Sorting Algorithms](../03-data-structures-algorithms/sorting-algorithms.md) (merge sort's real depth-first split/merge order, plus a Mermaid recursion tree of the same input), and [Stacks and Monotonic Stack](../03-data-structures-algorithms/stacks-and-monotonic-stack.md) (LC 84, stack contents after every index with each pop's `height x width = area`).
- **All nine traces are real executed output**, generated by one new program — `practice/java/algorithms/visual-traces/src/VisualTraces.java`, OpenJDK 21.0.12, transcript committed — rather than hand-drawn illustrations. Every chapter's trace block carries a link back to that directory, so any number in any diagram can be re-verified by re-running one file. This was a deliberate choice over drawing the diagrams: a hand-drawn DP grid or stack trace with one wrong cell is worse than no diagram, because a reader who cannot reproduce it has no way to tell.
- The tenth chapter, [Coding Interview Pattern-Recognition Methodology](../03-data-structures-algorithms/coding-interview-pattern-recognition-methodology.md), gained a Mermaid routing tree keyed on **input shape** instead of a trace — it teaches a decision, not an algorithm, so there is nothing to execute. It explicitly complements, rather than replaces, that chapter's IWI-ranked signal-to-pattern table: the table stays the reference, the tree is the 30-second version for an unseen problem, and the surrounding prose names its three limits (a leaf is a hypothesis to say out loud, several problems have two legitimate routes, and the tree stops where input shape stops deciding).
- All 10 chapters received a version bump and `last_updated: 2026-09-28`; the domain INDEX records the closure and its scope.
- `validate.py`: errors 0, warnings 13 (unchanged baseline). The anchor-resolution check across all touched files (82 anchored links) caught one real defect before commit — a same-file link written as `#4-core-concepts-l2--the-signal-to-pattern-table` with a doubled hyphen, which python-markdown collapses to a single one.

### Audit findings recorded but not acted on in this change

- Remaining approved queue, in the order the user set: (a) three zero-coverage gaps — graceful shutdown/SIGTERM/connection draining (1 mention repository-wide), HTTP caching headers (`Cache-Control` appears once, in a frontend chapter; the caching chapter is Redis/app-level only), and logging discipline (structured/JSON logs, log levels, correlation-ID propagation, PII in logs — the OpenTelemetry chapter covers traces but none of this); (b) `22-ai-llm-engineering` is the thinnest domain at 7 chapters of 2606–3361 words against a repository median of 4565, with zero coverage of hallucination/factuality, guardrails, context-window management, MCP, or the fine-tuning-versus-RAG decision; (c) the frontend domain's ladder convention — 33 of 39 chapters declare no L1 and use a prerequisite chain instead of this repository's "every topic file serves the whole spectrum in one place" rule, which is coherent pedagogy but an undocumented divergence.
- Lower-priority gaps found and deliberately deferred: PII/GDPR retention (4 incidental mentions, no chapter), infrastructure as code (3 mentions), branching strategy/semver/release management (1 mention each), and two `01-computer-science-foundations` holes (concurrency-versus-parallelism, file systems).
- What the audit confirmed is **not** broken, so it need not be re-audited: reading-time estimates are honest (zero chapters deviating more than 60% from their real word count), long paragraphs are not a systemic problem (101 paragraphs over 170 words across 309 chapters), and the backend Junior-through-Staff ladder is complete (zero backend chapters missing an L1 section).

## [2026-09-28] — Audit queue item 5 closed: file systems, concurrency vs. parallelism, data privacy, IaC, branching strategy

### Added

- Third and final change from the 2026-09-28 full-project audit. The user approved working the queue in order; this covers item 5, the lower-priority gaps that were explicitly deferred in the previous two entries. Every gap was **re-verified against real file contents** before writing rather than trusted from the earlier audit pass — which mattered, because it changed one decision: the concurrency-versus-parallelism gap turned out to belong inside the existing process/thread chapter rather than in a new chapter of its own.
- **[File Systems and Durable I/O](../01-computer-science-foundations/file-systems-and-durable-io.md)** (new chapter, T-2440, `01-computer-science-foundations`). `fsync`, `inode`, block size, and sequential-versus-random access had **zero** occurrences anywhere in the repository, despite this layer underpinning every database's durability claim, Kafka's design, and the meaning of "committed." Real measurements (`practice/java/cs-foundations/file-systems-and-durable-io/`, OpenJDK 21.0.12, APFS on NVMe): 2,000 appends of 256 bytes took **3 ms** with no syncing, **7,704 ms** syncing per record, and **91 ms** syncing every 100 — roughly a 2,000x cost for per-record durability, amortised to ~30x by batching, which is exactly what a database's group commit does. Reading the same 64 MB took 48 ms with a 512-byte buffer (131,072 syscalls) and 4 ms with a 1 MB buffer (64 syscalls). The chapter also carries an **honest negative result**: the textbook 10–100x random-read penalty did **not** reproduce — 20,000 4 KB reads each way came out at a ratio of 0.68x on NVMe with a warm page cache. Dropping the page cache requires elevated privileges, so the demo deliberately does not assert a cold-cache number it cannot measure, and the chapter states the claim's real scope (a spinning-disk, cache-miss phenomenon) rather than reprinting it as universal.
- **[OS Process and Thread Model](../01-computer-science-foundations/os-process-thread-model.md)** (`1.0 → 1.1`). The audit found the word `parallel` appearing **zero** times in the chapter that owns threading, while `concurren*` appeared 23 times — so the distinction most interviews open with was structurally absent. Rather than create a competing chapter, the material was added where it belongs, per the no-duplication rule. Real measurements on 10 cores (`practice/java/cs-foundations/concurrency-vs-parallelism/`): 40 CPU-bound tasks took 610 ms on one thread, 90 ms at one thread per core, and 84 ms at 40 threads — no gain past the core count; the identical thread-count increase applied to 200 tasks each waiting 50 ms went from 10,656 ms to **71 ms**, a 150x improvement, because a waiting thread consumes no CPU (virtual threads: 64 ms). Amdahl's law measured directly: a serial phase plus 40 parallelisable tasks gave **5.02x on 10 cores**, not 10x. Two new interview questions and a matching flashcard and cheat-sheet section.
- **[Data Privacy: PII Handling, Retention, and Erasure](../12-security/data-privacy-pii-handling-and-retention.md)** (new chapter, T-2437, `12-security`). GDPR, retention, encryption at rest, and data classification had zero occurrences in the security domain; PII appeared twice, incidentally. Real demo (`practice/java/security/data-privacy/`) does three things: recovers `alice@example.com` from its stored SHA-256 by guessing against a plausible value space, demonstrating that hashing an identifier is pseudonymisation and not anonymisation; shows a stable pseudonymous reference; and performs a real **crypto-shred**, where destroying one per-subject AES key makes two independently-stored ciphertexts permanently unreadable without touching either storage system. The chapter's central argument is that erasure is a distributed problem — backups and append-only event logs cannot delete a record, which is what crypto-shredding exists for — and that erasability is an architectural property decided at schema-design time.
- **[Infrastructure as Code](../15-cloud/infrastructure-as-code.md)** (new chapter, T-2438, `15-cloud`). Remote state, GitOps, and immutable infrastructure had zero occurrences across `15-cloud` and `14-devops-containers`; Terraform appeared in two chapters in passing. This chapter **deliberately carries no measured demo**, and says so in a scope note at the top: the behaviour described belongs to tools that provision real cloud resources, so running them honestly would mean creating billable infrastructure, and simulating them would produce output that looks measured and is not. Every behavioural claim is cited to official documentation inline. Content centres on state as the crown jewel (it contains secrets in plaintext, must be remote, locked, and versioned), the plan as a three-way comparison with three stated limits, replacement as a *provider* property rather than a tool property, drift as a signal with four distinct causes, and blast-radius state splitting.
- **[Branching Strategy, Versioning, and Release Management](../18-engineering-practices/branching-strategy-versioning-and-release-management.md)** (new chapter, T-2439, `18-engineering-practices`). `semver` had zero occurrences repository-wide, and [Git Internals and Collaboration Workflows](../18-engineering-practices/git-internals-and-collaboration-workflows.md) — which owns the object model, merge-versus-rebase, reflog, and bisect — mentioned trunk-based three times and GitFlow once in passing, with no strategy comparison. The new chapter is a complement, not an overlap, and cites the existing one for mechanics. Real git transcripts (`practice/git/branching-and-release/`, git 2.55.0) contrast trunk-based with release branches, and the back-port step produced a **genuine cherry-pick conflict** — kept and resolved in the demo rather than edited out, because it is the point: back-porting is a merge performed under incident pressure. The resulting graph shows one fix on two lines under two different commit hashes, which is the mechanical reason a forgotten back-port is silent and a bug fixed in 1.0.1 reappears in 1.1.0.
- Companions: 4 new cheat sheets and 4 new flashcard decks (5 cards each), plus a card and a cheat-sheet section added to the existing `os-process-thread-model` companions to match the extension. New totals, counted directly against the file system: **297 cheat sheets, 297 decks, 1061 cards** (293/293/1040 before this change). Question-bank entries added to `01-computer-science-foundations.md` (+5 — three for the new chapter, two for the process/thread extension; domain total 12→17), `12-security.md` (54→57), `15-cloud.md` (25→28), and `18-engineering-practices.md` (18→21). Domain INDEX tables updated for all four, plus the `00-overview` INDEX chapter counts.
- `validate.py`: errors 0, warnings 13 (unchanged baseline). Anchor-resolution check clean across every file touched.

### Audit status

- **The 2026-09-28 full-project audit's approved queue is now fully closed**, across three changes: item 1 (visual explanations for 10 algorithms chapters), items 2–4 (three zero-coverage gaps, `22-ai-llm-engineering` deepened, frontend ladder convention documented), and item 5 (this entry). Nothing from that audit remains outstanding.

## [2026-09-29] — Source-material gap audit: filters vs. interceptors, `@Mock` vs. `@Spy`

### Added

- The user supplied two interview-prep PDFs — a "30 Spring Boot Concepts" visual guide and a 33-question Java/Spring/microservices Q&A — and asked which of that material the repository does not already cover in depth, adding whatever was genuinely missing. Audited against real file contents rather than memory, with an early false-negative corrected: a `ugrep` alias silently broke `--include` handling on the first pass, so every count was re-run through `/usr/bin/grep` before any gap was claimed.
- **The audit's main finding is that most of the source material was already covered more deeply than the source itself.** The 30 Spring Boot concepts map onto `05-spring` chapters of 3,196–7,264 words (dependency injection, IoC, beans, component scanning, profiles, `@ConfigurationProperties`, REST controllers, validation, `@RestControllerAdvice`, `@Transactional`, Spring Data JPA, pagination, security, JWT, testing slices, Actuator, caching, `@Async`/`@Scheduled`, lifecycle). The Q&A PDF's garbage-collection questions are covered across 13 chapters in `02-java/jvm-internals`; its Kafka questions across 14 in `09-messaging-event-driven`; its PostgreSQL MVCC and isolation-level questions by dedicated chapters in `06-databases`; its `kubectl logs --previous` and pipeline-failure questions in `14-devops-containers`. `@Scheduled`'s default single-threaded scheduler risk and the `ScheduledAnnotationBeanPostProcessor` mechanism were already documented with a measured counter. JPA relationship mapping — `mappedBy`, cascade, and the `orphanRemoval`-versus-`CascadeType.REMOVE` distinction — was already covered in depth. None of this needed changing.
- **[Request Filters, Interceptors, and the Servlet Chain](../05-spring/request-filters-interceptors-and-the-servlet-chain.md)** (new chapter, T-2441, `05-spring`). The first genuine gap: `HandlerInterceptor` and `Interceptor` had **zero** occurrences anywhere in the repository, and the servlet-filter side existed only inside [Security Filter Chain](../05-spring/security-filter-chain.md), which owns authentication rather than the request pipeline. "What is the difference between a filter and an interceptor" is among the most common Spring screening questions. The demo (`practice/java/spring/filters-and-interceptors/`, Spring Boot 3.5.16, Spring Framework 6.2.19, embedded Tomcat 10.1.55, OpenJDK 21.0.12) deliberately registers **two** filters and **two** interceptors rather than one of each, because a single layer cannot distinguish "filters run before interceptors" from "the chain unwinds in reverse," and the chapter makes both claims.
  - Four predicted behaviours were confirmed by measurement rather than asserted: `@RestControllerAdvice` never runs for an exception thrown in a filter, and the client receives the container's own body (`{"timestamp":...,"status":500,"error":"Internal Server Error","path":...}`) — a genuinely **different JSON shape** from the application's `{"error":...}` for the identical failure thrown from a controller, so an API silently carries two error contracts and only one is typically covered by tests. `postHandle` does not run **at all** when the handler throws, even though the advice successfully mapped the exception to a 422. `afterCompletion`'s `ex` parameter arrives as **`null`** for every exception a `HandlerExceptionResolver` already resolved, so the common `if (ex != null)` failure-logging idiom records nothing in a well-built service. And a rejecting interceptor's **own** `afterCompletion` is skipped, because `triggerAfterCompletion` unwinds only as far as `interceptorIndex`.
  - A fifth finding was **unplanned**: the outermost filter's `finally` block recorded `status=200, response committed=false` for a request the client received as `500`. Tomcat sets the error status in `StandardHostValve`/`ErrorReportValve`, outside the entire application filter chain, so the status is still its default at the moment the filter's own stack frame unwinds. Access logs written the obvious way therefore under-report server errors, and the dashboard built on them looks healthiest during the incidents it exists to reveal. Kept as a first-class section of the chapter rather than a footnote.
  - One instrumentation artefact was found and fixed before committing: the first run printed the `/boom-filter` trace twice, because the demo's own trace printer fired both before the throw and again in the outer filter's `finally`. That was a defect in the demo, not in Spring, so it was corrected and the transcript regenerated rather than explained away.
- **[Test Strategy, the Pyramid, and Test Doubles](../08-testing/test-strategy-and-test-doubles.md)** (`1.0 → 1.1`). The second genuine gap: `@Spy` had **zero** occurrences repository-wide, and "spy" appeared exactly once, as a word in the test-double taxonomy. Mockito `@Mock` versus `@Spy` is question 33 of the supplied Q&A and a routine screening question. Real demo (`practice/java/testing/mock-vs-spy/`, Mockito 5.11.0, JUnit 5.10.2, OpenJDK 21.0.12, 7 passing tests) against a deliberately stateful collaborator, so every claim is read from observable side effects rather than return values.
  - Measured: `when(spy.write("hello")).thenReturn("STUBBED")` leaves the spy with `realCallCount() == 1` and `written() == [hello]` **before the test has done anything**, because Mockito must evaluate the argument to `when(...)` and evaluating it means really calling the method. `doReturn("STUBBED").when(log).write("hello")` leaves both at zero. This is the whole reason Mockito ships two stubbing syntaxes, and it is the classic spy bug.
  - Two further results were recorded rather than predicted. A stub **does** apply when a spy's real method calls that method on itself (`writeTwice("x")` returned `"STUBBED|STUBBED"` with zero real invocations) — the **opposite** of `@Transactional` self-invocation, where an internal `this.method()` bypasses the proxy entirely. And a `final` method stubs fine (`doReturn(...).when(log).sealedWrite("x")` returned the stub with the real method never running), because Mockito 5 ships the inline mock maker as its default — so the widely repeated "you cannot mock final methods" is pre-Mockito-5 folklore. Both are cross-linked to [Transactional Proxy Mechanics and Propagation](../05-spring/transactional-proxy-mechanics-and-propagation.md), since neither answer is derivable from "it's a proxy."
- **[AWS Core Services for Backend Engineers](../15-cloud/aws-core-services-for-backend-engineers.md)** (`1.2 → 1.3`). One minor omission: the chapter taught storage by access model only, so S3's size limits were absent. Added the 5 TB object ceiling, the 5 GB single-`PUT` cap that forces multipart upload, why multipart is worth using well below that threshold, and the billable-incomplete-parts consequence of an abandoned upload. Contributed a flashcard rather than a deep interview question, since the addition is a paragraph and not a new Interview Questions entry — recorded that way in the question bank rather than inflating the count.
- Companions: 1 new cheat sheet and 1 new flashcard deck (7 cards), plus `@Mock`/`@Spy` material added to the existing testing cheat sheet and deck (3 cards) and an S3 card to the AWS chapter, deck, and cheat sheet. New totals, counted directly against the file system: **298 cheat sheets, 298 decks, 1072 cards** (297/297/1061 before this change). Question-bank entries added to `05-spring.md` (+3 questions, +4 quick-fire; 90→97), `08-testing.md` (+1 question, +3 quick-fire; 33→37), and `15-cloud.md` (+1 quick-fire; 28→29). `05-spring/INDEX.md` gained T-2441; `00-overview/INDEX.md` chapter count for `05-spring` went 15→16.
- `validate.py`: errors 0, warnings 13 (unchanged baseline). Anchor-resolution check clean across every file touched.

## [2026-09-29] (second entry this date) — Production debugging: a symptom-first entry point, a mock round, and a cookbook symptom index

### Added

- The user supplied a "Top 10 toughest Java interview questions" list — ten open-ended production-troubleshooting scenarios — and asked whether the repository already answers them with a walkthrough. It was audited against real content rather than memory.
- **Eight of the ten already had a complete walkthrough**, and the audit says so rather than manufacturing gaps: `production-cookbook/` holds 202 worked incidents with symptoms, hypotheses, evidence, investigation timeline, root cause, mitigation, permanent fix, prevention, and interview framing; 617 chapter sections carry their own `## Production Scenarios`. Memory leaks, lost updates, N+1, deadlocks, CORS, scalability limits, and the behavioural "a failure you caused" question were all covered in depth, several of them by multiple entries each.
- **The audit's real finding was structural, and it applied to the whole repository.** Every chapter is organised by *cause* — the N+1 problem, connection pool sizing, lock ordering, GC tuning. A scenario interview question is organised by *symptom*, and the cause is precisely what the candidate does not yet know. A reader handed "the API got slow" had to already know the answer in order to find the chapter that contains it, which is exactly backwards from how the question is posed.
- **[Production Troubleshooting Methodology](../13-observability/production-troubleshooting-methodology.md)** (new chapter, T-2442, `13-observability`). The missing symptom-first entry point — the three triage questions that cost nothing and eliminate most of the candidate space, the two latency checks worth more than any profiler, tool costs ordered by what they discriminate, and full walkthroughs for the two scenarios that genuinely had none. It complements rather than overlaps [Incident Response and Blameless Postmortems](../13-observability/incident-response-and-blameless-postmortems.md), which owns the organisational process; this chapter owns the technical reasoning inside it.
  - Two real demos in `practice/java/observability/production-troubleshooting/` (Spring Boot 3.5.16, Spring Framework 6.2.19, embedded Tomcat 10.1.55, OpenJDK 21.0.12, 10 cores). **Data volume:** an N+1 implementation and a batched one measured 2,809 us versus 510 us at 10 rows — 6x, both instant, nothing a developer would notice — and **1,283,121 us versus 507 us at 5,000 rows**, a **2531x** gap. The point the measurement makes is structural rather than cautionary: the two have different *curves*, not different constants, so a dev dataset sits at the one point where they overlap and a faster machine moves both curves down without closing the gap.
  - **Concurrency:** with a connection pool of 10 and five round trips per request, throughput flattened at roughly 7,600 req/s from concurrency 10 onward and was **no better at 200**. More striking, and the reason the chapter leads with it: **p50 stayed at 1 ms at every concurrency level including 200, while p99 went from 1 ms to 152 ms.** The additional time is entirely queueing, distributed unevenly by an unfair semaphore, so it lands on the tail and never touches the middle — a saturated system that a mean-latency dashboard reports as perfectly healthy. This is the measured version of the argument in [Percentiles, Tail Latency, and Coordinated Omission](../13-observability/percentiles-tail-latency-and-coordinated-omission.md).
  - **Silent 500s:** four endpoints return byte-identical `{"error":"internal error"}` with HTTP 500 and fail for three genuinely different reasons — swallowed in a bare `catch` (evidence gone permanently), logged at a level production does not emit (recoverable by config alone, often without a deploy), and a catch-all `@ExceptionHandler` that standardises the response and logs nothing. Re-running the identical build with `--logging.level.demo=DEBUG` recovered the wrong-level case and **only** that one, leaving the other two silent. That asymmetry is what makes a log-level change a *discriminating* step rather than a hopeful one, and the three causes differ in the thing an on-call engineer needs first: whether the evidence can be recovered without shipping code.
  - An honest detail kept from the transcript rather than smoothed over: every access-log line records `escaped_exception=none`, including the failing ones, because none of those exceptions escaped the `DispatcherServlet` — each was converted to a 500 inside it. A filter only sees what escapes it, which is why the filter must record the status rather than rely on catching a throwable.
- **[Mock Interview: Production Debugging Round](../../practice/mock-interviews/production-debugging-round.md)** (new, 17th round). `CLAUDE.md`'s Mock Interview Standard has listed "production debugging" as an expected round since the project started, and it was the one type never built. All ten of the user's questions, 60 minutes, candidate and evaluator sections hard-separated, a 1–5 rubric adapted to a diagnostic round (scoring whether the candidate states what each check *eliminates*, not merely whether the order is sensible), pass/borderline/fail signals per question, and follow-ups for candidates who pass. It is the only round in the directory organised by symptom rather than by topic. The debrief guide names two cross-cutting patterns — a tool-first reflex and confirming-rather-than-discriminating checks — as single habits appearing four times each rather than as four separate knowledge gaps, and flags the common profile of strong recall with weak method (scoring well on the specific-cause questions and poorly on the open-ended ones).
- **[Production Cookbook](../../production-cookbook/README.md)** — new **symptom-first index**. The existing index lists 202 entries by cause (`n1-query-regression-from-a-lazy-collection-count-in-a-dto-loop`), which is only usable by someone who already knows the answer. The new table maps 18 symptoms — slow in prod but fine in dev, 500s with nothing in the logs, container OOM-killed while heap looks fine, throughput flat while load rises, averages healthy but users complaining, duplicate or missing messages, tests pass but production breaks, and others — to their candidate causes in rough order of frequency, with representative entries for each. Every one of the 60+ entry links was verified to resolve before committing. It is explicitly framed as a starting point for a differential, not an exhaustive mapping.
- Companions: 1 new cheat sheet and 1 new flashcard deck. The deck carries **7 cards** rather than the usual 5, because the chapter's measured results cover five genuinely distinct ideas plus two on method (tool ordering, and when "make it observable" is the correct next action). New totals, counted directly against the file system: **299 cheat sheets, 299 decks, 1079 cards** (298/298/1072 before this change). Question-bank entries added to `13-observability.md` (+3 questions, +5 quick-fire; 29→37). `13-observability/INDEX.md` gained T-2442; `00-overview/INDEX.md` chapter count for `13-observability` went 7→8; `practice/mock-interviews/README.md` gained row 17.
- `validate.py`: errors 0, warnings 13 (unchanged baseline). Anchor-resolution check clean across every file touched.

## [2026-09-29] (third entry this date) — Collector internals: characteristics, combiner lifecycle, and the Collectors catalog

### Added

- The user supplied a "Collectors Internals — How `collect()` Actually Works" infographic and asked whether the repository already covers it. Audited against real file contents.
- **The four-part collector model was already present** and the audit says so rather than manufacturing a gap. [Streams and Collectors](../02-java/language-core/streams-and-collectors.md) already carried a commented `Collector.of` example naming supplier, accumulator, combiner, and finisher; `groupingBy` with a downstream collector; measured `toMap` duplicate-key behaviour (`IllegalStateException: Duplicate key alice (attempted merging values 100.0 and 75.0)`) with the merge-function fix; measured parallel corruption of a shared `ArrayList` (**24,494** of 100,000 elements surviving); and a measured 6.6x parallel slowdown (3,111 ns versus 20,539 ns per iteration with 20,000 warmup iterations), which is a stronger statement of the infographic's own "don't assume `parallelStream()` is faster" than the infographic makes.
- **Three genuine gaps remained**, each verified by a repository-wide count before writing: `Collector.Characteristics` had **zero** occurrences anywhere; the claim that the combiner is unused in a sequential stream appeared nowhere; and only **four** `Collectors` factory methods were mentioned in the entire repository (`toList`, `toMap`, `groupingBy`, `counting`), against the thirteen-plus a working backend engineer uses.
- **[Streams and Collectors](../02-java/language-core/streams-and-collectors.md)** (`1.0 → 1.1`, 3,903 → 6,374 words). Extended rather than split into a new chapter, per the no-duplication rule — the topic already has a canonical home and a competing "collectors internals" chapter would have fragmented it. New demo at `practice/java/language-core/collectors-internals/` (pure JDK, OpenJDK 21.0.12, 10 cores) instruments a `Collector` so that every supplier, accumulator, combiner, and finisher call is counted, which makes each claim below an invocation count rather than an assertion.
  - **The combiner is never called in a sequential stream.** Over 1,000 elements: `supplier=1 accumulator=1000 combiner=0 finisher=1` sequentially, versus `supplier=64 accumulator=1000 combiner=63 finisher=1` in parallel. The 64 containers on a 10-core machine are worth noting on their own — fork/join splits well past the core count, so "one container per core" is not a safe assumption either.
  - The consequence is what the chapter leads with. A deliberately broken combiner — `(a, b) -> a`, discarding everything accumulated in the second partial — returned all **1,000** elements sequentially and **15** in parallel, across three consecutive runs, with no exception and no warning. That is **98.5% silent data loss** from a bug that no sequential test can detect, because the sequential path never executes the function that is wrong. It is the concrete answer to the infographic's own closing interview question, and it reframes the usual phrasing: the combiner does not matter *more* in parallel, it simply does not run otherwise.
  - **`Collector.Characteristics` is how a collector tells the pipeline what it may skip.** `IDENTITY_FINISH` genuinely skips the finisher rather than making it cheap — the same instrumented collector declared twice, differing only in that flag, recorded **1** finisher invocation without it and **0** with it. The real characteristic sets of nine built-in collectors are read from the JDK rather than described, and the contrasts explain themselves: `toList()` is `[IDENTITY_FINISH]` because it hands back its own `ArrayList`, while `toUnmodifiableList()` declares nothing because it must copy into an immutable list, and `joining()` declares nothing because it must convert a `StringBuilder` into a `String`. `toSet()` adds `UNORDERED` because set semantics make encounter order meaningless.
  - **`CONCURRENT` is a different execution strategy, not a faster `groupingBy`.** Grouping 10,000 elements into 4 groups in parallel with an instrumented downstream collector: `groupingBy` built **67** containers and merged **63** times; `groupingByConcurrent` built **exactly 4** — one per group — and merged **zero**. The chapter states the cost honestly rather than presenting it as a free win: it returns a `ConcurrentMap`, it is `UNORDERED` so within-group encounter order is not preserved, and on a sequential stream it does strictly more work for no benefit.
  - **The catalog.** A table of 24 collectors run against one shared six-employee dataset with real output, covering the thirteen that previously had zero mentions. Two distinctions are called out because they are routinely confused: `partitioningBy` always returns both `true` and `false` entries where `groupingBy` on a boolean key omits the empty side (so `get(false)` cannot NPE with one and can with the other), and `Collectors.filtering` filters *inside* each group keeping empty groups, where `Stream.filter` before grouping removes them.
  - Two mutability traps, checked rather than assumed: `Collectors.toList()` is **MUTABLE** while `Stream.toList()` (Java 16+) and `Collectors.toUnmodifiableList()` both throw `UnsupportedOperationException` — code that collects then mutates works with one and throws with the other, one "modernise this" refactor apart. And `Collectors.toList()` guarantees only mutability, not that the result is an `ArrayList`. Separately, `toMap`'s default `HashMap` visibly scrambles insertion order where a `LinkedHashMap::new` factory preserves it, both shown in the same transcript.
- One new interview question (Q3, the combiner's purpose — the infographic's own closing question), answered entirely from the measurements, with a Staff-level extension arguing that a hand-written collector is a concurrency primitive disguised as a utility method and that composing built-in collectors is the correct default.
- Companions: the `streams-and-collectors` cheat sheet gained a Collector Internals section and a Collector Selection table; its flashcard deck went from 3 cards to **7**. No new files, so sheet and deck counts are unchanged. No new cheat sheet or deck was created, so only the card count moves. New totals, counted directly against the file system: **300 cheat sheets, 300 decks, 1088 cards** (1,084 cards before this change). Question-bank entries added to `02-java-language-core.md` (+1 deep question, +4 quick-fire; 121→126, and the `02-java` four-subdomain total 276→281). `02-java/INDEX.md`'s T-107 row records the extension.

## [2026-09-29] (fourth entry this date) — Batch and scheduled job design

### Added

- Unlike the three preceding entries for this date, this gap was found by a **fresh topic-coverage scan** rather than from material the user supplied: a systematic sweep of common backend interview topics — idempotency keys, rate limiting, circuit breakers, sagas, outbox, CQRS, feature flags, canary deploys, service mesh, API gateways, GraphQL, gRPC, webhooks, WebSockets, leader election, distributed locks, consistent hashing, CAP, replication, sharding, connection pooling, backpressure, virtual threads, reactive — counted against real repository content with `/usr/bin/grep` rather than the shell's `ugrep` alias.
- **Most topics scored well.** The scan's honest output is that the repository already covers the large majority of them across multiple chapters each. One area came back at effectively zero: **batch and scheduled job design**. `Spring Batch`, `ShedLock`, `Quartz`, `long-running job`, and `re-runnable` all returned **zero** files. The classic "your scheduled job runs on every replica" failure appeared nowhere — every `every instance` match turned out to be about Java's `static` keyword or heap-dump retention, which is exactly the kind of false positive the scan was designed to catch before a gap is claimed. `checkpoint` had 22 hits, all of them safepoints, replication, or coding-interview time-boxing; none was job checkpointing.
- **The pieces existed, scattered, but nothing owned the job itself.** [Auto-Configuration and Bean Lifecycle](../05-spring/auto-configuration-and-bean-lifecycle.md) already covered `@Scheduled`'s thread model and the default single-threaded `TaskScheduler`; [Hibernate Flush Modes and Batch Writes](../06-databases/hibernate-flush-modes-and-batch-writes.md) covered JDBC batching; [Idempotency at System Edges](../11-system-design/idempotency.md) covered idempotency as a general property; [Zero-Downtime Schema Migration](../06-databases/zero-downtime-schema-migration.md) mentioned a crashing backfill as a follow-up question. What was missing was the shape of the job — chunking, restartability, exactly-once execution across replicas, partial-failure policy.
- **[Batch and Scheduled Job Design](../11-system-design/batch-and-scheduled-job-design.md)** (new chapter, T-2443, `11-system-design`). Organised around four decisions — who runs it, how often it commits, what happens after a crash, what happens to a bad record — on the explicit basis that a job which has not answered them has answered them anyway, by default, usually with "all instances, one transaction, start over, crash."
  - Real demo (`practice/java/system-design/batch-job-design/`) against a real **H2 2.3.232** in-memory database on OpenJDK 21.0.12. The job lock is a genuine conditional `UPDATE`, the checkpoint commits in the same transaction as its chunk, and correctness is measured by counting rows in a side-effect table rather than by trusting the job's own reporting — which matters, because in the failing case the job reports success.
  - **Multi-instance execution, measured:** three threads standing in for three pods, released together, against 1,000 invoices produced **3,000** side-effect rows from **3** workers. No error, no log line, and a success report from all three. Lock-guarded, the same input produced **1,000** rows from **1** worker. The chapter stresses why this ships so often: it is invisible until a second replica exists, so it works in development, works in a one-pod staging environment, and breaks the first time someone scales the deployment — a change nobody associates with a job.
  - The fix is one statement, `UPDATE job_lock SET held_by = ?, locked_at = CURRENT_TIMESTAMP WHERE name = ? AND held_by IS NULL`, atomic via the database's own row-level locking with no check-then-act window. The chapter refuses to present it without the **lease expiry**, because a lock with no timeout converts duplicate execution into the job never running again — a strictly worse failure, since it is even quieter. It also names the alternative honestly: moving the schedule to a Kubernetes `CronJob` removes the problem rather than guarding against it, at the cost of a second deployment artefact.
  - **Chunk size framed as a durability budget rather than a performance setting.** Measured over 10,000 rows: 148 ms at one row per commit versus 16 ms at 10,000 — **9.25x** — with worst-case rows lost to a crash going from 0 to 9,999, since the work at risk is exactly one chunk by construction. The argument is that "how fast can this go" leads to the largest chunk that fits in memory, which maximises rework, while "how much repeated work is acceptable" has an answer in seconds or minutes that determines the chunk size directly. A second cost is stated: a chunk is a transaction, so on a busy database the biggest chunk is often not the fastest in practice.
  - **The checkpoint must commit in the same transaction as the work.** A 1,000-invoice job crashing at invoice 600: the single-transaction version committed **zero** and needed **1,599** units of work to eventually commit 1,000; the chunked version committed **500**, resumed at **501**, and wasted exactly **99** — the open chunk. The chapter is explicit that the single-transaction version is *not incorrect*, only wasteful and operationally fragile, because a six-hour job failing in hour five redoes five hours and holds one transaction open throughout. Writing progress to a log file, an in-memory field, or a separate service reintroduces exactly the dual-write problem [Distributed Transactions: Saga, Outbox, and 2PC](../10-distributed-systems/distributed-transactions-saga-and-outbox.md) exists to describe.
  - **Restartable is not idempotent**, stated as its own concept with the "run it twice — is the result identical to running it once?" test. A checkpoint reduces how often a record is processed twice; only idempotent per-record work makes it safe when it happens, whether from a crash between work and commit, a stolen lease, or an operator rerun. The chapter prefers a status column on the rows over a checkpoint table wherever the data allows, because progress then becomes a property of the data and cannot disagree with it.
  - **Fail-fast versus skip**, measured on one poison record at position 500 of 1,000: fail-fast stopped, leaving 500 unprocessed, and was loud about it; skip processed 999, reported success, and the dropped record existed only as a counter. Skip is presented as safe only under two conditions — the skip count is an alerted metric, and skipped records land somewhere they can be inspected and replayed — with the Staff-level extension that the threshold should be a *rate*, since the same code reports success whether 1 or 400 records were skipped.
  - The Internal Implementation section states honestly what Spring Batch adds and costs: its real value is the `JobRepository` — durable, queryable execution history and restart-from-last-failure without hand-rolling a checkpoint table — and its cost is a substantial framework plus its own schema. For one job on one table the demo's fifty lines are the honest comparison; for a portfolio of jobs, re-implementing a `JobRepository` badly is the likelier outcome of avoiding it.
- Companions: 1 new cheat sheet and 1 new flashcard deck (5 cards). New totals, counted directly against this branch's base: **300 cheat sheets, 300 decks, 1084 cards** (299/299/1079 before this change). Question-bank entries added to `11-system-design.md` (+3 deep questions, +5 quick-fire; 50→58). `11-system-design/INDEX.md` gained T-2443; `00-overview/INDEX.md` chapter count for `11-system-design` went 10→11.
- `validate.py`: errors 0, warnings 13 (unchanged baseline). Anchor-resolution check clean across every file touched.
