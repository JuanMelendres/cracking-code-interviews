---
title: "Syllabus Changelog"
document_type: syllabus-changelog
status: active
last_updated: 2026-09-06
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
