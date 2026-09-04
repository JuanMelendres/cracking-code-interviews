---
title: "Syllabus Changelog"
document_type: syllabus-changelog
status: active
last_updated: 2026-09-04
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

- Eight more canonical chapters, completing the domain's full 18-chapter working list: [Dynamic Programming](../03-data-structures-algorithms/dynamic-programming.md) (T-2110, D14 T-1411, ⭐ — elevated from two study-packs, 12 problems total), [Intervals, Merging, and Sweep Line](../03-data-structures-algorithms/intervals-merging-and-sweep-line.md) (T-2111, D14 T-1412), [Greedy and the Exchange Argument](../03-data-structures-algorithms/greedy-and-the-exchange-argument.md) (T-2112, D14 T-1413), [Bit Manipulation](../03-data-structures-algorithms/bit-manipulation.md) (T-2113, D14 T-1414), [Tries and Prefix Structures](../03-data-structures-algorithms/tries-and-prefix-structures.md) (T-2114, D14 T-1415), [Design-Style Coding Problems](../03-data-structures-algorithms/design-style-coding-problems.md) (T-2115, D14 T-1416, ⭐), [Concurrency Coding Problems](../03-data-structures-algorithms/concurrency-coding-problems.md) (T-2116, D14 T-1417, ⭐), and [Advanced Structures](../03-data-structures-algorithms/advanced-structures-segment-tree-fenwick-rolling-hash.md) (T-2117, D14 T-1418 — elevated from `practice/java/advanced-structures/README.md` rather than a study-pack, since none exists for this Expert-tier, roadmap-excluded topic).
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
- T-1903 and T-1904 each state an explicit boundary against an existing `17-architecture` chapter that owns the same subject's technical/architectural mechanics ([Strangler Fig and Migration Patterns](../17-architecture/strangler-fig-and-migration-patterns.md) and [Technical Debt and Evolutionary Architecture](../17-architecture/technical-debt-and-evolutionary-architecture.md) respectively) — this domain's chapters cover only the organizational-leadership layer (sequencing, stakeholder buy-in, prioritization, advocacy), not the pattern or metaphor itself.
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
- Cheat sheets, flashcards, and production-cookbook entries for all Phase 5 new-writing chapters this session has produced — still deferred to a separate batch.
