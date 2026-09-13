---
title: "GIN Index Addition Regressing a Low-Selectivity JSONB Query"
document_type: production-cookbook-entry
domain: databases
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/06-databases/jsonb-and-advanced-index-types.md
source: syllabus/06-databases/jsonb-and-advanced-index-types.md#production-scenarios
---

# GIN Index Addition Regressing a Low-Selectivity JSONB Query

## Context

A product-catalog filter endpoint queries `attributes @> '{"category": "..."}'` against a JSONB column on a 300,000-row table. A team, following standard advice, adds a GIN index after noticing the endpoint feels slow.

## Symptoms

After deploying the index, the endpoint's p50 latency is unchanged, and p95 latency for the most common category filters (which each match a large fraction of the table) actually increases slightly.

## Impact

Wasted engineering effort, a real if modest regression for the most common queries, and a new index adding real write-path overhead to every product update.

## Initial Hypotheses

- Stale statistics after the index build — checked, `ANALYZE` was run.
- The index wasn't actually being used — checked, `EXPLAIN` confirms it is.
- The index genuinely doesn't help this specific query shape — correct.

## Evidence

`EXPLAIN ANALYZE` on the most common (least selective) category filters shows a single-threaded bitmap heap scan fetching thousands of individual heap blocks, slower than the parallel sequential scan it replaced.

## Investigation Timeline

1. Endpoint feels slow; team adds a GIN index as the standard fix.
2. p50 unchanged and p95 slightly worse for common filters, observed post-deploy.
3. Stale-statistics and index-not-used hypotheses ruled out.
4. `EXPLAIN ANALYZE` run before and after, isolating the regression specifically to low-selectivity filters, while rare-category filters genuinely improved.

## Root Cause

A GIN index was added uniformly, without checking the actual selectivity distribution of the predicate it was meant to accelerate — for low-selectivity predicates, parallel sequential scan already competes well, and single-threaded indexed random I/O can lose.

## Immediate Mitigation

None needed — no correctness issue, only a modest performance one for a subset of queries.

## Permanent Fix

Keep the GIN index (it genuinely helps selective, rare-category filters) but add covering statistics/monitoring to catch this class of regression, and explicitly measure — never assume — before adding a similar index elsewhere.

## Alternatives Considered

A partial GIN index restricted to less-common categories — considered but rejected as unnecessary complexity, since the modest regression on common categories didn't materially harm the product.

## Trade-offs

The index adds real write-path cost (every product update also updates the GIN index) in exchange for a real win only for the subset of queries matching a small fraction of the table.

## Prevention

Treat "add an index" as a hypothesis requiring `EXPLAIN ANALYZE` verification across the real distribution of query selectivity it will actually see in production — not a context-free best practice.

## Monitoring and Alerts

- Per-query-shape latency dashboards split by filter selectivity (common vs. rare category), rather than one aggregate endpoint latency metric that would hide this exact regression.
- A standing review step requiring `EXPLAIN ANALYZE` evidence, across a representative selectivity range, attached to any new index proposal.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent finding.

- **Situation:** adding a GIN index, following standard advice, made the most common queries slightly slower rather than faster.
- **Task:** determine whether the index itself was the problem or something else was.
- **Action:** ran `EXPLAIN ANALYZE` before and after across both common and rare filter values, isolating the regression to low-selectivity predicates specifically.
- **Result:** kept the index for the rare-category queries it genuinely helps, and established `EXPLAIN ANALYZE`-based verification as a standing requirement before future index additions.

## Staff-Level Discussion

This is Interview Question 1 in the canonical chapter's own Interview Questions section — "does a GIN index always speed up a JSONB containment query" — arriving as a real, measured production finding rather than a trick question. The organizational lesson is that "add an index" is a hypothesis, not a guaranteed win, and the only way to know whether it holds for a given query's actual selectivity distribution is to measure it — a general best practice can point you at a candidate fix, but only `EXPLAIN ANALYZE` against real data confirms it.

## Related Handbook Chapters

- [JSONB and Advanced Index Types](../syllabus/06-databases/jsonb-and-advanced-index-types.md) — the canonical GIN-index selectivity measurement behind this incident.
