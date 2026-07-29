# Week 1 Evaluation Rubric

Six dimensions, scored 1–5. 3 = Mid. **4 = Senior.** 5 = Staff. General definitions are in `00-project/learning-roadmap.md` §8; this file adds Week-1-specific evidence anchors — what to actually listen for in *this week's* mock, not the general definition alone.

---

## Technical Depth

| Score | Week 1 evidence anchor |
|---|---|
| 1 | Recites the hexagonal architecture definition, cannot say where a repository interface lives |
| 2 | Names ports/adapters correctly, chain collapses at "would you use this on every project" |
| 3 | Sustains 2 follow-ups from `01-…` §7; no production example offered unprompted |
| **4** | **Sustains 4 follow-ups; states the §4 trade-off table unprompted; names a real (or template-filled) production example** |
| 5 | Volunteers the folder-layout trap (§8) before being asked; extends to the Staff framing (§9) unprompted |

## Coding

| Score | Week 1 evidence anchor |
|---|---|
| 1 | Cannot produce a working LRU cache |
| 2 | Working LRU after hints; silent while coding |
| 3 | LRU correct in ~30 min; narrates partially |
| **4** | **LRU correct in ≤ 20 min from scratch; narrates all six phases; states complexity unprompted** |
| 5 | ≤ 15 min; proactively tests the "update existing key at capacity" edge case *before* being asked — the exact case the errata drill trains |

## System Design

*(Not a Week 1 Deep topic — scored only if a design exercise was attempted this week per the roadmap's optional Day 0 diagnostic.)*

| Score | Evidence anchor |
|---|---|
| 1–5 | See general rubric, `00-project/learning-roadmap.md` §8.3 — Week 1 has no dedicated design deliverable |

## Behavioral Communication

| Score | Week 1 evidence anchor |
|---|---|
| 1 | Story 1 rambles past 2:30, no clear S/T/A/R structure |
| 2 | Loose STAR, no quantified result |
| 3 | Clear STAR, quantified, first-person |
| **4** | **≤ 2 min; names the alternative considered; states what the choice cost — not just what it gained** |
| 5 | Represents the rejected alternative in its strongest form when asked "what if the other option had won" |

## Java Fluency

| Score | Week 1 evidence anchor |
|---|---|
| 1 | Syntax hesitation on basic collection operations (HashMap, ArrayList) |
| 2 | Correct but slow; doesn't justify `HashMap` vs `TreeMap` choice when asked |
| 3 | Fluent on the 7 warm-up problems; justifies choices when asked |
| **4** | **Justifies collection choice unprompted (e.g. "HashMap here because I don't need ordering, O(1) average lookup")** |
| 5 | Discusses the JVM-level cost difference between the buggy and fixed LRU (extra map operations are O(1) either way — the real cost of the bug is correctness, not performance; noting this distinction unprompted is a 5) |

## Production Judgment

| Score | Week 1 evidence anchor |
|---|---|
| 1 | No mention of what could go wrong with the hexagonal refactor |
| 2 | Acknowledges a cost when prompted |
| 3 | Names the mapping-code cost when asked |
| **4** | **Names the §4 cost unprompted, and separately identifies when the pattern is *not* worth it (Q5) without being asked "when would you not use this"** |
| 5 | Extends to the write-amplification cost of adding a database index (§11 of `02-…`) as a *production capacity planning* concern, unprompted |

---

## Week 1 pass bar

This week has **no formal checkpoint** — Week 3 is the first gated checkpoint (`00-project/learning-roadmap.md` §3, Week 3). Use this rubric to identify which of the six dimensions is weakest *this week specifically*, and weight Monday of Week 2 toward reinforcing it rather than moving on uniformly.
