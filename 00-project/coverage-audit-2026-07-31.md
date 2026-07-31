# Repository Coverage Audit
**Date:** 31 July 2026
**Scope:** Cross-reference of `00-project/knowledge-architecture-blueprint.md`'s Master Topic Register against actual repository content (`handbook/`, `study-packs/`, `practice/`, and the Phase 6 complementary-deliverable directories).
**Method:** Automated extraction and cross-reference, not manual estimation — see §6 for the exact commands. Every count below is reproducible.

---

## 0. Executive Summary

The technical handbook spine is real but uneven: 62 of 198 register topics (31%) have a canonical `handbook/` chapter, concentrated in Spring/Databases/Collections/Cloud/System-Design (50–57% each) and nearly absent in JVM (8%) and Security (0%). Coding-problem volume is the single largest shortfall against the program's own stated target (60 solved vs. 150–170 targeted). Behavioral stories and mock-interview volume both exceed their targets. Phase 6 complementary deliverables — interview-playbook, cheat-sheets, flashcards deck, architecture atlas, production cookbook, behavioral handbook — are almost entirely unbuilt; three of the six named directories don't exist at all.

A documentation-integrity issue surfaced as a side effect: the blueprint's own prose (§0, §4, §7) states the register holds "124 topics," but the actual Master Topic Register table (§3, lines 117–405) contains 198 unique topic IDs across 16 domains. This audit uses the real table count (198) as ground truth, not the stale prose figure.

---

## 1. Register Ground Truth

`00-project/knowledge-architecture-blueprint.md` §3 ("The Master Topic Register"), lines 117–405, contains 198 unique `T-NNNN` rows across 16 domains (D1–D16). No duplicate IDs within the table. This is the authoritative source for §2 below.

The blueprint's prose elsewhere claims "124 topics" / "20% of the register" (25 topics) / "1,338h... complete register" — these figures predate or otherwise disagree with the table itself and should be treated as stale until reconciled.

| Domain | Topics in register |
|---|---|
| D1 · Java Language Core | 16 |
| D2 · Collections | 9 |
| D3 · JVM | 12 |
| D4 · Concurrency | 16 |
| D5 · Spring | 17 |
| D6 · Persistence & Databases | 18 |
| D7 · Messaging & Kafka | 10 |
| D8 · System Design | 14 |
| D9 · Architecture | 16 |
| D10 · Cloud & Infrastructure | 9 |
| D11 · Testing | 8 |
| D12 · Performance & Observability | 8 |
| D13 · Security | 7 |
| D14 · Algorithms & Coding | 19 |
| D15 · Behavioral & Leadership | 15 |
| D16 · Interview Craft | 4 |
| **Total** | **198** |

---

## 2. Handbook Canonical-Chapter Coverage

A topic counts as "covered" if a `study-packs/week-NN/0N-*.md` file's `topic_id:` front-matter field names it AND a `canonical:` field points to a real `handbook/` chapter. 52 handbook chapter files carry 62 distinct topic IDs (some chapters bundle 2–3 IDs — e.g. the Spring transaction chapter covers T-503/504/505 in one file). All 62 covered IDs were verified to exist in the register table (zero false matches from ID typos or off-register references).

| Domain | Covered / Total | % |
|---|---|---|
| D1 · Java Language Core | 5 / 16 | 31% |
| D2 · Collections | 5 / 9 | 56% |
| D3 · JVM | 1 / 12 | 8% |
| D4 · Concurrency | 4 / 16 | 25% |
| D5 · Spring | 9 / 17 | 53% |
| D6 · Persistence & Databases | 9 / 18 | 50% |
| D7 · Messaging & Kafka | 4 / 10 | 40% |
| D8 · System Design | 8 / 14 | 57% |
| D9 · Architecture | 6 / 16 | 38% |
| D10 · Cloud & Infrastructure | 5 / 9 | 56% |
| D11 · Testing | 2 / 8 | 25% |
| D12 · Performance & Observability | 3 / 8 | 38% |
| D13 · Security | 0 / 7 | 0% |
| D14 · Algorithms & Coding | 0 / 19 | 0%* |
| D15 · Behavioral & Leadership | 1 / 15 | 7%* |
| D16 · Interview Craft | 0 / 4 | 0%* |
| **Total** | **62 / 198** | **31%** |

\* D14/D15/D16 are not meant to live in `handbook/` per CLAUDE.md's canonical-ownership model — see §3 for their actual coverage in their real homes (`practice/`, story references, mock-interview files).

### D13 Security = 0/7 is a real gap, not a miscategorization

T-511 (Spring Security filter chain), T-512 (OAuth2/OIDC), and T-513 (JWT design) — which physically live in `handbook/spring/` and `handbook/security/` — are classified under **D5 Spring** in the blueprint's own register, not D13. D13's actual 7 topics (T-1302 AuthN/AuthZ RBAC/ABAC, T-1304 secrets management & key rotation, T-1305 injection/input validation, T-1306 supply-chain/SBOM, T-1307 multi-tenancy isolation, plus 2 more) have zero coverage anywhere in the repository.

### JVM (1/12) and Testing (2/8) are the next-largest gaps in the covered domains

JVM has only the GC-fundamentals chapter (T-306); missing: memory layout, object headers/compressed oops, JIT tiered compilation, escape analysis, safepoints, ZGC/Shenandoah, native memory, container ergonomics beyond what Week 15's Cloud chapter covers tangentially. Testing has test-strategy/doubles and integration-testing only; missing: JUnit 5 advanced features, contract testing, performance/load testing methodology, live-coding-tests-in-interview.

---

## 3. Coverage Outside the Handbook (D14 / D15 / D16)

### D14 · Algorithms & Coding (19 topics, 0 handbook chapters — by design)

Lives in `practice/java/` + per-week coding-practice files.

- **60 distinct LeetCode problem numbers** solved across 13 coding-practice files (one per week except weeks 12 and 15, which use a loop-embedded and lab-embedded format respectively)
- **45 real `.java` problem-source files** under `practice/java/`
- Roadmap's own stated Plan B target: **150–170 coding problems cumulative**
- **Result: 60 / 150–170 — well short of the program's own target**, despite 19/19 algorithm *patterns* (arrays, hashing, binary search, trees, graphs, DP, backtracking, etc.) all being touched by at least one problem.

### D15 · Behavioral & Leadership (15 topics, 1 handbook chapter)

Stories are numbered inline (Story 1 through the highest reference found, Story 13) rather than housed in a standalone deliverable.

- **13 stories built** (highest "Story N" reference across all study-pack files)
- Roadmap's own stated target: **12–14 stories**
- **Result: 13 / 12–14 — target essentially met** on volume, but no `behavioral-handbook/` exists to consolidate them (see §4) — they're scattered as inline references across mock and checklist files.

### D16 · Interview Craft (4 topics, 0 handbook chapters — by design)

- **14 mock-interview files** + **3 checkpoint files** + **4 loop files** (Week 12) = **21 mock artifacts**
- Roadmap's own stated target: **14 mocks**
- **Result: 21 / 14 — target exceeded**, but again no dedicated `interview-playbook/` home; the actual mock content lives entirely inside `study-packs/`.

---

## 4. Phase 6 Complementary Deliverables (CLAUDE.md repository structure)

| Directory | Real content files | Status |
|---|---|---|
| `interview-playbook/` | 1 (`technical-answers/trade-off-narration-and-adrs.md`) | Started, essentially empty |
| `cheat-sheets/` | 0 | Empty (`.gitkeep` only) |
| `flashcards/` (top-level, spaced-repetition deck by topic ID) | 0 | Empty (`.gitkeep` only) — see note below |
| `templates/` | 0 | Empty (`.gitkeep` only) |
| `architecture-atlas/` | — | **Directory does not exist** |
| `production-cookbook/` | — | **Directory does not exist** |
| `behavioral-handbook/` | — | **Directory does not exist** |

**Note on flashcards:** per-week flashcard files *do* exist and are populated (`study-packs/week-NN/0N-flashcards.md`, 15–16 cards each, across ~13 weeks). This table's "0" refers specifically to the separate top-level aggregated-by-topic-ID spaced-repetition deck that CLAUDE.md's Flashcard Standard specifies as a distinct deliverable — that one was never built.

---

## 5. Net Assessment

| Area | State |
|---|---|
| Core technical handbook (Spring, DB, Kafka, System Design, Collections, Cloud) | Solid at top-IWI depth, 40–57% register coverage |
| JVM, Concurrency, Testing, Performance, Architecture handbook depth | Thin, 8–38% register coverage |
| Security handbook depth | Zero coverage |
| Coding-problem volume | Well short of program's own 150–170 target (60 solved) |
| Behavioral story volume | Target met (13 of 12–14) |
| Mock-interview volume | Target exceeded (21 of 14) |
| Phase 6 complementary deliverables | Almost entirely unbuilt; 3 of 6 named directories don't exist |
| Blueprint internal consistency | Stale topic-count figures in prose vs. the actual register table |

---

## 6. Methodology (reproducible)

```bash
# 1. Extract the true register (§3 of the blueprint only, lines 117-405,
#    to avoid double-counting topics that reappear in later summary tables)
sed -n '117,405p' 00-project/knowledge-architecture-blueprint.md | awk '
/^### D[0-9]+/ { domain=$0; sub(/^### /,"",domain) }
/^\| \*?\*?T-[0-9]+/ {
  line=$0; gsub(/\*/,"",line)
  split(line, f, "|")
  id=f[2]; gsub(/^ +| +$/,"",id)
  print domain "\t" id
}' > /tmp/topic_register.tsv

# 2. Extract every covered topic ID from study-pack front matter
grep -rh "^topic_id:" study-packs/*/*.md \
  | sed 's/topic_id: //' | tr '/' '\n' | sed 's/^ *//;s/ *$//' \
  | sort -u > /tmp/covered_ids.txt

# 3. Cross-reference: covered IDs not in the register (integrity check — result was empty, zero false matches)
comm -23 /tmp/covered_ids.txt <(cut -f2 /tmp/topic_register.tsv | sort -u)

# 4. Per-domain covered/total
awk -F'\t' 'NR==FNR{cov[$1]=1; next} {if ($2 in cov) c[$1]++; t[$1]++} \
  END{for (d in t) printf "%-30s %d/%d\n", d, (c[d]+0), t[d]}' \
  /tmp/covered_ids.txt /tmp/topic_register.tsv

# 5. D14/D15/D16 counts
grep -rohiE "LC ?[0-9]+" study-packs/*/*.md | tr 'A-Z' 'a-z' | sed 's/lc *//' | sort -n -u | wc -l
find practice/java -name "*.java" | wc -l
grep -rohE "Story [0-9]+" study-packs/*/*.md | grep -oE "[0-9]+" | sort -n | uniq | tail -1
find study-packs -iname "*mock*" -name "*.md" | wc -l
find study-packs -iname "*checkpoint*" -name "*.md" | wc -l
find study-packs/week-12 -iname "*loop*" -name "*.md" | wc -l

# 6. Structural directory checks
find interview-playbook cheat-sheets flashcards templates -name "*.md" | wc -l
test -d architecture-atlas; test -d production-cookbook; test -d behavioral-handbook
```

---

## 7. Suggested Next Actions (not prioritized — for discussion)

1. Reconcile the blueprint's stale "124 topics" prose against the real 198-row register (§1).
2. Close the coding-problem volume gap (60 vs. 150–170) — the largest single quantitative shortfall.
3. Decide whether to start Phase 6 (interview-playbook, cheat-sheets, atlas, cookbook, behavioral-handbook) or continue deepening handbook coverage in JVM/Security/Testing first.
4. If Phase 6 starts, the behavioral stories (13, already built) and mock-interview content (21 artifacts, already built) are the cheapest wins — they exist and just need consolidating into `behavioral-handbook/` and `interview-playbook/` per CLAUDE.md's canonical-ownership model, not written from scratch.
